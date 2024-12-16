class Day12 {
    data class Region(
        val type: Char,
        val area: Long,
        val sides: Long
    ) {
        val price: Long get() = area * sides
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
            println("Result2=$result")
        }

        private fun checkSample(filename: String, expectedResult: Long) {
            val result = calculateTotalPrice(filename)
            println("$filename result: $result")
            check(result == expectedResult) { "Expected $expectedResult but got $result for $filename" }
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
                                println("Region ${region.type}: Area=${region.area}, Sides=${region.sides}, Price=${region.price}")
                            }
                        }
                    }
                }
            }

            return regions.sumOf { it.price }
        }

        private fun traceSides(grid: Array<CharArray>, visited: Array<BooleanArray>, startRow: Int, startCol: Int): Region? {
            if (visited[startRow][startCol]) return null

            val type = grid[startRow][startCol]
            var area = 0L
            val sides = mutableSetOf<Triple<Int, Int, Int>>() // row, col, direction (0=top, 1=bottom, 2=left, 3=right)
            val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            val stack = ArrayDeque<Pair<Int, Int>>()
            stack.add(startRow to startCol)

            while (stack.isNotEmpty()) {
                val (row, col) = stack.removeLast()
                if (visited[row][col]) continue

                visited[row][col] = true
                area++

                for ((dx, dy) in directions) {
                    val newRow = row + dx
                    val newCol = col + dy

                    if (newRow !in grid.indices || newCol !in grid[0].indices ||
                        grid[newRow][newCol] != type) {
                        // Add side when reaching boundary or different type
                        when {
                            dx == -1 -> sides.add(Triple(row, col, 0)) // top
                            dx == 1 -> sides.add(Triple(row + 1, col, 1)) // bottom
                            dy == -1 -> sides.add(Triple(row, col, 2)) // left
                            dy == 1 -> sides.add(Triple(row, col + 1, 3)) // right
                        }
                        continue
                    }

                    if (!visited[newRow][newCol]) {
                        stack.add(newRow to newCol)
                    }
                }
            }

            val mergedSides = mergedSiblingSides(sides)

            return Region(type, area, mergedSides.size.toLong())
        }

        private fun mergedSiblingSides(sides: Set<Triple<Int, Int, Int>>): Set<Triple<Int, Int, Int>> {
            val mergedSides = mutableSetOf<Triple<Int, Int, Int>>()
            val sidesByDirection = sides.groupBy { it.third }

            for ((direction, directionSides) in sidesByDirection) {
                val sortedSides = directionSides.sortedWith(compareBy({ it.first }, { it.second }))
                var i = 0
                while (i < sortedSides.size) {
                    val current = sortedSides[i]
                    if (i + 1 < sortedSides.size) {
                        val next = sortedSides[i + 1]
                        // Check if sides are adjacent based on direction
                        if (direction <= 1 && current.first  == next.first && current.second +1== next.second ||  // vertical (top/bottom)
                            direction >= 2 && current.first +1== next.first && current.second  == next.second) {  // horizontal (left/right)
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
