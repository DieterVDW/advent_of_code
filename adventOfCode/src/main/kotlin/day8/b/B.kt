package day8.b

fun main() {
    var input = "LR\n" +
            "\n" +
            "11A = (11B, XXX)\n" +
            "11B = (XXX, 11Z)\n" +
            "11Z = (11B, XXX)\n" +
            "22A = (22B, XXX)\n" +
            "22B = (22C, 22C)\n" +
            "22C = (22Z, 22Z)\n" +
            "22Z = (22B, 22B)\n" +
            "XXX = (XXX, XXX)"
    var ret = calc(input)
    require(ret == 6L) { "Expected 6, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day8.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

fun calc(input: String): Long {
    val leftRights = input.lines().first()
    println(leftRights)

    val mappings = input.lines()
            .drop(2)
            .filter { it.isNotBlank() }
            .map {
                val matcher = "(?<from>[A-Z0-9]{3}) = \\((?<left>[A-Z0-9]{3}), (?<right>[A-Z0-9]{3})\\)".toRegex().matchEntire(it)
                val from = matcher!!.groupValues[1]
                val left = matcher!!.groupValues[2]
                val right = matcher!!.groupValues[3]
                println("$from -> ($left, $right)")
                from to Pair(left, right)
            }
            .toMap()

    val startPoints = mappings.keys.filter { it.endsWith("A") }
    startPoints.forEach { println("StartPoint: $it") }

    val walkContexts = startPoints.map { WalkContext(it, mappings, leftRights) }
    val periods = walkContexts.map {
        while (it.cycleDetails == null) {
            it.goToNextEndPosition()
        }
        it.cycleDetails!!.second
    }

    return lcmOfSet(periods)
}

fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) a else gcd(b, a % b)
}

fun lcm(a: Long, b: Long): Long {
    return if (a == 0L || b == 0L) 0 else Math.abs(a * b) / gcd(a, b)
}

fun lcmOfSet(numbers: List<Long>): Long {
    if (numbers.isEmpty()) {
        throw IllegalArgumentException("Input list is empty")
    }

    var result = numbers[0]

    for (i in 1 until numbers.size) {
        result = lcm(result, numbers[i])
    }

    return result
}

class WalkContext(
        val start: String,
        val mappings: Map<String, Pair<String, String>>,
        val leftRights: String
) {
    var currentCount = 0L
    var currentPos = start
    val visitedEndPoints = mutableMapOf<Pair<String, Long>, Long>() // <map_position, <map_key, lr_position>>
    var cycleDetails: Pair<Long, Long>? = null // <start_count, period>

    fun goToNextEndPosition() {
        if (cycleDetails != null) {
            currentCount += cycleDetails!!.second
        } else {
            do {
                val lOrR = leftRights[(currentCount % leftRights.length).toInt()]
                currentPos = when (lOrR) {
                    'L' -> mappings[currentPos]!!.first
                    'R' -> mappings[currentPos]!!.second
                    else -> throw IllegalStateException("Expected L or R")
                }
                currentCount++
            } while (!currentPos.endsWith("Z"))

            val currentState = Pair(currentPos, currentCount % leftRights.length)
            if (visitedEndPoints.containsKey(currentState) && visitedEndPoints[currentState] != currentCount) {
                cycleDetails = Pair(visitedEndPoints[currentState]!!, currentCount - visitedEndPoints[currentState]!!)
                println("[$currentPos] CyclePeriod of ${start} is ${cycleDetails!!.second}, started from ${cycleDetails!!.first}")
            } else {
                visitedEndPoints[currentState] = currentCount
            }
        }
    }
}
