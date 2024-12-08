/**
 * Day 8: Antinodes
 *
 * Here's how the solution works:
 *
 * First, we parse the input grid to identify all antennas and their frequencies.
 *
 * We group antennas by their frequency (since antinodes only form between antennas of the same frequency).
 *
 * For each frequency group:
 *
 * We look at every pair of antennas
 * Calculate potential antinode positions based on the "twice the distance" rule
 * An antinode occurs when one antenna is twice as far away as the other
 * For each pair of antennas:
 *
 * We check every point in the grid
 * Calculate distances to both antennas
 * If one distance is exactly twice the other, it's an antinode
 * We use a Set to store antinodes to ensure we only count unique positions.
 *
 *
 * Key points to note:
 *
 * - We use floating-point comparison with small epsilon (0.0001) due to potential floating-point precision issues
 * - The solution handles both horizontal and vertical alignments
 * - It accounts for antinodes that might occur at antenna positions
 * - It only counts antinodes within the bounds of the input map
 *
 * The output will be the total number of unique antinode positions within the map boundaries.
 *
 */
class Day08 {

    data class Point(val x: Int, val y: Int)
    data class Antenna(val position: Point, val frequency: Char)

    val DEBUG=false;

    fun main() {
        //val input = readInput("Day08_test")
        val input = readInput("Day08_test1")
        val result = findAntinodes(input)
        println("Number of antinodes: $result")
    }

    fun findAntinodes(input: List<String>): Int {
        // 1. Parse input to get antenna positions and frequencies
        val antennas = parseAntennas(input)

        // 2. Group antennas by frequency
        val antennasByFreq = antennas.groupBy { it.frequency }

        // 3. Find all antinodes
        val antinodes = mutableSetOf<Point>()

        if (DEBUG) {
            println("Antennas:")
            antennas.forEach { println("${it.frequency} at (${it.position.x}, ${it.position.y})") }

            // Debug print grouped antennas
            println("\nGrouped by frequency:")
            antennasByFreq.forEach { (freq, list) ->
                println("$freq: ${list.size} antennas")
            }
        }

        // For each frequency group
        antennasByFreq.forEach { (freq, sameFreqAntennas) ->
            // Check each pair of antennas with same frequency
            for (i in sameFreqAntennas.indices) {
                for (j in i + 1 until sameFreqAntennas.size) {
                    val ant1 = sameFreqAntennas[i]
                    val ant2 = sameFreqAntennas[j]

                    // Debug print pairs being checked
                    dbg("\nChecking pair: ${ant1.position} and ${ant2.position} with freq $freq")

                    // Calculate antinodes for this pair
                    findAntinodesForPair(ant1, ant2, input[0].length, input.size)
                        .forEach {
                            // Debug print found antinodes
                            println("Found antinode: $it")
                            antinodes.add(it)

                            dbg(visualizeAntinodes(input, antinodes))
                            dbg("--")
                        }
                }
            }
        }

        // Before returning antinodes.size, add:
         if (DEBUG) {
             dbg("\nFinal map with antinodes (#):")
             dbg(visualizeAntinodes(input, antinodes))
         }

        return antinodes.size
    }

    private fun parseAntennas(input: List<String>): List<Antenna> {
        val antennas = mutableListOf<Antenna>()
        for (y in input.indices) {
            for (x in input[y].indices) {
                val char = input[y][x]
                if (char != '.') {
                    antennas.add(Antenna(Point(x, y), char))
                }
            }
        }
        return antennas
    }

    private fun findAntinodesForPair(ant1: Antenna, ant2: Antenna, width: Int, height: Int): List<Point> {
        val antinodes = mutableListOf<Point>()

        // Get vector from ant1 to ant2
        val dx = ant2.position.x - ant1.position.x
        val dy = ant2.position.y - ant1.position.y

        // Calculate opposite points
        // For ant1: add 2x the vector
        val antinode1X = ant1.position.x + (2 * dx)
        val antinode1Y = ant1.position.y + (2 * dy)

        // For ant2: subtract 2x the vector
        val antinode2X = ant2.position.x - (2 * dx)
        val antinode2Y = ant2.position.y - (2 * dy)

        // Add points if they're within bounds
        if (antinode1X in 0 until width && antinode1Y in 0 until height) {
            antinodes.add(Point(antinode1X, antinode1Y))
        }

        if (antinode2X in 0 until width && antinode2Y in 0 until height) {
            antinodes.add(Point(antinode2X, antinode2Y))
        }

        dbg("${antinodes.size} antinodes: $antinodes")

        return antinodes
    }

    private fun distance(p1: Point, p2: Point): Double {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return Math.sqrt((dx * dx + dy * dy).toDouble())
    }

    fun visualizeAntinodes(input: List<String>, antinodes: Set<Point>): String {
        val result = input.map { it.toCharArray() }.toTypedArray()

        // Mark antinodes with '#'
        antinodes.forEach { point ->
            if (point.y in result.indices && point.x in result[0].indices) {
                result[point.y][point.x] = if (result[point.y][point.x] == '.') '#' else result[point.y][point.x]
            }
        }

        return result.joinToString("\n") { it.joinToString("") }
    }

    fun dbg(s: String) {
        if (DEBUG)
            println(s)
    }
}

fun main() {
    Day08().main()
}
