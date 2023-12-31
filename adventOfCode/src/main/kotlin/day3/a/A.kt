package day3.a

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
    require(ret == 4361) { "Expected 4361, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day3.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

open class Coord(
        val row: Int,
        val column: Int
)

class Symbol(
        row: Int, column: Int
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
    val symbols = coords.flatMap {
        when (it) {
            is Symbol -> listOf(it)
            else -> listOf()
        }
    }
    val numbers = coords.flatMap {
        when (it) {
            is Number -> listOf(it)
            else -> listOf()
        }
    }

    return numbers
            .filter { hasAdjecentSymbol(it, symbols) }
            .map { it.value }
            .sum()
}

fun hasAdjecentSymbol(number: Number, symbols: List<Symbol>): Boolean {
    val hasAdjecentSymbol = symbols.any {
        it.row in number.row - 1..number.row + 1
                && it.column in number.column - 1..(number.column + number.value.toString().length)
    }
    return hasAdjecentSymbol
}

fun parseNumbersAndSymbols(row: Int, line: String): List<Coord> {
    val fold = line.flatMapIndexed { index, c ->
        when (c) {
            '.' -> listOf(Coord(row, index))
            in '0'..'9' -> listOf(Number(row, index, c - '0'))
            else -> listOf(Symbol(row, index))
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
