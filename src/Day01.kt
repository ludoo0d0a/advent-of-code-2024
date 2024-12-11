class Day01 {

    fun parseInput(input: String): Pair<List<Int>, List<Int>> {
        val lines = input.trim().lines()
        val left = mutableListOf<Int>()
        val right = mutableListOf<Int>()

        for (line in lines) {
            val (l, r) = line.trim().split(Regex("\\s+")).map { it.toInt() }
            left.add(l)
            right.add(r)
        }

        return Pair(left, right)
    }

    fun solve(input: String): Int {
        val (left, right) = parseInput(input)

        val sortedLeft = left.sorted()
        val sortedRight = right.sorted()

        return sortedLeft.zip(sortedRight)
            .sumOf { (a, b) -> kotlin.math.abs(a - b) }
    }

    fun solve2(input: String): Int {
        val (left, right) = parseInput(input)

        return left.sumOf { num ->
            val frequency = right.count { it == num }
            num * frequency
        }
    }
    fun main() {
        val body = readFileContent("Day01_test")
        val total = solve(body)
        println("Total distance: $total")
        val similarity = solve2(body)
        println("Similarity score: $similarity")
    }

}

fun main() {
    Day01().main()
}
