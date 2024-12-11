import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Setup {
    val session_cookie = System.getenv("SESSION_COOKIE")
    val cody_endpoint = System.getenv("SRC_ENDPOINT")
    val cody_accesstoken = System.getenv("SRC_ACCESS_TOKEN")
    val path = getPathSrc();
    val client = HttpClient(CIO)
    val cody_path = "/Users/ludovic/.nvm/versions/node/v21.4.0/bin/cody"
    val node_path = "/usr/local/bin/node"
    val BIN_BASH = "/bin/bash"
    var dayPad: String = ""
    var star: Int = 1

    // Function to fetch the puzzle and input for the current day
    suspend fun fetchPuzzleAndInput(day: Int) {
        // Define URLs
        val puzzleUrl = "https://adventofcode.com/2024/day/$day"
        val inputUrl = "https://adventofcode.com/2024/day/$day/input"
        val postUrl = "https://adventofcode.com/2024/day/$day/answer/"
        dayPad = day.toString().padStart(2, '0')

        try {
            // Fetch the puzzle page
            val html = httpGet(puzzleUrl)
            val document = Jsoup.parse(html)
            val articles = document.select("main > article")
            val articlesCount = articles.size
            star = articlesCount
            val article = articles.get(star - 1)
            val puzzleContent = article?.text()?.trim() ?: "Puzzle content not found."
            val sample = article?.select("pre > code")?.first()?.text()?.trim() ?: "Sample content not found."
            val title = puzzleContent.substringBefore(" --- ", "").substringAfter("--- ", "")

            val content = puzzleContent.substringAfter(" --- ", "")
            // Save the puzzle content to a file
            writeFile("Day${dayPad}_star${star}.txt", content)
            writeFile("Day${dayPad}_star${star}_sample.txt", sample)

            // Fetch the puzzle input
            val inputData = httpGet(inputUrl)

            // Save the input data to a file
            writeFile("Day${dayPad}_input.txt", inputData)
            println("Puzzle and input for day $day fetched and saved successfully.")

            authCody()
            val prompt = getPrompt(content)
            val answer = runCody(prompt)

            writeFile("Day${dayPad}_star${star}_answer.txt", answer)
            println("Answer saved for Day${dayPad} star$star")

            writecodeFile(title, answer)

//        // execute kotlin program
//        val result1 = runProgram(dayPad)
//        val total = result1.substringBefore(":", "").trim()

//        //submit result1
//            val submissionResult = httpPost(client, postUrl + star, "total=$total")
//        //check submission Result
//        val ok = submissionResult.contains("You're right")
//        println("Submission day:$dayPad, star:$star total:$total = $submissionResult >> OK=$ok" )

        } catch (e: IOException) {
            println("Error fetching data: ${e.message}")
        } finally {
            client.close()
        }
    }

    private suspend fun httpGet(
        inputUrl: String
    ): String {
        val inputData = client.get(inputUrl) {
            headers {
                append("cookie", "session=$session_cookie")
            }
        }
        return inputData.bodyAsText().trim()
    }

    private suspend fun httpPost(
        postUrl: String,
        body: String
    ): String {
        val inputData = client.get(postUrl) {
            headers {
                append("cookie", "session=$session_cookie")
            }
            contentType(ContentType.Text.Html)
            setBody(body)
        }
        return inputData.bodyAsText().trim()
    }

    private fun writeFile(filename: String, content: String ) {
        File("${path}${filename}").writeText(content)
    }

    private fun writecodeFile(title: String, answer: String) {
        val code = answer.substringAfter("```kotlin").substringBefore("```").trim()
        val comments = answer.substringAfter("```kotlin").substringAfter("```").trim()

        val kotlinCode = """
/*
$title

$comments
*/
$code
"""
        writeFile("Day${dayPad}.kt", kotlinCode)
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
        println("Next run scheduled for: $nextRun")
        scheduler.scheduleAtFixedRate(task, delayMillis, 24 * 60 * 60 * 1000)  // Repeat daily
    }

    fun runDay(day: Int) {
        println("Running task for Day $day")

        runBlocking {
            fetchPuzzleAndInput(day)
        }
    }

    fun runToday() {
        val currentDay = LocalDate.now().dayOfMonth
        runDay(currentDay)
    }

    fun listContextFiles(path: String = "."): String {
        val currentDir = File(path)
        return currentDir.walkTopDown()
            .filter { it.isFile }
//        .filter { !it.name.endsWith(".txt") }
            .filter { it.name.endsWith(".kt") && it.path.contains("src/Day") }
            .map { it.relativeTo(currentDir).path }
            .joinToString(",")
    }

    fun authCody(): Boolean {
        val command = mutableListOf("sh", "-c", "cody auth login")
//    val command = mutableListOf("sh", "-c", "cody auth whoami")
        val r = execute(command)
//    val auth = r.contains("Authenticated as ")
        val auth = r.contains("You are already logged in as ")
        if (!auth)
            throw Exception("Cody auth failed")
        return auth
    }

    fun getPrompt(content: String): String {
        return """
        ${content}
        please write a kotlin class named ${dayPad}, with a main function, using ${dayPad}_input as input file. 
        Print the result to the console using the following format : "Result=(XX)" where XX is the result value.
        Optimize the algorithm to be be efficient and fast so that solution can be found in a reasonable amount of time.
        """;
    }

    /*
    cody auth login
    cody auth whoami
    cody chat --context-file src/Day01.kt,src/Day02.kt,... -m 'Are there code smells in this file?'
    */
    fun runCody(prompt: String): String {
        val fileList = listContextFiles()
        val cleanPrompt = prompt.replace("'", " ")
            .replace("\r", " ")
            .replace("\n", " ")
//    val command = mutableListOf("sh", "-c", "cody chat --context-file $fileList -m `$cleanPrompt`")
        val command = mutableListOf("sh", "-c", "cody chat --context-file $fileList -m '$cleanPrompt'")
//    val command = mutableListOf("sh", "-sc",  "cody chat --context-file $fileList --stdin", cleanPrompt)
//    val command = mutableListOf("sh", "-c", "echo -e '$cleanPrompt' | cody chat --context-file $fileList --stdin")
        val r = execute(command)
        return r
    }

    fun execute(command: String): String {
        return execute(command.split(' '))
    }

    fun execute(commands: List<String>, stdin: String? = null): String {
        println("> ${commands.joinToString(" ")}")
        val pb = ProcessBuilder(commands)
//    val f = File(path)
//    println("cwd> ${f.absolutePath}")
//    pb.directory(File(f.absolutePath));
        pb.inheritIO();
        val p = pb
            .redirectErrorStream(true)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .start()
        if (stdin != null) {
            p.outputWriter().use {
                it.write(stdin)
                it.flush()
            }
        }
        val output = p.inputStream.bufferedReader().readText()
        println("...waiting...")
        p.waitFor()

        println("-- Command output:")
        println(output)
        return output
    }

    fun runProgram(dayPad: String): String {
        return execute("gradle run --args=$dayPad")
        // Class.forName(className).newInstance()
    }

    fun isNumeric(toCheck: String): Boolean {
        return toCheck.toDoubleOrNull() != null
    }
}

fun main(args: Array<String>) {
    println("Starting Advent of Code puzzle solver...")

    val setup = Setup();
    val arg = args.joinToString(" ").trim();
    if (arg.isBlank()) {
        println("Running today's puzzle")
        setup.runToday()
    } else if ("--daemon".equals(arg)) {
        println("Starting daemon")
        setup.scheduleDailyTask()
    } else if (args.size > 1 && "--day".equals(args[0]) && setup.isNumeric(args[1])) {
        println("Running day ${args[1]}")
        setup.runDay(args[1].toInt())
    }
}

