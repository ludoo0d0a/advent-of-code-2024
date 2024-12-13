class Day13 {
    data class ClawMachine(
        val buttonA: Pair<Long, Long>,  // X,Y movements for button A
        val buttonB: Pair<Long, Long>,  // X,Y movements for button B
        val prize: Pair<Long, Long>     // Prize X,Y coordinates
    )

    private fun parseMachine(input: List<String>, addOffset: Boolean = false): List<ClawMachine> {
        val offset = if (addOffset) 10000000000000L else 0L

        return input.chunked(4).map { chunk ->
            val buttonA = chunk[0].substringAfter("Button A: ")
                .split(", ")
                .map { it.substringAfter("+").toLong() }
                .let { Pair(it[0], it[1]) }

            val buttonB = chunk[1].substringAfter("Button B: ")
                .split(", ")
                .map { it.substringAfter("+").toLong() }
                .let { Pair(it[0], it[1]) }

            val prize = chunk[2].substringAfter("Prize: ")
                .replace("X=", "")
                .replace("Y=", "")
                .split(", ")
                .map { it.toLong() + offset }
                .let { Pair(it[0], it[1]) }

            ClawMachine(buttonA, buttonB, prize)
        }
    }

    private fun findSolution(machine: ClawMachine): Pair<Long, Long>? {
        // Using extended Euclidean algorithm to find solution
        val (a1, b1) = machine.buttonA
        val (a2, b2) = machine.buttonB
        val (targetX, targetY) = machine.prize

        // Solve the system:
        // a1*x + a2*y = targetX
        // b1*x + b2*y = targetY

        val det = a1 * b2 - a2 * b1
        if (det == 0L) return null

        val x = (targetX * b2 - targetY * a2) / det
        val y = (targetY * a1 - targetX * b1) / det

        // Check if solution exists with integer coordinates
        if (x * det != (targetX * b2 - targetY * a2) ||
            y * det != (targetY * a1 - targetX * b1)) {
            return null
        }

        // Check if solution is non-negative
        if (x < 0 || y < 0) return null

        return Pair(x, y)
    }

    private fun calculateTokens(buttonA: Long, buttonB: Long): Long {
        return (buttonA * 3L) + buttonB
    }

    fun star1(input: Array<String>): String {
        val machines = parseMachine(input.toList())
        var totalTokens = 0L

        machines.forEach { machine ->
            val solution = findSolution(machine)
            if (solution != null) {
                totalTokens += calculateTokens(solution.first, solution.second)
            }
        }

        return totalTokens.toString()
    }

    fun star2(input: Array<String>): String {
        val machines = parseMachine(input.toList(), true)
        var totalTokens = 0L

        machines.forEach { machine ->
            val solution = findSolution(machine)
            if (solution != null) {
                totalTokens += calculateTokens(solution.first, solution.second)
            }
        }

        return totalTokens.toString()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val day13 = Day13()
            val input = readFileLines("Day13_input").toTypedArray()

            val result1 = day13.star1(input)
            println("Result1=$result1")

            val result2 = day13.star1(input)
            println("Result2=$result2")
        }
    }
}
