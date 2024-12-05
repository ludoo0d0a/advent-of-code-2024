fun main() {
    // sample()
    test1()
}

fun test1() {
    val body = readInputBody("Day05_test1")
    val total = solve(body) // -> 4637
    println("Total: $total")
}

fun sample() {
    val body = readInputBody("Day05_test")
    val total = solve(body) // -> 143
    println("Total: $total")
}


data class Rule(val before: Int, val after: Int)

fun parseInput(input: String): Pair<Set<Rule>, List<List<Int>>> {
    val (rulesSection, updatesSection) = input.trim().split("\n\n")

    val rules = rulesSection.lines().map { line ->
        val (before, after) = line.split("|").map { it.toInt() }
        Rule(before, after)
    }.toSet()

    val updates = updatesSection.lines().map { line ->
        line.split(",").map { it.toInt() }
    }

    return rules to updates
}

fun isValidOrder(update: List<Int>, rules: Set<Rule>): Boolean {
    for (i in update.indices) {
        for (j in i + 1 until update.size) {
            val x = update[i]
            val y = update[j]
            if (rules.any { it.before == y && it.after == x }) {
                return false
            }
        }
    }
    return true
}

fun solve(input: String): Int {
    val (rules, updates) = parseInput(input)

    return updates
        .filter { isValidOrder(it, rules) }
        .sumOf { update -> update[update.size / 2] }
}
