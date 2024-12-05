import kotlin.math.abs

class Day02 {
    fun main() {
        val body = readInputBody("Day02_test")
        val total = solve(body)
        println("Number of safe reports with Problem Dampener: $total")
    }

    private fun solve(input: String): Int {
        return input.lines()
            .filter { it.isNotEmpty() }
            .count { line -> isSafeReportWithDampener(line) }
    }

    private fun isSafeReportWithDampener(line: String): Boolean {
        val levels = line.split(" ").map { it.toInt() }

        // Check if already safe without removing any level
        if (isSafeReport(levels)) return true

        // Try removing each level one at a time
        for (i in levels.indices) {
            val modifiedLevels = levels.toMutableList()
            modifiedLevels.removeAt(i)
            if (isSafeReport(modifiedLevels)) return true
        }

        return false
    }

    private fun isSafeReport(levels: List<Int>): Boolean {
        if (levels.size < 2) return true

        val differences = levels.zipWithNext { a, b -> b - a }
        if (differences.any { abs(it) > 3 || abs(it) < 1 }) return false

        val isIncreasing = differences.all { it > 0 }
        val isDecreasing = differences.all { it < 0 }

        return isIncreasing || isDecreasing
    }
}

fun main() {
    Day02().main()
}
