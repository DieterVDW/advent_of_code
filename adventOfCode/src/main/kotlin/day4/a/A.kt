package day4.a

fun main() {
    var input = "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53\n" +
            "Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19\n" +
            "Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1\n" +
            "Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83\n" +
            "Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36\n" +
            "Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"
    var ret = calc(input)
    require(ret == 13) { "Expected 13, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day4.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

data class Card(
        var num: Int,
        var winningNumbers: List<Int>,
        var ownNumbers: List<Int>
)


private fun calc(input: String): Int {
    println()

    return input.lines()
            .filter { it.isNotBlank() }
            .map {
                println(it)
                Card(
                        it.split(":")[0].split(" +".toRegex())[1].toInt(),
                        splitNumbers(it.split(":")[1].split("|")[0]),
                        splitNumbers(it.split(":")[1].split("|")[1]),
                )
            }
            .map { it.score() }
            .sum()
}

fun splitNumbers(numString: String): List<Int> =
        numString.trim().split(" +".toRegex())
                .filter { it.isNotBlank() }
                .map { it.toInt() }

fun Card.score(): Int {
    val num = winningNumbers.intersect(ownNumbers).size
    val score = when (num) {
        0 -> 0
        1 -> 1
        else -> Math.pow(2.0, num - 1.0).toInt()
    }
    println("$winningNumbers $ownNumbers $num $score")
    return score
}
