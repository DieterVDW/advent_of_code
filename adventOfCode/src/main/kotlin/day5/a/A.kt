package day5.a

fun main() {
    var input = "seeds: 79 14 55 13\n" +
            "\n" +
            "seed-to-soil map:\n" +
            "50 98 2\n" +
            "52 50 48\n" +
            "\n" +
            "soil-to-fertilizer map:\n" +
            "0 15 37\n" +
            "37 52 2\n" +
            "39 0 15\n" +
            "\n" +
            "fertilizer-to-water map:\n" +
            "49 53 8\n" +
            "0 11 42\n" +
            "42 0 7\n" +
            "57 7 4\n" +
            "\n" +
            "water-to-light map:\n" +
            "88 18 7\n" +
            "18 25 70\n" +
            "\n" +
            "light-to-temperature map:\n" +
            "45 77 23\n" +
            "81 45 19\n" +
            "68 64 13\n" +
            "\n" +
            "temperature-to-humidity map:\n" +
            "0 69 1\n" +
            "1 0 69\n" +
            "\n" +
            "humidity-to-location map:\n" +
            "60 56 37\n" +
            "56 93 4"
    var ret = calc(input)
    require(ret == 35L) { "Expected 35, got ${ret}" }

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
        val destinationIndex: Long,
        val sourceIndex: Long,
        val num: Long
)

private fun calc(input: String): Long {
    println()

    val seeds = input.lines().first().split(":")[1].trim().split(" ").map { it.toLong() }

    val maps = input.lines()
            .filter { it.isNotBlank() }
            .drop(1)
            .fold(mutableListOf<CategoryMap>()) { maps, line ->
                if (line.matches("[0-9]+ [0-9]+ [0-9]+".toRegex())) {
                    var values = line.split(" ").map { it.toLong() }
                    maps.last().mappings.add(Mapping(values[0], values[1], values[2]))
                } else {
                    val matcher = "([a-z]+)-to-([a-z]+) map:".toRegex().matchEntire(line)
                    require(matcher != null) { "Can't match line: $line" }
                    maps.add(CategoryMap(matcher.groupValues[1], matcher.groupValues[2]))
                }
                maps
            }

    return seeds.map { seed ->
        println()
        seed to maps.fold(seed) { input, categoryMap ->
            val new = applyMap(input, categoryMap)
            println("${categoryMap.from} -> ${categoryMap.to}: $input -> $new")
            new
        }
    }.map { pair ->
        println(pair)
        pair
    }.minOf { it.second }
}

fun applyMap(input: Long, categoryMap: CategoryMap): Long {
    val matchingMappings = categoryMap.mappings
            .filter { input in it.sourceIndex..it.sourceIndex + (it.num - 1) }
    return when (matchingMappings.size) {
        0 -> input
        1 -> (input - matchingMappings[0].sourceIndex) + matchingMappings[0].destinationIndex
        else -> throw IllegalStateException("Matching mappings: $matchingMappings")
    }
}

