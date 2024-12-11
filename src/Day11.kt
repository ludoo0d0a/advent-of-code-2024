class Day11 {
    fun main(): String {
        val input = readLines("Day11_input")
        val initialStones = input.first().split(" ").map { it.toLong() }

        val answer1 = part1(initialStones)
        println("Part 1 (25 iterations): ${answer1}")

        val answer2 = part2(initialStones)
        println("Part 2 (75 iterations): ${answer2}")
        return "$answer1,$answer2"
    }

    fun part1(initialStones: List<Long>): Long {
        var stoneCounts = initialStones.groupingBy { it }.eachCount().mapValues { it.value.toLong() }

        repeat(25) {
            stoneCounts = processStoneMap(stoneCounts)
        }
        return stoneCounts.values.sum()
    }

    fun part2(initialStones: List<Long>): Long {
        var stoneCounts = initialStones.groupingBy { it }.eachCount().mapValues { it.value.toLong() }

        repeat(75) { iteration ->
            stoneCounts = processStoneMap(stoneCounts)

            if ((iteration + 1) % 10 == 0) {
                println("Completed iteration ${iteration + 1}, current stones: ${stoneCounts.values.sum()}")
            }
        }
        return stoneCounts.values.sum()
    }

    private fun processStoneMap(stoneCounts: Map<Long, Long>): Map<Long, Long> {
        val newCounts = mutableMapOf<Long, Long>()

        stoneCounts.forEach { (stone, count) ->
            val transformed = transformStone(stone)
            transformed.forEach { newStone ->
                newCounts[newStone] = newCounts.getOrDefault(newStone, 0L) + count
            }
        }
        return newCounts
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
