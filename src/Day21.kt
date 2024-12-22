
/*
--- Day 21 star 1 ---

Day 21: Keypad Conundrum

This implementation solves the problem as described. Here are some key points:

1. The `part1` function calculates the complexity for each code and sums them up.
2. The `findShortestSequence` function uses a breadth-first search to find the shortest sequence of button presses for each code.
3. The `move` function handles the movement on the numeric keypad based on the directional input.
4. The solution uses a `Queue` to efficiently explore possible sequences.
5. A `visited` set is used to avoid revisiting the same positions, improving efficiency.
6. The code uses `Long` for calculations to avoid potential overflow issues.

The `DEBUG` flag is set to false by default, but you can set it to true if you want to display the map on each iteration (you would need to implement the display logic).

Note that this solution assumes that the input files "Day21_star1_sample" and "Day21_input" are accessible via the `readFileLines` function from `Utils.kt`. Make sure these files are in the correct location and contain the appropriate input data.
*/
import java.util.*

// kotlin:Day21.kt
import java.util.*

/**
 * Day 21: Reindeer-class Starship Door Code
 *
 * This program solves a complex puzzle involving multiple keypads and robots.
 * It calculates the shortest sequence of button presses needed to input
 * specific codes on a numeric keypad through a series of remote-controlled robots.
 */

class Day21 {
    companion object {
        private const val DEBUG = true
        private const val EXPECTED_SAMPLE = 126384L

        private val numericKeypad = listOf(
            "789".toCharArray(),
            "456".toCharArray(),
            "123".toCharArray(),
            " 0A".toCharArray()
        )

        private val directionalKeypad = listOf(
            " ^A".toCharArray(),
            "<v>".toCharArray()
        )

        @JvmStatic
        fun main(args: Array<String>) {
            val sample1 = readFileLines("Day21_star1_sample")
            val result_sample1 = part1(sample1)
            expect(result_sample1, EXPECTED_SAMPLE)
            println("Sample result=$result_sample1")

            val input = readFileLines("Day21_input")
            val result_input = part1(input)
            println("Result=$result_input")
        }

        private fun part1(input: List<String>): Long {
            return input.sumOf { code ->
                val sequence = findShortestSequenceRec(code)
                println("$code: $sequence")
                val numericPart = code.filter { it.isDigit() }.toLong()
                sequence.length * numericPart
            }
        }
        private fun findShortestSequenceRec(code: String): String {
            var start = 'A';
            var res = ""
            (1 until code.length).forEach{
                    val subcode = code.substring(0, it)
                    val end = subcode.last()
                    val seq = findShortestSequence(subcode, start, end)
                    println("$start -> $end : $seq")
                    res += seq +'A';
                    start = end;
            }
            return res
        }


        private fun findShortestSequence(code: String, start: Char = 'A', end: Char = 'A'): String {
            val queue: Queue<Pair<String, List<Char>>> = LinkedList()
            val visited = mutableSetOf<Pair<Int, Int>>()
            queue.offer("" to listOf(start))

            while (queue.isNotEmpty()) {
                val (sequence, path) = queue.poll()
                val currentPos = path.last()
                val (x, y) = getPosition(currentPos)

                //println("$sequence")
                if (path.last() == end) {
                    return sequence
                }

                if (visited.contains(x to y)) continue
                visited.add(x to y)

                for (direction in listOf('^', 'v', '<', '>', 'A')) {
                    val newPos = move(x, y, direction)
                    if (newPos != null) {
//                        val newSequence = sequence + (if (direction == 'A') currentPos else "")
                        val newSequence = sequence + direction
                        queue.offer(newSequence to (path + newPos))
                    }
                }
            }

            throw IllegalStateException("No solution found for code $code")
        }

        private fun getPosition(button: Char): Pair<Int, Int> {
            for (y in numericKeypad.indices) {
                for (x in numericKeypad[y].indices) {
                    if (numericKeypad[y][x] == button) {
                        return x to y
                    }
                }
            }
            throw IllegalArgumentException("Button $button not found on numeric keypad")
        }

        private fun move(x: Int, y: Int, direction: Char): Char? {
            val (newX, newY) = when (direction) {
                '^' -> x to (y - 1)
                'v' -> x to (y + 1)
                '<' -> (x - 1) to y
                '>' -> (x + 1) to y
                'A' -> x to y
                else -> throw IllegalArgumentException("Invalid direction: $direction")
            }

            return if (newY in numericKeypad.indices && newX in numericKeypad[newY].indices) {
                numericKeypad[newY][newX].takeIf { it != ' ' }
            } else null
        }

        private fun expect(actual: Long, expected: Long) {
            if (actual != expected) {
                throw AssertionError("Expected $expected, but got $actual")
            }
        }
    }
}
// end-of-code
