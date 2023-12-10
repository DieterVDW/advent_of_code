package day9.a

fun main() {
    var input = "0 3 6 9 12 15\n" +
            "1 3 6 10 15 21\n" +
            "10 13 16 21 30 45"
    var ret = calc(input)
    require(ret == 114) { "Expected 114, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day9.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

fun calc(input: String): Int {
    return input.lines()
            .filter { it.isNotBlank() }
            .map { it.split(" ").map(String::toInt) }
            .sumOf { predictNextNumber(it) }
}

fun predictNextNumber(series: List<Int>): Int {
    if (series.all { it == 0 }) {
        return 0
    } else {
        val differences = series.zipWithNext().map { it.second - it.first }
        return series.last() + predictNextNumber(differences)
    }
}
