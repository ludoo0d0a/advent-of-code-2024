
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

    private  fun compactFilesWholeMove(state: DiskState): DiskState {
        val blocks = state.blocks.toMutableList()
        val fileIds = state.filePositions.keys.sortedDescending()

        // Create index of empty spaces: Map<Length, List<StartPosition>>
        val emptySpaces = mutableMapOf<Int, MutableList<Int>>()
        var currentLength = 0
        var startPos = -1

        blocks.forEachIndexed { index, block ->
            if (block == -1) {
                if (startPos == -1) startPos = index
                currentLength++
                if (index == blocks.lastIndex) {
                    emptySpaces.getOrPut(currentLength) { mutableListOf() }.add(startPos)
                }
            } else {
                if (currentLength > 0) {
                    emptySpaces.getOrPut(currentLength) { mutableListOf() }.add(startPos)
                    currentLength = 0
                    startPos = -1
                }
            }
        }

        for (fileId in fileIds) {
            println("$fileId/${fileIds.size}")

            val filePositions = state.filePositions[fileId] ?: continue
            val fileSize = filePositions.size

            // Find suitable empty space
            val suitableSpaces = emptySpaces.entries
                .filter { it.key >= fileSize }
                .flatMap { it.value }
                .sorted()

            val originalStart = filePositions.first()
            val newPos = suitableSpaces.firstOrNull { it < originalStart }

            if (newPos != null) {
                // Remove file from original position
                filePositions.forEach { pos -> blocks[pos] = -1 }
                // Place file in new position
                repeat(fileSize) { offset ->
                    blocks[newPos + offset] = fileId
                }

                // Update empty spaces index
                updateEmptySpaces(emptySpaces, newPos, originalStart, fileSize, blocks)
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

    fun updateEmptySpaces(
        emptySpaces: MutableMap<Int, MutableList<Int>>,
        newPos: Int,
        oldPos: Int,
        fileSize: Int,
        blocks: List<Int>
    ) {
        // Recalculate empty spaces around the affected areas
        var start = -1
        var length = 0

        // Clear old entries
        emptySpaces.clear()

        // Rebuild index
        blocks.forEachIndexed { index, block ->
            if (block == -1) {
                if (start == -1) start = index
                length++
                if (index == blocks.lastIndex && length > 0) {
                    emptySpaces.getOrPut(length) { mutableListOf() }.add(start)
                }
            } else {
                if (length > 0) {
                    emptySpaces.getOrPut(length) { mutableListOf() }.add(start)
                    length = 0
                    start = -1
                }
            }
        }
    }


    fun main() {
//        val sample = readInputBody("Day09_sample")
//        val result1 = solve(sample)
//        println("Filesystem checksum sample1: $result1")  // 1928
//        val result2 = solve2(sample) //2858
//        println("Filesystem checksum sample2: $result2")

        val input = readInputBody("Day09_input")
        val result = solve2(input)
        println("Filesystem checksum 2: $result")

    }

    fun dbg(s: String) {
        if (DEBUG) println(s)
    }
}

fun main() {
    Day09().main()
}
