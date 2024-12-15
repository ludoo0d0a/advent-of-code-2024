
/*
--- Day 15 star 1 ---

Day 15: Warehouse Woes

This implementation follows the requirements and includes the following optimizations:

1. Uses a 2D array (`Array<CharArray>`) to represent the warehouse for efficient access and modification.
2. Directly indexes the warehouse array to find the robot and simulate movements.
3. Uses `Long` for the GPS sum calculation to avoid potential overflow.
4. Parses the input once and stores the warehouse and moves separately.
5. Simulates the robot movement in-place, updating the warehouse array directly.

The `part1` function orchestrates the solution by parsing the input, finding the robot, simulating its movement, and calculating the final GPS sum. The `main` function reads both the sample and actual input files, runs the solution, and prints the results as requested.
*/
import java.util.*

// kotlin:Day15.kt
import readFileLines

class Day15 {
    companion object {
        private const val EXPECTED_SAMPLE = 2028L

        @JvmStatic
        fun main(args: Array<String>) {
            val sample = readFileLines("Day15_star1_sample")
            val resultSample = part1(sample)
            expect(resultSample, EXPECTED_SAMPLE)
            println("Sample result=$resultSample")

            val input = readFileLines("Day15_input")
            val resultInput = part1(input)
            println("Result=$resultInput")
        }

        private fun part1(input: List<String>): Long {
            val (warehouse, moves) = parseInput(input)
            val robot = findRobot(warehouse)
            simulateRobotMovement(warehouse, robot, moves)
            return calculateGPSSum(warehouse)
        }

        private fun parseInput(input: List<String>): Pair<Array<CharArray>, String> {
            val warehouseLines = input.takeWhile { it.isNotEmpty() }
            val warehouse = warehouseLines.map { it.toCharArray() }.toTypedArray()
            val moves = input.last { it.isNotEmpty() }
            return Pair(warehouse, moves)
        }

        private fun findRobot(warehouse: Array<CharArray>): Pair<Int, Int> {
            warehouse.forEachIndexed { y, row ->
                row.forEachIndexed { x, cell ->
                    if (cell == '@') return Pair(y, x)
                }
            }
            error("Robot not found")
        }

        private fun simulateRobotMovement(warehouse: Array<CharArray>, robot: Pair<Int, Int>, moves: String) {
            var (y, x) = robot
            moves.forEach { move ->
                val (dy, dx) = when (move) {
                    '^' -> -1 to 0
                    'v' -> 1 to 0
                    '<' -> 0 to -1
                    '>' -> 0 to 1
                    else -> error("Invalid move: $move")
                }
                val newY = y + dy
                val newX = x + dx

                when {
                    warehouse[newY][newX] == '.' -> {
                        warehouse[y][x] = '.'
                        warehouse[newY][newX] = '@'
                        y = newY
                        x = newX
                    }
                    warehouse[newY][newX] == 'O' -> {
                        val nextY = newY + dy
                        val nextX = newX + dx
                        if (warehouse[nextY][nextX] == '.') {
                            warehouse[y][x] = '.'
                            warehouse[newY][newX] = '@'
                            warehouse[nextY][nextX] = 'O'
                            y = newY
                            x = newX
                        }
                    }
                }
            }
        }

        private fun calculateGPSSum(warehouse: Array<CharArray>): Long {
            var sum = 0L
            warehouse.forEachIndexed { y, row ->
                row.forEachIndexed { x, cell ->
                    if (cell == 'O') {
                        sum += (y + 1) * 100L + (x + 1)
                    }
                }
            }
            return sum
        }

        private fun expect(actual: Long, expected: Long) {
            check(actual == expected) { "Expected $expected, but got $actual" }
        }
    }
}

// Prompt: Implement a solution for the warehouse robot problem described.
// The algorithm simulates the robot's movement and calculates the GPS sum of boxes.
// Optimization: Use 2D array for efficient warehouse representation and direct indexing.
// Use Long for GPS sum to avoid overflow.
// end-of-code
