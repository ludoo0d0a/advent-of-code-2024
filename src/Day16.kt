import java.util.*

class Day16 {
    companion object {
        private const val DEBUG = false
        private const val EXPECTED_SAMPLE = 7036L

        private const val ANSI_RED = "\u001B[31m"
        private const val ANSI_GREEN = "\u001B[32m"
        private const val ANSI_YELLOW = "\u001B[33m"
        private const val ANSI_BLUE = "\u001B[34m"
        private const val ANSI_CYAN = "\u001B[36m"
        private const val ANSI_MAGENTA = "\u001B[35m"
        private const val ANSI_RESET = "\u001B[0m"

        data class Position(
            val row: Int,
            val col: Int,
            val direction: Int,
            val score: Long,
            val path: List<Triple<Int, Int, Int>> = listOf(),
            val moves: Int = 0,
            val rotations: Int = 0
        )

        @JvmStatic
        fun main(args: Array<String>) {
//            val input = readFileLines("Day16_sinput")
            val input = readFileLines("Day16_star1_sample")
            val map = input.map { it.toCharArray() }
            val equivalentPaths = findAllEquivalentPaths(map)

            // Display statistics
            val distinctTiles = countDistinctTiles(equivalentPaths)
            val stepFrequencies = countStepFrequencies(equivalentPaths)

            println("\nStatistics:")
            println("Total equivalent paths: ${equivalentPaths.size}")
            println("Distinct tiles used: $distinctTiles")
            println("\nStep frequencies:")
            var t =0
            stepFrequencies.forEach { (pos, count) ->
                //println("(${pos.first},${pos.second}): $count times")
                t+=count
            }
            println("times $t")
        }

        private fun countStepFrequencies(optimalPaths: List<Position>): Map<Pair<Int, Int>, Int> {
            return optimalPaths
                .flatMap { path -> path.path.map { it.first to it.second } }
                .groupingBy { it }
                .eachCount()
        }

        private fun countDistinctTiles(optimalPaths: List<Position>): Int {
            return optimalPaths
                .flatMap { path -> path.path.map { it.first to it.second } }
                .toSet()
                .size
        }

        private fun findAllEquivalentPaths(map: List<CharArray>): List<Position> {
            val start = findStart(map)
            val end = findEnd(map)
            val directions = arrayOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1) // N,E,S,W

            val queue = PriorityQueue<Position>(compareBy { it.moves })
            val visited = mutableSetOf<Triple<Int, Int, Int>>()
            val equivalentPaths = mutableListOf<Position>()
            var bestMoveCount = Int.MAX_VALUE

            // Initialize with all possible starting directions
            directions.forEachIndexed { index, _ ->
                queue.offer(Position(start.first, start.second, index, 0L))
            }

            while (queue.isNotEmpty()) {
                val current = queue.poll()
                if (current.moves > bestMoveCount) continue

                val state = Triple(current.row, current.col, current.direction)
                if (state in visited) continue
                visited.add(state)

                if (current.row == end.first && current.col == end.second) {
                    when {
                        current.moves < bestMoveCount -> {
                            bestMoveCount = current.moves
                            equivalentPaths.clear()
                            equivalentPaths.add(current)
                        }
                        current.moves == bestMoveCount -> {
                            equivalentPaths.add(current)
                        }
                    }
                    continue
                }

                // Forward movement
                val (dr, dc) = directions[current.direction]
                val nextRow = current.row + dr
                val nextCol = current.col + dc
                if (isValidMove(map, nextRow, nextCol)) {
                    queue.offer(Position(
                        nextRow, nextCol, current.direction,
                        current.score + 1,
                        current.path + Triple(nextRow, nextCol, current.direction),
                        current.moves + 1,
                        current.rotations
                    ))
                }

                // Try rotations
                listOf((current.direction + 1) % 4, (current.direction + 3) % 4).forEach { newDir ->
                    queue.offer(Position(
                        current.row, current.col, newDir,
                        current.score,
                        current.path + Triple(current.row, current.col, newDir),
                        current.moves,
                        current.rotations + 1
                    ))
                }
            }

            println("\nFound ${equivalentPaths.size} equivalent paths with $bestMoveCount moves")
            equivalentPaths.forEachIndexed { index, path ->
                println("\nPath ${index + 1}:")
                displaySolutionPath(map, path, index + 1, equivalentPaths.size)
            }

            return equivalentPaths
        }

        private fun findAllOptimalPaths(map: List<CharArray>): Pair<Long, List<Position>> {
            val start = findStart(map)
            val end = findEnd(map)
            val directions = arrayOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1) // N,E,S,W

            val queue = PriorityQueue<Position>(compareBy { it.score })
            val visited = mutableSetOf<Triple<Int, Int, Int>>()
            val optimalPaths = mutableListOf<Position>()
            var bestScore = Long.MAX_VALUE

