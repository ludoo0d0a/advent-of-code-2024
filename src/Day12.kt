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
//            checkSample("Day12_star2_sample0", 80)
//            checkSample("Day12_star2_sample1", 236)
            checkSample("Day12_star2_sample2", 368)
//            checkSample("Day12_star2_sample3", 1206)

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

            val totalPriceOld = regions.sumOf { it.price }
            println("totalPriceOld: $totalPriceOld")

            return totalPriceOld
        }

        private fun traceSides(
            grid: Array<CharArray>,
            visited: Array<BooleanArray>,
            startRow: Int,
            startCol: Int
        ): Region? {
            if (visited[startRow][startCol]) return null

            val type = grid[startRow][startCol]
            var area = 0L
            val sides = mutableSetOf<Side>()
            val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            val queue = ArrayDeque<Pair<Int, Int>>()
            queue.add(startRow to startCol)

            // Track region bounds for collision detection
            var minRow = startRow
            var maxRow = startRow
            var minCol = startCol
            var maxCol = startCol

            // First pass: collect region bounds and sides
            while (queue.isNotEmpty()) {
                val (row, col) = queue.removeFirst()
                if (visited[row][col]) continue

                visited[row][col] = true
                area++

                // Update region bounds
                minRow = minOf(minRow, row)
                maxRow = maxOf(maxRow, row)
                minCol = minOf(minCol, col)
                maxCol = maxOf(maxCol, col)

                for ((dx, dy) in directions) {
                    val newRow = row + dx
                    val newCol = col + dy

                    if (newRow !in grid.indices || newCol !in grid[0].indices || grid[newRow][newCol] != type) {
                        when {
                            dx == -1 -> sides.add(Side(row, col, 0, type))
                            dx == 1 -> sides.add(Side(row + 1, col, 1, type))
                            dy == -1 -> sides.add(Side(row, col, 2, type))
                            dy == 1 -> sides.add(Side(row, col + 1, 3, type))
                        }
                    }

                    if (newRow in grid.indices && newCol in grid[0].indices &&
                        grid[newRow][newCol] == type && !visited[newRow][newCol]
                    ) {
                        queue.add(newRow to newCol)
                    }
                }
            }

            // Filter out internal sides
//            val externalSides = getOnlyExternalSides(sides, 0, maxRow, 0, maxCol)
            val externalSides = (sides)

            val mergedSides = mergedSiblingSides(externalSides)
            println(" -- ${sides.size} sides >  ${externalSides.size} externalSides > ${mergedSides.size} mergedSides")

            return Region(type, area, mergedSides.size.toLong(), mergedSides.toList())
        }

        private fun getOnlyExternalSides(sides: Set<Side>, minRow: Int, maxRow: Int, minCol: Int, maxCol: Int): Set<Side> {
            val externalSides = mutableSetOf<Side>()
            val sideGroups = mutableListOf<MutableList<Side>>()
            val remainingSides = sides.toMutableSet()

            printSides(sides)

            // Form closed shapes by linking connected sides
            while (remainingSides.isNotEmpty()) {
                val currentGroup = mutableListOf<Side>()
                val startSide = remainingSides.first()
                var currentSide = startSide

                do {
                    currentGroup.add(currentSide)
                    remainingSides.remove(currentSide)

                    // Find next connected side
                    currentSide = remainingSides.firstOrNull { nextSide ->
                        when (currentSide.dir) {
                            0 -> nextSide.row == currentSide.row && nextSide.col == currentSide.col
                            1 -> nextSide.row == currentSide.row + 1 && nextSide.col == currentSide.col
                            2 -> nextSide.col == currentSide.col && nextSide.row == currentSide.row
                            3 -> nextSide.col == currentSide.col + 1 && nextSide.row == currentSide.row
                            else -> false
                        }
                    } ?: break

                } while (currentSide != startSide && remainingSides.isNotEmpty())

                if (currentGroup.isNotEmpty()) {
                    sideGroups.add(currentGroup)
                }
            }

            // Filter groups where topmost sides are on the region boundary
            for (group in sideGroups) {
                val topSides = group.filter { it.dir == 0 }
                val isExternalShape = topSides.any { it.row == minRow }

                if (isExternalShape) {
                    externalSides.addAll(group)
                }
            }

            return externalSides
        }

        private fun printSides(sides: Set<Side>) {
            val maxRow = sides.maxOf { it.row } + 1
            val maxCol = sides.maxOf { it.col } + 1
            val grid = Array(maxRow) { Array(maxCol) { " " } }

            // Fill grid with direction indicators
            sides.forEach { side ->
                val dirChar = when (side.dir) {
                    0 -> "^"
                    1 -> "v"
                    2 -> "<"
                    3 -> ">"
                    else -> "?"
                }
                val color = when (side.type) {
                    '#' -> "\u001B[31m" // Red
                    '.' -> "\u001B[32m" // Green
                    else -> "\u001B[33m" // Yellow
                }
                grid[side.row][side.col] = "$color$dirChar\u001B[0m"
            }

            // Print the grid
            grid.forEach { row ->
                println(row.joinToString(""))
            }
            println("")
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
                        if (direction <= 1 && current.row == next.row && current.col + 1 == next.col ||  // vertical (top/bottom)
                            direction >= 2 && current.row + 1 == next.row && current.col == next.col
                        ) {  // horizontal (left/right)
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
