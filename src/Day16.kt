import java.util.*

class Day16 {
    companion object {
        private const val DEBUG = false
        private const val EXPECTED_SAMPLE = 7036L

        private const val ANSI_RED = "\u001B[31m"
        private const val ANSI_GREEN = "\u001B[32m"
        private const val ANSI_YELLOW = "\u001B[33m"
        private const val ANSI_BLUE = "\u001B[34m"
        private const val ANSI_RESET = "\u001B[0m"

        data class Position(
            val row: Int,
            val col: Int,
            val direction: Int,
            val score: Long,
            val path: List<Triple<Int, Int, Int>> = listOf()
        )

        @JvmStatic
        fun main(args: Array<String>) {
            val sample1 = readFileLines("Day16_star1_sample")
            val result_sample1 = part1(sample1)
            expect(result_sample1, EXPECTED_SAMPLE)
            println("Sample result=$result_sample1")

            val input = readFileLines("Day16_input")
            val result_input = part1(input)
            println("Result=$result_input")
        }

        private fun part1(input: List<String>): Long {
            val map = input.map { it.toCharArray() }.toList()
            return solveMaze(map)
        }

        private fun solveMaze(map: List<CharArray>): Long {
            val start = findStart(map)
            val end = findEnd(map)
            val directions = arrayOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1) // N,E,S,W

            val queue = PriorityQueue<Position>(compareBy { it.score })
            val visited = mutableSetOf<Triple<Int, Int, Int>>()

            // Start facing East (1) with no rotation cost
            queue.offer(Position(start.first, start.second, 1, 0))
            // Other initial directions require rotation cost
            queue.offer(Position(start.first, start.second, 0, 1000)) // North
            queue.offer(Position(start.first, start.second, 2, 1000)) // South
            queue.offer(Position(start.first, start.second, 3, 1000)) // West

            var bestSolution: Position? = null

            while (queue.isNotEmpty()) {
                val current = queue.poll()
                val state = Triple(current.row, current.col, current.direction)

                if (current.row == end.first && current.col == end.second) {
                    if (bestSolution == null || current.score < bestSolution.score) {
                        bestSolution = current
                        if (DEBUG) {
                            println("Found better solution with score: ${current.score}")
                            displaySolutionPath(map, current.path)
                        }
                    }
                    continue
                }

                if (state in visited) continue
                visited.add(state)

                if (DEBUG) {
                    displayMap(map, current.row, current.col, current.direction)
                    Thread.sleep(100) // Slow down visualization
                }

                // Move forward
                val (dr, dc) = directions[current.direction]
                val nextRow = current.row + dr
                val nextCol = current.col + dc
                if (isValidMove(map, nextRow, nextCol)) {
                    queue.offer(Position(
                        nextRow,
                        nextCol,
                        current.direction,
                        current.score + 1,
                        current.path + Triple(nextRow, nextCol, current.direction)
                    ))
                }

                // Rotations
                val rotations = listOf(
                    (current.direction + 1) % 4,  // clockwise
                    (current.direction + 3) % 4   // counterclockwise
                )

                rotations.forEach { newDir ->
                    queue.offer(Position(
                        current.row,
                        current.col,
                        newDir,
                        current.score + 1000,
                        current.path + Triple(current.row, current.col, newDir)
                    ))
                }
            }

            return bestSolution?.score ?: Long.MAX_VALUE
        }

        private fun displayMap(map: List<CharArray>, currentRow: Int, currentCol: Int, direction: Int) {
            val directionSymbol = when(direction) {
                0 -> "^"
                1 -> ">"
                2 -> "v"
                3 -> "<"
                else -> "+"
            }

            for (i in map.indices) {
                for (j in map[0].indices) {
                    when {
                        i == currentRow && j == currentCol ->
                            print("$ANSI_BLUE$directionSymbol$ANSI_RESET")
                        map[i][j] == 'S' ->
                            print("${ANSI_GREEN}S$ANSI_RESET")
                        map[i][j] == 'E' ->
                            print("${ANSI_RED}E$ANSI_RESET")
                        map[i][j] == '#' ->
                            print("${ANSI_YELLOW}#$ANSI_RESET")
                        else -> print(map[i][j])
                    }
                }
                println("")
            }
            println("")
        }

        private fun displaySolutionPath(map: List<CharArray>, path: List<Triple<Int, Int, Int>>) {
            println("\nFinal solution path:")
            for (i in map.indices) {
                for (j in map[0].indices) {
                    val position = path.find { (r, c, _) -> r == i && c == j }
                    when {
                        position != null -> {
                            val symbol = when(position.third) {
                                0 -> "^"
                                1 -> ">"
                                2 -> "v"
                                3 -> "<"
                                else -> "+"
                            }
                            print("$ANSI_BLUE$symbol$ANSI_RESET")
                        }
                        map[i][j] == 'S' -> print("${ANSI_GREEN}S$ANSI_RESET")
                        map[i][j] == 'E' -> print("${ANSI_RED}E$ANSI_RESET")
                        map[i][j] == '#' -> print("${ANSI_YELLOW}#$ANSI_RESET")
                        else -> print('.')
                    }
                }
                println("")
            }
            println("\nPath length: ${path.size}")
        }

        private fun isValidMove(map: List<CharArray>, row: Int, col: Int): Boolean {
            return row in map.indices &&
                    col in map[0].indices &&
                    map[row][col] != '#'
        }

        private fun findStart(map: List<CharArray>): Pair<Int, Int> {
            for (i in map.indices) {
                for (j in map[0].indices) {
                    if (map[i][j] == 'S') return i to j
                }
            }
            throw IllegalStateException("Start position not found")
        }

        private fun findEnd(map: List<CharArray>): Pair<Int, Int> {
            for (i in map.indices) {
                for (j in map[0].indices) {
                    if (map[i][j] == 'E') return i to j
                }
            }
            throw IllegalStateException("End position not found")
        }

        private fun expect(actual: Long, expected: Long) {
            if (actual != expected) {
                throw AssertionError("Expected $expected but got $actual")
            }
        }
    }
}
