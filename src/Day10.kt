import java.io.File

/*
Day 10: Hoof It
*/
class Day10 {

    val DEBUG=true;

    fun main() {
        val input = File("src/Day10_input.txt").readLines()
        val result = sumTrailheadScores(input)
        println("Sum of trailhead scores: $result")
    }

    fun sumTrailheadScores(input: List<String>): Int {
        val grid = input.map { it.map { c -> c.digitToInt() } }
        val rows = grid.size
        val cols = grid[0].size

        fun isValid(r: Int, c: Int) = r in 0 until rows && c in 0 until cols

        val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        fun bfs(startR: Int, startC: Int): Int {
            val visited = Array(rows) { BooleanArray(cols) }
            val queue = ArrayDeque<Triple<Int, Int, Int>>()
            queue.add(Triple(startR, startC, 0))
            visited[startR][startC] = true
            var score = 0

            while (queue.isNotEmpty()) {
                val (r, c, height) = queue.removeFirst()

                if (grid[r][c] == 9) {
                    score++
                }

                for ((dr, dc) in directions) {
                    val newR = r + dr
                    val newC = c + dc
                    if (isValid(newR, newC) && !visited[newR][newC] && grid[newR][newC] == height + 1) {
                        queue.add(Triple(newR, newC, height + 1))
                        visited[newR][newC] = true
                    }
                }
            }

            return score
        }

        var totalScore = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (grid[r][c] == 0) {
                    totalScore += bfs(r, c)
                }
            }
        }

        return totalScore
    }
}

fun main() {
    Day10().main()
}
