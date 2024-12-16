class Day12 {
    data class Region(
        val type: Char,
        val area: Long,
        val sidesCount: Long,
        val sides: List<Side>
    ) {
        val price: Long get() = area * sidesCount
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

//            val result = calculateTotalPrice("Day12_input")
//            println("Result2=$result")
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
            val visited = Array(grid.size) { BooleanArray(grid[0].size) }
            val regions = mutableListOf<Region>()

            for (i in grid.indices) {
                for (j in grid[0].indices) {
                    if (!visited[i][j]) {
                        val region = traceSides(grid, visited, i, j)
                        if (region != null) {
                            regions.add(region)
                            if (DEBUG) {
                                println("Region ${region.type}: Area=${region.area}, Sides=${region.sidesCount}, Price=${region.price}")
                            }
                        }
                    }
                }
            }

//            //filter region by distinct sides
//            val distinctSides = getDistinctSides(regions)
//            // Calculate the total price
//            val totalPrice = distinctSides.sumOf { it.row * it.col }.toLong()
//            return totalPrice

            val totalPriceOld =  regions.sumOf { it.price }
            println("totalPriceOld: $totalPriceOld")

            return totalPriceOld
        }

//        private fun getDistinctSides(regions: List<Region>): Set<Side> {
//            val sidesSet = mutableSetOf<Side>()
//            for (region in regions) {
//                for (side in region.sides) {
//                    sidesSet.add(side)
//                }
//            }
//            return sidesSet
//        }


        private fun traceSides(grid: Array<CharArray>, visited: Array<BooleanArray>, startRow: Int, startCol: Int): Region? {
            if (visited[startRow][startCol]) return null

            val type = grid[startRow][startCol]
            var area = 0L
            val sides = mutableSetOf<Side>()
            val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            val queue = ArrayDeque<Pair<Int, Int>>()
            queue.add(startRow to startCol)

            while (queue.isNotEmpty()) {
                val (row, col) = queue.removeFirst()
                if (visited[row][col]) continue

                visited[row][col] = true
                area++

                // Check if this cell is on the boundary
                var isOnBoundary = false
                for ((dx, dy) in directions) {
                    val newRow = row + dx
                    val newCol = col + dy

                    if (newRow !in grid.indices || newCol !in grid[0].indices ||
                        grid[newRow][newCol] != type) {
                        isOnBoundary = true
                        // Add side only for boundary cells
                        when {
                            dx == -1 -> sides.add(Side(row, col, 0, type))
                            dx == 1 -> sides.add(Side(row + 1, col, 1, type))
                            dy == -1 -> sides.add(Side(row, col, 2, type))
                            dy == 1 -> sides.add(Side(row, col + 1, 3, type))
                        }
                    } else if (!visited[newRow][newCol]) {
                        queue.add(newRow to newCol)
                    }
                }
            }

            val mergedSides = mergedSiblingSides(sides)
            return Region(type, area, mergedSides.size.toLong(), mergedSides.toList())
        }


        private fun mergedSiblingSides(sides: Set<Side>): Set<Side> {
            val mergedSides = mutableSetOf<Side>()
            val sidesByDirection = sides.groupBy { it.dir }

            for ((direction, directionSides) in sidesByDirection) {
                val sortedSides = directionSides.sortedWith(compareBy({ it.row }, { it.col }))
                var i = 0
                while (i < sortedSides.size) {
                    val current = sortedSides[i]
                    if (i + 1 < sortedSides.size) {
                        val next = sortedSides[i + 1]
                        // Check if sides are adjacent based on direction
                        if (direction <= 1 && current.row  == next.row && current.col +1== next.col ||  // vertical (top/bottom)
                            direction >= 2 && current.row +1== next.row && current.col  == next.col) {  // horizontal (left/right)
                            i += 1
                            continue
                        }
                    }
                    mergedSides.add(current)
                    i++
                }
            }
            return mergedSides
        }

    }
}
