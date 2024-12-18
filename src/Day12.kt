class Day12 {
    data class Region(
        val type: Char,
        val area: Long,
//        val sidesCount: Long,
//        val sides: List<Side>,
        val countCorners: Int
    ) {
        val price: Long get() = area * countCorners
    }

    data class Side(
        val row: Int,
        val col: Int,
        val dir: Int,
        val type: Char
    )

    companion object {
        private const val DEBUG = true

        @JvmStatic
        fun main(args: Array<String>) {
            checkSample("Day12_star2_sample0", 80)
            checkSample("Day12_star2_sample1", 236)
            checkSample("Day12_star2_sample2", 368)
            checkSample("Day12_star2_sample3", 1206)

            val result = calculateTotalPrice("Day12_input")
            println("Result2=$result")
        }

        private fun checkSample(filename: String, expectedResult: Long) {
            val result = calculateTotalPrice(filename)
            println("$filename result: $result")
            //check(result == expectedResult) { "Expected $expectedResult but got $result for $filename" }
            assert(result == expectedResult) { "Expected $expectedResult but got $result for $filename" }
            println("----------------------------------------")
        }

        private fun calculateTotalPrice(filename: String): Long {
            val grid = readFileLines(filename).map { it.toCharArray() }.toTypedArray()
            val regions = detectRegions(grid)
            val totalPriceOld = regions.sumOf { it.price }
            println("totalPriceOld: $totalPriceOld")
            return totalPriceOld
        }

        private fun detectRegions(grid: Array<CharArray>): List<Region> {
            val visited = Array(grid.size) { BooleanArray(grid[0].size) }
            val regions = mutableListOf<Region>()

            for (i in grid.indices) {
                for (j in grid[0].indices) {
                    if (!visited[i][j] && grid[i][j] != '.') {
                        val cells = mutableListOf<Pair<Int, Int>>()
                        //val sides = mutableListOf<Side>()
                        val type = grid[i][j]

                        // DFS to find connected cells of same type
                        fun dfs(row: Int, col: Int) {
                            if (row !in grid.indices || col !in grid[0].indices ||
                                visited[row][col] || grid[row][col] != type
                            ) {
                                return
                            }

                            visited[row][col] = true
                            cells.add(row to col)

                            // Check all 4 directions
                            val directions = listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)
                            for ((dirIndex, dir) in directions.withIndex()) {
                                val newRow = row + dir.first
                                val newCol = col + dir.second

//                                // Add side if adjacent cell is different
//                                if (newRow !in grid.indices || newCol !in grid[0].indices ||
//                                    grid[newRow][newCol] != type
//                                ) {
//                                    //sides.add(Side(row, col, dirIndex, type))
//                                }

                                dfs(newRow, newCol)
                            }
                        }

                        dfs(i, j)

                        if (cells.isNotEmpty()) {
                            val region = Region(
                                type = type,
                                area = cells.size.toLong(),
//                                sidesCount = sides.size.toLong(),
//                                sides = sides,
                                countCorners = countCorners(cells)
                            )
                            if (DEBUG) {
                                println("Region ${region.type}: area=${region.area} corners=${region.countCorners}, Price=${region.price}")
                            }
                            regions.add(region)
                        }
                    }
                }
            }

            return regions
        }


        private fun countCorners(cells: List<Pair<Int, Int>>): Int {
            val cellSet = cells.toSet()
            var corners = 0

            // Directions: UP, RIGHT, DOWN, LEFT
            val directions = listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)

            for ((row, col) in cells) {
                corners += directions.count { (dx, dy) ->
                    val n1 = (row + dx) to (col + dy) !in cellSet
                    val n2 = (row + dx + dy) to (col + dy - dx) !in cellSet
                    val n3 = (row + dy) to (col - dx) !in cellSet

                    (n1 && n3) || (n1.not() && n2 && n3.not())
                }
            }

            return corners
        }
    }
}
