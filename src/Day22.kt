
/*
--- Day 22 star 2 ---

Part Two

This implementation includes the `part1` method as requested, and a placeholder for the `part2` method. The `main` function is set up to run `part2` with the input file, as specified.

The core logic is in the `findMaxBananas` function, which generates all possible sequences of price changes and finds the one that yields the maximum number of bananas across all buyers.

The `findBananasForBuyer` function simulates the price changes for a single buyer and checks if the given sequence occurs.

Note that this implementation uses `Long` instead of `Int` to avoid overflow, and it includes a `DEBUG` flag for optional debug output.

To complete the solution, you would need to implement the `part2` method based on the specific requirements of the second part of the problem, which were not provided in the original question.
*/
import java.util.*

// kotlin:Day22.kt
import java.util.*

/**
 * Day 22: Monkey Math
 * This program solves a problem involving secret numbers, price changes, and banana trades.
 * It calculates the maximum number of bananas that can be obtained by finding the optimal
 * sequence of price changes to instruct a monkey when to sell hiding spot information.
 */

class Day22 {
    companion object {
        private const val DEBUG = false
        private const val SEQUENCE_LENGTH = 4
        private const val PRICE_CHANGES = 2000

        @JvmStatic
        fun main(args: Array<String>) {
            val input = readFileLines("Day22_input")
            val result1_input = part1(input)
            println("Result1=$result1_input")

            val result2_input = part2(input)
            println("Result2=$result2_input")
        }

        private fun part1(input: List<String>): Long {
            return input.map { it.toLong() }
                .map { generateSecretNumbers(it, 2000).last() }
                .sum()
        }

        private fun generateSecretNumbers(initial: Long, count: Int): List<Long> {
            val secrets = mutableListOf<Long>()
            var current = initial
            repeat(count) {
                current = nextSecret(current)
                secrets.add(current)
            }
            return secrets
        }

        private fun nextSecret(secret: Long): Long {
            var result = secret
            result = mixAndPrune(result, result * 64)
            result = mixAndPrune(result, result / 32)
            result = mixAndPrune(result, result * 2048)
            return result
        }

        private fun mixAndPrune(secretNumber: Long, valueToMix: Long): Long {
            return prune(mix(secretNumber, valueToMix))
        }
        private fun mix(secretNumber: Long, valueToMix: Long): Long {
            return valueToMix xor secretNumber;
//            return secretNumber xor valueToMix;
        }
        private fun prune( value: Long): Long {
            return value % 16777216
        }

        private fun getScores(prices: List<Long>, changes: List<Long>): Map<List<Long>, Long> {
            val scores = mutableMapOf<List<Long>, Long>()

            for (i in 0..changes.size - 4) {
                val pattern = listOf(changes[i], changes[i + 1], changes[i + 2], changes[i + 3])
                if (!scores.containsKey(pattern)) {
                    scores[pattern] = prices[i + 4]
                }
            }

            return scores
        }

        private fun part2(input: List<String>): Long {
            var p1 = 0L
            val score = mutableMapOf<List<Long>, Long>()

            input.forEach { line ->
                val prices = generateSecretNumbers(line.toLong(), 2000)
                p1 += prices.last()

                val moduloPrices = prices.map { it % 10 }
                val changes = getChanges(moduloPrices)
                val newScores = getScores(moduloPrices, changes)

                newScores.forEach { (pattern, value) ->
                    score[pattern] = score.getOrDefault(pattern, 0L) + value
                }
            }

            println("Part 1: $p1")
            return score.values.maxOrNull() ?: 0L
        }

        private fun getChanges(prices: List<Long>): List<Long> {
            return prices.zipWithNext { a, b -> b - a }
        }


        private fun findMaxBananas(initialSecrets: List<Long>): Long {
            val allSequences = generateAllSequences()
            var maxBananas = 0L

            for (sequence in allSequences) {
                var bananas = 0L
                for (secret in initialSecrets) {
                    bananas += findBananasForBuyer(secret, sequence)
                }
                if (bananas > maxBananas) {
                    maxBananas = bananas
                    if (DEBUG) println("New max: $maxBananas with sequence ${sequence.joinToString()}")
                }
            }

            return maxBananas
        }

        private fun generateAllSequences(): List<List<Int>> {
            val sequences = mutableListOf<List<Int>>()
            for (a in -3..3) {
                for (b in -3..3) {
                    for (c in -3..3) {
                        for (d in -3..3) {
                            sequences.add(listOf(a, b, c, d))
                        }
                    }
                }
            }
            return sequences
        }

        private fun findBananasForBuyer(initialSecret: Long, sequence: List<Int>): Long {
            var secret = initialSecret
            var prevPrice = -1
            val changes = mutableListOf<Int>()

            for (i in 0 until PRICE_CHANGES + 1) {
                val price = (secret % 10).toInt()
                if (prevPrice != -1) {
                    val change = price - prevPrice
                    changes.add(change)
                    if (changes.size > SEQUENCE_LENGTH) {
                        changes.removeAt(0)
                    }
                    if (changes == sequence) {
                        return price.toLong()
                    }
                }
                prevPrice = price
                secret = nextSecret(secret)
            }

            return 0L
        }

        private fun expect(actual: Long, expected: Long) {
            if (actual != expected) {
                throw AssertionError("Expected $expected but got $actual")
            }
        }
    }
}
// end-of-code
