
/*
--- Day 12 star 2 ---

Part Two

This solution uses a breadth-first search (BFS) approach to efficiently calculate the area and number of sides for each region. Here's a breakdown of the implementation:

1. The `part2` function reads the input file and initializes the grid and visited array.
2. It iterates through each cell in the grid, and if not visited, calls `calculateRegionProperties` to compute the area and number of sides for the region starting at that cell.
3. The `calculateRegionProperties` function uses BFS to explore the region, counting the area and sides simultaneously.
4. We use a queue to keep track of cells to visit, and mark cells as visited to avoid revisiting them.
5. The sides are counted by checking neighboring cells that are either of a different type or outside the grid.
6. We ensure that each region has at least 4 sides, as per the problem description.
7. The total price is calculated by multiplying the area and number of sides for each region and summing them up.

This implementation is optimized for efficiency:
- It uses BFS to explore regions, which is generally faster than depth-first search for this type of problem.
- It calculates area and sides in a single pass, avoiding the need for separate traversals.
- It uses a visited array to prevent revisiting cells, reducing unnecessary computations.
- It uses `Long` for calculations to avoid integer overflow.

The main function calls `part2` and prints the result in the required format. Make sure to have the `readFileLines` function available in your `Utils.kt` file to read the input file.
*/
import java.util.*

// kotlin:Day12.kt
import kotlin.math.max

class Day12 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val result2 = part2()
            println("Result2=$result2")
        }

        private fun part2(): Long {
            val input = readFileLines("Day12_input")
            val grid = input.map { it.toCharArray() }.toTypedArray()
            val rows = grid.size
            val cols = grid[0].size

            val visited = Array(rows) { BooleanArray(cols) }
            var totalPrice = 0L

            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    if (!visited[i][j]) {
                        val (area, sides) = calculateRegionProperties(grid, visited, i, j)
                        totalPrice += area * sides
                    }
                }
            }

            return totalPrice
        }

        private fun calculateRegionProperties(grid: Array<CharArray>, visited: Array<BooleanArray>, startRow: Int, startCol: Int): Pair<Long, Long> {
            val rows = grid.size
            val cols = grid[0].size
            val type = grid[startRow][startCol]
            var area = 0L
            var sides = 0L

            val queue = ArrayDeque<Pair<Int, Int>>()
            queue.add(Pair(startRow, startCol))
            visited[startRow][startCol] = true

            val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

            while (queue.isNotEmpty()) {
                val (row, col) = queue.removeFirst()
                area++

                for ((dx, dy) in directions) {
                    val newRow = row + dx
                    val newCol = col + dy

                    if (newRow in 0 until rows && newCol in 0 until cols) {
                        if (grid[newRow][newCol] == type) {
                            if (!visited[newRow][newCol]) {
                                queue.add(Pair(newRow, newCol))
                                visited[newRow][newCol] = true
                            }
                        } else {
                            sides++
                        }
                    } else {
                        sides++
                    }
                }
            }

            return Pair(area, max(sides, 4))
        }
    }
}
// end-of-code
