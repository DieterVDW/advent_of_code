package day5.b

fun main() {
    var input = "seeds: 79 14 55 13\n" + "\n" + "seed-to-soil map:\n" + "50 98 2\n" + "52 50 48\n" + "\n" + "soil-to-fertilizer map:\n" + "0 15 37\n" + "37 52 2\n" + "39 0 15\n" + "\n" + "fertilizer-to-water map:\n" + "49 53 8\n" + "0 11 42\n" + "42 0 7\n" + "57 7 4\n" + "\n" + "water-to-light map:\n" + "88 18 7\n" + "18 25 70\n" + "\n" + "light-to-temperature map:\n" + "45 77 23\n" + "81 45 19\n" + "68 64 13\n" + "\n" + "temperature-to-humidity map:\n" + "0 69 1\n" + "1 0 69\n" + "\n" + "humidity-to-location map:\n" + "60 56 37\n" + "56 93 4"
    var ret = calc(input)
    require(ret == 46L) { "Expected 46, got ${ret}" }

    input = Thread.currentThread().contextClassLoader.getResourceAsStream("day5.txt").readAllBytes().decodeToString()
    val output = calc(input)
    println(output)
}

data class CategoryMap(
        val from: String,
        val to: String,
        val mappings: MutableList<Mapping> = mutableListOf()
)

data class Mapping(
        val sourceIndex: Long,
        val destinationIndex: Long,
        val num: Long,
        val offset: Long = destinationIndex - sourceIndex,
        val sourceEnd: Long = sourceIndex + (num - 1)
)

data class SeedRange(val start: Long, val end: Long, val type: String, val parent: SeedRange? = null) {
    constructor(parent: SeedRange, type: String) : this(parent.start, parent.end, type, parent)

    init {
        require((parent == null) || ((end - start) <= (parent.end - parent.start))) { "$parent -> $start,$end" }
    }

    override fun toString(): String {
        return "SeedRange(start=$start, end=$end, type=$type)\n\t$parent"
    }
}

private fun calc(input: String): Long {
    println()

    val seedsRanges = input.lines().first().split(":")[1].trim().split(" ").map { it.toLong() }.chunked(2).map {
        SeedRange(it[0], it[0] + it[1] - 1, "seed")
    }

    val categories = input.lines().filter { it.isNotBlank() }.drop(1).fold(mutableListOf<CategoryMap>()) { maps, line ->
        if (line.matches("[0-9]+ [0-9]+ [0-9]+".toRegex())) {
            var values = line.split(" ").map { it.toLong() }
            maps.last().mappings.add(Mapping(values[1], values[0], values[2]))
        } else {
            val matcher = "([a-z]+)-to-([a-z]+) map:".toRegex().matchEntire(line)
            require(matcher != null) { "Can't match line: $line" }
            maps.add(CategoryMap(matcher.groupValues[1], matcher.groupValues[2]))
        }
        maps
    }

    categories.forEach {
        println("${it.from} -> ${it.to}: ${it.mappings}")
    }

    val lowestRange = seedsRanges.map { seedRange ->
        categories.fold(listOf(seedRange)) { input, categoryMap -> applyCategory(input, categoryMap) }
    }

    println(lowestRange.map { it.sortedBy { it.start }.first() })

    return lowestRange.flatten().sortedBy { it.start }.first().start
}

fun applyCategory(input: List<SeedRange>, categoryMap: CategoryMap): List<SeedRange> {
//    println("Apply category ${categoryMap.from} -> ${categoryMap.to}")
    return categoryMap.mappings.fold(input) { seedRanges, map ->
        seedRanges
                .flatMap {
                    when {
                        it.type == categoryMap.from -> applyMap(it, map, categoryMap.from, categoryMap.to)
                        else -> listOf(it)
                    }
                }
    }.map { SeedRange(it, categoryMap.to) }
}

fun applyMap(seedRange: SeedRange, map: Mapping, fromType: String, toType: String): List<SeedRange> {
    val seedRanges = when {
        seedRange.end < map.sourceIndex -> listOf(seedRange)
        seedRange.start < map.sourceIndex && seedRange.end >= map.sourceIndex && seedRange.end <= map.sourceEnd -> listOf(
                SeedRange(seedRange.start, map.sourceIndex - 1, seedRange.type, seedRange),
                SeedRange(map.sourceIndex + map.offset, seedRange.end + map.offset, toType, seedRange)
        )

        seedRange.start < map.sourceIndex && seedRange.end > map.sourceEnd -> listOf(
                SeedRange(seedRange.start, map.sourceIndex - 1, seedRange.type, seedRange),
                SeedRange(map.sourceIndex + map.offset, map.sourceEnd + map.offset, toType, seedRange),
                SeedRange(map.sourceEnd + 1, seedRange.end, seedRange.type, seedRange),
        )

        seedRange.start >= map.sourceIndex && seedRange.end <= map.sourceEnd -> listOf(
                SeedRange(seedRange.start + map.offset, seedRange.end + map.offset, toType, seedRange)
        )

        seedRange.start >= map.sourceIndex && seedRange.start <= map.sourceEnd && seedRange.end > map.sourceEnd -> listOf(
                SeedRange(seedRange.start + map.offset, map.sourceEnd + map.offset, toType, seedRange),
                SeedRange(map.sourceEnd + 1, seedRange.end, seedRange.type, seedRange),
        )

        seedRange.start > map.sourceEnd -> listOf(seedRange)

        else -> throw IllegalStateException("$seedRange $map")
    }
    require(seedRanges.map { it.end - it.start + 1 }.sum() == seedRange.end - seedRange.start + 1) {
        "INPUT: $seedRange -> MAP: $map -> OUTPUT: $seedRanges"
    }
    return seedRanges
}

