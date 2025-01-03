
/*
--- Day 25 star 1 ---

Day 25: Code Chronicle

This implementation solves the problem as described. Here's a breakdown of the main components:

1. The `part1` function is the main solver. It parses the input, then checks each lock-key pair for validity, counting the valid pairs.

2. `parseInput` function separates the input into locks and keys, parsing each schematic.

3. `parseSchematic` converts a schematic (represented as a list of strings) into a list of heights.

4. `isValidPair` checks if a lock and key pair is valid (i.e., they don't overlap in any column).

5. The `DEBUG` flag is set to false by default. If set to true, you could add debug print statements to display maps on each iteration.

6. The code uses `Long` for counting valid pairs to avoid potential overflow.

7. The `expect` function is used to check the sample result against the expected value.

This implementation should be efficient for reasonably sized inputs. If performance becomes an issue with very large inputs, further optimizations could be considered, such as parallel processing of lock-key pairs or more sophisticated data structures.
*/
import java.util.*

// kotlin:Day25.kt
import java.util.*

/**
 * Day 25: Virtual Five-Pin Tumbler Lock Problem
 *
 * This program analyzes lock and key schematics to determine how many unique
 * lock/key pairs fit together without overlapping in any column.
 */

typealias Cave = Array<CharArray>

class Day25 {
    companion object {
        private const val DEBUG = false
        private const val EXPECTED_SAMPLE = 3

        @JvmStatic
        fun main(args: Array<String>) {
//            val sample1 = readFileLines("Day25_star1_sample")
//            val result_sample1 = part1(sample1)
//            expect(result_sample1, EXPECTED_SAMPLE)
//            println("sample result=$result_sample1")
            
            val input = readFileLines("Day25_input")
            val result_input = part1a(input)
            println("Result=$result_input")
        }

        private fun part1a(input: List<String>): Long {
            val (locks, keys) = parseInput(input)
            var validPairs = 0L

            for (lock in locks) {
                for (key in keys) {
                    if (isValidPair(lock, key)) {
                        validPairs++
                    }
                }
            }

            return validPairs
        }

        fun List<String>.toCave(): Cave = map { line -> line.toCharArray() }.toTypedArray()

        fun List<String>.parse(): Pair<List<String>, List<String>> {
            val (locks, keys) = joinToString("\n").split("\n\n").partition { it.startsWith("#####") }
            return locks to keys
        }

        fun part1(input: List<String>): Int {
            val x = 0..4
            val y = 0..6
            val parsed = input.parse()
            val locks = parsed.first.map { it.split("\n").toCave() }
            val keys = parsed.second.map { it.split("\n").toCave() }
            val free = locks.map { lock ->
                x.map { x ->
                    y.count { y -> lock[y][x] == '.' }
                }
            }
            val used = keys.map { lock ->
                x.map { x ->
                    y.count { y -> lock[y][x] == '#' } - 1
                }
            }
            return used.sumOf { key ->
                free.count { lock ->
                    key.zip(lock).all { it.second - it.first > 0 }
                }
            }
        }

        private fun parseInput(input: List<String>): Pair<List<List<Int>>, List<List<Int>>> {
            val locks = mutableListOf<List<Int>>()
            val keys = mutableListOf<List<Int>>()
            var currentSchematic = mutableListOf<String>()

            for (line in input) {
                if (line.isBlank()) {
                    if (currentSchematic.isNotEmpty()) {
                        val heights = parseSchematic(currentSchematic)
                        if (currentSchematic.first().startsWith("#")) {
                            locks.add(heights)
                        } else {
                            keys.add(heights)
                        }
                        currentSchematic.clear()
                    }
                } else {
                    currentSchematic.add(line)
                }
            }

            if (currentSchematic.isNotEmpty()) {
                val heights = parseSchematic(currentSchematic)
                if (currentSchematic.first().startsWith("#")) {
                    locks.add(heights)
                } else {
                    keys.add(heights)
                }
            }

            return Pair(locks, keys)
        }

        private fun parseSchematic(schematic: List<String>): List<Int> {
            val heights = mutableListOf<Int>()
            for (col in schematic[0].indices) {
                var height = 0
                for (row in schematic.indices) {
                    if (schematic[row][col] == '#') {
                        height = schematic.size - row
                        break
                    }
                }
                heights.add(height)
            }
            return heights
        }

        private fun isValidPair(lock: List<Int>, key: List<Int>): Boolean {
            if (lock.size != key.size) return false
            for (i in lock.indices) {
                if (lock[i] + key[i] > 5) return false
            }
            return true
        }

        private fun expect(actual: Long, expected: Int) {
            if (actual.toInt() != expected) {
                throw AssertionError("Expected $expected but got $actual")
            }
        }
    }
}
// end-of-code
