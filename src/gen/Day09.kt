
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

    fun solve2(input: String): Long {
        val lengths = parseInput(input)
        val initialState = createInitialState(lengths)

        if (DEBUG) {
            dbg("Initial state:")
            dbg(initialState.blocks.joinToString("") { if (it == -1) "." else it.toString() })
        }

        val compactedState = compactFilesWholeMove(initialState)

        if (DEBUG) {
            dbg("\nFinal state:")
            dbg(compactedState.blocks.joinToString("") { if (it == -1) "." else it.toString() })
        }

        return calculateChecksum(compactedState)
    }

    private fun compactFilesWholeMove(state: DiskState): DiskState {
        val blocks = state.blocks.toMutableList()
        val fileIds = state.filePositions.keys.sortedDescending()

        for (fileId in fileIds) {
            val filePositions = state.filePositions[fileId] ?: continue
            val fileSize = filePositions.size

            // Find leftmost suitable free space
            var currentPos = 0
            while (currentPos < blocks.size) {
                // Skip non-free space
                if (blocks[currentPos] != -1) {
                    currentPos++
                    continue
                }

                // Check if we have enough contiguous free space
                val freeSpace = blocks.drop(currentPos).takeWhile { it == -1 }.count()
                if (freeSpace >= fileSize) {
                    // Move the whole file if it's to the left of current position
                    val originalStart = filePositions.first()
                    if (originalStart > currentPos) {
                        // Remove file from original position
                        filePositions.forEach { pos -> blocks[pos] = -1 }
                        // Place file in new position
                        repeat(fileSize) { offset ->
                            blocks[currentPos + offset] = fileId
                        }
                    }
                    break
                }
                currentPos++
            }

            if (DEBUG) {
                dbg("After moving file $fileId:")
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


    fun main() {
        //val input = readInputBody("gen/Day09_sample") // 1928
        val input = readInputBody("gen/Day09_input")
        //val result = solve(input)
        //println("Filesystem checksum: $result")

        val result2 = solve2(input)
        println("Filesystem checksum2: $result2")
    }

    fun dbg(s: String) {
        if (DEBUG) println(s)
    }
}

fun main() {
    Day09().main()
}
