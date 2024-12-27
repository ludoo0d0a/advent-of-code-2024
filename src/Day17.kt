import kotlinx.coroutines.*
import kotlin.system.exitProcess

/**
 * I noticed that in each step, the value of the A-register can be expressed as 8X+Y, where X is the value of the A-register from the previous step, and Y is a small additional value.
 * Using this observation, I wrote a simple program to calculate the A-register value for a subsequence of the program. I multiplied the found A-register value by 8 and performed a small brute-force search to find the next subsequence, which was one item longer than the previous one
 * https://github.com/int02h/advent-of-code/blob/main/src/main/kotlin/aoc2024/Day17.kt
 *
 */

/*
--- Day 17 star 2 ---

Part Two

This implementation includes both `part1` and `part2` methods, which are called from the `main` function as requested. The `findLowestInitialValue` function is the core of the solution, which iteratively tries different initial values for register A until it finds one that produces an output matching the input program.

The `runProgram` function simulates the execution of the program with the given initial value for register A. It follows the instructions as described in the problem.

The `displayProgram` function is used to show the current state of the program during debugging, using ANSI color codes for better visibility.

Note that this solution might be slow for large inputs or complex programs. To optimize it further, you could consider:

1. Using a more efficient search algorithm for finding the initial value of A.
2. Implementing memoization to cache results of previous runs.
3. Parallelizing the search process.

Also, make sure to create the input files "Day17_star2_sample" and "Day17_input" with the appropriate content before running the program.
*/

// kotlin:Day17.kt

// Prompt: [The full prompt text has been omitted for brevity]

private const val MILLION = 1000000L

class Day17 {
    companion object {
        const val DEBUG = false

        val visited = mutableMapOf<Pair<Int, IntArray>, Boolean >()

        @JvmStatic
        fun main(args: Array<String>) {
//            runSample("")
//            runSample("1")
//            runSample("2")
//            runSample("3")
//            runSample("4")
//            runSample("5")

//            // star 2 sample
//            runSample("", 2)

            val input = readFileLines("Day17_input")
//            val result_input = part1(input)
//            println("Result1=${result_input.output}")
//
            val result2_input = part2(input)
            println("Result2=$result2_input")
        }

        private fun runSample(id: String, star: Int = 1) {
            val sample = readFileLines("Day17_star${star}_sample$id")
            val result = part1(sample)
            val expected = parseExpected(sample)
            val expectedRegisters = parseExpectedRegisters(sample)
            checkRegisters(result.registers, expectedRegisters)
            if (expected.length>1)
                expect(result.output, expected)
            println("after ${expected.length} iterations, sample$id result=${result.output}")
            println("----")
        }

        private fun runSample2(id: String, star: Int = 2) {
            val sample = readFileLines("Day17_star${star}_sample$id")
            val result = part2(sample)
            val total = parseExpected(sample)
            val expectedRegisters = parseExpectedRegisters(sample)
//            checkRegisters(result.registers, expectedRegisters)
//            if (expected.length>1)
//                expect(result.output, expected)
            println("sample$id total=${total}")
            println("----")
        }

        data class Result(
            val outputs: List<Int>,
            val output: String,
            val registers: LongArray,
            val visited: Map<Pair<Int, IntArray>, Boolean >?
            )

         fun part2(input: List<String>): Long {
            val programArray = parseProgram(input)
            val program=programArray.toList()
            fun decipher(subA: Long, left: Int): Long {
                if (left < 0) return subA
                for (i in 0..7) {
                    //this is the key pattern of the algo
                    val a = subA * 8 + i
                    val outputs = runSoftware(programArray, longArrayOf(a, 0, 0)).outputs
                    if (outputs == program.subList(left, program.size)) {
                        val result = decipher(a, left - 1)
                        if (result != 0L) return result
                    }
                }
                return 0L
            }
            return decipher(0, program.lastIndex)
        }


        fun part1(input: List<String>): Result {
            val registers = parseRegisters(input)
            val program = parseProgram(input)
            return runSoftware(program, registers)
        }

        private fun getComboValue(operand: Int, registers: LongArray): Long {
            return when (operand) {
                in 0..3 -> operand.toLong()
                4 -> registers[0]
                5 -> registers[1]
                6 -> registers[2]
                else -> -1 //throw IllegalArgumentException("Invalid combo operand: $operand")
            }
        }

        private fun runSoftware(program: IntArray, registers: LongArray): Result {
            val output = mutableListOf<Int>()
            var pointer = 0
            val localVisited = mutableMapOf<Pair<Int, IntArray>, Boolean >()
            while (pointer < program.size) {
                val opcode = program[pointer++]
                val operand = program[pointer++]
                val combo = getComboValue(operand, registers)
                when (opcode) {
                    0 -> registers[0] = (registers[0] / (1L shl combo.toInt()))
                    1 -> registers[1] = registers[1] xor operand.toLong()
                    2 -> registers[1] = combo % 8
                    3 -> if (registers[0] != 0L) {
                        pointer = operand
                        //continue
                    }
                    4 -> registers[1] = registers[1] xor registers[2]
                    5 -> output.add((combo % 8).toInt())
                    6 -> registers[1] = (registers[0] / (1L shl combo.toInt()))
                    7 -> registers[2] = (registers[0] / (1L shl combo.toInt()))
                }
            }
            val out = output.joinToString(",")
            return Result(output, out, registers, localVisited);
        }

        private fun dbg(text: String) {
            if (DEBUG)
                println(text)
        }

        private fun parseRegisters(input: List<String>): LongArray {
            return input.take(3).map { it.split(": ")[1].toLong() }.toLongArray()
        }

        private fun parseProgram(input: List<String>): IntArray {
            val programLine = input.filter{it.startsWith("Program")}.first()
            return programLine.split(": ")[1].split(",").map { it.toInt() }.toIntArray()
        }
        private fun parseExpected(input: List<String>): String {
            val expectedLine = input.filter{it.startsWith("Expected")}.first()
            return expectedLine.split(": ").getOrNull(1).orEmpty()
        }
        private fun parseExpectedRegisters(input: List<String>): Map<String, Long> {
            val registers = input
                .filter{it.startsWith("out-Register")}
                .map { it.substringAfter("out-Register ").split(": ") }
                .associate { list ->
                    list[0] to list[1].toLong()
                }
            return registers
        }

        private fun checkRegisters(registers: LongArray, expectedRegisters: Map<String, Long>) {
            expectedRegisters.forEach({ (register, expectedValue) ->
                val registerPos = getRegisterPosition(register)
                val actualValue = registers[registerPos]
                if (actualValue != expectedValue) {
                    throw AssertionError("Expected $register to be $expectedValue, but got $actualValue")
                }
            })
        }
        private fun getRegisterPosition(register: String): Int {
            return when (register) {
                "A" -> 0
                "B" -> 1
                "C" -> 2
                else -> throw IllegalArgumentException("Invalid register: $register")
            }
        }

        private fun expect(actual: String, expected: String) {
            if (actual != expected) {
                throw AssertionError("Expected $expected, but got $actual")
            }
        }


    }
}
// end-of-code
