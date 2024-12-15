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



## TODO

> npm install -g @sourcegraph/cody

```bash
export SRC_ENDPOINT=ENDPOINT
export SRC_ACCESS_TOKEN=ACCESS_TOKEN
cody auth login

cody auth whoami

cody chat --context-file src/Day01.kt,src/Day02.kt,... -m 'write kotlin code for this problem: ...'
```

To run : 
```
export OUTPUT_DIR=.\out\production\advent
export USER_HOME=~
export MVN_HOME=$USER_HOME\.m2\repository
export JAVA_HOME=$USER_HOME\scoop\apps\corretto-lts-jdk\current

$JAVA_HOME\bin\java.exe 
-Dfile.encoding=UTF-8 -classpath $OUTPUT_DIR;
$MVN_HOME\org\jetbrains\kotlin\kotlin-stdlib\2.0.21\kotlin-stdlib-2.0.21.jar;
$MVN_HOME\org\jetbrains\annotations\13.0\annotations-13.0.jar;
$MVN_HOME\org\jetbrains\kotlin\kotlin-stdlib-jdk8\2.0.21\kotlin-stdlib-jdk8-2.0.21.jar;
$MVN_HOME\org\jetbrains\kotlin\kotlin-stdlib-jdk7\2.0.21\kotlin-stdlib-jdk7-2.0.21.jar 
Day01
```

or
```
gradle run --args="--day 11"
```




# TODO
store tentatives results in a file
retry if not success, adding 'suggest another alternative' in prompt