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


val session_cookie = System.getenv("SESSION_COOKIE")
val path = "src/gen"

// Function to fetch the puzzle and input for the current day
suspend fun fetchPuzzleAndInput(day: Int) {
    val client = HttpClient(CIO)

    // Define URLs
    val puzzleUrl = "https://adventofcode.com/2024/day/$day"
    val inputUrl = "https://adventofcode.com/2024/day/$day/input"
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
        val puzzleContent = document.select("main > article").first()?.text()?.trim() ?: "Puzzle content not found."
        val sample = document.select("main > article > pre > code").first()?.text()?.trim() ?: "Sample content not found."

        // Save the puzzle content to a file
        File("$path/Day${dayPad}.txt").writeText(puzzleContent)
        File("$path/Day${dayPad}_sample.txt").writeText(sample)

        val title = puzzleContent.substringBefore(" --- ", "").substringAfter("--- ", "")

        val kotlinCode = """
/*
$title
*/
class Day$dayPad {

    val DEBUG=true;

    fun main() {
        val input = readInput("Day${dayPad}_sample")
        //val input = readInput("Day${dayPad}_input")


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
    val target = now.withHour(6).withMinute(0).withSecond(0)
    val nextRun = if (target.isAfter(now)) {
        target
    } else {
        target.plusHours(24)
    }

    val delayMillis = java.time.Duration.between(now, nextRun).toMillis()
    scheduler.scheduleAtFixedRate(task, delayMillis, 24 * 60 * 60 * 1000)  // Repeat daily
}

fun runToday(){
    val currentDay = LocalDate.now().dayOfMonth
    println("Running task for Day $currentDay")

    runBlocking {
        fetchPuzzleAndInput(currentDay)
    }
}

fun main() {
    println("Starting Advent of Code puzzle fetcher...")
    //scheduleDailyTask()
    runToday()
}

