/*
Day 10: Hoof It

You need to find the shortest path from (0,0) to (70,70) while avoiding corrupted memory locations.
Each byte falls into memory space at given coordinates, making that location corrupted.
You can move up, down, left, right but cannot enter corrupted locations or leave boundaries.
*/

class Day18 {
    companion object {
        private const val DEBUG = false
        private const val EXPECTED_SAMPLE = 22L
        private val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1) // up, down, left, right

        @JvmStatic
        fun main(args: Array<String>) {
            val sample1 = readFileLines("Day18_star1_sample")
            val result_sample1 = part1(sample1)
            //e
            println("sample result=$result_sample1")

            val input = readFileLines("Day18_input")
            val result_input = part1(input)
            println("Result=$result_input")
        }

        private fun part1(input: List<String>): Long {
            // Parse coordinates
            val bytes = input.map { line ->
                val (x, y) = line.split(",").map { it.toInt() }
                x to y
            }

            // Determine grid size
            val maxX = bytes.maxOf { it.first }
            val maxY = bytes.maxOf { it.second }
            val grid = Array(maxY + 1) { CharArray(maxX + 1) { '.' } }

            // Mark corrupted locations for first 1024 bytes
            val bytesToSimulate = minOf(1024, bytes.size)
            for (i in 0 until bytesToSimulate) {
                val (x, y) = bytes[i]
                grid[y][x] = '#'
                if (DEBUG) {
                    printGrid(grid)
                    println("")
                }
            }

            // Find shortest path using BFS
            return findShortestPath(grid)
        }

        private fun findShortestPath(grid: Array<CharArray>): Long {
            val rows = grid.size
            val cols = grid[0].size
            val visited = Array(rows) { BooleanArray(cols) }
            val queue = ArrayDeque<Triple<Int, Int, Long>>()
            queue.add(Triple(0, 0, 0)) // row, col, steps
            visited[0][0] = true

            while (queue.isNotEmpty()) {
                val (row, col, steps) = queue.removeFirst()

                // Check if reached destination
                if (row == rows - 1 && col == cols - 1) {
                    return steps
                }

                // Try all directions
                for ((dr, dc) in directions) {
                    val newRow = row + dr
                    val newCol = col + dc

                    if (isValid(newRow, newCol, grid) && !visited[newRow][newCol] && grid[newRow][newCol] != '#') {
                        queue.add(Triple(newRow, newCol, steps + 1))
                        visited[newRow][newCol] = true
                    }
                }
            }

            return -1L // No path found
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
