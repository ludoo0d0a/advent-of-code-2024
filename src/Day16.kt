import java.util.*

class Day16 {
    companion object {
        private const val DEBUG = true
        private const val EXPECTED_SAMPLE = 7036L

        data class State(val row: Int, val col: Int, val direction: Int, val score: Long)
        data class Node(val state: State, val priority: Long): Comparable<Node> {
            override fun compareTo(other: Node) = priority.compareTo(other.priority)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val sample1 = readFileLines("Day16_star1_sample")
            val result_sample1 = part1(sample1)
            expect(result_sample1, EXPECTED_SAMPLE)
            println("sample result=$result_sample1")

//            val input = readFileLines("Day16_input")
//            val result_input = part1(input)
//            println("Result=$result_input")
        }

        private fun part1(input: List<String>): Long {
            val map = input.map { it.toCharArray() }.toList()
            val start = findStart(map)
            val end = findEnd(map)
            return findShortestPath(map, start, end)
        }

        private fun findShortestPath(map: List<CharArray>, start: Pair<Int, Int>, end: Pair<Int, Int>): Long {
            val directions = arrayOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)
            val visited = mutableSetOf<Triple<Int, Int, Int>>()
            val queue = PriorityQueue<Node>()

            // Start with all possible initial directions
            for (dir in 0..3) {
                queue.offer(Node(State(start.first, start.second, dir, 0), 0))
            }

            while (queue.isNotEmpty()) {
                val current = queue.poll().state
                val key = Triple(current.row, current.col, current.direction)

                if (current.row == end.first && current.col == end.second) {
                    return current.score
                }

                if (key in visited) continue
                visited.add(key)

                if (DEBUG) {
                    displayMap(map, current.row, current.col)
                }

                // Forward movement
                val (dr, dc) = directions[current.direction]
                val newRow = current.row + dr
                val newCol = current.col + dc
                if (isValid(map, newRow, newCol)) {
                    queue.offer(Node(
                        State(newRow, newCol, current.direction, current.score + 1),
                        current.score + 1 + manhattanDistance(newRow, newCol, end)
                    ))
                }

                // Rotations
                val rotations = arrayOf(
                    (current.direction + 1) % 4,  // clockwise
                    (current.direction + 3) % 4   // counter-clockwise
                )

                for (newDir in rotations) {
                    queue.offer(Node(
                        State(current.row, current.col, newDir, current.score + 1000),
                        current.score + 1000 + manhattanDistance(current.row, current.col, end)
                    ))
                }
            }

            return Long.MAX_VALUE
        }

        private fun manhattanDistance(row: Int, col: Int, end: Pair<Int, Int>): Long {
            return (Math.abs(row - end.first) + Math.abs(col - end.second)).toLong()
        }

        private fun isValid(map: List<CharArray>, row: Int, col: Int): Boolean {
            return row in map.indices && col in map[0].indices && map[row][col] != '#'
        }

        // Existing helper functions remain the same
        private fun findStart(map: List<CharArray>) = map.indices.firstNotNullOf { i ->
            map[i].indices.firstOrNull { j -> map[i][j] == 'S' }?.let { j -> i to j }
        }

        private fun findEnd(map: List<CharArray>) = map.indices.firstNotNullOf { i ->
            map[i].indices.firstOrNull { j -> map[i][j] == 'E' }?.let { j -> i to j }
        }

        private fun displayMap(map: List<CharArray>, currentRow: Int, currentCol: Int) {
            for (i in map.indices) {
                for (j in map[i].indices) {
                    print(if (i == currentRow && j == currentCol) '*' else map[i][j])
                }
                println("")
            }
            println("")
        }

        private fun expect(actual: Long, expected: Long) {
            if (actual != expected) throw AssertionError("Expected $expected but got $actual")
        }
    }
}