            // Start facing East (1)
            queue.offer(Position(start.first, start.second, 1, 0, rotations = 0))
            // Other directions require initial rotation
            queue.offer(Position(start.first, start.second, 0, 1000, rotations = 1))
            queue.offer(Position(start.first, start.second, 2, 1000, rotations = 1))
            queue.offer(Position(start.first, start.second, 3, 1000, rotations = 1))

            while (queue.isNotEmpty()) {
                val current = queue.poll()
                if (current.score > bestScore) continue

                val state = Triple(current.row, current.col, current.direction)

                if (current.row == end.first && current.col == end.second) {
                    when {
                        current.score < bestScore -> {
                            bestScore = current.score
                            optimalPaths.clear()
                            optimalPaths.add(current)
                            if (DEBUG) {
                                println("\nNew best path found! Score: ${current.score}")
                                displaySolutionPath(map, current, 1, optimalPaths.size)
                            }
                        }
                        current.score == bestScore -> {
                            optimalPaths.add(current)
                            if (DEBUG) {
                                println("\nAlternative optimal path found!")
                                displaySolutionPath(map, current, optimalPaths.size, optimalPaths.size)
                            }
                        }
                    }
                    continue
                }

                if (state in visited) continue
                visited.add(state)

                if (DEBUG) {
                    displayMap(map, current)
                    Thread.sleep(20)
                }

                // Forward movement
                val (dr, dc) = directions[current.direction]
                val nextRow = current.row + dr
                val nextCol = current.col + dc
                if (isValidMove(map, nextRow, nextCol)) {
                    queue.offer(Position(
                        nextRow, nextCol, current.direction,
                        current.score + 1,
                        current.path + Triple(nextRow, nextCol, current.direction),
                        current.moves + 1,
                        current.rotations
                    ))
                }

                // Rotations
                listOf((current.direction + 1) % 4, (current.direction + 3) % 4).forEach { newDir ->
                    queue.offer(Position(
                        current.row, current.col, newDir,
                        current.score + 1000,
                        current.path + Triple(current.row, current.col, newDir),
                        current.moves,
                        current.rotations + 1
                    ))
                }
            }

            // Display all optimal paths at the end
            println("\nAll optimal paths (Score: $bestScore):")
            optimalPaths.forEachIndexed { index, path ->
                displaySolutionPath(map, path, index + 1, optimalPaths.size)
            }

            // After finding all optimal paths
            val distinctTilesCount = countDistinctTiles(optimalPaths)
            println("\nAll optimal paths (Score: $bestScore):")
            println("Number of optimal paths: ${optimalPaths.size}")
            println("Number of distinct tiles used: $distinctTilesCount")
            optimalPaths.forEachIndexed { index, path ->
                displaySolutionPath(map, path, index + 1, optimalPaths.size)
            }

            val stepFrequencies = countStepFrequencies(optimalPaths)
            println("\nStep frequencies across all optimal paths:")
            stepFrequencies.forEach { (pos, count) ->
                println("Position (${pos.first},${pos.second}): used in $count paths")
            }


            return bestScore to optimalPaths
        }

        private fun displayMap(map: List<CharArray>, current: Position) {
            val directionSymbol = when(current.direction) {
                0 -> "^"
                1 -> ">"
                2 -> "v"
                3 -> "<"
                else -> "+"
            }

            for (i in map.indices) {
                for (j in map[0].indices) {
                    when {
                        i == current.row && j == current.col ->
                            print("$ANSI_BLUE$directionSymbol$ANSI_RESET")
                        map[i][j] == 'S' ->
                            print("${ANSI_GREEN}S$ANSI_RESET")
                        map[i][j] == 'E' ->
                            print("${ANSI_RED}E$ANSI_RESET")
                        map[i][j] == '#' ->
                            print("${ANSI_YELLOW}#$ANSI_RESET")
                        else -> print('.')
                    }
                }
                println("")
            }
            println("Score: ${current.score}, Moves: ${current.moves}, Rotations: ${current.rotations}")
        }

        private fun displaySolutionPath(map: List<CharArray>, solution: Position, pathNumber: Int, totalPaths: Int) {
            println("\nPath $pathNumber of $totalPaths:")
            println("Score: ${solution.score}")
            println("Moves: ${solution.moves}")
            println("Rotations: ${solution.rotations}")
            println("Movement cost: ${solution.moves}")
            println("Rotation cost: ${solution.rotations * 1000}")

            val pathPositions = solution.path.toSet()

            for (i in map.indices) {
                for (j in map[0].indices) {
                    val position = solution.path.find { (r, c, _) -> r == i && c == j }
                    when {
                        position != null -> {
                            val symbol = when(position.third) {
                                0 -> "^"
                                1 -> ">"
                                2 -> "v"
                                3 -> "<"
                                else -> "+"
                            }
                            print("$ANSI_MAGENTA$symbol$ANSI_RESET")
                        }
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
