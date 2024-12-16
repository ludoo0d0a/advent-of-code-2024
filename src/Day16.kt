import java.util.*

/**
 * prompt :
 *
 * compute it again to reduce computations and minimize the number of turns
 * Steps to solve the problem:
 * Initialize a deque dq to store the current cell with its current direction.
 * Create a 2D vector dist[][] to store the minimum number of turns to reach a cell.
 * Push the starting cell (0, 0) and its initial direction (0) to the deque.
 * Pop a cell from the deque.
 * For each valid and unblocked neighbor of the popped cell, check if moving to that neighbor is optimal by checking the dist[][] vector.
 * If moving to the neighbor is optimal, update the minimum number of turns to reach that neighbor and push the neighbor to the front if the neighbor is in the same direction else push it to the back of queue.
 * Return the minimum number of turns to reach the destination cell.
 *
 * find all solutions with the same score.
 * show the whole code
 */


/**
 *
 * This implementation1:
 *
 * Uses a deque to prioritize straight movements over turns
 * Maintains a 3D distance array to track minimum turns for each cell and direction
 * Adds straight movements to the front of the deque
 * Adds turns to the back of the deque
 * Returns the first path that reaches the end, which will have minimal turns due to the deque ordering
 *
 * This implementation2 uses a deque-based approach to find the path with minimal turns from start to end, prioritizing straight movements over turns. The solution includes colorized path display and efficient path finding using a 3D distance array to track the minimum turns for each cell and direction combination.
 *
 * + optim3:
 *
 * The key improvements in this version:
 *
 * Collects all paths that have the same optimal score
 * Uses <= comparison for both turns and distance checks to find alternative paths
 * Maintains a list of optimal paths with the same score
 * Shows distinct steps used across all optimal paths
 * Displays each optimal solution with its path visualization
 */
class Day16 {
    companion object {
        private const val DEBUG = true
        private const val ANSI_RED = "\u001B[31m"
        private const val ANSI_GREEN = "\u001B[32m"
        private const val ANSI_YELLOW = "\u001B[33m"
        private const val ANSI_BLUE = "\u001B[34m"
        private const val ANSI_MAGENTA = "\u001B[35m"
        private const val ANSI_RESET = "\u001B[0m"

        data class Position(
            val row: Int,
            val col: Int,
            val direction: Int,
            val path: List<Pair<Int, Int>> = listOf(),
            val turns: Int = 0
        )

        data class ScoredPath(
            val path: List<Pair<Int, Int>>,
            val turns: Int,
            val score: Int
        )

        fun findOptimalPaths(map: List<CharArray>): List<ScoredPath> {
            val start = findStart(map)
            val end = findEnd(map)
            val directions = arrayOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1) // N,E,S,W
            val rows = map.size
            val cols = map[0].size

            val dist = Array(rows) { Array(cols) { Array(4) { Int.MAX_VALUE } } }
            val deque = ArrayDeque<Position>()
            val optimalPaths = mutableListOf<ScoredPath>()
            var bestScore = Int.MAX_VALUE

            directions.indices.forEach { dir ->
                deque.addFirst(Position(start.first, start.second, dir, listOf(start), 0))
                dist[start.first][start.second][dir] = 0
            }

            while (deque.isNotEmpty()) {
                val current = deque.removeFirst()

                if (current.row == end.first && current.col == end.second) {
                    val score = 1000 * current.turns + current.path.size
                    if (score <= bestScore) {
                        if (score < bestScore) {
                            bestScore = score
                            optimalPaths.clear()
                        }
                        optimalPaths.add(ScoredPath(current.path, current.turns, score))
                    }
                    continue
                }

                val (dr, dc) = directions[current.direction]
                val nextRow = current.row + dr
                val nextCol = current.col + dc
                if (isValidMove(map, nextRow, nextCol)) {
                    val newTurns = current.turns
                    if (newTurns <= dist[nextRow][nextCol][current.direction]) {
                        dist[nextRow][nextCol][current.direction] = newTurns
                        deque.addFirst(Position(
                            nextRow,
                            nextCol,
                            current.direction,
                            current.path + (nextRow to nextCol),
                            newTurns
                        ))
                    }
                }

                listOf((current.direction + 1) % 4, (current.direction + 3) % 4).forEach { newDir ->
                    if (current.turns + 1 <= dist[current.row][current.col][newDir]) {
                        dist[current.row][current.col][newDir] = current.turns + 1
                        deque.addLast(Position(
                            current.row,
                            current.col,
                            newDir,
                            current.path,
                            current.turns + 1
                        ))
                    }
                }
            }

            return optimalPaths
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val input = readFileLines("Day16_input")
            val map = input.map { it.toCharArray() }
            val optimalPaths = findOptimalPaths(map)

            println("Found ${optimalPaths.size} optimal solutions:")
            optimalPaths.forEachIndexed { index, path ->
                println("\nPath ${index + 1}:")
                println("Score: ${path.score} (Turns: ${path.turns}, Steps: ${path.path.size})")
                displayPath(map, path.path)
            }

            val distinctSteps = optimalPaths.flatMap { it.path }.toSet()
            println("\nTotal distinct steps across optimal paths: ${distinctSteps.size}")
        }

        private fun isValidMove(map: List<CharArray>, row: Int, col: Int): Boolean {
            return row in map.indices && col in map[0].indices && map[row][col] != '#'
        }

        private fun findStart(map: List<CharArray>) = map.indices.firstNotNullOf { i ->
            map[0].indices.firstNotNullOfOrNull { j -> if (map[i][j] == 'S') i to j else null }
        }

        private fun findEnd(map: List<CharArray>) = map.indices.firstNotNullOf { i ->
            map[0].indices.firstNotNullOfOrNull { j -> if (map[i][j] == 'E') i to j else null }
        }

        private fun displayPath(map: List<CharArray>, path: List<Pair<Int, Int>>) {
            for (i in map.indices) {
                for (j in map[0].indices) {
                    when {
                        path.contains(i to j) -> print("$ANSI_MAGENTA●$ANSI_RESET")
                        map[i][j] == 'S' -> print("${ANSI_GREEN}S$ANSI_RESET")
                        map[i][j] == 'E' -> print("${ANSI_RED}E$ANSI_RESET")
                        map[i][j] == '#' -> print("${ANSI_YELLOW}#$ANSI_RESET")
                        else -> print('.')
                    }
                }
                println("")
            }
            println("-".repeat(map[0].size))
        }
    }
}
