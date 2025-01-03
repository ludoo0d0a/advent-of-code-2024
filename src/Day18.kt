/*
Day 10: Hoof It - Part 2

Find the coordinates of the first byte that will prevent the exit from being reachable.
Need to simulate bytes falling and check path existence after each byte.
Return coordinates as "x,y" when no path exists.
*/

class Day18 {
    companion object {
        private const val DEBUG = false
        private const val EXPECTED_SAMPLE1 = 22L
        private const val EXPECTED_SAMPLE2 = "6,1"
        private val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        @JvmStatic
        fun main(args: Array<String>) {
//            val sample2 = readFileLines("Day18_star2_sample")
//            val result_sample2 = part2(sample2)
//            //expect(result_sample2, EXPECTED_SAMPLE2)
//            println("sample2 result=$result_sample2")

            val input = readFileLines("Day18_input")
            val result2_input = part2(input)
            println("Result2=$result2_input")
        }

        private fun part2(input: List<String>): String {
            val bytes = input.map { line ->
                val (x, y) = line.split(",").map { it.toInt() }
                x to y
            }

            val maxX = bytes.maxOf { it.first }
            val maxY = bytes.maxOf { it.second }
            val grid = Array(maxY + 1) { CharArray(maxX + 1) { '.' } }

            // Try each byte until path is blocked
            for (i in bytes.indices) {
                val (x, y) = bytes[i]
                grid[y][x] = '#'

                if (DEBUG) {
                    println("\nAfter byte $i at ($x,$y):")
                    printGrid(grid)
                }

                // Check if path still exists
                if (!hasPathToExit(grid)) {
                    return "$x,$y"
                }
            }

            return "No blocking byte found"
        }

        private fun hasPathToExit(grid: Array<CharArray>): Boolean {
            val rows = grid.size
            val cols = grid[0].size
            val visited = Array(rows) { BooleanArray(cols) }
            val queue = ArrayDeque<Pair<Int, Int>>()

            queue.add(0 to 0)
            visited[0][0] = true

            while (queue.isNotEmpty()) {
                val (row, col) = queue.removeFirst()

                if (row == rows - 1 && col == cols - 1) {
                    return true
                }

                for ((dr, dc) in directions) {
                    val newRow = row + dr
                    val newCol = col + dc

                    if (isValid(newRow, newCol, grid) &&
                        !visited[newRow][newCol] &&
                        grid[newRow][newCol] != '#') {
                        queue.add(newRow to newCol)
                        visited[newRow][newCol] = true
                    }
                }
            }

            return false
        }

        private fun isValid(row: Int, col: Int, grid: Array<CharArray>): Boolean {
            return row in grid.indices && col in grid[0].indices
        }

        private fun printGrid(grid: Array<CharArray>) {
            for (row in grid) {
                for (c in row) {
                    val color = when (c) {
                        '#' -> "\u001B[31m" // Red for corrupted
                        'O' -> "\u001B[32m" // Green for path
                        else -> "\u001B[37m" // White for safe
                    }
                    print("$color$c\u001B[0m")
                }
                println("")
            }
        }
    }
}
