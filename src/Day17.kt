class Day17 {
    companion object {
        const val DEBUG = true

        @JvmStatic
        fun main(args: Array<String>) {
            runSample("")
            runSample("1")
            runSample("2")
            runSample("3")
            runSample("4")
            runSample("5")

//            val input = readFileLines("Day17_input")
//            val result_input = part1(input)
//            println("Result=$result_input")
        }

        private fun runSample(id: String) {
            val sample = readFileLines("Day17_star1_sample$id")
            val result = part1(sample)
            val expected = parseExpected(sample)
            val expectedRegisters = parseExpectedRegisters(sample)
            checkRegisters(result.registers, expectedRegisters)
            expect(result.output, expected)
            println("sample$id result=$result.output")
            println("----")
        }

        data class Result(val output: String, val registers: IntArray)

        fun part1(input: List<String>): Result {
            val registers = parseRegisters(input)
            val program = parseProgram(input)

            val output = mutableListOf<Int>()
            var instructionPointer = 0
            var iteration = 0

            while (instructionPointer < program.size) {
                val opcode = program[instructionPointer]
                val operand = program[instructionPointer + 1]

                println("\nIteration ${iteration++}")
                println("IP=$instructionPointer, Opcode=$opcode, Operand=$operand")
                println("Registers before: [${registers.joinToString()}]")

                when (opcode) {
                    0 -> {
                        val shift = getComboValue(operand, registers)
                        // r / (2^B)
                        registers[0] = (registers[0] / (1L shl shift)).toInt()
                        println("Operation: R0 = R0 / (1 << $shift)")
                    }
                    1 -> {
                        registers[1] = registers[1] xor operand
                        println("Operation: R1 = R1 XOR $operand")
                    }
                    2 -> {
                        val combo = getComboValue(operand, registers)
                        registers[1] = combo % 8
                        println("Operation: R1 = $combo % 8")
                    }
                    3 -> {
                        println("Operation: JNZ R0 -> $operand")
                        if (registers[0] != 0) {
                            instructionPointer = operand
                            println("Jump taken to $operand")
                            continue
                        }
                    }
                    4 -> {
                        registers[1] = registers[1] xor registers[2]
                        println("Operation: R1 = R1 XOR R2")
                    }
                    5 -> {
                        val value = getComboValue(operand, registers) % 8
                        output.add(value)
                        println("Operation: OUTPUT ${value}")
                    }
                    6 -> {
                        val shift = getComboValue(operand, registers)
                        registers[1] = registers[0] / (1L shl shift).toInt()
                        println("Operation: R1 = R0 / (1 << $shift)")
                    }
                    7 -> {
                        val shift = getComboValue(operand, registers)
                        registers[2] = registers[0] / (1L shl shift).toInt()
                        println("Operation: R2 = R0 / (1 << $shift)")
                    }
                }

                println("Registers after: [${registers.joinToString()}]")
                instructionPointer += 2
            }

            val out = output.joinToString(",")
            return Result(out, registers);
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
            return expectedLine.split(": ")[1]
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
                val actualValue = registers[register.toInt()]
                if (actualValue != expectedValue) {
                    throw AssertionError("Expected $register to be $expectedValue, but got $actualValue")
                }
            })
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
    }
}
