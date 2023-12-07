package day7.a

import java.lang.IllegalStateException
import java.util.Comparator

fun main() {
    var input = "32T3K 765\n" +
            "T55J5 684\n" +
            "KK677 28\n" +
            "KTJJT 220\n" +
            "QQQJA 483"
    var ret = calc(input)
    require(ret == 6440L) { "Expected 6440, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day7.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

object cardComparator : Comparator<Pair<List<Card>, Int>> {
    override fun compare(a: Pair<List<Card>, Int>, b: Pair<List<Card>, Int>): Int {
        val valA = determineCardsValue(a.first)
        val valB = determineCardsValue(b.first)

        if (valA != valB) {
            return valA - valB
        } else {
            val highestCards = a.first.zip(b.first)
                    .first { it.first.value != it.second.value }
            return highestCards.first.value - highestCards.second.value
        }
    }
}

fun determineCardsValue(cards: List<Card>): Int {
    return when {
        cards.groupBy { it.value }.mapValues { it.value.count() }.size == 1 -> 7 // Five of a kind
        cards.groupBy { it.value }.mapValues { it.value.count() }.containsValue(4) -> 6 // Four of a kind
        cards.groupBy { it.value }.mapValues { it.value.count() }.size == 2 -> 5 // Full house
        cards.groupBy { it.value }.mapValues { it.value.count() }.containsValue(3) -> 4 // Three of a kind
        cards.groupBy { it.value }.mapValues { it.value.count() }.filterValues { it == 2 }.count() == 2 -> 3 // Double pair
        cards.groupBy { it.value }.mapValues { it.value.count() }.filterValues { it == 2 }.count() == 1 -> 2 // One pair
        else -> 1
    }
}

fun calc(input: String): Long {
    return input.lines()
            .filter { it.isNotBlank() }
            .map {
                val cards = it.split(" ")[0].map { c -> parseCard(c) }
                val bid = it.split(" ")[1].toInt()
                cards to bid
            }
            .sortedWith(cardComparator)
            .mapIndexed { index, pair ->
                println("[${index + 1}] ${pair.first.map { it.value }} -> ${determineCardsValue(pair.first)} * ${pair.second}")
                (index + 1) * pair.second
            }.sumOf { it.toLong() }
}

data class Card(
    val value: Int
)

fun parseCard(c: Char): Card {
    val value = when (c) {
        in '2' .. '9' -> c.digitToInt()
        'T' -> 10
        'J' -> 11
        'Q' -> 12
        'K' -> 13
        'A' -> 14
        else -> throw IllegalStateException("Unknown card: $c")
    }
    return Card(value)
}
