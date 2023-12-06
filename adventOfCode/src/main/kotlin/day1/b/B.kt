package day1.b

fun main() {
    var input = "1abc2\n" +
            "pqr3stu8vwx\n" +
            "a1b2c3d4e5f\n" +
            "treb7uchet\n"
    var ret = calc(input)
    require(ret == 142) { "Expected 142, got ${ret}" }

    input = "two1nine\n" +
            "eightwothree\n" +
            "abcone2threexyz\n" +
            "xtwone3four\n" +
            "4nineeightseven2\n" +
            "zoneight234\n" +
            "7pqrstsixteen"
    ret = calc(input)
    require(ret == 281) { "Expected 281, got ${ret}" }

    ret = calc("eighthree")
    require(ret == 83) { "Expected 83, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day1.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

private fun calc(input: String): Int {
    println()
    val replacements = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
            "6" to 6,
            "7" to 7,
            "8" to 8,
            "9" to 9
    )
    val output = input.lines()
            .filter { it.isNotBlank() }
            .map { line ->
                val firstDigit = replacements.entries
                        .filter { entry -> line.indexOf(entry.key) >= 0 }
                        .sortedBy { entry -> line.indexOf(entry.key) }
                        .map { entry -> entry.value }.first()
                val lastDigit = replacements.entries
                        .filter { entry -> line.reversed().indexOf(entry.key.reversed()) >= 0 }
                        .sortedBy { entry -> line.reversed().indexOf(entry.key.reversed()) }
                        .map { entry -> entry.value }.first()
                val num = firstDigit * 10 + lastDigit
                println("$line -> $num")
                num
            }
            .sum()
    return output
}
