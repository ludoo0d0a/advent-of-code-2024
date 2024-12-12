
/*
Day 12: Garden Groups


*/
// kotlin:src/Day12.kt
import java.util.*

class Day12 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val input = readFileContent("Day12_input")
            val result = calculateTotalFencePrice(input)
            println("Result=$result")
        }

        private fun calculateTotalFencePrice(input: String): Long {
            val grid = input.trim().split("\n").map { it.toCharArray() }
            val rows = grid.size
            val cols = grid[0].size
            val visited = Array(rows) { BooleanArray(cols) }
            var totalPrice = 0L

            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    if (!visited[i][j]) {
                        val (area, perimeter) = bfs(grid, i, j, visited)
                        totalPrice += area.toLong() * perimeter.toLong()
                    }
                }
            }

            return totalPrice
        }

        private fun bfs(grid: List<CharArray>, startI: Int, startJ: Int, visited: Array<BooleanArray>): Pair<Int, Int> {
            val queue: Queue<Pair<Int, Int>> = LinkedList()
            queue.offer(Pair(startI, startJ))
            visited[startI][startJ] = true
            val plantType = grid[startI][startJ]
            var area = 0
            var perimeter = 0

            val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

            while (queue.isNotEmpty()) {
                val (i, j) = queue.poll()
                area++

                for ((di, dj) in directions) {
                    val ni = i + di
                    val nj = j + dj

                    if (ni in grid.indices && nj in grid[0].indices) {
                        if (grid[ni][nj] == plantType && !visited[ni][nj]) {
                            queue.offer(Pair(ni, nj))
                            visited[ni][nj] = true
                        } else if (grid[ni][nj] != plantType) {
                            perimeter++
                        }
                    } else {
                        perimeter++
                    }
                }
            }

            return Pair(area, perimeter)
        }
    }
}
// end-of-code
