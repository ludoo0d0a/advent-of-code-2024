import kotlin.math.abs

class Day02 {
    fun main() {
        val body = readInputBody("Day02_test")
        val total = solve(body)
        println("Number of safe reports: $total")
    }

    private fun solve(input: String): Int {
        return input.lines()
            .filter { it.isNotEmpty() }
            .count { line -> isSafeReport(line) }
    }

    private fun isSafeReport(line: String): Boolean {
        val levels = line.split(" ").map { it.toInt() }
        if (levels.size < 2) return true

        // Check if differences are between 1 and 3
        val differences = levels.zipWithNext { a, b -> b - a }
        if (differences.any { abs(it) > 3 || abs(it) < 1 }) return false

        // Check if all differences are consistently increasing or decreasing
        val isIncreasing = differences.all { it > 0 }
        val isDecreasing = differences.all { it < 0 }

        return isIncreasing || isDecreasing
    }
}

fun main() {
    Day02().main()
}
