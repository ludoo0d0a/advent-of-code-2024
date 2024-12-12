import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
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
    val client = HttpClient(CIO){
        install(Logging){
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
    val cody_path = "/Users/ludovic/.nvm/versions/node/v21.4.0/bin/cody"
    val node_path = "/usr/local/bin/node"
    val BIN_BASH = "/bin/bash"
    var dayPad: String = ""

    var star: Int = 1

    // Function to fetch the puzzle and input for the current day
    suspend fun fetchPuzzleAndInput(day: Int, requestedStar: Int) {
        // Define URLs
        val puzzleUrl = "https://adventofcode.com/2024/day/$day"
        val inputUrl = "https://adventofcode.com/2024/day/$day/input"

        dayPad = day.toString().padStart(2, '0')

        try {
            // Fetch the puzzle page
            val html = httpGet(puzzleUrl)
            val document = Jsoup.parse(html)
            val articles = document.select("main > article")
            val articlesCount = articles.size
            star = requestedStar.or(articlesCount)
            println("********** Current star : $star")
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

            // execute kotlin program
            val result = buildRunProgram(dayPad)
            val totals = result.split(",")
            println("Result for totals: $totals")

            val total = totals.getOrNull(star - 1) ?:
                    throw Exception("Total not found for star $star in totals $totals")

            println("Will submit answer for day:$dayPad, star:$star = $total")
            //submit result
            submitSolution(total, day, star)


        } catch (e: IOException) {
            println("Error fetching data: ${e.message}")
        } finally {
            client.close()
        }
    }

    private fun submitSolution(total: String, day: Int, star: Int) {
        val postUrl = "https://adventofcode.com/2024/day/$day/answer"
        val submissionResult = runBlocking {
                httpForm(postUrl, total, day, star)
        }
        println("Submission result: $submissionResult")
        //check submission Result
        val ok = submissionResult.contains("You're right")
        val nok = submissionResult.contains("That's not the right answer")
        println("Submission day:$dayPad, star:$star total:$total = $submissionResult >> OK=$ok" )
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
    private suspend fun httpForm(
        postUrl: String,
        answer: String,
        day: Int,
        star: Int
    ): String {
        println("post to: ${postUrl}");
        val response: HttpResponse = client.post(postUrl) {
            headers {
                append("content-type", "application/x-www-form-urlencoded")
                append("referer", "https://adventofcode.com/2024/day/$day")
                append("cookie", "session=$session_cookie")
                append("origin", "https://adventofcode.com")
                append("host", "adventofcode.com")
            }
            setBody("level=$star&answer=$answer")
//            setBody(
//                FormDataContent(
//                    Parameters.build {
//                        append("level", "$star")
//                        append("answer", answer)
//                    }
//                )
//            )
        }
        println("HTTP form submit status: $response.status.value");
        println("HTTP form submit headers: ${response.headers}");
        println("HTTP form submit body: ${response.bodyAsText()}");
        return response.bodyAsText().trim()
    }

    private fun writeFile(filename: String, content: String) {
        File("${path}${filename}").writeText(content)
    }

    private fun writecodeFile(title: String, answer: String) {
        val code = answer
            .replaceBefore("```kotlin", "").replace("```kotlin", "// kotlin")
            .replaceAfter("```", "").replace("```", "// end-of-code ")
            .trim()
        val comments = answer.substringAfter("end-of-code", "").trim()

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

    fun runDay(day: Int, star: Int) {
        println("Running task for Day $day")

        runBlocking {
            fetchPuzzleAndInput(day, star)
        }
    }

    fun runToday(star: Int = 1) {
        val currentDay = LocalDate.now().dayOfMonth
        runDay(currentDay, star)
    }

    fun listContextFiles(path: String = "."): String {
        val currentDir = File(path)
        return currentDir.walkTopDown()
            .filter { it.isFile }
            .filter { !it.name.endsWith(".txt") }
            .filter { it.name.endsWith(".kt") }
            .map { it.relativeTo(currentDir).path }
            .filter { !it.startsWith(".") }
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
        please write a kotlin class named ${dayPad}, with a main function, using 'Day${dayPad}_input' as input file.
        Use readFileContent() method to read the content of the file. 
        Print the result to the console using the following format : "Result=XX" where XX is the result value.
        Optimize the algorithm to be be efficient and fast so that solution can be found in a reasonable amount of time.
        Use indexes as soon as you can to avoid re-calculating the same value and lost time in long computation.
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
        return executeSh("cody chat --context-file $fileList -m '$cleanPrompt'")
    }

    fun executeSh(command: String): String {
        val commands = mutableListOf("sh", "-c", command)
        return execute(commands)
    }

    fun execute(command: String): String {
        return execute(command.split(' '))
    }

    fun execute(commands: List<String>, stdin: String? = null): String {
        println("-- execute: ${commands.joinToString(" ")}")
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

        println("-- Command output>")
        println("$output")
        println("<Command output")

        return output
    }

    fun buildRunProgram(dayPad: String): String {
        val content = executeSh("gradle run --args='--run $dayPad'")
        val response = content.substringAfter("Result=").replaceAfter("\n", "")
            .replace("\n", "")
            .replace(")", "")
            .replace("(", "")
            .trim()
        return response
    }

    fun runProgram(dayPad: String): String {
        val instance = Class.forName("Day$dayPad").getDeclaredConstructor().newInstance()
        val method = instance.javaClass.getMethod("main", Array<String>::class.java)
        val args = arrayOf("$star")
        val result = method.invoke(instance, args)
        // Case main() return value (not the case), result is printed out
        return "Result=${result}"
    }

    fun isNumeric(toCheck: String): Boolean {
        return toCheck.toDoubleOrNull() != null
    }

    companion object {
        fun main(args: Array<String>) {
            println("Starting Advent of Code puzzle solver...")

            val setup = Setup();
            val arguments = parseArgs(args)
            println("Arguments: $arguments")
            val day = arguments.getOrDefault("day", "0").toInt()
            val star = arguments.getOrDefault("star", "1").toInt()
            if (arguments.isEmpty()) {
                println(">>Running today's puzzle")
                setup.runToday(star)
            } else if (arguments.containsKey("daemon")) {
                println(">>Starting daemon")
                setup.scheduleDailyTask()
            } else if (arguments.containsKey("run")) {
                val dayPad = arguments.getOrDefault("run", "00")
                println(">>Run program ${dayPad}")
                val response = setup.runProgram(dayPad)
                println(response)
            } else if (arguments.containsKey("build")) {
                val dayPad = arguments.getOrDefault("build", "00")
                println(">>Build & run program ${dayPad}")
                val response = setup.buildRunProgram(dayPad)
                println(">>Build & run program returned: ${response}")
            }else if (arguments.containsKey("solution")) {
                val solution = arguments.getOrDefault("solution", "")
                val response = setup.submitSolution(solution, day, star)
                println(">>Build & run program returned: ${response}")
            } else if (arguments.containsKey("day")) {
                println(">>Running day ${day} star ${star}")
                setup.runDay(day, star)
            }

        }
    }
}

fun main(args: Array<String>) {
    Setup.main(args)
}

