
/*
Day 9: Disk Fragmenter
*/
class Day09 {
    val DEBUG = false

    data class DiskState(
        val blocks: List<Int>, // -1 for free space, file ID otherwise
        val filePositions: Map<Int, List<Int>> // file ID to list of positions
    )

    fun parseInput(input: String): List<Int> {
        // Parse alternating file and free space lengths
        return input.trim().map { it.toString().toInt() }
    }

    fun createInitialState(lengths: List<Int>): DiskState {
        val blocks = mutableListOf<Int>()
        var fileId = 0

        lengths.forEachIndexed { index, length ->
            repeat(length) {
                blocks.add(if (index % 2 == 0) fileId else -1)
            }
            if (index % 2 == 0) fileId++
        }

        return DiskState(
            blocks = blocks,
            filePositions = blocks.mapIndexed { index, id ->
                index to id
            }.filter { it.second != -1 }
                .groupBy({ it.second }, { it.first })
        )
    }

    fun compactFiles(state: DiskState): DiskState {
        val blocks = state.blocks.toMutableList()
        var changed = true

        while (changed) {
            changed = false
            // Find rightmost file block
            val lastFileIndex = blocks.indexOfLast { it != -1 }
            if (lastFileIndex == -1) break

            // Find leftmost free space
            val firstFreeIndex = blocks.indexOf(-1)
            if (firstFreeIndex == -1 || firstFreeIndex > lastFileIndex) break

            // Move file block to free space
            blocks[firstFreeIndex] = blocks[lastFileIndex]
            blocks[lastFileIndex] = -1
            changed = true

            if (DEBUG) {
                dbg(blocks.joinToString("") { if (it == -1) "." else it.toString() })
            }
        }

        return DiskState(
            blocks = blocks,
            filePositions = blocks.mapIndexed { index, id ->
                index to id
            }.filter { it.second != -1 }
                .groupBy({ it.second }, { it.first })
        )
    }

    fun calculateChecksum(state: DiskState): Long {
        return state.blocks.mapIndexed { index, fileId ->
            if (fileId == -1) 0L else index.toLong() * fileId
        }.sum()
    }

    fun solve(input: String): Long {
        val lengths = parseInput(input)
        val initialState = createInitialState(lengths)

        if (DEBUG) {
            dbg("Initial state:")
            dbg(initialState.blocks.joinToString("") { if (it == -1) "." else it.toString() })
        }

        val compactedState = compactFiles(initialState)

        if (DEBUG) {
            dbg("\nFinal state:")
            dbg(compactedState.blocks.joinToString("") { if (it == -1) "." else it.toString() })
        }

        return calculateChecksum(compactedState)
    }

    fun main() {
        //val input = readInputBody("gen/Day09_sample") // 1928
        val input = readInputBody("gen/Day09_input")
        val result = solve(input)
        println("Filesystem checksum: $result")
    }

    fun dbg(s: String) {
        if (DEBUG) println(s)
    }
}

fun main() {
    Day09().main()
}
