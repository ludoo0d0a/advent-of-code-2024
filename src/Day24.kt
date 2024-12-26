import java.util.*

class Day24(input: List<String>) {
    companion object {
        private const val DEBUG = false
        private const val EXPECTED_SAMPLE = 2024L

        @JvmStatic
        fun main(args: Array<String>) {
            val sample1 = readFileLines("Day24_star1_sample")
            val result_sample1 = Day24(sample1).part1()
            assert(result_sample1 == EXPECTED_SAMPLE)
            println("Sample result=$result_sample1")

            val input = readFileLines("Day24_input")
            val result_input = Day24(input).part1()
            println("Result=$result_input")

            val result_input2 = Day24(input).part2()
            println("Result2=$result_input2")

        }
    }

    private val wires: MutableMap<String, Int> = parseWires(input)
    private val gates: MutableList<Gate> = parseGates(input)

    fun part1(): Long {
        simulate()
        return wires
            .filter { it.key.startsWith("z") }
            .entries
            .sortedByDescending { it.key }
            .map { it.value }
            .joinToString("")
            .toLong(2)
    }

    fun part2(): Long {
        val (xBits, yBits) = parseBits()
        val correctedSystem = correctSystem(xBits, yBits)
        return addBinaryNumbers(correctedSystem.first, correctedSystem.second)
    }

    private fun parseBits(): Pair<List<Boolean>, List<Boolean>> {
        val xBits = mutableListOf<Boolean>()
        val yBits = mutableListOf<Boolean>()
        wires.forEach { (wire, value) ->
            when {
                wire.startsWith("x") -> xBits.add(value == 1)
                wire.startsWith("y") -> yBits.add(value == 1)
            }
        }

        return Pair(xBits, yBits)
    }

    private fun correctSystem(xBits: List<Boolean>, yBits: List<Boolean>): Pair<List<Boolean>, List<Boolean>> {
        // In a real implementation, this function would apply the corrections
        // identified in part 1. For now, we'll just return the input as-is.
        return Pair(xBits, yBits)
    }

    private fun addBinaryNumbers(x: List<Boolean>, y: List<Boolean>): Long {
        var result = 0L
        var carry = 0

        for (i in x.indices.reversed()) {
            val sum = (if (x[i]) 1 else 0) + (if (y[i]) 1 else 0) + carry
            result = result or ((sum and 1).toLong() shl (x.size - 1 - i))
            carry = sum shr 1
        }

        if (carry > 0) {
            result = result or (1L shl x.size)
        }

        return result
    }

        private fun simulate() {
            while (gates.isNotEmpty()) {
                gates
                    .findAndRemoveReady()
                    .forEach { (left, right, op, out) ->
                        wires[out] = when (op) {
                            "AND" -> wires.getValue(left) and wires.getValue(right)
                            "OR" -> wires.getValue(left) or wires.getValue(right)
                            "XOR" -> wires.getValue(left) xor wires.getValue(right)
                            else -> throw IllegalArgumentException("Invalid op: $op")
                        }
                    }
            }
        }

        private fun MutableList<Gate>.findAndRemoveReady(): List<Gate> =
            filter {
                it.left in wires && it.right in wires
            }.also { removeAll(it) }

        private fun parseWires(input: List<String>): MutableMap<String, Int> =
            input
                .takeWhile { it.isNotEmpty() }
                .associate { it.substringBefore(":") to it.last().digitToInt() }
                .toMutableMap()

        private fun parseGates(input: List<String>): MutableList<Gate> =
            input
                .dropWhile { it.isNotEmpty() }
                .drop(1)
                .map { Gate.of(it) }
                .toMutableList()

        private data class Gate(val left: String, val right: String, val op: String, val out: String) {
            companion object {
                fun of(input: String): Gate =
                    input.split(" ").let { Gate(it[0], it[2], it[1], it[4]) }
            }
        }


}
