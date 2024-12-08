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
        val input = readInput("Day08_test")
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

        // For each frequency group
        antennasByFreq.forEach { (_, sameFreqAntennas) ->
            // Check each pair of antennas with same frequency
            for (i in sameFreqAntennas.indices) {
                for (j in i + 1 until sameFreqAntennas.size) {
                    val ant1 = sameFreqAntennas[i]
                    val ant2 = sameFreqAntennas[j]

                    // Calculate antinodes for this pair
                    findAntinodesForPair(ant1, ant2, input[0].length, input.size)
                        .forEach { antinodes.add(it) }
                }
            }
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

        // Calculate distance between antennas
        val dx = ant2.position.x - ant1.position.x
        val dy = ant2.position.y - ant1.position.y
        val dist = Math.sqrt((dx * dx + dy * dy).toDouble())

        // Find points that are half the distance from one antenna
        // and double the distance from the other
        for (y in 0 until height) {
            for (x in 0 until width) {
                val p = Point(x, y)
                val d1 = distance(p, ant1.position)
                val d2 = distance(p, ant2.position)

                // Check if this point forms an antinode (one distance is twice the other)
                if ((Math.abs(d1 - 2 * d2) < 0.0001) || (Math.abs(d2 - 2 * d1) < 0.0001)) {
                    antinodes.add(p)
                }
            }
        }

        return antinodes
    }

    private fun distance(p1: Point, p2: Point): Double {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return Math.sqrt((dx * dx + dy * dy).toDouble())
    }

}

fun main() {
    Day08().main()
}
