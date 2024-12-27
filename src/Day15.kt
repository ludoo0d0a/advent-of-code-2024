class Day15 {
    companion object {
        const val DEBUG = false

        @JvmStatic
        fun main(args: Array<String>) {
//            val sample1 = readFileLines("Day15_star1_sample")
//            val result_sample1 = part1(sample1)
//            println("sample1 result=$result_sample1")
//            expect(result_sample1, 2028L)

//            //val sample2 = readFileLines("Day15_star2_sample")
//            val sample2 = readFileLines("Day15_star1_sample2")
//            val result_sample2 = part2(sample2)
//            println("sample2 result=$result_sample2")
//            expect(result_sample2, 9021L)

            val input = readFileLines("Day15_input")
            val result1_input = part1(input)
            println("Result1=$result1_input") //ko

            val result2_input = part2_ludo(input)
            println("Result2=$result2_input") //ko

            val result1k_input = part1_king(input)
            println("Result1 (by king)=$result1k_input")

            val result2k_input = part2_king(input)
            println("Result2 (by king)=$result2k_input")
        }

        fun part1(input: List<String>): Long {
            val (map, moves) = parseInput(input)
            println("${moves.size} moves ")
            val warehouse = Warehouse(map)
            if (DEBUG)
                warehouse.printMap()
            moves.forEachIndexed{ i, move ->
                warehouse.moveRobot(move, i)
                if (DEBUG) {
                    println("$i - After move $move:")
                    warehouse.printMap(i = i)
                }
            }
            return warehouse.calculateGPSSum()
        }

        fun part2_ludo(input: List<String>): Long {
            val (map, moves) = parseInput(input)
            println("${moves.size} moves ")
            val scaledMap = scaleMap(map)
            val warehouse = Warehouse(scaledMap)
            if (DEBUG)
                warehouse.printMap()
            moves.forEachIndexed{ i, move ->
                warehouse.moveRobot(move, i, 1)
                if (DEBUG) {
                    println("$i - After move $move:")
                    warehouse.printMap(i = i)
                }
            }
            return warehouse.calculateGPSSum()
        }

        private fun parseInput(input: List<String>) =
            Pair(
                input.takeWhile { it.isNotEmpty() },
                input.dropWhile { it.isNotEmpty() }
                    .drop(1)  // Skip the empty line
                    .flatMap { it.toList() }
            )

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


        private fun setupKing(input: List<String>): Triple<MutableMap<Index, Char>, Index, String> {
            val (lines0, moves0) = parseInput(input)
            val warehouse = mutableMapOf<Index, Char>()
            var robot = Index(0, 0)
            lines0.forEachIndexed { y, line ->
                for (x in line.indices) when (val c = line[x]) {
                    '#', 'O' -> warehouse[Index(x, y)] = c
                    '@' -> robot = Index(x, y)
                }
            }
            val moves = moves0.joinToString("")
            return Triple(warehouse, robot, moves)
        }

        fun part1_king(input: List<String>): Int {
            val (warehouse,robot,moves) = setupKing(input)
            return solve(warehouse, robot, moves)
        }

        fun part2_king(input: List<String>): Int {
            val (warehouse,robot,moves) = setupKing(input)
            return solve(warehouse.enlarge(), Index(robot.x * 2, robot.y), moves)
        }

        //source https://github.com/kingsleyadio/adventofcode/blob/main/src/com/kingsleyadio/adventofcode/y2024/Day15.kt
        private fun solve(wh: Map<Index, Char>, robot: Index, moves: String): Int {
            val warehouse = wh.toMutableMap()
            fun move(start: Index, direction: Index): Boolean {
                fun collectDeps(box: Index, map: MutableMap<Index, Char>) {
                    val parts = buildList {
                        add(box)
                        if (warehouse[box] == '[') add(box + Directions.E)
                        if (warehouse[box] == ']') add(box + Directions.W)
                    }
                    val deps = parts.mapNotNull { part -> (part + direction).takeIf { warehouse[it]?.isBox() == true } }
                    for (part in parts) map[part] = warehouse.getValue(part)
                    for (dep in deps) if (dep !in map) collectDeps(dep, map)
                }

                if (start !in warehouse) return true
                if (warehouse[start] == '#') return false
                val deps = buildMap { collectDeps(start, this) }
                for ((part) in deps) if (warehouse[part + direction] == '#') return false
                deps.entries.sortedBy { (k, _) -> k.y * -direction.y + k.x * -direction.x }.forEach { (part, value) ->
                    warehouse[part + direction] = value
                    warehouse.remove(part)
                }
                return true
            }

            moves.fold(robot) { current, move ->
                val direction = when (move) {
                    '^' -> Directions.N
                    '>' -> Directions.E
                    'v' -> Directions.S
                    else -> Directions.W
                }
                val next = current + direction
                if (move(next, direction)) next else current
            }
            var sum = 0
            for ((k, v) in warehouse) if (v.isBox(includeClose = false)) sum += 100 * k.y + k.x
            return sum
        }

        private fun Map<Index, Char>.enlarge() = buildMap {
            for ((k, v) in this@enlarge) {
                put(Index(k.x * 2, k.y), if (v == 'O') '[' else '#')
                put(Index(k.x * 2 + 1, k.y), if (v == 'O') ']' else '#')
            }
        }

        private fun Char.isBox(includeClose: Boolean = true) = this == 'O' || this == '[' || (includeClose && this == ']')



    }



    data class Position(val x: Int, val y: Int) {
        operator fun plus(other: Position) = Position(x + other.x, y + other.y)
        operator fun minus(other: Position) = Position(x - other.x, y - other.y)
    }

    data class Wall(val position: Position)
    data class Box(var leftPosition: Position, var rightPosition: Position) {
        fun move(movement: Position) {
            leftPosition += movement
            rightPosition += movement
        }

        fun isAdjacent(other: Box, direction: Position): Boolean {
            return when {
                direction.x > 0 -> this.rightPosition.x + 1 == other.leftPosition.x && this.leftPosition.y == other.leftPosition.y
                direction.x < 0 -> this.leftPosition.x - 1 == other.rightPosition.x && this.leftPosition.y == other.leftPosition.y
                direction.y > 0 -> Math.abs(this.leftPosition.x - other.leftPosition.x)<=1 && this.leftPosition.y + 1 == other.leftPosition.y
                direction.y < 0 -> Math.abs(this.leftPosition.x - other.leftPosition.x)<=1 && this.leftPosition.y - 1 == other.leftPosition.y
                else -> false
            }
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
                        'O' -> boxes.add(Box(Position(x, y),Position(x, y)))
                        '[' -> if (x + 1 < width && map[y][x + 1] == ']') {
                            boxes.add(Box(Position(x, y), Position(x + 1, y)))
                        }
                        '@' -> robotPosition = Position(x, y)
                    }
                }
            }
            robot = Robot(robotPosition)
        }

        fun moveRobot(direction: Char, i: Int=0, boost: Int = 1) {
            val movement = when (direction) {
                '>' -> Position(1*boost, 0)
                '<' -> Position(-1*boost, 0)
                '^' -> Position(0, -1*boost)
                'v' -> Position(0, 1*boost)
                else -> throw IllegalArgumentException("Invalid direction: $direction")
            }

            val newPosition = robot.position + movement
            when {
                canMoveTo(newPosition) -> {
                    robot.position = newPosition
                    if (DEBUG) printMap(direction = direction, i = i)
                }
                isBoxAt(newPosition) -> {
                    val boxesToMove = findAdjacentBoxes(boxes.first { it.leftPosition == newPosition || it.rightPosition == newPosition }, movement)
                    pushBoxes(newPosition, movement)
                    if (DEBUG) printMap(boxesToMove, direction, i)
                }
            }
        }


        private fun pushBoxes(startPosition: Position, movement: Position) {
            val firstBox = boxes.find { it.leftPosition == startPosition || it.rightPosition == startPosition }
                ?: return

            val boxesToMove = findAdjacentBoxes(firstBox, movement)

            if (canMoveAllTo(movement, boxesToMove)) {
                boxesToMove.reversed().forEach { it.move(movement) }
                robot.position += movement
            }
        }

        private fun findAdjacentBoxes(firstBox: Box, movement: Position): List<Box> {
            val boxChain = mutableSetOf(firstBox)
            var currentBoxes = setOf(firstBox)

            while (true) {
                val nextBoxes = currentBoxes.flatMap { currentBox ->
                    boxes.filter { other ->
                        other !in boxChain && currentBox.isAdjacent(other, movement)
                    }
                }.toSet()


                if (nextBoxes.isEmpty()) break

                boxChain.addAll(nextBoxes)
                currentBoxes = nextBoxes
            }

            return boxChain.toList()
        }


        private fun canMoveAllTo(movement: Position, boxesToMove: List<Box>): Boolean {
            val lastBoxes = findLastBoxes(boxesToMove, movement)

            if (movement.x > 0)
                return lastBoxes.all{ box -> canMoveTo(box.rightPosition + movement) }
            else if (movement.x < 0)
                return lastBoxes.all{ box -> canMoveTo(box.leftPosition + movement) }
            else if  (movement.y != 0)
                return lastBoxes.all{ box -> canMoveTo(box.leftPosition + movement) && canMoveTo(box.rightPosition + movement)}
            else
                return false
        }
        private fun findLastBoxes(boxes: List<Box>, movement: Position) : List<Box>{
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
                val closestX = box.leftPosition.x
                val y = box.leftPosition.y  // y is same for both edges
                100L * y + closestX
            }
        }

        val RESET = "\u001B[0m"
        val RED = "\u001B[31m"
        val GREEN = "\u001B[32m"
        val YELLOW = "\u001B[33m"
        val BLUE = "\u001B[34m"

        fun printMap(boxesToHighlight: List<Box> = emptyList(), direction: Char = '@', i: Int=0) {
            println("Map @$i")
            val display = Array(height) { CharArray(width) { '.' } }
            walls.forEach { display[it.position.y][it.position.x] = '#' }
            boxes.forEach { box ->
                if (box in boxesToHighlight) {
                    display[box.leftPosition.y][box.leftPosition.x] = '('
                    display[box.rightPosition.y][box.rightPosition.x] = ')'
                } else {
                    display[box.leftPosition.y][box.leftPosition.x] = '['
                    display[box.rightPosition.y][box.rightPosition.x] = ']'
                }
            }

            display[robot.position.y][robot.position.x] = direction //'@'

            display.forEach { line ->
                println(line.joinToString("") { char ->
                    when (char) {
                        '#' -> "$YELLOW█$RESET"
                        '@' -> "$RED$char$RESET"
                        direction -> "$RED$direction$RESET"
                        'O' -> "$GREEN█$RESET"
                        '[', ']' -> "$GREEN█$RESET"
                        '(', ')' -> "$BLUE█$BLUE"
                        else -> char.toString()
                    }
                })
            }
            println("")
        }

    }
}
