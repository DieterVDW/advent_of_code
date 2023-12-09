package day8.a

fun main() {
    var input = "RL\n" +
            "\n" +
            "AAA = (BBB, CCC)\n" +
            "BBB = (DDD, EEE)\n" +
            "CCC = (ZZZ, GGG)\n" +
            "DDD = (DDD, DDD)\n" +
            "EEE = (EEE, EEE)\n" +
            "GGG = (GGG, GGG)\n" +
            "ZZZ = (ZZZ, ZZZ)"
    var ret = calc(input)
    require(ret == 2) { "Expected 2, got ${ret}" }

    input = "LLR\n" +
            "\n" +
            "AAA = (BBB, BBB)\n" +
            "BBB = (AAA, ZZZ)\n" +
            "ZZZ = (ZZZ, ZZZ)\n"
    ret = calc(input)
    require(ret == 6) { "Expected 6, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day8.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

fun calc(input: String): Int {
    val leftRights = input.lines().first()
    println(leftRights)

    val mappings = input.lines()
            .drop(2)
            .filter { it.isNotBlank() }
            .map {
                val matcher = "(?<from>[A-Z]{3}) = \\((?<left>[A-Z]{3}), (?<right>[A-Z]{3})\\)".toRegex().matchEntire(it)
                val from = matcher!!.groupValues[1]
                val left = matcher!!.groupValues[2]
                val right = matcher!!.groupValues[3]
                println("$from -> ($left, $right)")
                from to Pair(left, right)
            }
            .toMap()

    var current = "AAA"
    var count = 0
    while (current != "ZZZ") {

        when (leftRights[count % leftRights.length]) {
            'L' -> current = mappings[current]!!.first
            'R' -> current = mappings[current]!!.second
            else -> throw IllegalStateException("Expected L or R")
        }
        println(current)
        count++
    }

    println(count)
    return count
}
