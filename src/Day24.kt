import java.util.*

class Day24 {
    companion object {
        private const val DEBUG = false
        private const val EXPECTED_SAMPLE = 2024L

        @JvmStatic
        fun main(args: Array<String>) {
            val sample1 = readFileLines("Day24_star1_sample")
//            val result_sample1 = part1(sample1)
//            expect(result_sample1, EXPECTED_SAMPLE)
//            println("Sample result=$result_sample1")

            val input = readFileLines("Day24_input")
            val result_input = part1(input)
            println("Result=$result_input")
        }

        private fun part1(input: List<String>): Long {
            val initialValues = mutableMapOf<String, Boolean>()
            val gates = mutableListOf<Gate>()
            var readingInitialValues = true

            for (line in input) {
                if (line.isBlank()) {
                    readingInitialValues = false
                    continue
                }

                if (readingInitialValues) {
                    val (wire, value) = line.split(": ")
                    initialValues[wire] = value.toInt() == 1
                } else {
                    val parts = line.split(" -> ")
                    val inputs = parts[0].split(" ")
                    val output = parts[1]
                    when {
                        "AND" in inputs -> gates.add(Gate(inputs[0], inputs[2], output, GateType.AND))
                        "OR" in inputs -> gates.add(Gate(inputs[0], inputs[2], output, GateType.OR))
                        "XOR" in inputs -> gates.add(Gate(inputs[0], inputs[2], output, GateType.XOR))
                    }
                }
            }

            val wireValues = initialValues.toMutableMap()
            var changed = true
            while (changed) {
                changed = false
                for (gate in gates) {
                    if (gate.canEvaluate(wireValues)) {
                        val result = gate.evaluate(wireValues)
                        if (wireValues[gate.output] != result) {
                            wireValues[gate.output] = result
                            changed = true
                        }
                    }
                }

                if (DEBUG) {
                    println("\nCurrent wire values:")
                    wireValues.forEach { (wire, value) ->
                        println("$wire: ${if (value) "\u001B[32m1\u001B[0m" else "\u001B[31m0\u001B[0m"}")
                    }
                }
            }

            val zWires = wireValues.filter { it.key.startsWith("z") }.toSortedMap()
            val binaryResult = zWires.values.joinToString("") { if (it) "1" else "0" }
            return binaryResult.toLong(2)
        }

        private fun expect(actual: Long, expected: Long) {
            if (actual != expected) {
                throw AssertionError("Expected $expected but got $actual")
            }
        }

        private data class Gate(val input1: String, val input2: String, val output: String, val type: GateType) {
            fun canEvaluate(wireValues: Map<String, Boolean>): Boolean {
                return wireValues.containsKey(input1) && wireValues.containsKey(input2)
            }

            fun evaluate(wireValues: Map<String, Boolean>): Boolean {
                val value1 = wireValues[input1] ?: false
                val value2 = wireValues[input2] ?: false
                return when (type) {
                    GateType.AND -> value1 && value2
                    GateType.OR -> value1 || value2
                    GateType.XOR -> value1 xor value2
                }
            }
        }

        private enum class GateType {
            AND, OR, XOR
        }
    }
}
