package day6.b

fun main() {
    var input = "Time:      7  15   30\n" +
            "Distance:  9  40  200"
    var ret = calc(input)
    require(ret == 71503) { "Expected 288, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day6.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

fun calc(input: String): Int {
    val time = input.lines()
            .filter { it.startsWith("Time:") }
            .map { it.split(":")[1].trim().replace(" ", "") }
            .joinToString().toLong()
    val distance = input.lines()
            .filter { it.startsWith("Distance:") }
            .map { it.split(":")[1].trim().replace(" ", "") }
            .joinToString().toLong()

    return calculateWinningOptions(time, distance)
}

fun calculateWinningOptions(time: Long, currentRecord: Long): Int {
    return LongRange(0, time)
            .map { calculateDistance(it, time) }
            .filter { it > currentRecord }
            .count()
}

fun calculateDistance(timePressed: Long, raceTime: Long): Long {
    return (raceTime - timePressed) * timePressed
}


