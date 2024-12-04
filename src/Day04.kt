val search = "XMAS"
val rexSearch = search.toRegex()

fun main() {
    //val lines = readInput("Day04_test") //-> 18
    val lines = readInput("Day04_test1") //->2414
    val lineH = lines.joinToString(" ")
    val totalH = countRev(lineH)

    val lineV = readMatrixVertically(lines)
    val totalV = countRev(lineV)

    var diags = getDiagonals(lines)
    val lineDiag = diags.joinToString(" ")
    var totalDiag = countRev(lineDiag)

    var diags2 = getDiagonals2(lines)
    val lineDiag2 = diags2.joinToString(" ")
    var totalDiag2 = countRev(lineDiag2)

    val total = totalH +  totalV +   totalDiag +   totalDiag2
    println("total=$total")

}

fun countRev(line: String): Int {
    val total = rexSearch.findAll(line).count()
    val totalRev = rexSearch.findAll(line.reversed()).count()
    return total + totalRev
}

fun readMatrixVertically(lines: List<String>): String {
    val len = lines[0].length
    return (0 until len).map { index ->
        lines.map { it[index] }.joinToString("")
    }.joinToString(" ")
}

fun readMatrixDiagonally(lines: List<String>): List<String> {
    val n = lines.size
    val m = lines[0].length

    return (0 until n + m - 1).map { d ->
        (maxOf(0, d - m + 1) until minOf(n, d + 1))
            .map { x -> lines[x][d - x] }
            .joinToString { it.toString() }
    }
}
