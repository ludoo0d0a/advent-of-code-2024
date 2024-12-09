import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jsoup.Jsoup
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.*
import java.util.*
import java.io.IOException
import java.time.LocalDateTime
import java.io.File

val session_cookie = System.getenv("SESSION_COOKIE")
val cody_endpoint = System.getenv("SRC_ENDPOINT")
val cody_accesstoken = System.getenv("SRC_ACCESS_TOKEN")
val path = "src"

// Function to fetch the puzzle and input for the current day
suspend fun fetchPuzzleAndInput(day: Int, star: Int = 1) {
    val client = HttpClient(CIO)

    // Define URLs
    val puzzleUrl = "https://adventofcode.com/2024/day/$day"
    val inputUrl = "https://adventofcode.com/2024/day/$day/input"
    val postUrl = "https://adventofcode.com/2024/day/$day/answer/"
    val dayPad = day.toString().padStart(2, '0')

    try {
        // Fetch the puzzle page
        val html = client.get(puzzleUrl) {
            headers {
                append("cookie", "session=$session_cookie")
            }
        }

        // Parse the HTML with Jsoup
        val text = html.bodyAsText()
        val document = Jsoup.parse(text)
        val article = document.select("main > article").get(star)
        val puzzleContent = article?.text()?.trim() ?: "Puzzle content not found."
        val sample = article?.select("pre > code").first()?.text()?.trim() ?: "Sample content not found."
        val title = puzzleContent.substringBefore(" --- ", "").substringAfter("--- ", "")

        val content1 = puzzleContent.substringBefore(" --- ", "")
        // Save the puzzle content to a file
        File("$path/Day${dayPad}_star${star}.txt").writeText(content1)
        File("$path/Day${dayPad}_star${star}_sample.txt").writeText(sample)

        val kotlinCode = """
/*
$title
*/
class Day$dayPad {

    val DEBUG=true;

    fun main() {
        val input = readInput("Day${dayPad}_star${star}_sample")
        //val input = readInput("Day${dayPad}_star${star}_input")


    }


    fun dbg(s: String) {
        if (DEBUG)
            println(s)
    }
}

fun main() {
    Day$dayPad().main()
}
"""
        File("$path/Day${dayPad}.kt").writeText(kotlinCode)

        // Fetch the puzzle input
        val inputData = client.get(inputUrl) {
            headers {
                append("cookie", "session=$session_cookie")
            }
        }

        // Save the input data to a file
        File("$path/Day${dayPad}_input.txt").writeText(inputData.bodyAsText().trim())
        println("Puzzle and input for day $day fetched and saved successfully.")

        // request Cody star 1
        authCody()
        runCody(content1)
        // execute kotlin program
        val result1 = runProgram1("Day$dayPad")
        val total = result1.substringBefore(":", "").trim()
        //submit result1
        val submissionResult = client.post(postUrl+star) {
            headers {
                append("cookie", "session=$session_cookie")
            }
            body { "total=$total" }
        }
        //chekc submission Result
        val ok = submissionResult.contains("You're right")
        println("Submission day:$dayPad, star:$star total:$total = $submissionResult >> OK=$ok" )

    } catch (e: IOException) {
        println("Error fetching data: ${e.message}")
    } finally {
        client.close()
    }
}

// Function to run the task at 6 PM every day
fun scheduleDailyTask() {
    val scheduler = Timer()

    val task = object : TimerTask() {
        override fun run() {
            runBlocking {
                runToday()
            }
        }
    }

    // Set the schedule: Every day at 6 AM
    val now = LocalDateTime.now()
    val target = now.withHour(6).withMinute(1).withSecond(0)
    val nextRun = if (target.isAfter(now)) {
        target
    } else {
        target.plusHours(24)
    }

    val delayMillis = java.time.Duration.between(now, nextRun).toMillis()
    scheduler.scheduleAtFixedRate(task, delayMillis, 24 * 60 * 60 * 1000)  // Repeat daily
}

fun runDay(day: Int){
    println("Running task for Day $day")

    runBlocking {
        fetchPuzzleAndInput(day)
    }
}

fun runToday(){
    val currentDay = LocalDate.now().dayOfMonth
    runDay(currentDay)
}

fun listContextFiles(path: String=".") {
    val currentDir = File(path)
    return currentDir.walkTopDown()
        .filter { it.isFile }
        .filter { !it.name.endsWith(".txt") }
        .map { it.relativeTo(currentDir).path }
        .joinToString(",")
}

fun authCody(): String {
    val command = mutableListOf("cody", "auth", "login")
    return execute(command)
}
/*
cody auth login
cody auth whoami
cody chat --context-file src/Day01.kt,src/Day02.kt,... -m 'Are there code smells in this file?'
*/
fun runCody(prompt: String): String {
    val fileList = listContextFiles()
    val command = mutableListOf("cody", "chat", "--context-file", fileList, "-m", prompt)
    return execute(command)
}
fun execute(command: String): String {
    return execute(command.split(' '))
}
fun execute(command: List<String>): String {
    println("> $command")
    val process = ProcessBuilder(command)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .start()
        
    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()
    
    println("Command output:")
    println(output)
    return output
}
fun runProgram(className: String){
    execute("gradle run")
    // Class.forName(className).newInstance()
}
fun isNumeric(toCheck: String): Boolean {
    return toCheck.toDoubleOrNull() != null
}
fun main(args: Array<String>) {
    println("Starting Advent of Code puzzle fetcher...")

    val arg = args.contentToString();
    if (arg.isBlank()){
        runToday()
    }else if ("--daemon".equals(arg)){
        scheduleDailyTask()
    }else if (args.size>1 && "--day".equals(args[0]) && isNumeric(args[1]) ){{
        runDay(args[1].toInt())
    }

}

