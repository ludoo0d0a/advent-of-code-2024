class Day15 {
    companion object {
        const val DEBUG = true

        @JvmStatic
        fun main(args: Array<String>) {
            val sample2 = readFileLines("Day15_star2_sample")
            val result_sample2 = part2(sample2)
            println("sample2 result=$result_sample2")
        }

        fun part1(input: List<String>): Long {
            val (map, moves) = parseInput(input)
            val warehouse = Warehouse(map)

            for (move in moves) {
                warehouse.moveRobot(move)
                if (DEBUG) {
                    println("After move $move:")
                    warehouse.printMap()
                }
            }

            return warehouse.calculateGPSSum()
        }

        fun part2(input: List<String>): Long {
            val (map, moves) = parseInput(input)
            val scaledMap = scaleMap(map)
            val warehouse = Warehouse(scaledMap)

            for (move in moves) {
                warehouse.moveRobot(move)
                if (DEBUG) {
                    println("After move $move:")
                    warehouse.printMap()
                }
            }

            return warehouse.calculateGPSSum()
        }

        private fun parseInput(input: List<String>): Pair<List<String>, List<Char>> {
            val mapEndIndex = input.indexOfFirst { it.isEmpty() }
            val map = input.subList(0, mapEndIndex)
            val moves = input.last().toList()
            return Pair(map, moves)
        }

        private fun scaleMap(map: List<String>): List<String> {
            return map.map { line ->
                line.map { char ->
                    when (char) {
                        '#' -> "##"
                        'O' -> "[]"
                        '.' -> ".."
                        '@' -> "@."
                        else -> throw IllegalArgumentException("Invalid character: $char")
                    }
                }.joinToString("")
            }
        }
    }

    data class Position(val x: Int, val y: Int) {
        operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    }

    data class Wall(val position: Position)
    data class Box(var leftPosition: Position, var rightPosition: Position) {
        fun move(movement: Position) {
            leftPosition = leftPosition + movement
            rightPosition = rightPosition + movement
        }
    }
    data class Robot(var position: Position)

    class Warehouse(map: List<String>) {
        private val width: Int = map[0].length
        private val height: Int = map.size
        private val walls = mutableListOf<Wall>()
        private val boxes = mutableListOf<Box>()
        private val robot: Robot

        init {
            var robotPosition = Position(0, 0)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    when (map[y][x]) {
                        '#' -> walls.add(Wall(Position(x, y)))
                        '[' -> if (x + 1 < width && map[y][x + 1] == ']') {
                            boxes.add(Box(Position(x, y), Position(x + 1, y)))
                        }
                        '@' -> robotPosition = Position(x, y)
                    }
                }
            }
            robot = Robot(robotPosition)
        }

        fun moveRobot(direction: Char) {
            val movement = when (direction) {
                '>' -> Position(1, 0)
                '<' -> Position(-1, 0)
                '^' -> Position(0, -1)
                'v' -> Position(0, 1)
                else -> throw IllegalArgumentException("Invalid direction: $direction")
            }

            val newPosition = robot.position + movement

            when {
                canMoveTo(newPosition) -> {
                    robot.position = newPosition
                }
                isBoxAt(newPosition) -> {
                    pushBoxes(newPosition, movement)
                }
            }
        }

        private fun canMoveTo(position: Position): Boolean {
            return position.x in 0 until width &&
                    position.y in 0 until height &&
                    !walls.any { it.position == position } &&
                    !boxes.any { it.leftPosition == position || it.rightPosition == position }
        }

        private fun isBoxAt(position: Position): Boolean {
            return boxes.any { it.leftPosition == position || it.rightPosition == position }
        }

        private fun pushBoxes(startPosition: Position, movement: Position) {
            val boxChain = mutableListOf<Box>()
            var currentPos = startPosition

            while (isBoxAt(currentPos)) {
                val box = boxes.first { it.leftPosition == currentPos || it.rightPosition == currentPos }
                boxChain.add(box)
                currentPos = if (movement.x > 0) box.rightPosition + movement else box.leftPosition + movement
            }

            if (canMoveTo(currentPos)) {
                boxChain.reversed().forEach { box ->
                    box.move(movement)
                }
                robot.position = robot.position + movement
            }
        }

        fun calculateGPSSum(): Long {
            return boxes.sumOf { box ->
                100L * (box.leftPosition.y + 1) + (box.leftPosition.x + 1)
            }
        }

        fun printMap() {
            val display = Array(height) { CharArray(width) { '.' } }

            walls.forEach { display[it.position.y][it.position.x] = '#' }
            boxes.forEach { box ->
                display[box.leftPosition.y][box.leftPosition.x] = '['
                display[box.rightPosition.y][box.rightPosition.x] = ']'
            }
            display[robot.position.y][robot.position.x] = '@'

            display.forEach { println(it.joinToString("")) }
            println()
        }
    }
}