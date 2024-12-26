class Day19 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val sample = """
                r, wr, b, g, bwu, rb, gb, br

                brwrr
                bggr
                gbbr
                rrbgbr
                ubwu
                bwurrg
                brgr
                bbrgwb
            """.trimIndent().lines()

            println("Sample result: ${part1(sample)}")

            val input = readFileLines("Day19_input")
            println("Result: ${part1(input)}")
            println("Result: ${part2(input)}")
        }

        fun part1(input: List<String>): Int {
            val patterns = parsePatterns(input)
            val designs = parseDesigns(input)
            return designs.count { design -> isDesignPossible(design, patterns) }
        }
        // part1bis like part2
        fun part1bis(input: List<String>): Int {
            val patterns = parsePatterns(input)
            val designs = parseDesigns(input)
            val cache = hashMapOf("" to 1L)
            fun options(d: String): Long = cache.getOrPut(d) {
                patterns.sumOf { if (d.startsWith(it)) options(d.substring(it.length)) else 0L }
            }
            return designs.count { options(it) > 0 }
        }

        fun part2_failed(input: List<String>): Int {
            val patterns = parsePatterns(input)
            val designs = parseDesigns(input)

            return designs.sumOf { design ->
                val count = countArrangements(design, patterns)
                println("Design: $design -> $count arrangements")
                count
            }
        }

        // source https://github.com/kingsleyadio/adventofcode/blob/main/src/com/kingsleyadio/adventofcode/y2024/Day19.kt
        fun part2(input: List<String>): Long {
            val patterns = parsePatterns(input)
            val designs = parseDesigns(input)

            val cache = hashMapOf("" to 1L)
            fun options(d: String): Long = cache.getOrPut(d) {
                patterns.sumOf { if (d.startsWith(it)) options(d.substring(it.length)) else 0L }
            }
            return designs.sumOf { options(it) }
        }

        private fun countArrangements(design: String, patterns: Set<String>, currentPath: List<String> = emptyList()): Int {
            if (design.isEmpty()) {
                //println("  Found arrangement: ${currentPath.joinToString(" + ")}")
                return 1
            }

            var total = 0
            for (pattern in patterns) {
                if (design.startsWith(pattern)) {
                    total += countArrangements(
                        design.substring(pattern.length),
                        patterns,
                        currentPath + pattern
                    )
                }
            }
            return total
        }

        private fun parsePatterns(input: List<String>): Set<String> {
            return input.first().split(", ").toSet()
        }

        private fun parseDesigns(input: List<String>): List<String> {
            return input.drop(2).filter { it.isNotEmpty() }
        }

        private fun isDesignPossible(design: String, patterns: Set<String>): Boolean {
            return canMakeDesign(design, patterns, mutableListOf())
        }

        private fun canMakeDesign(
            remaining: String,
            patterns: Set<String>,
            usedPatterns: MutableList<String>
        ): Boolean {
            if (remaining.isEmpty()) {
                return true
            }

            for (pattern in patterns) {
                if (remaining.startsWith(pattern)) {
                    usedPatterns.add(pattern)
                    if (canMakeDesign(remaining.substring(pattern.length), patterns, usedPatterns)) {
                        return true
                    }
                    usedPatterns.removeLast()
                }
            }

            return false
        }
    }
}
