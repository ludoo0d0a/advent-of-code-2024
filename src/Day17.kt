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

            // star 2 sample
//            runSample("", 2)

            val input = readFileLines("Day17_input")
//            val result_input = part1(input)
//            dbg("Result=$result_input")
            
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
            println("sample$id result=${result.output}")
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
            val output: String,
            val registers: IntArray,
            val visited: Map<Pair<Int, IntArray>, Boolean >?
            )

        fun part2(input: List<String>): Long {
            val program = parseProgram(input)
//            return findLowestInitialValue(program)
            return findSequencePattern(program)
        }

        fun findSequencePattern(program: IntArray): Long {
            var currentA = 1L
            var sequence = mutableListOf<Int>()

            for (length in 1..program.size/2) {
                // Try values around currentA * 8
                val baseValue = currentA * 8
                for (offset in -10..10) {
                    val testA = baseValue + offset
                    val registers = intArrayOf(testA.toInt(), 0, 0)
                    val result = runSoftware(program, registers)

                    // Check if output matches our target sequence length
                    val outputs = result.output.split(",").map { it.toInt() }
                    if (outputs.size == length && outputs == program.take(length)) {
                        currentA = testA
                        sequence = outputs.toMutableList()
                        println("Found match for length $length: A=$testA, sequence=${outputs.joinToString(",")}")
                        break
                    }
                }
            }

            return currentA
        }


        fun part1(input: List<String>): Result {
            val registers = parseRegisters(input)
            val program = parseProgram(input)
            return runSoftware(program, registers)
        }

        private fun runSoftware(program: IntArray, registers: IntArray): Result {
            var output = mutableListOf<Int>()
            var instructionPointer = 0
            var iteration = 0

            val localVisited = mutableMapOf<Pair<Int, IntArray>, Boolean >()

            while (instructionPointer < program.size) {
                // Check if we already processed this register state
                //println("$instructionPointer ; ${registers.joinToString(",")}")
//                if (visited.containsKey(Pair(instructionPointer,registers))) {
//                    return Result("", registers, null);
//                }

                val opcode = program[instructionPointer]
                val operand = program[instructionPointer + 1]

//                dbg("\nIteration ${iteration++}")
//                dbg("IP=$instructionPointer, Opcode=$opcode, Operand=$operand")
//                dbg("Registers before: [${registers.joinToString()}]")

                when (opcode) {
                    0 -> {
                        val shift = getComboValue(operand, registers)
                        // r / (2^B)
                        registers[0] = (registers[0] / (1L shl shift)).toInt()
                      //  dbg("Operation: R0 = R0 / (1 << $shift)")
                    }

                    1 -> {
                        registers[1] = registers[1] xor operand
                      //  dbg("Operation: R1 = R1 XOR $operand")
                    }

                    2 -> {
                        val combo = getComboValue(operand, registers)
                        registers[1] = combo % 8
                      //  dbg("Operation: R1 = $combo % 8")
                    }

                    3 -> {
                        dbg("Operation: JNZ R0 -> $operand")
                        if (registers[0] != 0) {
                            instructionPointer = operand
                      //      dbg("Jump taken to $operand")
                            continue
                        }
                    }

                    4 -> {
                        registers[1] = registers[1] xor registers[2]
                       // dbg("Operation: R1 = R1 XOR R2")
                    }

                    5 -> {
                        val value = getComboValue(operand, registers) % 8
                        output.add(value)
                       // dbg("Operation: OUTPUT ${value}")
                    }

                    6 -> {
                        val shift = getComboValue(operand, registers)
                        registers[1] = (registers[0] / (1L shl shift)).toInt()
                       // dbg("Operation: R1 = R0 / (1 << $shift)")
                    }

                    7 -> {
                        val shift = getComboValue(operand, registers)
                        registers[2] = (registers[0] / (1L shl shift)).toInt()
                       // dbg("Operation: R2 = R0 / (1 << $shift)")
                    }
                }

                //dbg("Registers after: [${registers.joinToString()}]")
                instructionPointer += 2

//                localVisited.put(Pair(instructionPointer,registers), true)
            }
//            return output
            val out = output.joinToString(",")

            return Result(out, registers, localVisited);
        }

        private fun dbg(text: String) {
            if (DEBUG)
                println(text)

        }

        private fun parseRegisters(input: List<String>): IntArray {
            return input.take(3).map { it.split(": ")[1].toInt() }.toIntArray()
        }

        private fun parseProgram(input: List<String>): IntArray {
            val programLine = input.filter{it.startsWith("Program")}.first()
            return programLine.split(": ")[1].split(",").map { it.toInt() }.toIntArray()
        }
        private fun parseExpected(input: List<String>): String {
            val expectedLine = input.filter{it.startsWith("Expected")}.first()
            return expectedLine.split(": ").getOrNull(1).orEmpty()
        }
        private fun parseExpectedRegisters(input: List<String>): Map<String, Int> {
            val registers = input
                .filter{it.startsWith("out-Register")}
                .map { it.substringAfter("out-Register ").split(": ") }
                .associate { list ->
                    list[0] to list[1].toInt()
                }
            return registers
        }

        private fun checkRegisters(registers: IntArray, expectedRegisters: Map<String, Int>) {
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


        private fun getComboValue(operand: Int, registers: IntArray): Int {
            return when (operand) {
                in 0..3 -> operand
                4 -> registers[0]
                5 -> registers[1]
                6 -> registers[2]
                else -> throw IllegalArgumentException("Invalid combo operand: $operand")
            }
        }

        private fun expect(actual: String, expected: String) {
            if (actual != expected) {
                throw AssertionError("Expected $expected, but got $actual")
            }
        }

        private fun findLowestInitialValue(program: IntArray): Long {
            val scope = CoroutineScope(Dispatchers.Default)
            val initialValue = 15400 * MILLION
            val batchSize = 500 * MILLION
            val logSize = 10 * MILLION
            val jobs = mutableListOf<Deferred<Long?>>()
            val programRef = program.joinToString(",")
            val processors = Runtime.getRuntime().availableProcessors()
            repeat(processors) { processor ->
                jobs.add(scope.async {
                    val min = initialValue + processor * batchSize + 1
                    var max = initialValue + (processor+1) * batchSize + 1
                    val range = " from ${min/MILLION} to ${max/MILLION}"
                    var a = min
                    while (a < max) {
                        val registers = makeRegisters(a)
                        val r = runSoftware(program, registers)
                        if (r.output == programRef) {
                            println("Found >>>>> $a")
                            exitProcess(0)
                            return@async a
                        }
                        //a += processors
                        ++a
                        if (a % logSize == 0L) {
                            println("Processor $processor  - Processed up to ${a/MILLION} - $range")
                        }
                    }
                    println("End of processor $processor  -  reached ${a/MILLION} M")
                    return@async null;
                })
            }

            return runBlocking {
                jobs.awaitAll().filterNotNull().min()
            }
        }


        private fun makeRegisters(initialA: Long): IntArray {
            val registers: IntArray = listOf(initialA, 0, 0).map { it.toInt() }.toIntArray()
            return registers
        }

    }
}
// end-of-code
