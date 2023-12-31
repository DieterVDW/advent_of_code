package day2.a

fun main() {
    var input = "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green\n" + "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue\n" + "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red\n" + "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red\n" + "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"
    var ret = calc(input, 12, 13, 14)
    require(ret == 8) { "Expected 8, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day2.txt").readAllBytes().decodeToString()
    val output = calc(input, 12, 13, 14)
    println(output)
}

data class Try(val red: Int, val green: Int, val blue: Int)

data class Game(val num: Int, val tries: List<Try>)

private fun calc(input: String, numRed: Int, numGreen: Int, numBlue: Int): Int {
    println()

    return input.lines()
            .filter { it.isNotBlank() }
            .map { parseGame(it) }
            .filter { isGamePossibleWithBalls(it, numRed, numGreen, numBlue) }
            .map { it.num }
            .sum()
}

fun isGamePossibleWithBalls(game: Game, numRed: Int, numGreen: Int, numBlue: Int): Boolean =
        game.tries.all {
            it.red <= numRed
                    && it.green <= numGreen
                    && it.blue <= numBlue
        }

fun parseGame(it: String): Game {
    val game = it.split(":")[0].split(" ")[1].toInt()
    val tries = it.split(": ")[1].split(";").map {
        // Parse balls in one try
        it.split(",")
                .map { it.trim() }
                .map {
                    // Parse "3 blue"
                    val num = it.split(" ")[0].toInt()
                    val color = it.split(" ")[1]
                    when (color) {
                        "red" -> Try(num, 0, 0)
                        "green" -> Try(0, num, 0)
                        "blue" -> Try(0, 0, num)
                        else -> throw IllegalStateException("Unknown color ${color}")
                    }
                }.reduce { a, b ->
                    Try(a.red + b.red, a.green + b.green, a.blue + b.blue)
                }

    }
    return Game(game, tries)
}
