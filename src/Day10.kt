/*
Day 10: Hoof It
*/
class Day10 {

    val DEBUG=true;

    fun main() {
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

        fun parseMap(input: List<String>): Array<IntArray> {
            return input.map { line ->
                line.map {
                    if (it=='.')  0 else it.digitToInt()
                }.toIntArray()
            }.toTypedArray()
        }

        fun isTrailhead(map: Array<IntArray>, x: Int, y: Int): Boolean {
            val height = map[y][x]
            return height==0
//            val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
//            return directions.all { (dx, dy) ->
//                val nx = x + dx
//                val ny = y + dy
//                nx !in map[0].indices || ny !in map.indices || map[ny][nx] < height
//            }
        }

        fun calculateRating(map: Array<IntArray>, startX: Int, startY: Int): Int {
            val paths = mutableSetOf<List<Pair<Int, Int>>>()
            fun dfs(x: Int, y: Int, currentPath: List<Pair<Int, Int>>) {
                if (map[y][x] == 9) {
                    paths.add(currentPath)
                    return
                }

                val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
                for ((dx, dy) in directions) {
                    val nx = x + dx
                    val ny = y + dy
                    if (nx in map[0].indices && ny in map.indices &&
                        map[ny][nx] == map[y][x] + 1 &&
                        Pair(nx, ny) !in currentPath) {
                        dfs(nx, ny, currentPath + (nx to ny))
                    }
                }
            }

            dfs(startX, startY, listOf(startX to startY))
            return paths.size
        }


        fun part2(input: List<String>): Int {
            val map = parseMap(input)
            var totalRating = 0

            for (y in map.indices) {
                for (x in map[0].indices) {
                    if (isTrailhead(map, x, y)) {
                        val rating =  calculateRating(map, x, y)
                        //println("Rating $x,$y : $rating")
                        totalRating += rating
                    }
                }
            }

            return totalRating
        }

        val input = readLines("Day10_input")

//        val sample1 = readInput("Day10_star1_sample")
//        val r1 = sumTrailheadScores(sample1)
//        check(r1 == 227)

        val result1 = sumTrailheadScores(input)
        println("star1: Sum of trailhead scores: $result1")

        println("---- star2:")
        val sample2 = readLines("Day10_star2_sample")
        val r2 = part2(sample2)
        println("star2: Result sample = $r2")
        check(r2 == 227)

        val result2=println(part2(input))
        println("star2: Sum of trailhead ratings: $result2")
    }
}

fun main() {
    Day10().main()
}
