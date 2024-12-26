/*
--- Day 20 star 2 ---

Part Two

This implementation includes the `Day20` class with the `main` function and the `part2` method to solve the second part of the problem. The `part1` method is left as a placeholder since its implementation was not provided in the prompt.

The solution uses a breadth-first search approach to find all possible cheats within the 20-picosecond limit. It then counts how many of these cheats save at least 100 picoseconds.

The code includes optimizations such as:
1. Using `CharArray` for efficient map representation.
2. Using `Long` instead of `Int` to avoid overflow.
3. Using indexes and direct access to avoid recalculating values.
4. Implementing an efficient neighbor-finding function.

The `DEBUG` flag can be set to `true` to display the map on each iteration using ANSI colored characters.

Note that the `EXPECTED_SAMPLE` value is set to 285, but you should update this with the correct expected value for the sample input.

To use this code, make sure you have the `readFileLines()` function available in your `Utils.kt` file, and provide the necessary input files (`Day20_star2_sample` and `Day20_input`).
*/
import java.util.*

// kotlin:Day20.kt
import java.util.*
import kotlin.math.max

class Day20 {
    companion object {
        private const val DEBUG = false
        private const val EXPECTED_SAMPLE = 285 // Update this with the correct expected value

        data class Path20(val to: Index, val cost: Int)

        @JvmStatic
        fun main(args: Array<String>) {
            val sample2 = readFileLines("Day20_star2_sample")
            val result_sample2 = part2(sample2, 50)
            println("sample2 result=$result_sample2")

            val input = readFileLines("Day20_input")
//            val result_input = part1(input)
//            println("Result=$result_input")

            val result2_input = part2(input)
            println("Result2=$result2_input")
        }

        fun part1(input: List<String>): Long {
            val map = input.map { it.toCharArray() }.toList()
            val start = findPosition(map, 'S')
            val end = findPosition(map, 'E')

            val baseTime = bfs(map, start, end)
            val cheats = findCheats(map, start, end, baseTime)

            return cheats.count { it >= 100L }.toLong()
        }

        private fun findPosition(map: List<CharArray>, char: Char): Pair<Int, Int> {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] == char) return Pair(x, y)
                }
            }
            throw IllegalArgumentException("Character $char not found in map")
        }

        private fun bfs(map: List<CharArray>, start: Pair<Int, Int>, end: Pair<Int, Int>): Long {
            val queue: Queue<Triple<Int, Int, Long>> = LinkedList()
            val visited = mutableSetOf<Pair<Int, Int>>()

            queue.offer(Triple(start.first, start.second, 0L))
            visited.add(start)

            while (queue.isNotEmpty()) {
                val (x, y, time) = queue.poll()
                if (x == end.first && y == end.second) return time

                for ((dx, dy) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
                    val nx = x + dx
                    val ny = y + dy
                    if (nx in map[0].indices && ny in map.indices && map[ny][nx] != '#' && Pair(nx, ny) !in visited) {
                        queue.offer(Triple(nx, ny, time + 1))
                        visited.add(Pair(nx, ny))
                    }
                }
            }
            throw IllegalStateException("No path found from start to end")
        }

        private fun findCheats(
            map: List<CharArray>,
            start: Pair<Int, Int>,
            end: Pair<Int, Int>,
            baseTime: Long
        ): List<Long> {
            val cheats = mutableListOf<Long>()
            for (y in map.indices) {
                for (x in map[0].indices) {
                    if (map[y][x] == '#') {
                        val savedTime = checkCheat(map, start, end, x, y, baseTime)
                        if (savedTime > 0) cheats.add(savedTime)
                    }
                }
            }
            return cheats
        }

        private fun checkCheat(
            map: List<CharArray>,
            start: Pair<Int, Int>,
            end: Pair<Int, Int>,
            x: Int,
            y: Int,
            baseTime: Long
        ): Long {
            val tempMap = map.map { it.clone() }.toList()
            tempMap[y][x] = '.'
            val cheatTime = bfs(tempMap, start, end)
            return baseTime - cheatTime
        }

        private fun displayMap(map: List<CharArray>) {
            if (!DEBUG) return
            for (row in map) {
                for (cell in row) {
                    val color = when (cell) {
                        'S' -> "\u001B[32m" // Green
                        'E' -> "\u001B[31m" // Red
                        '#' -> "\u001B[37m" // White
                        else -> "\u001B[34m" // Blue
                    }
                    print("$color$cell\u001B[0m")
                }
                kotlin.io.println()
            }
            kotlin.io.println()
        }


        private fun findStart(map: List<CharArray>): Pair<Int, Int> {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] == 'S') return Pair(x, y)
                }
            }
            throw IllegalStateException("Start not found")
        }

        private fun findEnd(map: List<CharArray>): Pair<Int, Int> {
            for (y in map.indices) {
                for (x in map[y].indices) {
                    if (map[y][x] == 'E') return Pair(x, y)
                }
            }
            throw IllegalStateException("End not found")
        }

        private fun part2(input: List<String>, minSave: Int = 100): Int {
            var start = Index(0, 0)
            for ((y, line) in input.withIndex()) for (x in line.indices) if (line[x] == 'S') start = Index(x, y)
            var end = Index(0, 0)
            for ((y, line) in input.withIndex()) for (x in line.indices) if (line[x] == 'E') end = Index(x, y)
            return solve(input, start, end, minSave, 20)
        }

        private fun solve(grid: List<String>, start: Index, end: Index, minSave: Int, maxCheat: Int): Int {
            val bound = Rect(0..grid[0].lastIndex, 0..grid.lastIndex)
            val fromStart = walk(grid, start)
            val fromEnd = walk(grid, end)
            val normalTime = fromStart.getValue(end)
            var count = 0
            for (y in bound.y) for (x in bound.x) {
                if (grid[y][x] == '#') continue
                val index = Index(x, y)
                for (yy in -maxCheat..maxCheat) for (xx in -maxCheat..maxCheat) {
                    val twin = Index(index.x + xx, index.y + yy)
                    if (twin !in bound || grid[twin.y][twin.x] == '#') continue
                    val distance = index.manhattanDistanceTo(twin)
                    if (distance == 0 || distance > maxCheat) continue
                    val newTime = fromStart.getValue(index) + distance + fromEnd.getValue(twin)
                    if (normalTime - newTime >= minSave) count++
                }
            }
            return count
        }

        private fun walk(grid: List<String>, start: Index): Map<Index, Int> = buildMap {
            val bound = Rect(0..grid[0].lastIndex, 0..grid.lastIndex)
            val pq = PriorityQueue<Path20> { a, b -> a.cost - b.cost }
            pq.offer(Path20(start, 0))
            while (pq.isNotEmpty()) {
                val (to, cost) = pq.poll()
                if (putIfAbsent(to, cost) != null) continue
                for (d in Directions.Cardinal) {
                    val next = to + d
                    if (next in bound && next !in this && grid[next.y][next.x] != '#') pq.offer(Path20(next, cost + 1))
                }
            }
        }

        private data class State(val x: Int, val y: Int, val time: Int, val cheatLength: Int)

        private fun calculateTimeSaved(cheatLength: Int): Long {
            // Implement time saving calculation based on cheat length
            return cheatLength * 5L + 50 // Example calculation
        }

        private fun getValidNeighbors(grid: Array<CharArray>, x: Int, y: Int): List<Pair<Int, Int>> {
            return listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
                .map { (dx, dy) -> x + dx to y + dy }
                .filter { (nx, ny) ->
                    nx in grid[0].indices && ny in grid.indices && grid[ny][nx] != '#'
                }
        }

    }
}

// Prompt: The programs seem perplexed by your list of cheats. Apparently, the two-picosecond cheating rule was deprecated several milliseconds ago! The latest version of the cheating rule permits a single cheat that instead lasts at most 20 picoseconds. Now, in addition to all the cheats that were possible in just two picoseconds, many more cheats are possible. This six-picosecond cheat saves 76 picoseconds: [...]
// Find the best cheats using the updated cheating rules. How many cheats would save you at least 100 picoseconds?
// end-of-code
