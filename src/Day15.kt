
/*
--- Day 15 star 2 ---

Problem day 15 star 2

This implementation includes both `part1` and `part2` methods, as well as helper functions to parse the input, scale the map, simulate the robot's movements, and calculate the GPS sum. The main method is set up as requested, computing results for both the sample and input data.

Key optimizations and considerations:
1. Using `MutableList` for the map to allow in-place updates.
2. Efficient box pushing algorithm that handles multiple boxes at once.
3. Using `Long` for GPS coordinate calculations to avoid overflow.
4. Displaying the map after each move for debugging purposes.

Note that this solution assumes the existence of a `readFileLines()` function from `Utils.kt` to read the input files. Make sure this function is available in your project.

The algorithm should be reasonably efficient, but depending on the size and complexity of the input, it may still take some time to compute the result. Further optimizations could be made if needed, such as using a more efficient data structure for the map or implementing a smarter box-pushing algorithm.
*/
import java.util.*

// kotlin:Day15.kt
import java.util.*

class Day15 {
    companion object {
        // Prompt: Optimize the algorithm to be efficient and fast so that solution can be found in a reasonable amount of time.
        // Use indexes as soon as you can to avoid re-calculating the same value and lost time in long computation.
        // Use Long instead of Int to avoid overflow.

        @JvmStatic
        fun main(args: Array<String>) {
            val sample2 = readFileLines("Day15_star2_sample")
//            val result_sample2 = part2(sample2)
//            println("sample2 result=$result_sample2. Expected=9021")

            val sample3 = readFileLines("Day15_star1_sample2")
            val result_sample3 = part2(sample3)
            println("sample3 result=$result_sample3")
            
//            val input = readFileLines("Day15_input")
//            val result2_input = part2(input)
//            println("Result2=$result2_input")
        }

        fun part1(input: List<String>): Long {
            val (map, moves) = parseInput(input)
            return simulateRobot(map, moves)
        }

        fun part2(input: List<String>): Long {
            val (originalMap, moves) = parseInput(input)
            val scaledMap = scaleMap(originalMap)
            printMap(scaledMap)
            return simulateRobot(scaledMap, moves)
        }

        private fun parseInput(input: List<String>): Pair<List<String>, String> {
            val mapEndIndex = input.indexOfFirst { it.isEmpty() }
            val map = input.subList(0, mapEndIndex)
            //val moves = input.last()
            val moves = input.subList(mapEndIndex+1, input.size).joinToString("")
            return Pair(map, moves)
        }

        private fun scaleMap(originalMap: List<String>): List<String> {
            return originalMap.map { row ->
                row.map { char ->
                    when (char) {
                        '#' -> "##"
                        'O' -> "[]"
                        '.' -> ".."
                        '@' -> "@."
                        else -> throw IllegalArgumentException("Invalid character: $char")
                    }
                }.joinToString("")
            }
        }

        private fun simulateRobot(map: List<String>, moves: String): Long {
            var currentMap = map.toMutableList()
            var robotPosition = findRobotPosition(currentMap)
            var i=0

            for (move in moves) {
                i++
                val newPosition = when (move) {
                    '>' -> Pair(robotPosition.first, robotPosition.second + 1)
                    '<' -> Pair(robotPosition.first, robotPosition.second - 1)
                    '^' -> Pair(robotPosition.first - 1, robotPosition.second)
                    'v' -> Pair(robotPosition.first + 1, robotPosition.second)
                    else -> throw IllegalArgumentException("Invalid move: $move")
                }

                if (canMove(currentMap, newPosition)) {
                    currentMap = updateMap(currentMap, robotPosition, newPosition)
                    robotPosition = newPosition
                }

                println("$i - After move $move:")
                printMap(currentMap)
            }

            return calculateGPSSum(currentMap)
        }

        private fun printMap(currentMap: List<String>) {
            currentMap.forEach { println(it) }
            println()
        }

        private fun findRobotPosition(map: List<String>): Pair<Int, Int> {
            for (i in map.indices) {
                for (j in map[i].indices) {
                    if (map[i][j] == '@') return Pair(i, j)
                }
            }
            throw IllegalStateException("Robot not found in the map")
        }

        private fun canMove(map: List<String>, position: Pair<Int, Int>): Boolean {
            val (row, col) = position
            if (row < 0 || row >= map.size || col < 0 || col >= map[0].length) return false
            return map[row][col] != '#'
        }

        private fun updateMap(map: MutableList<String>, oldPos: Pair<Int, Int>, newPos: Pair<Int, Int>): MutableList<String> {
            val (oldRow, oldCol) = oldPos
            val (newRow, newCol) = newPos

            val updatedMap = map.toMutableList()

            // Move robot
            updatedMap[oldRow] = updatedMap[oldRow].replaceRange(oldCol, oldCol + 1, ".")
            updatedMap[newRow] = updatedMap[newRow].replaceRange(newCol, newCol + 1, "@")

            // Push boxes
            if (updatedMap[newRow][newCol] == '[') {
                val pushDirection = Pair(newRow - oldRow, newCol - oldCol)
                pushBoxes(updatedMap, newPos, pushDirection)
            }

            return updatedMap
        }

        private fun pushBoxes(map: MutableList<String>, startPos: Pair<Int, Int>, direction: Pair<Int, Int>) {
            var currentPos = startPos
            while (true) {
                val nextPos = Pair(currentPos.first + direction.first, currentPos.second + direction.second)
                if (!canMove(map, nextPos)) break

                if (map[nextPos.first][nextPos.second] == '[') {
                    currentPos = nextPos
                } else {
                    map[nextPos.first] = map[nextPos.first].replaceRange(nextPos.second, nextPos.second + 1, "[")
                    map[currentPos.first] = map[currentPos.first].replaceRange(currentPos.second, currentPos.second + 1, "]")
                    break
                }
            }
        }

        private fun calculateGPSSum(map: List<String>): Long {
            var sum = 0L
            for (i in map.indices) {
                for (j in map[i].indices step 2) {
                    if (map[i][j] == '[') {
                        sum += (100L * (i + 1)) + ((j / 2) + 1)
                    }
                }
            }
            return sum
        }
    }
}
// end-of-code
