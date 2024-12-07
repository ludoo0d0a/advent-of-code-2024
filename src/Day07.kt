
class Day07 {

    val DEBUG=false;

    fun main() {
        val input = readInputBody("Day07_test1")

        val result = findValidEquations(input)
        println("Total calibration result: $result")
    }

    data class Equation(val testValue: Long, val numbers: List<Int>)

    fun parseEquation(line: String): Equation {
        val (testValue, numbers) = line.split(": ")
        return Equation(
            testValue.toLong(),
            numbers.split(" ").map { it.toInt() }
        )
    }

    fun evaluateExpression(numbers: List<Int>, operators: List<Char>): Long {
        var result = numbers[0].toLong()
        for (i in operators.indices) {
            when (operators[i]) {
                '+' -> result += numbers[i + 1]
                '*' -> result *= numbers[i + 1]
                '|' -> {
                    // Convert current result and next number to strings, concatenate, then back to Long
                    result = (result.toString() + numbers[i + 1].toString()).toLong()
                }
            }
        }
        return result
    }

    fun findValidEquations(input: String): Long {
        val equations = input.lines().map { parseEquation(it) }
        //val possibleOperators = listOf('+', '*')
        val possibleOperators = listOf('+', '*', '|') // Using '|' to represent '||'

        return equations.sumOf { equation ->
            val operatorCount = equation.numbers.size - 1
            val validCombination = generateSequence {
                possibleOperators
            }.take(operatorCount)
                .fold(listOf(listOf<Char>())) { acc, ops ->
                    acc.flatMap { combo -> ops.map { combo + it } }
                }
                .any { operators ->
                    evaluateExpression(equation.numbers, operators) == equation.testValue
                }

            if (validCombination) equation.testValue else 0L
        }
    }

}

fun main() {
    Day07().main()
}
