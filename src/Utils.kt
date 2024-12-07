import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

fun readInputBody(name: String) = Path("src/$name.txt").readText().trim().replace("\r","")

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

fun readInputMatrix(name: String) = Path("src/$name.txt").readText().trim().lines().map { it.toCharArray().toList() }

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun printMatrix(matrix: List<List<Char>>) {
    matrix.joinToString(""){ it.joinToString("")+"\n" }.println()
}

fun printLines(matrix: List<String>) {
    matrix.joinToString("\n").println()
}
