import java.util.*

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
            val path: List<Pair<Int, Int>> = listOf()
        )

        fun findAllPossiblePaths(map: List<CharArray>): Set<List<Pair<Int, Int>>> {
            val start = findStart(map)
            val end = findEnd(map)
            val directions = arrayOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1) // N,E,S,W
            val allPaths = mutableSetOf<List<Pair<Int, Int>>>()
            val visited = mutableSetOf<Triple<Int, Int, Int>>()
            val queue = LinkedList<Position>()

            // Start with all possible directions
            directions.indices.forEach { dir ->
                queue.add(Position(start.first, start.second, dir, listOf(start)))
            }

            while (queue.isNotEmpty()) {
                val current = queue.poll()
                val state = Triple(current.row, current.col, current.direction)

                if (state in visited) continue
                visited.add(state)

                if (current.row == end.first && current.col == end.second) {
                    allPaths.add(current.path)
                    continue
                }

                // Forward movement
                val (dr, dc) = directions[current.direction]
                val nextRow = current.row + dr
                val nextCol = current.col + dc
                if (isValidMove(map, nextRow, nextCol)) {
                    queue.add(Position(
                        nextRow,
                        nextCol,
                        current.direction,
                        current.path + (nextRow to nextCol)
                    ))
                }

                // Try rotations
                listOf((current.direction + 1) % 4, (current.direction + 3) % 4).forEach { newDir ->
                    queue.add(Position(
                        current.row,
                        current.col,
                        newDir,
                        current.path
                    ))
                }
            }

            return allPaths
        }

        private fun countDistinctSteps(allPaths: Set<List<Pair<Int, Int>>>): Int {
            return allPaths.flatMap { it }.toSet().size
        }

        private fun analyzePathStatistics(allPaths: Set<List<Pair<Int, Int>>>) {
            val stepFrequencies = allPaths
                .flatMap { it }
                .groupingBy { it }
                .eachCount()

            println("Total distinct paths found: ${allPaths.size}")
            println("Total distinct steps used: ${countDistinctSteps(allPaths)}")
            println("\nStep frequencies:")
            var t = 0;
            stepFrequencies.forEach { (pos, count) ->
                //println("Position (${pos.first},${pos.second}): used in $count paths")
                t+= count
            }
            println("disctinct steps: ${t}")
        }

        @JvmStatic
        fun main(args: Array<String>) {
//            val input = readFileLines("Day16_input")
            val input = readFileLines("Day16_star1_sample")
            val map = input.map { it.toCharArray() }
            val allPaths = findAllPossiblePaths(map)
            analyzePathStatistics(allPaths)
            displayAllPaths(map, allPaths)

        }

        // Helper functions remain the same as in original code
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

        private fun displayAllPaths(map: List<CharArray>, allPaths: Set<List<Pair<Int, Int>>>) {
            println("\nAll possible solutions (${allPaths.size} paths):")

            allPaths.forEachIndexed { index, path ->
                println("\nPath ${index + 1}:")

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
}
