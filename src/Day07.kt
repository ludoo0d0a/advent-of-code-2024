/**
 * Explanation of Day07.kt:
 *
 * This code solves a mathematical equation puzzle. Here's how it works:
 *
 * Purpose: The code tries to find valid mathematical equations where you can insert operators (+, *, or concatenation |) between numbers to reach a target value.
 *
 * Inputs:
 *
 * It reads input from a file named "Day07_test1"
 * Each line of input contains two parts:
 * A target number (test value)
 * A list of numbers separated by spaces
 * Outputs:
 * It outputs the sum of all test values where valid equations can be formed
 * If no valid equation can be formed for a test value, it contributes 0 to the sum
 * How it works: The code processes each line through these steps:
 * Splits each input line into a test value and list of numbers
 * Tries different combinations of operators (+, *, |) between the numbers
 * Checks if any combination produces the target test value
 * If a valid combination is found, adds that test value to the running total
 * Key Logic:
 * The parseEquation function breaks down each input line into an Equation object
 * evaluateExpression calculates the result of applying operators to numbers:
 * '+' adds numbers
 * '*' multiplies numbers
 * '|' concatenates numbers (joins them as strings)
 * findValidEquations tries all possible operator combinations until it finds one that works or exhausts all possibilities
 * For example, if the input is "123: 1 2 3", the code would try combinations like:
 *
 * 1 + 2 + 3
 * 1 * 2 * 3
 * 1 | 2 | 3 (joining numbers: 123) Until it finds a combination that equals 123 or determines none exist.
 * The code uses a systematic approach to try all possibilities and keeps track of which equations are valid, ultimately summing up the test values of all valid equations to produce the final answer.
 */
class Day07 {

    val DEBUG=false;

    fun main() {
        val input = readFileContent("Day07_test1")

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
