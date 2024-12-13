class Day13 {
    data class ClawMachine(
        val buttonA: Pair<Long, Long>,  // X,Y movements for button A
        val buttonB: Pair<Long, Long>,  // X,Y movements for button B
        val prize: Pair<Long, Long>     // Prize X,Y coordinates
    )

    private fun parseMachine(input: List<String>): List<ClawMachine> {
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
                .map { it.toLong() }
                .let { Pair(it[0], it[1]) }

            ClawMachine(buttonA, buttonB, prize)
        }
    }

    private fun canWinPrize(machine: ClawMachine): Pair<Long, Long>? {
        // Try all combinations of button presses up to 100
        for (a in 0..100) {
            for (b in 0..100) {
                val x = a.toLong() * machine.buttonA.first + b.toLong() * machine.buttonB.first
                val y = a.toLong() * machine.buttonA.second + b.toLong() * machine.buttonB.second
                
                if (x == machine.prize.first && y == machine.prize.second) {
                    return Pair(a.toLong(), b.toLong())
                }
            }
        }
        return null
    }

    private fun calculateTokens(buttonA: Long, buttonB: Long): Long {
        return (buttonA * 3) + buttonB
    }

    fun star1(input: Array<String>): String {
        val machines = parseMachine(input.toList())
        var totalTokens = 0L

        machines.forEach { machine ->
            val solution = canWinPrize(machine)
            if (solution != null) {
                totalTokens += calculateTokens(solution.first, solution.second)
            }
        }

        return totalTokens.toString()
    }

    private fun gcd(a: Long, b: Long): Long {
        if (b == 0L) return a
        return gcd(b, a % b)
    }

    private fun findSolution(machine: ClawMachine): Pair<Long, Long>? {
        // Convert to Long to handle large numbers
        val a = machine.buttonA.first.toLong()
        val b = machine.buttonA.second.toLong()
        val c = machine.buttonB.first.toLong()
        val d = machine.buttonB.second.toLong()
        val targetX = (machine.prize.first + 10000000000000L)
        val targetY = (machine.prize.second + 10000000000000L)

        // Solve system of equations:
        // ax + cy = targetX
        // bx + dy = targetY

        val det = a * d - b * c
        if (det == 0L) return null // No solution exists

        // Check if solution exists
        val gcdXMoves = gcd(a, c)
        val gcdYMoves = gcd(b, d)

        if (targetX % gcdXMoves != 0L || targetY % gcdYMoves != 0L) {
            return null
        }

        // Find particular solution
        val x0 = (targetX * d - targetY * c) / det
        val y0 = (targetY * a - targetX * b) / det

        if (x0 < 0 || y0 < 0) return null

        return Pair(x0, y0)
    }

    fun star2(input: Array<String>): String {
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


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val day13 = Day13()
            val input = readLines("Day13_input").toTypedArray()
            println(day13.star1(input))
            println(day13.star2(input))
        }
    }
}
