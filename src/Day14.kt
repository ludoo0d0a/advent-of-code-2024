
/*
--- Day 14 star 1 ---

Day 14: Restroom Redoubt

This solution implements the problem as described, with the following key points:

1. The `part1` method reads the input file, parses the robots, simulates their movement for 100 seconds, counts the robots in each quadrant, and calculates the safety factor.

2. The `parseRobots` function converts the input strings into `Robot` objects, using `Long` for all position and velocity values.

3. The `simulateRobots` function calculates the final positions of all robots after the specified number of seconds, using modular arithmetic to handle wrapping around the edges.

4. The `countRobotsInQuadrants` function counts the number of robots in each quadrant, excluding robots on the middle lines.

5. The algorithm is optimized by:
   - Calculating the final positions directly instead of simulating each second.
   - Using `Long` to avoid integer overflow.
   - Minimizing recalculations by using indexes and direct computations.

6. The result is printed to the console in the format "Result1=XX" where XX is the calculated safety factor.

This solution should be efficient and able to handle the problem within a reasonable amount of time.
*/
import java.util.*

// kotlin:Day14.kt
import java.io.File

class Day14 {
    companion object {
        // Prompt: Optimize the algorithm to be efficient and fast so that solution can be found in a reasonable amount of time.
        // Use indexes as soon as you can to avoid re-calculating the same value and lost time in long computation.
        // Use Long instead of Int to avoid overflow.

        @JvmStatic
        fun main(args: Array<String>) {
            val result1 = part1()
            println("Result1=$result1")
        }

        private fun part1(): Long {
            val input = readFileLines("Day14_input")
            val robots = parseRobots(input)
            val width = 101L
            val height = 103L
            val seconds = 100L

            val finalPositions = simulateRobots(robots, width, height, seconds)
            val quadrants = countRobotsInQuadrants(finalPositions, width, height)

            return quadrants.fold(1L) { acc, count -> acc * count }
        }

        private fun parseRobots(input: List<String>): List<Robot> {
            return input.map { line ->
                val (pos, vel) = line.split(" ")
                val (px, py) = pos.substringAfter("=").split(",").map { it.toLong() }
                val (vx, vy) = vel.substringAfter("=").split(",").map { it.toLong() }
                Robot(px, py, vx, vy)
            }
        }

        private fun simulateRobots(robots: List<Robot>, width: Long, height: Long, seconds: Long): List<Pair<Long, Long>> {
            return robots.map { robot ->
                val x = (robot.px + robot.vx * seconds).mod(width)
                val y = (robot.py + robot.vy * seconds).mod(height)
                x to y
            }
        }

        private fun countRobotsInQuadrants(positions: List<Pair<Long, Long>>, width: Long, height: Long): List<Long> {
            val midX = width / 2
            val midY = height / 2
            val quadrants = LongArray(4)

            for ((x, y) in positions) {
                when {
                    x < midX && y < midY -> quadrants[0]++
                    x > midX && y < midY -> quadrants[1]++
                    x < midX && y > midY -> quadrants[2]++
                    x > midX && y > midY -> quadrants[3]++
                }
            }

            return quadrants.toList()
        }

    }

    data class Robot(val px: Long, val py: Long, val vx: Long, val vy: Long)
}
// end-of-code
