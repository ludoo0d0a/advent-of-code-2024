// solution 106780429

val rex = "mul\\((\\d+),(\\d+)\\)".toRegex()
val rexdo = "do\\(\\)".toRegex()
val rexdont = "don't\\(\\)".toRegex()
val disablesMul = "don't\\(\\).+?do\\(\\)".toRegex()

fun main() {

    var testInputs = readInput("Day03_test")

    val line = testInputs.joinToString ("") { it }
    testInputs = listOf(line)

    println("File has ${testInputs.size} lines")

    var total = 0;
    testInputs.forEach { testInput ->
        countDos(testInput)

        var input = testInput.replace(disablesMul, "")
        val ido = rexdo.find( input ).let { it?.range?.first ?: 0 }

        val idont = rexdont.find( input ).let { it?.range?.first ?: 0 }
        if (idont > ido){
            //remove last dont anf after
            println("ends with dont")
            input=input.substring(0, idont)
        }

        countDos(input)
        val matches = rex.findAll(input)
        for (match in matches) {
            val a = match.groups[1]?.value!!.toInt()
            val b = match.groups[2]?.value!!.toInt()
            val c = a * b
            // println("$a * $b = $c")
            total += c
        }

        println("---")
    }

    println("total = $total")

}

fun countDos(testInput: String): Int {
    val countDo = rexdo.findAll(testInput).count()
    val countDont = rexdont.findAll(testInput).count()
    println("remains $countDo do, $countDont don't()")

    println("remaining do @" + rexdo.findAll(testInput).joinToString { it.range.first.toString() })
    println("remaining dont @" + rexdont.findAll(testInput).joinToString { it.range.first.toString() })

    return countDont
}
