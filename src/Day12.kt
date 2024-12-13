/*
using an input file name 'Day12_input' representing gardens.
A garden is an region defined by the same letter contiguous.
Region can be found inside another one, so there are internal and external sides.
Compute the numbers of  internal and external sides for each region.
compute the area of each region, equals the count of same letters.
compute the price of each area, by multiplying the region's area by its number of sides.
compute the total price for files Day12_star2_sample0 and assert result is 80.
compute the total price for files Day12_star2_sample1 and assert result is 236.
compute the total price for files Day12_star2_sample2 and assert result is 368.
compute the total price for files Day12_star2_sample3 and assert result is 1206.
compute the total price for file Day12_input
 */

class Day12 {
    data class Region(val area: Long, val sides: Long)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Verify all samples
            checkSample("Day12_star2_sample0", 80)
            checkSample("Day12_star2_sample1", 236)
            checkSample("Day12_star2_sample2", 368)
            checkSample("Day12_star2_sample3", 1206)

            // Calculate final result
            val result = calculateTotalPrice("Day12_input")
            println("Final result: $result")
        }

        private fun checkSample(filename: String, expected: Long) {
            val result = calculateTotalPrice(filename)
            check(result == expected) { "Sample $filename: got $result, expected $expected" }
            println("Sample $filename verified: $result")
        }

        private fun calculateTotalPrice(filename: String): Long {
            val grid = readFileLines(filename).map { it.toCharArray() }.toTypedArray()
            val visited = Array(grid.size) { BooleanArray(grid[0].size) }
            var totalPrice = 0L

            for (row in grid.indices) {
                for (col in grid[0].indices) {
                    if (!visited[row][col]) {
                        val region = exploreRegion(grid, visited, row, col)
                        totalPrice += region.area * region.sides
                    }
                }
            }
            return totalPrice
        }

        private fun exploreRegion(grid: Array<CharArray>, visited: Array<BooleanArray>, startRow: Int, startCol: Int): Region {
            val type = grid[startRow][startCol]
            var area = 0L
            var sides = 0L
            val queue = ArrayDeque<Pair<Int, Int>>()
            queue.add(Pair(startRow, startCol))

            val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

            while (queue.isNotEmpty()) {
                val (row, col) = queue.removeFirst()
                if (visited[row][col]) continue

                visited[row][col] = true
                area++

                for ((dx, dy) in directions) {
                    val newRow = row + dx
                    val newCol = col + dy

                    when {
                        // Count edge sides
                        newRow !in grid.indices || newCol !in grid[0].indices -> sides++

                        // Count sides between different types
                        grid[newRow][newCol] != type -> sides++

                        // Add unvisited same-type cells to queue
                        !visited[newRow][newCol] -> queue.add(Pair(newRow, newCol))
                    }

                    // Check diagonals for internal sides
                    if (newRow in grid.indices && newCol in grid[0].indices) {
                        val diagonals = listOf(
                            Pair(dx, 0), Pair(0, dy),
                            Pair(-dx, 0), Pair(0, -dy)
                        )
                        for ((diagX, diagY) in diagonals) {
                            val diagRow = row + diagX
                            val diagCol = col + diagY
                            if (diagRow in grid.indices && diagCol in grid[0].indices &&
                                grid[diagRow][diagCol] != type) {
                                sides++
                            }
                        }
                    }
                }
            }

            return Region(area, maxOf(sides, 4))
        }
    }
}
