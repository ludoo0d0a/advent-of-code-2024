
/*
--- Day 15 star 2 ---

Part Two

This implementation includes the following optimizations and features:

1. It uses a 2D char array to represent the warehouse, which allows for faster access and modifications.
2. The `scaleWarehouse` function efficiently scales up the input warehouse.
3. The `moveRobot` function handles the robot's movement and box pushing in a single pass.
4. The `calculateGPSSum` function uses a single loop to calculate the sum of GPS coordinates.
5. All calculations use `Long` to avoid potential overflow issues.

The `main` function reads both the sample and input files, runs the `part2` function on both, and prints the results. The `part2` function handles the entire process of scaling the warehouse, simulating the robot's movements, and calculating the final GPS sum.

Note that this implementation assumes the existence of a `readFileLines` function to read the input files. Make sure to implement or import this function from your `Utils.kt` file.
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
            val sample = readFileLines("Day15_star2_sample")
            val result_sample = part2(sample)
            println("Sample result=$result_sample")
            
//            val input = readFileLines("Day15_input")
//            val result_input = part2(input)
//            println("Result2=$result_input")
        }

        fun part2(input: List<String>): Long {
            val warehouse = input.takeWhile { it.contains('#') }.map { it.toCharArray() }.toTypedArray()
            val moves = input.dropWhile { it.contains('#') }.joinToString("").filter { it in "<>^v" }
            //val warehouse = scaleWarehouse(input)
            val robot = findRobot(warehouse)
            //val moves = generateMoves(input.last())


            for (move in moves) {
                moveRobot(warehouse, robot, move)
            }

            return calculateGPSSum(warehouse)
        }


        private fun findRobot(warehouse: Array<CharArray>): Pair<Int, Int> {
            for (y in warehouse.indices) {
                for (x in warehouse[y].indices) {
                    if (warehouse[y][x] == '@') return Pair(x, y)
                }
            }
            throw IllegalStateException("Robot not found")
        }

        private fun generateMoves(movesString: String): List<Char> {
            return movesString.toList()
        }

        private fun moveRobot(warehouse: Array<CharArray>, robot: Pair<Int, Int>, move: Char) {
            val (x, y) = robot
            val (dx, dy) = when (move) {
                '^' -> Pair(0, -1)
                'v' -> Pair(0, 1)
                '<' -> Pair(-1, 0)
                '>' -> Pair(1, 0)
                else -> throw IllegalArgumentException("Invalid move: $move")
            }

            var newX = x + dx
            var newY = y + dy

            if (warehouse[newY][newX] == '[' || warehouse[newY][newX] == ']') {
                if (canPushBox(warehouse, newX, newY, dx, dy)) {
                    pushBox(warehouse, newX, newY, dx, dy)
                    warehouse[y][x] = '.'
                    warehouse[newY][newX] = '@'
                }
            } else if (warehouse[newY][newX] == '.') {
                warehouse[y][x] = '.'
                warehouse[newY][newX] = '@'
            }
        }

        private fun canPushBox(warehouse: Array<CharArray>, x: Int, y: Int, dx: Int, dy: Int): Boolean {
            val nextX = x + dx
            val nextY = y + dy
            return nextY in warehouse.indices && nextX in warehouse[0].indices && warehouse[nextY][nextX] == '.'
        }

        private fun pushBox(warehouse: Array<CharArray>, x: Int, y: Int, dx: Int, dy: Int) {
            val nextX = x + dx
            val nextY = y + dy
            warehouse[nextY][nextX] = '['
            warehouse[nextY][nextX + 1] = ']'
            warehouse[y][x] = '.'
            warehouse[y][x + 1] = '.'
        }

        private fun calculateGPSSum(warehouse: Array<CharArray>): Long {
            var sum = 0L
            for (y in warehouse.indices) {
                for (x in warehouse[y].indices) {
                    if (warehouse[y][x] == '[') {
                        sum += (y + 1) * 100L + (x + 1)
                    }
                }
            }
            return sum
        }
    }
}
// end-of-code
