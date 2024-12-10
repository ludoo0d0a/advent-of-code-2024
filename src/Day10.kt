import java.io.File

/*
Day 10: Hoof It
*/
class Day10 {

    val DEBUG=true;

    fun main() {
        fun parseMap(input: List<String>): Array<IntArray> {
            return input.map { line ->
                line.map { it.digitToInt() }.toIntArray()
            }.toTypedArray()
        }

        fun isTrailhead(map: Array<IntArray>, x: Int, y: Int): Boolean {
            val height = map[y][x]
            val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
            return directions.all { (dx, dy) ->
                val nx = x + dx
                val ny = y + dy
                nx !in map[0].indices || ny !in map.indices || map[ny][nx] < height
            }
        }

        fun calculateRating(map: Array<IntArray>, startX: Int, startY: Int): Int {
            val visited = mutableSetOf<Pair<Int, Int>>()
            val queue = ArrayDeque<Triple<Int, Int, Int>>()
            queue.add(Triple(startX, startY, map[startY][startX]))

            while (queue.isNotEmpty()) {
                val (x, y, height) = queue.removeFirst()
                if (visited.add(x to y)) {
                    val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
                    for ((dx, dy) in directions) {
                        val nx = x + dx
                        val ny = y + dy
                        if (nx in map[0].indices && ny in map.indices && map[ny][nx] > height) {
                            queue.add(Triple(nx, ny, map[ny][nx]))
                        }
                    }
                }
            }
            return visited.size
        }

        fun part2(input: List<String>): Int {
            val map = parseMap(input)
            var totalRating = 0

            for (y in map.indices) {
                for (x in map[0].indices) {
                    if (isTrailhead(map, x, y)) {
                        totalRating += calculateRating(map, x, y)
                    }
                }
            }

            return totalRating
        }

        val testInput = readInput("Day10_star2_sample")
        val res = part2(testInput)
        println("Result sample star 2 : $res")
        check(res == 81)

        val input = readInput("Day10_input")
        println(part2(input))
    }
}

fun main() {
    Day10().main()
}
