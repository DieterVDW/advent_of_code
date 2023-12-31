package day2.b

fun main() {
    var input = "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green\n" + "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue\n" + "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red\n" + "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red\n" + "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"
    var ret = calc(input)
    require(ret == 2286) { "Expected 2286, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day2.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

data class Try(val red: Int, val green: Int, val blue: Int)

data class Game(val num: Int, val tries: List<Try>)

private fun calc(input: String): Int {
    println()

    return input.lines()
            .asSequence()
            .filter { it.isNotBlank() }
            .map { parseGame(it) }
            .map { minimumBalls(it) }
            .map { it.power() }
            .sum()
}

private fun Try.power(): Int {
    return red * green * blue
}

fun minimumBalls(game: Game): Try {
    return Try(
            red = game.tries.map { it.red }.max(),
            green = game.tries.map { it.green }.max(),
            blue = game.tries.map { it.blue }.max(),
    )
}

fun parseGame(it: String): Game {
    val game = it.split(":")[0].split(" ")[1].toInt()
    val tries = it.split(": ")[1].split(";").map {
        // Parse balls in one try
        it.split(",").map { it.trim() }.map {
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
