package day10.a

fun main() {
    var input = ".....\n" +
            ".S-7.\n" +
            ".|.|.\n" +
            ".L-J.\n" +
            "....."
    var ret = calc(input)
    require(ret == 4) { "Expected 4, got ${ret}" }

    input = "..F7.\n" +
            ".FJ|.\n" +
            "SJ.L7\n" +
            "|F--J\n" +
            "LJ..."
    ret = calc(input)
    require(ret == 8) { "Expected 8, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day10.txt")!!.readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

data class Position(
        val row: Int,
        val column: Int,
)

fun calc(input: String): Int {
    val maze = input.lines()
            .filter { it.isNotBlank() }

    val startPoint = maze.withIndex()
            .filter { it.value.contains("S") }
            .map { Position(it.index, it.value.indexOf("S")) }
            .first()

    var currentStep = 0
    val visited: MutableSet<Position> = mutableSetOf(startPoint)
    var currentSteps = getNextSteps(maze, visited, startPoint)
    while (currentSteps.isNotEmpty()) {
        visited.addAll(currentSteps)
        currentSteps = currentSteps.flatMap {
            getNextSteps(maze, visited, it)
        }
        currentStep++
    }

    return currentStep
}

enum class Directions {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

fun getNextSteps(maze: List<String>, visited: MutableSet<Position>, pos: Position): List<Position> {

    val nextSteps = listOf(
            // Try all directions
            Position(pos.row - 1, pos.column) to Directions.UP,
            Position(pos.row + 1, pos.column) to Directions.DOWN,
            Position(pos.row, pos.column - 1) to Directions.LEFT,
            Position(pos.row, pos.column + 1) to Directions.RIGHT,
    ).filter {// Filter outside of maze positions
        val pos = it.first
        pos.row >= 0 && pos.column >= 0 && pos.row < maze.size && pos.column < maze[0].length
    }.filter {// Filter only connecting pipe tiles
        val pos = it.first
        val symbol = maze[pos.row][pos.column]
        symbol in getConnectingChars(it.second)
    }.map {
        it.first
    }.filter { // Don't go back
        it !in visited
    }
    return nextSteps
}

fun getConnectingChars(dir: Directions): Set<Char> =
        when (dir) {
            Directions.UP -> setOf('|', '7', 'F')
            Directions.DOWN -> setOf('|', 'L', 'J')
            Directions.LEFT -> setOf('-', 'L', 'F')
            Directions.RIGHT -> setOf('-', '7', 'J')
        }
