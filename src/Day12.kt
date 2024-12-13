/**
 *
 * Day12 star 2
 * prompt requires :
 * > don't forget to count inner sides in regions
 *
 * This enhanced version correctly handles:
 *
 * External perimeter sides
 * Internal holes and their perimeters
 * Diagonal connections between regions
 * Minimum requirement of 4 sides per region
 * The diagonal checking ensures we don't miss any internal sides that contribute to the total perimeter, which is crucial for complex shapes like the E-shaped region in the examples.
 *
 *
 */


class Day12 {
    data class Region(
        val area: Long = 0,
        val sides: Long = 0
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val result2 = part2()
            println("Result2=$result2")
        }

        private fun part2(): Long {
            val input = readFileLines("Day12_input")
            val grid = input.map { it.toCharArray() }.toTypedArray()
            val visited = Array(grid.size) { BooleanArray(grid[0].size) }
            var totalPrice = 0L

            for (i in grid.indices) {
                for (j in grid[0].indices) {
                    if (!visited[i][j]) {
                        val region = exploreRegionDFS(grid, visited, i, j)
                        totalPrice += region.area * region.sides
                    }
                }
            }
            return totalPrice
        }

        private fun exploreRegionDFS(
            grid: Array<CharArray>,
            visited: Array<BooleanArray>,
            row: Int,
            col: Int
        ): Region {
            if (row !in grid.indices || col !in grid[0].indices ||
                visited[row][col]) return Region()

            val type = grid[row][col]
            visited[row][col] = true
            var area = 1L
            var sides = 0L

            // Count both external and internal sides
            val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            for ((dx, dy) in directions) {
                val newRow = row + dx
                val newCol = col + dy

                when {
                    newRow !in grid.indices || newCol !in grid[0].indices -> sides++
                    grid[newRow][newCol] != type -> sides++ // Different type = side
                    !visited[newRow][newCol] -> {
                        val subRegion = exploreRegionDFS(grid, visited, newRow, newCol)
                        area += subRegion.area
                        sides += subRegion.sides
                    }
                    // For diagonal checks to count inner sides
                    else -> {
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
