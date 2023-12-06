package day3.b

fun main() {
    var input = "467..114..\n" +
            "...*......\n" +
            "..35..633.\n" +
            "......#...\n" +
            "617*......\n" +
            ".....+.58.\n" +
            "..592.....\n" +
            "......755.\n" +
            "...\$.*....\n" +
            ".664.598.."
    var ret = calc(input)
    require(ret == 467835) { "Expected 467835, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day3.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

open class Coord(
        val row: Int,
        val column: Int
)

class Symbol(
        row: Int, column: Int, val symbol: Char
) : Coord(row, column) {
    override fun toString(): String {
        return "$row/$column"
    }
}

class Number(
        row: Int, column: Int,
        val value: Int
) : Coord(row, column) {
    override fun toString(): String {
        return "$row,$column:$value"
    }
}

private fun calc(input: String): Int {
    println()

    val coords = input.lines()
            .flatMapIndexed { row, line ->
                parseNumbersAndSymbols(row, line)
            }
    val gearSymbols = coords.flatMap {
        when (it) {
            is Symbol -> listOf(it)
            else -> listOf()
        }
    }.filter { it.symbol == '*' }

    val numbers = coords.flatMap {
        when (it) {
            is Number -> listOf(it)
            else -> listOf()
        }
    }

    return gearSymbols
            .map { getAdjecentNumbers(numbers, it) }
            .map { it.map { n -> n.value } }
            .filter { it.size == 2 }
            .sumOf { it[0] * it[1] }
}

fun getAdjecentNumbers(numbers: List<Number>, symbol: Symbol): List<Number> {
    val adjNumbers = numbers
            .filter { it.row in symbol.row - 1..symbol.row + 1 }
            .filter {
                it.column in symbol.column - it.value.toString().length..symbol.column + 1
            }
    println("$symbol -> $adjNumbers")
    return adjNumbers
}

fun parseNumbersAndSymbols(row: Int, line: String): List<Coord> {
    val fold = line.flatMapIndexed { index, c ->
        when (c) {
            '.' -> listOf(Coord(row, index))
            in '0'..'9' -> listOf(Number(row, index, c - '0'))
            else -> listOf(Symbol(row, index, c))
        }
    }.fold(mutableListOf<Coord>()) { list, coord ->
        if (coord is Number && list.size > 0 && list.last() is Number) {
            // Merge numbers
            val lastCoord = list.removeLast() as Number
            list.add(Number(lastCoord.row, lastCoord.column, lastCoord.value * 10 + coord.value))
        } else {
            list.add(coord)
        }
        list
    }.filter { it is Number || it is Symbol }
    // println("$line -> $fold")
    return fold
}
