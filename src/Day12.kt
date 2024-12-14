/*
Using an input file name 'Day12_input' representing gardens.
A garden is an region defined by the limit around the same contiguous letter.
A region can be found inside another one.
The region borders draw a polygon, count the numbers of lines of the polygon.
Don't forget to count inside lines.
Compute the area of each region which is the count of letters in this region.
Compute the price of each area, by multiplying the region's area by the number of lines of the related polygon.
Compute the total price for files Day12_star2_sample0 and assert result is 80.
Compute the total price for files Day12_star2_sample1 and assert result is 236.
Compute the total price for files Day12_star2_sample2 and assert result is 368.
Compute the total price for files Day12_star2_sample3 and assert result is 1206.
Compute the total price for file Day12_input
Log information for each region: lines count, area, price
show the whole code for the kotlin class.
*/
class Day12 {
    data class Region(
        val type: Char,
        val area: Long,
        val lines: Long
    ) {
        val price: Long get() = area * lines
    }

    companion object {
        private const val DEBUG = true

        @JvmStatic
        fun main(args: Array<String>) {
            checkSample("Day12_star2_sample0", 80)
            checkSample("Day12_star2_sample1", 236)
            checkSample("Day12_star2_sample2", 368)
            checkSample("Day12_star2_sample3", 1206)

            val result = calculateTotalPrice("Day12_input")
            println("Final result: $result")
        }

        private fun checkSample(filename: String, expectedResult: Long) {
            val result = calculateTotalPrice(filename)
            println("$filename result: $result")
            check(result == expectedResult) { "Expected $expectedResult but got $result for $filename" }
        }

        private fun calculateTotalPrice(filename: String): Long {
            val grid = readFileLines(filename).map { it.toCharArray() }.toTypedArray()
            val visited = Array(grid.size) { BooleanArray(grid[0].size) }
            val regions = mutableListOf<Region>()

            for (i in grid.indices) {
                for (j in grid[0].indices) {
                    if (!visited[i][j]) {
                        val region = exploreRegion(grid, visited, i, j)
                        if (region != null) {
                            regions.add(region)
                            if (DEBUG) {
                                println("Region ${region.type}: Area=${region.area}, Lines=${region.lines}, Price=${region.price}")
                            }
                        }
                    }
                }
            }

            return regions.sumOf { it.price }
        }

        private fun exploreRegion(grid: Array<CharArray>, visited: Array<BooleanArray>, startRow: Int, startCol: Int): Region? {
            if (visited[startRow][startCol]) return null

            val type = grid[startRow][startCol]
            var area = 0L
            var lines = 0L
            val toExplore = ArrayDeque<Pair<Int, Int>>()
            toExplore.add(startRow to startCol)

            while (toExplore.isNotEmpty()) {
                val (row, col) = toExplore.removeFirst()
                if (visited[row][col]) continue

                visited[row][col] = true
                area++

                val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
                for ((dx, dy) in directions) {
                    val newRow = row + dx
                    val newCol = col + dy

                    when {
                        newRow !in grid.indices || newCol !in grid[0].indices -> lines++
                        grid[newRow][newCol] != type -> lines++
                        !visited[newRow][newCol] -> toExplore.add(newRow to newCol)
                    }
                }
            }

            return Region(type, area, maxOf(lines / 2, 4))
        }
    }
}
