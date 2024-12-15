
/*
--- Day 15 star 1 ---

Problem day 15 star 1

This implementation solves the problem as described. Here's a breakdown of the main components:

1. The `part1` function processes the input, separates the map from the moves, and simulates the robot's movement.
2. The `moveRobot` function handles the robot's movement, including pushing boxes.
3. The `moveBoxes` function handles the movement of multiple aligned boxes.
4. The `calculateGPSSum` function calculates the final GPS sum of all boxes.
5. The `displayMap` function prints the current state of the map after each move.

The algorithm is optimized by:
- Using mutable lists to modify the map in-place.
- Using character arrays for efficient string manipulation.
- Handling the movement of multiple aligned boxes in a single operation.

The code uses Long for calculations to avoid potential overflow issues.

Note that this implementation assumes the existence of a `readFileLines` function in a `Utils.kt` file, as mentioned in the problem description. Make sure to include that function or replace it with an appropriate method to read the input files.
*/
import java.util.*

// kotlin:Day15.kt
import java.util.*

class Day15 {
    companion object {
        // Prompt: Optimize the algorithm to be efficient and fast so that solution can be found in a reasonable amount of time.
        // Use indexes as soon as you can to avoid re-calculating the same value and lost time in long computation.
        // Use Long instead of Int to avoid overflow.

        private const val EXPECTED_SAMPLE = 2028L
        private const val EXPECTED_SAMPLE2 = 10092L

        @JvmStatic
        fun main(args: Array<String>) {
            val sample1 = readFileLines("Day15_star1_sample")
            val result_sample1 = part1(sample1)
            expect(result_sample1, EXPECTED_SAMPLE)
            println("sample result=$result_sample1")

            val sample2 = readFileLines("Day15_star1_sample2")
            val result_sample2 = part1(sample2)
            expect(result_sample2, EXPECTED_SAMPLE2)
            println("sample2 result=$result_sample1")

//            val input = readFileLines("Day15_input")
//            val result_input = part1(input)
//            println("Result=$result_input")
        }

        private fun part1(input: List<String>): Long {
            val map = input.takeWhile { it.contains('#') }.toMutableList()
            val moves = input.last().filter { it in "<>^v" }

            var robotPos = findRobot(map)
            displayMap(map)

            for (move in moves) {
                when (move) {
                    '<' -> moveRobot(map, robotPos, -1, 0)
                    '>' -> moveRobot(map, robotPos, 1, 0)
                    '^' -> moveRobot(map, robotPos, 0, -1)
                    'v' -> moveRobot(map, robotPos, 0, 1)
                }
                robotPos = findRobot(map)
                println("Move $move")
                displayMap(map)
            }

            return calculateGPSSum(map)
        }

        private fun findRobot(map: List<String>): Pair<Int, Int> {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] == '@') return Pair(x, y)
                }
            }
            throw IllegalStateException("Robot not found")
        }

        private fun moveRobot(map: MutableList<String>, robotPos: Pair<Int, Int>, dx: Int, dy: Int) {
            var (x, y) = robotPos
            var newX = x + dx
            var newY = y + dy

            if (newX < 0 || newX >= map[0].length || newY < 0 || newY >= map.size || map[newY][newX] == '#') {
                return
            }

            if (map[newY][newX] == 'O') {
                val boxesMoved = moveBoxes(map, newX, newY, dx, dy)
                if (boxesMoved == 0) return
            }

            val row = map[y].toCharArray()
            row[x] = '.'
            map[y] = String(row)

            val newRow = map[newY].toCharArray()
            newRow[newX] = '@'
            map[newY] = String(newRow)
        }

        private fun moveBoxes(map: MutableList<String>, startX: Int, startY: Int, dx: Int, dy: Int): Int {
            var x = startX
            var y = startY
            val boxesToMove = mutableListOf<Pair<Int, Int>>()

            while (x in map[0].indices && y in map.indices && map[y][x] == 'O') {
                boxesToMove.add(Pair(x, y))
                x += dx
                y += dy
            }

            if (x < 0 || x >= map[0].length || y < 0 || y >= map.size || map[y][x] != '.') {
                return 0
            }

            for ((boxX, boxY) in boxesToMove.reversed()) {
                val newBoxX = boxX + dx
                val newBoxY = boxY + dy

                val oldRow = map[boxY].toCharArray()
                oldRow[boxX] = '.'
                map[boxY] = String(oldRow)

                val newRow = map[newBoxY].toCharArray()
                newRow[newBoxX] = 'O'
                map[newBoxY] = String(newRow)
            }

            return boxesToMove.size
        }

        private fun calculateGPSSum(map: List<String>): Long {
            var sum = 0L
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] == 'O') {
                        sum += (y ) * 100L + (x )
                    }
                }
            }
            return sum
        }

        private fun displayMap(map: List<String>) {
            println(map.joinToString("\n"))
            println()
        }

        private fun expect(actual: Long, expected: Long) {
            if (actual != expected) {
                throw AssertionError("Expected $expected, but got $actual")
            }
        }
    }
}
// end-of-code
