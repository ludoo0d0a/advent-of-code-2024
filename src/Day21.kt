
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
import kotlin.math.absoluteValue
import kotlin.math.sign

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

        data class Index(val x: Int, val y: Int)
        operator fun Index.plus(other: Index) = Index(x + other.x, y + other.y)
        operator fun Index.minus(other: Index) = Index(x - other.x, y - other.y)
        operator fun Index.times(multiplier: Int) = Index(x * multiplier, y * multiplier)

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

            val result2_input = part2(input)
            println("Result2=$result2_input")
        }

        private fun part1(input: List<String>): Long {
            return solve(input, 2)
        }
        private fun part2(input: List<String>): Long {
            return solve(input, 25)
        }

        // copied from: https://github.com/kingsleyadio/adventofcode/blob/main/src/com/kingsleyadio/adventofcode/y2024/Day21.kt
        fun solve(input: List<String>, robots: Int): Long = input.sumOf { complexity(it, robots) }

        private val NUMERIC = mapOf(
            '7' to Index(0, 0), '8' to Index(1, 0), '9' to Index(2, 0),
            '4' to Index(0, 1), '5' to Index(1, 1), '6' to Index(2, 1),
            '1' to Index(0, 2), '2' to Index(1, 2), '3' to Index(2, 2),
            '.' to Index(0, 3), '0' to Index(1, 3), 'A' to Index(2, 3),
        )

        private val ROBOTIC = mapOf(
            '.' to Index(0, 0), '^' to Index(1, 0), 'A' to Index(2, 0),
            '<' to Index(0, 1), 'v' to Index(1, 1), '>' to Index(2, 1),
        )

        private fun complexity(keys: String, robots: Int): Long {
            val cache = mutableMapOf<Pair<String, Int>, Long>()
            fun simulate(aa: Char, bb: Char, level: Int): Long {
                if (aa == bb) return 1
                val key = "$aa$bb" to level
                if (key in cache) return cache.getValue(key)
                val keypad = if (level == 0) NUMERIC else ROBOTIC
                val a = keypad.getValue(aa)
                val b = keypad.getValue(bb)
                val invalid = keypad.getValue('.')
                val options = mutableListOf<List<Char>>()
                val (dx, dy) = b - a
                if (Index(a.x + dx, a.y) != invalid) options += buildList<Char> {
                    add('A')
                    for (i in 1..dx.absoluteValue) if (dx.sign < 0) add('<') else add('>')
                    for (i in 1..dy.absoluteValue) if (dy.sign < 0) add('^') else add('v')
                    add('A')
                }
                if (Index(a.x, a.y + dy) != invalid) options += buildList<Char> {
                    add('A')
                    for (i in 1..dy.absoluteValue) if (dy.sign < 0) add('^') else add('v')
                    for (i in 1..dx.absoluteValue) if (dx.sign < 0) add('<') else add('>')
                    add('A')
                }
                val size = options.minOf { option ->
                    if (level == robots) option.size.toLong() - 1
                    else option.zipWithNext().sumOf { (a, b) -> simulate(a, b, level + 1) }
                }
                return size.also { cache[key] = it }
            }

            val lowest = "A$keys".zipWithNext().sumOf { (a, b) -> simulate(a, b, 0) }
            val multiplier = keys.substringBeforeLast('A').toLong()
            return lowest * multiplier
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
