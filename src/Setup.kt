import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
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
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 10)
            delayMillis { retry ->
                retry * 1000L
            } // retries every second
        }
//        install(Logging){
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL
//        }
    }
    val cody_path = "/Users/ludovic/.nvm/versions/node/v21.4.0/bin/cody"
    val node_path = "/usr/local/bin/node"
    val BIN_BASH = "/bin/bash"
    var dayPad: String = ""

    var day: Int = 0
    var star: Int = 1

    fun getPrompt(content: String): String {
        val prompt_star = if (star==1){
            """
A first method named 'part1', called from main() method, solves this first part of the problem.
Use readFileLines() method from Utils.kt to read the content of the input file, named 'Day${dayPad}_input'.
Print the result of part1 to the console using the following format : "Result1=XX" where XX is the result value. 
            """
        }else {
            """
The first part of the problem is solved, now consider the second part of the problem, 
Another method named 'part2', called from main() method, solves this second part of the problem.
Use already provided readFileLines() method from Utils.kt to read the content of the input file, named 'Day${dayPad}_input'.
Print the result of part2 to the console using the following format : "Result2=XX" where XX is the result value. 
            """
        }

        return """
considering the following problem: 

${content}

then please write a Kotlin class named Day${dayPad}, with a main function, to solve this problem. 
the main method should compute : 
$prompt_star
Optimize the algorithm to be be efficient and fast so that solution can be found in a reasonable amount of time. 
Use indexes as soon as you can to avoid re-calculating the same value and lost time in long computation. 
use Long instead of Int to avoid overflow.
Add this prompt in comment in the code.
Show the whole code for the kotlin class.
""".trim();
    }

    // Function to fetch the puzzle and input for the current day
    private suspend fun fetchPuzzleAndInput(): Boolean {
        // Define URLs
        val puzzleUrl = "https://adventofcode.com/2024/day/$day"
        val inputUrl = "https://adventofcode.com/2024/day/$day/input"

        initDay(day, star)

        try {
            // Fetch the puzzle page
            val html = httpGet(puzzleUrl)
            val document = Jsoup.parse(html)
            val articles = document.select("main > article")
            val articlesCount = articles.size
            star = Math.max(star, articlesCount) //star2 if found star1 solved
            println("********** Current star : $star")
            val article = articles.get(star - 1)
            val puzzleContent = article?.text()?.trim() ?: "Puzzle content not found."
            val sample = article?.select("pre > code")?.first()?.text()?.trim() ?: "Sample content not found."
            val title = puzzleContent.substringBefore(" --- ", "").substringAfter("--- ", "")

            val content = puzzleContent.substringAfter(" --- ", "")
            // Save the puzzle content to a file
            writeFile("Day${dayPad}_star${star}.txt", content)
            writeFile("Day${dayPad}_star${star}_sample.txt", sample)

            val inputData = httpGet(inputUrl)
            writeFile("Day${dayPad}_input.txt", inputData)
            println("Puzzle and input for day $day star $star fetched and saved successfully.")

            val res =  promptAndSolve(content, title, day)
            if (res){
                archiveCode(day)
            }
            return res

        } catch (e: IOException) {
            println("Error fetching data: ${e.message}")
        } finally {
            client.close()
        }
        return false
    }

    private fun initDay(day: Int, star: Int): String {
        this.day = day
        this.star = star
        dayPad = day.toString().padStart(2, '0')
        return dayPad
    }

    private fun archiveCode(day: Int) {
        try {
        execute("git add -A") //add all
        execute("git commit -m Solution day $day star $star")
        } catch (e: IOException) {
            println(" **** Error ***** git add/commit: ${e.message}")
        }
    }

    fun promptFromFile(): Boolean {
        val prompt = readFile("Day${dayPad}_star${star}_prompt.txt")
        if (prompt.isBlank())
            throw Exception("Prompt is blank")

        val title = "Problem day $day star $star"
        return promptAndSolve(prompt, title, day)
    }

    private fun promptAndSolve(content: String, title: String, day: Int): Boolean {
        authCody()
        val prompt = getPrompt(content)
        writeFile("Day${dayPad}_star${star}_prompt.txt", prompt)
        val answer = runCody(prompt)

        if (answer.isBlank())
            throw Exception("Answer is blank")

        writeFile("Day${dayPad}_star${star}_answer.txt", answer)
        println("Answer saved for Day${dayPad} star$star")

        writecodeFile(title, answer)

        // execute kotlin program
        val result = buildRunProgram()
        val totals = result.split(",")
        println("Result for totals: $totals")

        val total = if (totals.size == 1)
            result
        else
            totals.getOrNull(star - 1) ?: throw Exception("Total not found for star $star in totals $totals")

        if (!isNumeric(total))
            throw Exception("Total is not numeric: $total")

        println("Will submit answer for day:$dayPad, star:$star = $total")
        //submit result
        return submitSolution(total)
    }

    private fun submitSolution(total: String): Boolean {
        val postUrl = "https://adventofcode.com/2024/day/$day/answer"
        val submissionResult = runBlocking {
                httpForm(postUrl, total, day, star)
        }
        //println("Submission result: $submissionResult")
        //check submission Result
        val ok = submissionResult.contains("That's the right answer")
        val nok1 = submissionResult.contains("That's not the right answer")
        val nok2 = submissionResult.contains("You don't seem to be solving the right level")
        val nok3 = submissionResult.contains("You gave an answer too recently")
        if (nok1 || nok2 || nok3) {
            println(" ** KO ** Submission day:$dayPad, star:$star total:$total ")
        }else if (ok) {
            println(" ** SUCCESS **. Submission day:$dayPad, star:$star total:$total >> OK=$ok")
            return true
        }else{
            println(" ** KO **  with unknown error code detected - please check pattern in HTML")
            println(submissionResult)
        }
        return false
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
        val response: HttpResponse = client.post(postUrl) {
            headers {
                append("content-type", "application/x-www-form-urlencoded")
                append("referer", "https://adventofcode.com/2024/day/$day")
                append("cookie", "session=$session_cookie")
                append("origin", "https://adventofcode.com")
                append("host", "adventofcode.com")
            }
            setBody("level=$star&answer=$answer")
        }
        return response.bodyAsText().trim()
    }

    private fun writeFile(filename: String, content: String) {
        File("${path}${filename}").writeText(content)
    }
    private fun readFile(filename: String): String {
        return File("${path}${filename}").readText()
    }

    private fun writecodeFile(title: String, answer: String) {
        val EOC = "// end-of-code"
        var code = answer
            .replaceBefore("```kotlin", "").replace("```kotlin", "// kotlin")
            .replace("```", EOC)
            .trim()
        val comments = code.substringAfter("// end-of-code", "").trim()

        code = code.replaceAfter(EOC, "")

        if (code.isBlank())
            throw Exception("No code found in anwser : $answer")

        val kotlinCode = """
/*
--- Day $dayPad star $star ---

$title

$comments
*/
import java.util.*

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
        val target = now.withHour(6).withMinute(0).withSecond(0)
        val nextRun = if (target.isAfter(now)) {
            target
        } else {
            target.plusHours(24)
        }

        val delta = java.time.Duration.between(now, nextRun)
        println("Next run scheduled for: $nextRun in $delta")
        scheduler.schedule(task, delta.toMillis(), 24 * 60 * 60 * 1000)
    }

    fun runDay() {
        println("Running task for Day $day")

        runBlocking {
            val res = fetchPuzzleAndInput()
            if (res && star==1) {
                initDay(day, 2)
                fetchPuzzleAndInput()
            }
        }
    }

    fun runToday(star: Int = 1) {
        val currentDay = LocalDate.now().dayOfMonth
        initDay(currentDay, star)
        runDay()
    }

    private fun listContextFiles(): String {
        val currentDir = File(path)

        return listOf("${path}Day$dayPad.kt", "${path}Utils.kt").map{ File(it) }
//        return currentDir.walkTopDown()
            .filter { it.isFile }
            .filter { it.exists() }
            .filter { !it.name.endsWith(".txt") }
            .filter { !it.name.endsWith(".bat") }
            .filter { !it.name.endsWith(".md") }
            .filter { it.name.endsWith(".kt") }
            .map { it.relativeTo(currentDir).path }
            .filter { !it.startsWith(".") }
            .joinToString(",")
    }

    private fun authCody(): Boolean {
        val command = mutableListOf("sh", "-c", "cody auth login")
        val r = execute(command)
        // login in error stream ??
        val auth = r.error.contains("You are already logged in as ")
        if (!auth)
            throw Exception("Cody auth failed")
        return auth
    }


    /*
    cody auth login
    cody auth whoami
    cody chat --context-file src/Day01.kt,src/Day02.kt,... -m 'Are there code smells in this file?'
    */
    private fun runCody(prompt: String): String {
        val fileList = listContextFiles()
        if (fileList.isBlank())
            throw Exception("No context files found")

        val cleanPrompt = prompt.replace("'", " ")
            .replace("\r", " ")
            .replace("\n", " ")
//        return executeSh("cody chat --context-file $fileList -m '$cleanPrompt'")
        return executeSh("cody chat --context-file $fileList --stdin", prompt)
    }

    private fun executeSh(command: String, stdin: String? = null): String {
        val commands = mutableListOf("sh", "-c", command)
        writeFile("Day${dayPad}_star${star}_command.txt", command)
        return execute(commands, stdin).output
    }

    private fun execute(command: String): ShellResponse {
        return execute(command.split(' '))
    }

    data class ShellResponse(val exitCode: Int, val output: String, val error: String)

    private fun execute(commands: List<String>, stdin: String? = null): ShellResponse {
        println("-- execute: ${commands.joinToString(" ")}")
        val pb = ProcessBuilder(commands)
//    val f = File(path)
//    println("cwd> ${f.absolutePath}")
//    pb.directory(File(f.absolutePath));
        //pb.inheritIO();
        val p = pb
          //  .redirectErrorStream(true)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .start()
        if (stdin != null) {
            p.outputWriter().use {
                it.write(stdin)
                it.flush()
            }
        }
        val error = p.errorStream.bufferedReader().readText()
        val output = p.inputStream.bufferedReader().readText()
        println("...waiting...")
        p.waitFor()

        println("-- Command output >>>>>")
        println(output)
        println("<<<<< Command output")
        if (error.isNotBlank()) {
            println("-- Command error >>>>>")
            println(error)
            println("<<<<< Command error")
        }

        return ShellResponse(0,output, error)
    }

    fun buildRunProgram(): String {
        val content = executeSh("gradle run --args='--run $dayPad'")
        val response = content
//            .substringAfter("Result=").
            .substringAfter("Result") //Result2=1471452
            .substringAfter("=")
            .replaceAfter("\n", "")
            .replace("\n", "")
            .replace(")", "")
            .replace("(", "")
            .trim()
        return response
    }

    fun runProgram(): String {
        val instance = Class.forName("Day$dayPad").getDeclaredConstructor().newInstance()
        val method = instance.javaClass.getMethod("main", Array<String>::class.java)
        val args = arrayOf("$star")
        val result = method.invoke(instance, args)
        // Case main() return value (not the case), result is printed out
        return "(run-program) Result=${result}"
    }

    private fun isNumeric(toCheck: String): Boolean {
        return toCheck.toDoubleOrNull() != null
    }

    companion object {
        fun main(args: Array<String>) {
            println("Starting Advent of Code puzzle solver...")

            val arguments = parseArgs(args)
            println("Arguments: $arguments")
            val day = arguments.getOrDefault("day", "0").toInt()
            val star = arguments.getOrDefault("star", "1").toInt()
            val setup = Setup();
            setup.initDay(day, star)
            //TODO val setup = Setup(day, star);
            if (arguments.containsKey("today")) {
                println(">>Running today's puzzle")
                setup.runToday(star)
            } else if (arguments.containsKey("daemon")) {
                println(">>Starting daemon")
                setup.scheduleDailyTask()
            } else if (arguments.containsKey("run")) {
                println(">>Run program $day")
                val response = setup.runProgram()
                println(response)
            } else if (arguments.containsKey("prompt")) {
                println(">>Request prompt day $day star $star")
                val response = setup.promptFromFile()
                println(response)
            } else if (arguments.containsKey("build")) {
                println(">>Build & run program $day")
                val response = setup.buildRunProgram()
                println(">>Build & run program returned: $response")
            }else if (arguments.containsKey("solution")) {
                val solution = arguments.getOrDefault("solution", "")
                val response = setup.submitSolution(solution)
                println(">>Build & run program returned: $response")
            } else if (arguments.containsKey("day")) {
                println(">>Running day $day star $star")
                setup.runDay()
            }else{
                println(">>No arguments provided, running today's puzzle")
                setup.runToday(star)
            }

        }
    }
}

fun main(args: Array<String>) {
    Setup.main(args)
}

