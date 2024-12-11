class Day11 {
    fun main() {
        val input = readLines("Day11_input")
        val initialStones = input.first().split(" ").map { it.toLong() }

        println("Part 1 (25 iterations): ${part1(initialStones)}")
        println("Part 2 (75 iterations): ${part2(initialStones)}")
    }

    fun part1(initialStones: List<Long>): Int {
        var stones = initialStones
        repeat(25) {
            stones = stones.flatMap { transformStone(it) }
        }
        return stones.size
    }

    fun part2(initialStones: List<Long>): Int {
        var stones = initialStones
        // Process in chunks to avoid excessive memory usage
        repeat(75) { iteration ->
            stones = stones.chunked(10000)
                .flatMap { chunk -> chunk.flatMap { transformStone(it) } }

            // Optional progress tracking
            if ((iteration + 1) % 10 == 0) {
                println("Completed iteration ${iteration + 1}, current stones: ${stones.size}")
            }
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