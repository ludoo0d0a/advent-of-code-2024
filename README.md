# advent-of-code-2024

Welcome to the Advent of Code[^aoc] Kotlin project created by [ludoo0d0a][github] using the [Advent of Code Kotlin Template][template] delivered by JetBrains.

In this repository, ludoo0d0a is about to provide solutions for the puzzles using [Kotlin][kotlin] language.

If you're stuck with Kotlin-specific questions or anything related to this template, check out the following resources:

- [Kotlin docs][docs]
- [Kotlin Slack][slack]
- Template [issue tracker][issues]


[^aoc]:
    [Advent of Code][aoc] – An annual event of Christmas-oriented programming challenges started December 2015.
    Every year since then, beginning on the first day of December, a programming puzzle is published every day for twenty-five days.
    You can solve the puzzle and provide an answer using the language of your choice.

[aoc]: https://adventofcode.com
[docs]: https://kotlinlang.org/docs/home.html
[github]: https://github.com/ludoo0d0a
[issues]: https://github.com/kotlin-hands-on/advent-of-code-kotlin-template/issues
[kotlin]: https://kotlinlang.org
[slack]: https://surveys.jetbrains.com/s3/kotlin-slack-sign-up
[template]: https://github.com/kotlin-hands-on/advent-of-code-kotlin-template



TODO
write a script , schedule it every day at 6 PM, to query puzzle of the day from url 'https://adventofcode.com/2024/day/XX' where XX is the current day of the month. Parse html to extract text only of first puzzle located in first article tag, under main tag. create a file aside from the content of url https://adventofcode.com/2024/day/XX/input where XX is the current day of the month. The script should query Cody to solve problem in Kotlin