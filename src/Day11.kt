import java.io.File

/*
Day 11: Plutonian Pebbles
*/
class Day11 {


    fun main() {
        val input = readInput("Day11_input")
        val initialStones = input.first().split(" ").map { it.toLong() }

        println(part1(initialStones))
    }

    fun part1(initialStones: List<Long>): Int {
        var stones = initialStones
        repeat(25) {
            stones = stones.flatMap { transformStone(it) }
        }
        return stones.size
    }

    fun transformStone(stone: Long): List<Long> {
        return when {
            stone == 0L -> listOf(1)
            stone.toString().length % 2 == 0 -> splitStone(stone)
            else -> listOf(stone * 2024)
        }
    }

    fun splitStone(stone: Long): List<Long> {
        val digits = stone.toString()
        val mid = digits.length / 2
        val left = digits.substring(0, mid).toLong()
        val right = digits.substring(mid).toLong()
        return listOf(left, right)
    }

}

fun main() {
    Day11().main()
}
