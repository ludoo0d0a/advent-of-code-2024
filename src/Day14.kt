/*
--- Day 14 star 2 ---
Problem: Find the fewest seconds for robots to display a Christmas tree pattern.
Requirements:
- Detect adjacent robots in the grid
- Pattern is a Christmas tree if more than half of robots are adjacent
- Display each found pattern and continue searching
- Optimize for performance using efficient indexing
- Use Long type to prevent overflow
*/

class Day14 {
    companion object {
        private val directions = listOf(
            -1 to 0, 1 to 0, 0 to -1, 0 to 1,  // orthogonal
            -1 to -1, -1 to 1, 1 to -1, 1 to 1 // diagonal
        )

        @JvmStatic
        fun main(args: Array<String>) {
            val result1 = part1()
            val result2 = part2()
            println("Result1=$result1")
            println("Result2=$result2")
        }

        private fun part2(): Long {
            val input = readFileLines("Day14_input")
            val robots = parseRobots(input)
            val width = 101L
            val height = 103L
            var firstTreeFound = -1L

            var seconds = 0L
            while (seconds < 10000L) {
                val positions = simulateRobots(robots, width, height, seconds)
                val grid = createGrid(positions, width, height)

                if (isChristmasTree(grid, positions)) {
                    if (firstTreeFound == -1L) {
                        firstTreeFound = seconds
                    }
                    println("\nFound Christmas tree at $seconds seconds:")
                    displayGrid(grid)
                }
                seconds++
            }
            return firstTreeFound
        }

        private fun createGrid(positions: List<Pair<Long, Long>>, width: Long, height: Long): Array<CharArray> {
            val grid = Array(height.toInt()) { CharArray(width.toInt()) { '.' } }
            positions.forEach { (x, y) -> grid[y.toInt()][x.toInt()] = '#' }
            return grid
        }

        private fun isChristmasTree(grid: Array<CharArray>, positions: List<Pair<Long, Long>>): Boolean {
            val height = grid.size
            val width = grid[0].size
            val totalRobots = positions.size
            var adjacentRobots = 0

            val visited = mutableSetOf<Pair<Int, Int>>()

            positions.forEach { (x, y) ->
                val xi = x.toInt()
                val yi = y.toInt()

                if (Pair(xi, yi) !in visited) {
                    directions.forEach { (dx, dy) ->
                        val newX = xi + dx
                        val newY = yi + dy
                        if (newX in 0 until width &&
                            newY in 0 until height &&
                            grid[newY][newX] == '#' &&
                            Pair(newX, newY) !in visited) {
                            adjacentRobots++
                            visited.add(Pair(newX, newY))
                        }
                    }
                    visited.add(Pair(xi, yi))
                }
            }

            return adjacentRobots >= totalRobots / 2
        }

        private fun displayGrid(grid: Array<CharArray>) {
            grid.forEach { println(it.joinToString("")) }
        }

        private fun parseRobots(input: List<String>): List<Robot> {
            return input.map { line ->
                val (pos, vel) = line.split(" ")
                val (px, py) = pos.substringAfter("=").split(",").map { it.toLong() }
                val (vx, vy) = vel.substringAfter("=").split(",").map { it.toLong() }
                Robot(px, py, vx, vy)
            }
        }

        private fun simulateRobots(robots: List<Robot>, width: Long, height: Long, seconds: Long): List<Pair<Long, Long>> {
            return robots.map { robot ->
                val x = (robot.px + robot.vx * seconds).mod(width)
                val y = (robot.py + robot.vy * seconds).mod(height)
                x to y
            }
        }

        private fun part1(): Long {
            val input = readFileLines("Day14_input")
            val robots = parseRobots(input)
            val width = 101L
            val height = 103L
            val seconds = 100L

            val finalPositions = simulateRobots(robots, width, height, seconds)
            val quadrants = countRobotsInQuadrants(finalPositions, width, height)
            return quadrants.fold(1L) { acc, count -> acc * count }
        }

        private fun countRobotsInQuadrants(positions: List<Pair<Long, Long>>, width: Long, height: Long): List<Long> {
            val midX = width / 2
            val midY = height / 2
            val quadrants = LongArray(4)

            for ((x, y) in positions) {
                when {
                    x < midX && y < midY -> quadrants[0]++
                    x > midX && y < midY -> quadrants[1]++
                    x < midX && y > midY -> quadrants[2]++
                    x > midX && y > midY -> quadrants[3]++
                }
            }
            return quadrants.toList()
        }
    }

    data class Robot(val px: Long, val py: Long, val vx: Long, val vy: Long)
}
