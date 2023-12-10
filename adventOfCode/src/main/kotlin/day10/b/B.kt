package day10.b

fun main() {
    var input = ""
    var ret = 0

    input = ".....\n" +
            ".S-7.\n" +
            ".|.|.\n" +
            ".L-J.\n" +
            "....."
    ret = calc(input)
    require(ret == 1) { "Expected 1, got ${ret}" }

    input = "..F7.\n" +
            ".FJ|.\n" +
            "SJ.L7\n" +
            "|F--J\n" +
            "LJ..."
    ret = calc(input)
    require(ret == 1) { "Expected 1, got ${ret}" }

    input = "...........\n" +
            ".S-------7.\n" +
            ".|F-----7|.\n" +
            ".||.....||.\n" +
            ".||.....||.\n" +
            ".|L-7.F-J|.\n" +
            ".|..|.|..|.\n" +
            ".L--J.L--J.\n" +
            "..........."
    ret = calc(input)
    require(ret == 4) { "Expected 4, got ${ret}" }

    input = "..........\n" +
            ".S------7.\n" +
            ".|F----7|.\n" +
            ".||....||.\n" +
            ".||....||.\n" +
            ".|L-7F-J|.\n" +
            ".|..||..|.\n" +
            ".L--JL--J.\n" +
            ".........."
    ret = calc(input)
    require(ret == 4) { "Expected 4, got ${ret}" }

    input = ".F----7F7F7F7F-7....\n" +
            ".|F--7||||||||FJ....\n" +
            ".||.FJ||||||||L7....\n" +
            "FJL7L7LJLJ||LJ.L-7..\n" +
            "L--J.L7...LJS7F-7L7.\n" +
            "....F-J..F7FJ|L7L7L7\n" +
            "....L7.F7||L7|.L7L7|\n" +
            ".....|FJLJ|FJ|F7|.LJ\n" +
            "....FJL-7.||.||||...\n" +
            "....L---J.LJ.LJLJ..."
    ret = calc(input)
    require(ret == 8) { "Expected 8, got ${ret}" }

    input = "FF7FSF7F7F7F7F7F---7\n" +
            "L|LJ||||||||||||F--J\n" +
            "FL-7LJLJ||||||LJL-77\n" +
            "F--JF--7||LJLJ7F7FJ-\n" +
            "L---JF-JLJ.||-FJLJJ7\n" +
            "|F|F-JF---7F7-L7L|7|\n" +
            "|FFJF7L7F-JF7|JL---7\n" +
            "7-L-JL7||F7|L7F-7F7|\n" +
            "L.L7LFJ|||||FJL7||LJ\n" +
            "L7JLJL-JLJLJL--JLJ.L"
    ret = calc(input)
    require(ret == 10) { "Expected 10, got ${ret}" }

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

    val nonPathTiles = maze.flatMapIndexed { rowNum, row ->
        row.mapIndexed { colNum, _ -> Position(rowNum, colNum) }
    }.filter { it !in visited }
            .toMutableSet()

    val pools = mutableListOf<Set<Position>>()
    while (nonPathTiles.isNotEmpty()) {
        var pool = mutableListOf(nonPathTiles.first())
        nonPathTiles.remove(nonPathTiles.first())
        do {
            val beginPoolSize = pool.size
            growPool(pool, nonPathTiles)

        } while (pool.size != beginPoolSize)
        pools.add(pool.toSet())
    }

    return pools.filter { pool ->
        pool.none { isTouchingOuter(maze, it) }
    }.sumOf { it.size }
}

// Take touching tiles from nonPathTiles and add them to the pool
fun growPool(pool: MutableList<Position>, nonPathTiles: MutableSet<Position>) {
    val newTiles = pool.flatMap {// Map each tile to its potential neighbours
        listOf(
                Position(it.row - 1, it.column),
                Position(it.row + 1, it.column),
                Position(it.row, it.column - 1),
                Position(it.row, it.column + 1),
        )
    }.filter { // Filter out ones already in the pool
        it !in pool
    }.intersect(nonPathTiles)

    nonPathTiles.removeIf { it in newTiles }
    pool.addAll(newTiles)
}

fun isTouchingOuter(maze: List<String>, it: Position): Boolean =
        it.row == 0
                || it.column == 0
                || it.row == maze.size - 1
                || it.column == maze[0].length - 1

enum class Directions {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

fun getNextSteps(maze: List<String>, visited: MutableSet<Position>, pos: Position): List<Position> =
        listOf(
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

fun getConnectingChars(dir: Directions): Set<Char> =
        when (dir) {
            Directions.UP -> setOf('|', '7', 'F')
            Directions.DOWN -> setOf('|', 'L', 'J')
            Directions.LEFT -> setOf('-', 'L', 'F')
            Directions.RIGHT -> setOf('-', '7', 'J')
        }
