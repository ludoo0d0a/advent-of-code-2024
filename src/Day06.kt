/*
Day 6: Treetop Tree House

This solution:

Creates a Position data class to track coordinates and a Direction enum for the guard's facing direction
Implements the main logic in countVisitedPositions which:
Finds the starting position (^)
Tracks visited positions in a Set
Simulates movement according to the rules until leaving the map
Includes helper functions for:
Finding the start position
Checking bounds
Detecting obstacles
Turning right
Moving forward
To use this solution, create a test input file named "Day06_test.txt" with the map from the example. The program will output the number of distinct positions visited by the guard before leaving the mapped area.

For the example provided, it should output 41 distinct positions.


 */


class Day06 {
    data class Position(val x: Int, val y: Int)
    enum class Direction { UP, RIGHT, DOWN, LEFT }

    val DEBUG=false;

    fun main() {
        val input = readFileContent("Day06_test1")

        val result1 = countVisitedPositions(input)
        println("Distinct positions visited: $result1")

        val result2 = findLoopingObstacles(input)
        println("Number of possible obstacle positions: $result2")
    }

    fun findLoopingObstacles(input: String): Int {
        val grid = input.lines().map { it.toCharArray() }.toMutableList()
        val startPos = findStart(grid)
        val candidates = mutableSetOf<Position>()

        // Try each empty position
        for (y in grid.indices) {
            for (x in grid[0].indices) {
                if (Position(x, y) != startPos && grid[y][x] == '.') {
                    // Place temporary obstacle
                    grid[y][x] = '#'

                    if (createsLoop(grid.map { it.clone() }.toMutableList(), startPos)) {
                        candidates.add(Position(x, y))
                    }

                    // Remove temporary obstacle
                    grid[y][x] = '.'
                }
            }
        }

        return candidates.size
    }

    private fun createsLoop(grid: MutableList<CharArray>, startPos: Position): Boolean {
        val visited = mutableSetOf<Pair<Position, Direction>>()
        var currentPos = startPos
        var currentDir = Direction.UP

        while (isInBounds(currentPos, grid)) {
            val state = Pair(currentPos, currentDir)
            if (state in visited) {
                return true // Found a loop
            }
            visited.add(state)

            val next = moveForward(currentPos, currentDir)
            if (!isInBounds(next, grid)) {
                return false
            }

            if (hasObstacleAhead(next, grid)) {
                currentDir = turnRight(currentDir)
            } else {
                currentPos = next
            }
        }

        return false
    }


    private fun countVisitedPositions(input: String): Int {
        //val grid = input.lines()
        val grid = input.lines().map { it.toCharArray() }.toMutableList()
        val visited = mutableSetOf<Position>()

        // Find starting position and direction
        var currentPos = findStart(grid)
        var currentDir = Direction.UP
        visited.add(currentPos)
        printGrid(grid)

        while (isInBounds(currentPos, grid)) {

            val next = moveForward(currentPos, currentDir)
            if(!isInBounds(next, grid)){
                break
            }

            if (hasObstacleAhead(next, grid)) {
                currentDir = turnRight(currentDir)
            } else {
                currentPos = moveForward(currentPos, currentDir)
                visited.add(currentPos)

                // Mark visited position with 'X'
                if (isInBounds(currentPos, grid)) {
                    grid[currentPos.y][currentPos.x] = 'X'
                    println("\nAfter move:")
                    printGrid(grid)
                    //Thread.sleep(500) // Add delay to make visualization easier to follow
                }
            }
        }

        return visited.size
    }

    private fun printGrid(grid: List<CharArray>) {
        if (!DEBUG) return;
        println("-".repeat(grid[0].size + 2))
        grid.forEach { row ->
            println("|${row.joinToString("")}|")
        }
        println("-".repeat(grid[0].size + 2))
    }

    private fun findStart(grid: List<CharArray>): Position {
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                if (char == '^') return Position(x, y)
            }
        }
        throw IllegalArgumentException("No starting position found")
    }

    private fun isInBounds(pos: Position, grid: List<CharArray>): Boolean {
        return pos.y in grid.indices && pos.x in grid[0].indices
    }

    //    private fun hasObstacleAhead(pos: Position, dir: Direction, grid: List<CharArray>): Boolean {
//        val next = moveForward(pos, dir)
//        return !isInBounds(next, grid) || grid[next.y][next.x] == '#'
//    }
    private fun hasObstacleAhead(next: Position, grid: List<CharArray>): Boolean {
        return grid[next.y][next.x] == '#'
    }

    private fun turnRight(dir: Direction): Direction {
        return when (dir) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
        }
    }

    private fun moveForward(pos: Position, dir: Direction): Position {
        return when (dir) {
            Direction.UP -> Position(pos.x, pos.y - 1)
            Direction.RIGHT -> Position(pos.x + 1, pos.y)
            Direction.DOWN -> Position(pos.x, pos.y + 1)
            Direction.LEFT -> Position(pos.x - 1, pos.y)
        }
    }
}

fun main() {
    Day06().main()
}
