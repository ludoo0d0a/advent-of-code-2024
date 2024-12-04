fun main() {
    val lines = listOf(
        "abc",
        "efgh",
        "ijkl",
        "mnop"
    )

    val res = getDiagonals(lines)
    println(res)

    val res2 = getDiagonals2(lines)
    println(res2)
}

fun matrixLines(matrix: List<String>): List<List<String>> {
    return matrix.map { it.split("") }
}

fun getDiagonals(lines: List<String>): List<String> {
    val matrix = matrixLines(lines)
    val n = matrix.size  // n lines
    val m = matrix[0].size  // line[0].length
    return (0 until n + m - 1).map { d ->
        (maxOf(0, d - m + 1) until minOf(n, d + 1))
            .map { x -> matrix[x][d - x] }
            .joinToString("")
    }
    //  //// ["a", "eb", "ifc", "mjgd", "nkh", "ol","p"]
}



fun getDiagonals2(lines: List<String>): List<String> {
    val matrix = matrixLines(lines)
    val n = matrix.size // n lines
    val m = matrix[0].size // n lines
    return (0 until n + m - 1).map { d ->
        (maxOf(0, d - m + 1) until minOf(n, d + 1))
            .map { x -> matrix[n-1-x][d - x] }
            .joinToString("")
    }
    // \\\\ ["m", "in", "ejo", ... "bgl", "ch", "d"]
}
