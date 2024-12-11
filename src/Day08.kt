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

    val DEBUG=true;

    fun main() {
        val input = readLines("Day08_test1")   // BIG -> 966 not

        //val input = readInput("Day08_test") // ->34
        //val input = readInput("Day08_test2") // ->9
        val result = findAntinodes(input)
        println("Number of antinodes: $result")

        val result2 = findAntinodes(input)
        println("star2: Number of antinodes: $result")
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
                    val foundAntinodes = findCollinearAntinodes(ant1, ant2, input[0].length, input.size)

                    if (DEBUG) {
                        dbg("Already found ${antinodes.size} antinodes:")
                        dbg("Found ${foundAntinodes.size} new antinodes:")
                        foundAntinodes.forEach {
                            // Debug print found antinodes
                            antinodes.add(it)
                            dbg("   Found antinode: $it")
                            dbg(visualizeAntinodes(input, antinodes, ant1, ant2))
                            dbg("--")
                        }
                    }else{
                        antinodes.addAll(foundAntinodes)
                    }



                }
            }
        }

        // Before returning antinodes.size, add:
         if (DEBUG) {
             dbg("\nFinal map with antinodes (#):")
             dbg(visualizeAntinodes(input, antinodes, null, null))
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
    fun findCollinearAntinodes(ant1: Antenna, ant2: Antenna, width: Int, height: Int): Set<Point> {
        val antinodes = mutableSetOf<Point>()
        val dx = ant2.position.x - ant1.position.x
        val dy = ant2.position.y - ant1.position.y
        // Map is bigger, multiplier is bigger
        var multiplier = -40
        while (multiplier <= 40) {
            val x = ant1.position.x + (dx * multiplier)
            val y = ant1.position.y + (dy * multiplier)

            val point = Point(x, y)
            if (isInBounds(point, width, height)) {
                antinodes.add(point)
            }
            multiplier++
        }

        return antinodes
    }

    private fun isInBounds(point: Point, width: Int, height: Int): Boolean {
        return point.x >= 0 && point.x < width && point.y >= 0 && point.y < height
    }
    private fun samePoint(point: Point, ant: Antenna): Boolean {
        return point.x==ant.position.x && point.y==ant.position.y
    }

    // Helper function to check if three points are collinear
    private fun isCollinear(p1: Point, p2: Point, p3: Point): Boolean {
        return (p2.y - p1.y) * (p3.x - p2.x) == (p3.y - p2.y) * (p2.x - p1.x)
    }

    private fun distance(p1: Point, p2: Point): Double {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return Math.sqrt((dx * dx + dy * dy).toDouble())
    }

    fun visualizeAntinodes(input: List<String>, antinodes: Set<Point>, ant1: Antenna? , ant2: Antenna?): String {
        val result = input.map { it.toCharArray() }.toTypedArray()

        // Mark antinodes with '#'
        antinodes.forEach { point ->
            if (point.y in result.indices && point.x in result[0].indices) {
                var value = result[point.y][point.x]
                if (value == '.') {
                    value = '#'
                }else {
                    value = '/'  //antenna AND antinode
                }
                result[point.y][point.x] = value
            }
        }
        if (ant1!=null && ant2!=null) {
            result[ant1.position.y][ant1.position.x] = '1'
            result[ant2.position.y][ant2.position.x] = '2'
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
