var search = "MAS"
val rexSearch = search.toRegex()

fun main() {
    wordFinder() //star 1 //->2414
    patternFinder() //star 2  //->1871
}
fun patternFinder() {
    search = "MAS"
    val lines = readInputMatrix("Day04_test")
    val n = lines.size
    val m = lines[0].size
    var total=0;
    lines.forEachIndexed { iline, line ->
        line
            .forEachIndexed { pos, c ->
                if (c=='A')
                    total+=getPatternDetected(iline, pos, lines, n, m)
            }
    }
    println("total=$total")
}

fun getPatternDetected(iline: Int, pos: Int, lines: List<List<Char>>, n: Int, m: Int): Int {
    //border detection
    if (iline<1 || pos<1 || iline>n-2 || pos>m-2)
        return 0;

    val matrix = (iline-1 until iline+2).map { x ->
        (pos - 1 until pos +2).map { y ->
            lines[x][y]
        }.joinToString("")
    }

    val diags = getDiagonals(matrix);
    val totalDiag = countDiagonals(diags);

    val diags2 = getDiagonals2(matrix);
    val totalDiag2 = countDiagonals(diags2);

    val total=totalDiag + totalDiag2

    if (total>1){
        println("found $total @ line $iline, pos $pos: ${lines[iline].joinToString("")}")
        printLines(matrix)
        return 1
    }
    return 0
}

fun countDiagonals(diags: List<String>): Int {
    val lineDiag = diags.joinToString(" ")
    return countRev(lineDiag)
}

fun wordFinder() {
    search = "XMAS"
    //val lines = readInput("Day04_test") //-> 18
    val lines = readInput("Day04_test1")
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
