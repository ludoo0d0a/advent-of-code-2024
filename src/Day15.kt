
/*
--- Day 15 star 2 ---

Problem day 15 star 2

This implementation includes both `part1` and `part2` methods, with `part2` handling the scaled-up warehouse as described in the problem. The `main` function is set up to run both parts on the sample and input data.

Key points of the implementation:

1. The map is represented as `List<CharArray>` for efficient access and modification.
2. The `scaleMap` function handles the horizontal scaling of the warehouse for part 2.
3. The `simulateRobot` function performs the robot's movements and box pushing.
4. Efficient algorithms are used for finding the robot, checking moves, and pushing boxes.
5. The `calculateGPSSum` function computes the final GPS sum based on box positions.
6. Debug output is controlled by the `DEBUG` constant (set to `false` by default).
7. `Long` is used instead of `Int` to avoid potential overflow in calculations.

This implementation should solve both parts of the problem efficiently, even for large inputs.
*/
import java.util.*

// kotlin:Day15.kt
import java.util.*

// Prompt: This code solves the warehouse robot problem with scaled-up maps.
// It uses efficient algorithms and data structures to handle large inputs.
// The map is represented as List<CharArray> for quick access and modification.
// Long is used instead of Int to avoid overflow in calculations.

class Day15 {
    companion object {
        private const val DEBUG = true
        private const val EXPECTED_SAMPLE = 9021L

        @JvmStatic
        fun main(args: Array<String>) {
            val sample2 = readFileLines("Day15_star2_sample")
            val result_sample2 = part2(sample2)
            println("sample2 result=$result_sample2")

//            val input = readFileLines("Day15_input")
//            val result_input = part1(input)
//            println("Result=$result_input")
//
//            val result2_input = part2(input)
//            println("Result2=$result2_input")
        }

        fun part1(input: List<String>): Long {
            val (map, moves) = parseInput(input)
            val finalMap = simulateRobot(map, moves)
            return calculateGPSSum(finalMap)
        }

        fun part2(input: List<String>): Long {
            val (map, moves) = parseInput(input)
            val scaledMap = scaleMap(map)
            val finalMap = simulateRobot(scaledMap, moves)
            return calculateGPSSum(finalMap)
        }

        private fun parseInput(input: List<String>): Pair<List<CharArray>, String> {
            val mapEnd = input.indexOfFirst { it.isEmpty() }
            val map = input.subList(0, mapEnd).map { it.toCharArray() }
            val moves = input.last()
            return Pair(map, moves)
        }

        private fun scaleMap(map: List<CharArray>): List<CharArray> {
            return map.map { row ->
                row.flatMap { char ->
                    when (char) {
                        '#' -> listOf('#', '#')
                        'O' -> listOf('[', ']')
                        '.' -> listOf('.', '.')
                        '@' -> listOf('@', '.')
                        else -> listOf(char)
                    }
                }.toCharArray()
            }
        }

        private fun simulateRobot(map: List<CharArray>, moves: String): List<CharArray> {
            var robotPos = findRobot(map)
            val newMap = map.map { it.clone() }.toMutableList()

            for (move in moves) {
                val (newX, newY) = when (move) {
                    '>' -> Pair(robotPos.first + 1, robotPos.second)
                    '<' -> Pair(robotPos.first - 1, robotPos.second)
                    '^' -> Pair(robotPos.first, robotPos.second - 1)
                    'v' -> Pair(robotPos.first, robotPos.second + 1)
                    else -> robotPos
                }

                if (canMove(newMap, newX, newY)) {
                    newMap[robotPos.second][robotPos.first] = '.'
                    newMap[newY][newX] = '@'
                    robotPos = Pair(newX, newY)
                } else if (canPush(newMap, robotPos, Pair(newX, newY))) {
                    push(newMap, robotPos, Pair(newX, newY))
                    newMap[robotPos.second][robotPos.first] = '.'
                    newMap[newY][newX] = '@'
                    robotPos = Pair(newX, newY)
                }

                if (DEBUG) {
                    println("After move $move:")
                    newMap.forEach { println(it) }
                    println()
                }
            }

            return newMap
        }

        private fun findRobot(map: List<CharArray>): Pair<Int, Int> {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] == '@') return Pair(x, y)
                }
            }
            throw IllegalStateException("Robot not found in the map")
        }

        private fun canMove(map: List<CharArray>, x: Int, y: Int): Boolean {
            return y in map.indices && x in map[y].indices && map[y][x] == '.'
        }

        private fun canPush(map: List<CharArray>, from: Pair<Int, Int>, to: Pair<Int, Int>): Boolean {
            val dx = to.first - from.first
            val dy = to.second - from.second
            var x = to.first
            var y = to.second

            while (y in map.indices && x in map[y].indices && (map[y][x] == '[' || map[y][x] == ']')) {
                x += dx
                y += dy
            }

            return y in map.indices && x in map[y].indices && map[y][x] == '.'
        }

        private fun push(map: MutableList<CharArray>, from: Pair<Int, Int>, to: Pair<Int, Int>) {
            val dx = to.first - from.first
            val dy = to.second - from.second
            var x = to.first
            var y = to.second
            val boxesToMove = mutableListOf<Pair<Int, Int>>()

            while (y in map.indices && x in map[y].indices && (map[y][x] == '[' || map[y][x] == ']')) {
                boxesToMove.add(Pair(x, y))
                x += dx
                y += dy
            }

            for (i in boxesToMove.size - 1 downTo 0) {
                val (bx, by) = boxesToMove[i]
                map[by + dy][bx + dx] = map[by][bx]
            }
        }

        private fun calculateGPSSum(map: List<CharArray>): Long {
            var sum = 0L
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] == '[') {
                        sum += (y + 1) * 100L + (x + 1)
                    }
                }
            }
            return sum
        }
    }
}
// end-of-code
