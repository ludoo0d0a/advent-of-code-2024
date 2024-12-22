
/*
--- Day 22 star 1 ---

Day 22: Monkey Market

This implementation solves the problem efficiently by:

1. Using `Long` instead of `Int` to avoid overflow issues.
2. Implementing the secret number generation process as described in the problem.
3. Optimizing the `mixAndPrune` function to perform both operations in a single step.
4. Using a list to store and return the generated secret numbers, which allows for easy access to the 2000th number.

The `part1` function processes each initial secret number, generates 2000 new secret numbers for each, takes the 2000th number, and sums these numbers to produce the final result.

The `DEBUG` flag is set to `false` by default, as requested. If set to `true`, you could add debug print statements to show intermediate results.

Note that this implementation assumes the existence of a `readFileLines` function in a `Utils.kt` file, as mentioned in the problem description. Make sure this function is available in your project.
*/
import java.util.*

// kotlin:Day22.kt
import java.util.*

/**
 * Day 22: Monkey Exchange Market
 *
 * Problem: Predict secret numbers for buyers in the Monkey Exchange Market.
 * Each buyer starts with an initial secret number and generates new ones using a specific process.
 * The task is to simulate 2000 new secret numbers for each buyer and sum the 2000th number for all buyers.
 */

class Day22 {
    companion object {
        private const val DEBUG = false
        private const val EXPECTED_SAMPLE = 37327623L

        @JvmStatic
        fun main(args: Array<String>) {

            val test1 = generateSecretNumbers(123L, 10)
            println("test1 result=$test1")

//            val sample1 = readFileLines("Day22_star1_sample")
//            val result_sample1 = part1(sample1)
//            assert(result_sample1==EXPECTED_SAMPLE)
//            println("sample result=$result_sample1")

            
            val input = readFileLines("Day22_input")
            val result_input = part1(input)
            println("Result=$result_input")
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

        private fun mixAndPrune0(secretNumber: Long, valueToMix: Long): Long {
            return (secretNumber xor valueToMix) % 16777216
        }

        private fun expect(actual: Long, expected: Long) {
            if (actual != expected) {
                throw AssertionError("Expected $expected but got $actual")
            }
        }
    }
}
// end-of-code
