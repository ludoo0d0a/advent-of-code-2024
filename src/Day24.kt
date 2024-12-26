import java.util.*
import kotlin.math.max

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

            val content = readFileContent("Day24_input")
            println("Part 2: ${Day24(input).part2(content)}")
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


    private fun part2(input: String): String {
        val inputWires = mutableMapOf<String, Int>()
        val formulas = mutableMapOf<String, String>()
        val zWires = mutableSetOf<String>()
        val allWires = mutableSetOf<String>()
        var highBit: Int? = null

        val (lines1, lines2) = input.split("\n\n")
        for (line in lines1.split("\n")) {
            val (wire, valueStr) = line.split(": ")
            val value = valueStr.toInt()
            inputWires[wire] = value
            allWires += wire
        }
        for (line in lines2.split("\n")) {
            val (formula, wire) = line.split(" -> ")
            formulas[wire] = formula
            allWires += wire
            if (wire.startsWith("z")) {
                zWires += wire
                val index = wire.removePrefix("z").toInt()
                highBit = if (highBit == null) index else max(index, highBit)
            }
        }

        val argsHighBit = highBit!! - 1
        val resHighBit = highBit

        val cachedWires = mutableMapOf<String, Int>()
        val involvedWires = mutableSetOf<String>()
        fun calcValue(w: String, swaps: Map<String, String>): Int? {
            val wire = if (w in swaps) {
                swaps[w]!!
            } else {
                w
            }
            involvedWires += wire
            if (wire in inputWires) {
                return inputWires[wire]!!
            }
            if (wire in cachedWires) {
                if (cachedWires[wire] == -1) {
                    return null
                }
                return cachedWires[wire]!!
            }
            cachedWires[wire] = -1
            if (wire in formulas) {
                val formula = formulas[wire]!!
                when {
                    formula.contains(" AND ") -> {
                        val (w1, w2) = formula.split(" AND ")
                        val value1 = calcValue(w1, swaps) ?: return null
                        val value2 = calcValue(w2, swaps) ?: return null
                        val value = value1 and value2
                        cachedWires[wire] = value
                        return value
                    }

                    formula.contains(" OR ") -> {
                        val (w1, w2) = formula.split(" OR ")
                        val value1 = calcValue(w1, swaps) ?: return null
                        val value2 = calcValue(w2, swaps) ?: return null
                        val value = value1 or value2
                        cachedWires[wire] = value
                        return value
                    }

                    formula.contains(" XOR ") -> {
                        val (w1, w2) = formula.split(" XOR ")
                        val value1 = calcValue(w1, swaps) ?: return null
                        val value2 = calcValue(w2, swaps) ?: return null
                        val value = value1 xor value2
                        cachedWires[wire] = value
                        return value
                    }

                    else -> throw IllegalStateException("invalid formula $formula for $wire")
                }
            }
            throw IllegalStateException("no formula for $wire")
        }

        fun xWires() = inputWires.filterKeys { it.startsWith("x") }.keys
        fun yWires() = inputWires.filterKeys { it.startsWith("y") }.keys

        fun formatZName(index: Int) = "z${String.format("%02d", index)}"
        fun formatXName(index: Int) = "x${String.format("%02d", index)}"
        fun formatYName(index: Int) = "y${String.format("%02d", index)}"

        fun startTests(allSwaps: Map<String, String>, checkBit: Int): Boolean {
            for (xValue in 0..3) {
                for (yValue in 0..3) {

                    var xBinaryString = ""
                    var rx = 0
                    while (rx <= argsHighBit) {
                        if (rx == checkBit - 1) {
                            val xBin = xValue.toString(2)
                            xBinaryString = xBin + xBinaryString
                            rx += xBin.length
                        } else {
                            xBinaryString = "0$xBinaryString"
                            ++rx
                        }
                    }

                    val x = java.lang.Long.parseLong(xBinaryString, 2)
                    for (index in 0..argsHighBit) {
                        inputWires[formatXName(index)] = xBinaryString[xBinaryString.lastIndex - index] - '0'
                    }

                    var yBinaryString = ""
                    var ry = 0
                    while (ry <= argsHighBit) {
                        if (ry == checkBit - 1) {
                            val yBin = yValue.toString(2)
                            yBinaryString = yBin + yBinaryString
                            ry += yBin.length
                        } else {
                            yBinaryString = "0$yBinaryString"
                            ++ry
                        }
                    }

                    val y = java.lang.Long.parseLong(yBinaryString, 2)
                    for (index in 0..argsHighBit) {
                        inputWires[formatYName(index)] = yBinaryString[yBinaryString.lastIndex - index] - '0'
                    }

                    val z = x + y
                    var expectedBinaryString = z.toString(2)
                    for (index in expectedBinaryString.length..resHighBit) {
                        expectedBinaryString = "0$expectedBinaryString"
                    }
                    cachedWires.clear()
                    involvedWires.clear()
                    var actualBinaryString = ""
                    for (zWire in zWires.toList().sortedDescending()) {
                        val value = calcValue(zWire, allSwaps) ?: return false
                        actualBinaryString += value
                    }

                    val hasCorruptedBits = expectedBinaryString != actualBinaryString
                    if (hasCorruptedBits) {
                        return false
                    }
                }
            }
            return true
        }

        fun findGoodSwaps(allSwaps: Map<String, String>, checkBit: Int): Set<Pair<String, String>> {
            cachedWires.clear()
            involvedWires.clear()
            calcValue(formatZName(checkBit), allSwaps)

            val possibleBrokenWires = involvedWires.toMutableSet()
            possibleBrokenWires += formatZName(checkBit + 1)

            cachedWires.clear()
            involvedWires.clear()
            for (i in 0..<checkBit) {
                calcValue(formatZName(i), allSwaps)
            }
            val correctWires = involvedWires.toSet()

            possibleBrokenWires -= correctWires
            possibleBrokenWires -= xWires()
            possibleBrokenWires -= yWires()
            possibleBrokenWires -= allSwaps.keys

            val possibleFixingWires = mutableSetOf<String>().apply {
                this += allWires
                this -= correctWires
                this -= xWires()
                this -= yWires()
                this -= allSwaps.keys
                this -= possibleBrokenWires
                this -= zWires
            }

            val goodSwaps = mutableSetOf<Pair<String, String>>()
            for (ps1 in possibleBrokenWires) {
                for (ps2 in possibleFixingWires) {
                    val success = startTests(mutableMapOf<String, String>().apply {
                        this += allSwaps
                        this += mapOf(ps1 to ps2, ps2 to ps1)
                    }, checkBit)
                    if (success) {
                        goodSwaps += ps1 to ps2
                    }
                }
            }

            return goodSwaps
        }

        fun findAllSwaps(allSwaps: Map<String, String>, r: Int): Map<String, String> {
            println("considering $allSwaps $r")

            if (r > argsHighBit) {
                return allSwaps
            }

            val success = startTests(allSwaps, r)
            if (success) {
                return findAllSwaps(allSwaps, r + 1)
            }

            val goodSwaps = findGoodSwaps(allSwaps, r)

            for ((swap1, swap2) in goodSwaps) {
                val checkAllSwaps = mutableMapOf<String, String>().apply {
                    this += allSwaps;this[swap1] = swap2;this[swap2] = swap1
                }
                val result = findAllSwaps(checkAllSwaps, r + 1)
                if (result.isNotEmpty()) {
                    return result
                }
            }

            return emptyMap()
        }

        val allSwaps = findAllSwaps(emptyMap(), 0)
        println("solution is $allSwaps")

        for (r in 0..argsHighBit) {
            val success = startTests(allSwaps, r)
            if (!success) {
                println("tests failed for bit $r")
            }
        }

        return allSwaps.keys.toList().sorted().joinToString(",")
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
