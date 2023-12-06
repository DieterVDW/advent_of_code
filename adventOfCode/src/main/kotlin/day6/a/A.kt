package day6.a

fun main() {
    var input = "Time:      7  15   30\n" +
            "Distance:  9  40  200"
    var ret = calc(input)
    require(ret == 288) { "Expected 288, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day6.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

fun calc(input: String): Int {
    val times = input.lines()
            .filter { it.startsWith("Time:") }
            .map { it.split(":")[1].trim() }
            .flatMap { it.split(" +".toRegex()) }
            .map { it.toInt() }
    val distances = input.lines()
            .filter { it.startsWith("Distance:") }
            .map { it.split(":")[1].trim() }
            .flatMap { it.split(" +".toRegex()) }
            .map { it.toInt() }
    require(times.size == distances.size)

    val races = times.zip(distances)

    return races.map { calculateWinningOptions(it) }
            .fold(1) { a, b -> a * b }
}

fun calculateWinningOptions(race: Pair<Int, Int>): Int {
    val time = race.first
    val currentRecord = race.second

    return IntRange(0, time)
            .map { calculateDistance(it, time) }
            .filter { it > currentRecord }
            .count()
}

fun calculateDistance(timePressed: Int, raceTime: Int): Int {
    return (raceTime - timePressed) * timePressed
}


