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
            moves.forEachIndexed{ i, move ->
                warehouse.moveRobot(move)
                if (DEBUG) {
                    println("$i - After move $move:")
                    warehouse.printMap()
                }
            }
            return warehouse.calculateGPSSum()
        }

        fun part2(input: List<String>): Long {
            val (map, moves) = parseInput(input)
            val scaledMap = scaleMap(map)
            val warehouse = Warehouse(scaledMap)
            if (DEBUG)
                warehouse.printMap()
            moves.forEachIndexed{ i, move ->
                warehouse.moveRobot(move)
                if (DEBUG) {
                    println("$i - After move $move:")
                    warehouse.printMap()
                }
            }
            return warehouse.calculateGPSSum()
        }

        private fun parseInput(input: List<String>) =
            Pair(input.takeWhile { it.isNotEmpty() }, input.last().toList())

        private fun scaleMap(map: List<String>) = map.map { line ->
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

    data class Position(val x: Int, val y: Int) {
        operator fun plus(other: Position) = Position(x + other.x, y + other.y)
        operator fun minus(other: Position) = Position(x - other.x, y - other.y)
    }

    data class Wall(val position: Position)
    data class Box(var leftPosition: Position, var rightPosition: Position) {
        fun move(movement: Position) {
            leftPosition = leftPosition + movement
            rightPosition = rightPosition + movement
        }

        fun isAdjacent(other: Box, direction: Position): Boolean {
            return when {
                direction.x > 0 -> this.rightPosition.x + 1 == other.leftPosition.x
                direction.x < 0 -> this.leftPosition.x - 1 == other.rightPosition.x
                direction.y > 0 -> this.rightPosition.y + 1 == other.leftPosition.y
                direction.y < 0 -> this.leftPosition.y - 1 == other.rightPosition.y
                else -> false
            } && this.leftPosition.x == other.leftPosition.x || this.leftPosition.y == other.leftPosition.y
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

        private fun pushBoxes(startPosition: Position, movement: Position) {
            val firstBox = boxes.find { it.leftPosition == startPosition || it.rightPosition == startPosition }
                ?: return

            val boxesToMove = mutableListOf(firstBox)
            var lastBoxes = listOf(firstBox)

            while (true) {
                val nextBoxes = boxes.filter { box ->
                     lastBoxes.all { lastBox ->
                         lastBox.isAdjacent(box, movement)
                     }
                 }
                lastBoxes = nextBoxes.filter { nextBox ->
                    !boxesToMove.contains(nextBox)
                }
                if (lastBoxes.isEmpty())
                    break;
                boxesToMove.addAll(lastBoxes)
            }

            if (canMoveAllTo(movement, boxesToMove)) {
                boxesToMove.reversed().forEach { it.move(movement) }
                robot.position = robot.position + movement
            }
        }

        private fun canMoveAllTo(movement: Position, boxesToMove: MutableList<Box>): Boolean {
            val lastBoxes = findLastBoxes(boxesToMove, movement)

            if (movement.x > 0)
                return lastBoxes.all{ box -> canMoveTo(box.rightPosition + movement) }
            else if (movement.x < 0)
                return lastBoxes.all{ box -> canMoveTo(box.leftPosition + movement) }
            else if  (movement.y > 0)
                return lastBoxes.all{ box -> canMoveTo(box.leftPosition + movement) && canMoveTo(box.rightPosition + movement)}
            else if  (movement.y < 0)
                return lastBoxes.all{ box -> canMoveTo(box.leftPosition + movement) && canMoveTo(box.rightPosition + movement)}
            else
                return false
        }
        private fun findLastBoxes(boxes: MutableList<Box>, movement: Position) : List<Box>{
            return boxes.filter { box ->
                val positionToCheck = if (movement.x > 0) {
                    box.rightPosition + movement
                } else if (movement.x < 0) {
                    box.leftPosition + movement
                } else if (movement.y > 0) {
                    box.rightPosition + movement
                } else {
                    box.leftPosition + movement
                }

                // A box is "last" if there are no other boxes in the movement direction
                !boxes.any { otherBox ->
                    otherBox != box &&
                            (otherBox.leftPosition == positionToCheck || otherBox.rightPosition == positionToCheck)
                }
            }
        }


        private fun canMoveTo(position: Position): Boolean {
            if (position.x !in 0 until width || position.y !in 0 until height) {
                return false
            }

            return !walls.any { it.position == position } &&
                    !boxes.any { it.leftPosition == position || it.rightPosition == position }
        }


        private fun isBoxAt(position: Position): Boolean {
            return boxes.any { it.leftPosition == position || it.rightPosition == position }
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
