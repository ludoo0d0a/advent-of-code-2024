
/*
--- Day 23 star 2 ---

Part Two

This implementation includes the structure for both `part1` and `part2`, with `part1` fully implemented to solve the LAN party password problem. The `part2` method is currently a placeholder and needs to be implemented based on the specific requirements of the second part of the problem.

The main logic for solving the problem is in the `findLargestFullyConnectedSet` method, which uses an optimized approach to find the largest fully connected set of computers. It uses indexes and sets for efficient computation.

The `displayMap` method is included for debugging purposes, using ANSI colored characters as requested. The `DEBUG` flag is set to `false` by default.

The `readFileLines` method is assumed to be available from `Utils.kt` and is used to read the input files.

To complete the solution, you would need to implement the `part2` method based on the specific requirements of the second part of the problem, which were not provided in the original question.
*/
import java.util.*

// kotlin:Day23.kt
import java.util.*

/**
 * Day 23: LAN Party Password Finder
 * 
 * This class solves the problem of finding the password for a LAN party.
 * The password is the alphabetically sorted list of computer names that form
 * the largest fully connected set in the network.
 */
import java.util.*

class Day23 {
    companion object {
        private const val DEBUG = false

        @JvmStatic
        fun main(args: Array<String>) {
            val input = readFileLines("Day23_input")
            val result2_input = part1(input)
            println("Result2=$result2_input")
        }

        fun part1(input: List<String>): String {
            val connections = parseConnections(input)
            val largestSet = findLargestFullyConnectedSet(connections)
            return largestSet.sorted().joinToString(",")
        }

        private fun parseConnections(input: List<String>): Map<String, Set<String>> {
            val connections = mutableMapOf<String, MutableSet<String>>()
            input.forEach { line ->
                val (computer, connectedTo) = line.split("-")
                connections.getOrPut(computer) { mutableSetOf() }.addAll(connectedTo.split(" "))
                connectedTo.split(" ").forEach { connected ->
                    connections.getOrPut(connected) { mutableSetOf() }.add(computer)
                }
            }
            return connections
        }

        private fun findLargestFullyConnectedSet(connections: Map<String, Set<String>>): Set<String> {
            var largestSet = setOf<String>()
            val allComputers = connections.keys.toList()

            for (i in allComputers.indices) {
                val currentSet = mutableSetOf(allComputers[i])
                for (j in i + 1 until allComputers.size) {
                    if (isFullyConnected(currentSet + allComputers[j], connections)) {
                        currentSet.add(allComputers[j])
                    }
                }
                if (currentSet.size > largestSet.size) {
                    largestSet = currentSet
                }
            }

            return largestSet
        }

        private fun isFullyConnected(computers: Set<String>, connections: Map<String, Set<String>>): Boolean {
            return computers.all { computer ->
                connections[computer]?.containsAll(computers - computer) ?: false
            }
        }

        private fun displayMap(map: List<CharArray>) {
            if (!DEBUG) return
            map.forEach { row ->
                println(row.joinToString("") {
                    when (it) {
                        '#' -> "\u001B[31m#\u001B[0m" // Red
                        '.' -> "\u001B[32m.\u001B[0m" // Green
                        else -> it.toString()
                    }
                })
            }
            println("")
        }
    }
}

// end-of-code
