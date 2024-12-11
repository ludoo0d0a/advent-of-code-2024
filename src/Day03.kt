class Day03(){
    fun main() {

    val testInputs = readLines("Day03_test")
    println("File has ${testInputs.size} lines")

    val rex = "mul\\((\\d+),(\\d+)\\)".toRegex()
    var total = 0;
    testInputs.forEach { testInput ->
        val matches = rex.findAll(testInput)
        for (match in matches) {
            val a = match.groups[1]?.value!!.toInt()
            val b = match.groups[2]?.value!!.toInt()
            val c = a * b
            println("$a * $b = $c")
            total += c
        }

        println("---")
    }

    println("total=$total")
  }
}

fun main() {
     Day03().main()
}
