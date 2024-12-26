import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText
import java.io.File
import kotlin.math.abs

fun readFileContent(name: String) = Path("src/$name.txt").readText().trim().replace("\r","")

fun readFileLines(name: String) = readFileContent(name).lines()

fun readMatrix(name: String) = readFileLines(name).map { it.toCharArray().toList() }

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

fun getPathSrc(): String {
    val f = File("")
    var path = f.absolutePath
    if (!path.endsWith("src")){
        path+="/src"
    }
    val dirSrc = File(path)
    if (!dirSrc.exists())
        throw Exception("Dir $path don't exist")
    println("Current dir: $dirSrc")
    val p = Path(path)
    return "$p${File.separator}"
}

fun parseArgs(args: Array<String>): Map<String, String> {
    return args.asSequence()
        .withIndex()
        .filter { it.value.startsWith("--") }
        .map { indexed ->
            val key = indexed.value.substring(2)
            val nextIndex = indexed.index + 1
            val value = if (nextIndex < args.size && !args[nextIndex].startsWith("--")) {
                args[nextIndex]
            } else ""
            key to value
        }
        .toMap()
}

// source https://github.com/kingsleyadio/adventofcode/blob/main/src/com/kingsleyadio/adventofcode/util/DS.kt
data class Index(val x: Int, val y: Int)
operator fun Index.plus(other: Index) = Index(x + other.x, y + other.y)
operator fun Index.minus(other: Index) = Index(x - other.x, y - other.y)
operator fun Index.times(multiplier: Int) = Index(x * multiplier, y * multiplier)

fun Index.manhattanDistanceTo(other: Index) = abs(x - other.x) + abs(y - other.y)

data class Rect(val x: IntRange, val y: IntRange)

operator fun Rect.contains(other: Index) = other.y in y && other.x in x

object Directions {
    @JvmField val N = Index(0, -1)
    @JvmField val S = Index(0, 1)
    @JvmField val E = Index(1, 0)
    @JvmField val W = Index(-1, 0)
    @JvmField val NE = Index(1, -1)
    @JvmField val NW = Index(-1, -1)
    @JvmField val SE = Index(1, 1)
    @JvmField val SW = Index(-1, 1)

    @JvmField val Cardinal = listOf(N, E, S, W)
    @JvmField val InterCardinal = listOf(NE, SE, SW, NW)
    @JvmField val Compass = Cardinal + InterCardinal
}