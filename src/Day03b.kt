// solution 106780429

class Day03b(){
    val rex = "mul\\((\\d+),(\\d+)\\)".toRegex()
    val rexdo = "do\\(\\)".toRegex()
    val rexdont = "don't\\(\\)".toRegex()
    val disablesMul = "don't\\(\\).+?do\\(\\)".toRegex()

   fun main() {

        var testInputs = readLines("Day03_test")
        val line = testInputs.joinToString ("") { it }
            println("File has ${testInputs.size} lines")
    
        var input = line.replace(disablesMul, "")
        val ido = rexdo.find( input ).let { it?.range?.first ?: 0 }
    
        val idont = rexdont.find( input ).let { it?.range?.first ?: 0 }
        if (idont > ido){
            //remove last dont and after
            println("ends with dont")
            input=input.substring(0, idont)
        }
    
        val total = rex.findAll(input)
            .map { it.groups[1]?.value!!.toInt() * it.groups[2]?.value!!.toInt() }
            .reduce { acc, next -> acc + next }
    
        println("total = $total")

  }
}

fun main() {
     Day03b().main()
}
