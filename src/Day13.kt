class Day13 {
    data class ClawMachine(
        val buttonA: Pair<Int, Int>,  // X,Y movements for button A
        val buttonB: Pair<Int, Int>,  // X,Y movements for button B
        val prize: Pair<Int, Int>     // Prize X,Y coordinates
    )

    private fun parseMachine(input: List<String>): List<ClawMachine> {
        return input.chunked(4).map { chunk ->
            val buttonA = chunk[0].substringAfter("Button A: ")
                .split(", ")
                .map { it.substringAfter("+").toInt() }
                .let { Pair(it[0], it[1]) }

            val buttonB = chunk[1].substringAfter("Button B: ")
                .split(", ")
                .map { it.substringAfter("+").toInt() }
                .let { Pair(it[0], it[1]) }

            val prize = chunk[2].substringAfter("Prize: ")
                .replace("X=", "")
                .replace("Y=", "")
                .split(", ")
                .map { it.toInt() }
                .let { Pair(it[0], it[1]) }

            ClawMachine(buttonA, buttonB, prize)
        }
    }

    private fun canWinPrize(machine: ClawMachine): Pair<Int, Int>? {
        // Try all combinations of button presses up to 100
        for (a in 0..100) {
            for (b in 0..100) {
                val x = a * machine.buttonA.first + b * machine.buttonB.first
                val y = a * machine.buttonA.second + b * machine.buttonB.second
                
                if (x == machine.prize.first && y == machine.prize.second) {
                    return Pair(a, b)
                }
            }
        }
        return null
    }

    private fun calculateTokens(buttonA: Int, buttonB: Int): Int {
        return (buttonA * 3) + buttonB
    }

    fun star1(input: Array<String>): String {
        val machines = parseMachine(input.toList())
        var totalTokens = 0

        machines.forEach { machine ->
            val solution = canWinPrize(machine)
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
        }
    }
}
