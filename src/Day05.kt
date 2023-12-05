import kotlin.math.min

fun main() {

    fun part1(input: List<String>): Long {
        val deque = ArrayDeque(input)
        val seeds = deque.removeFirst().split(' ').drop(1).map { it.toLong() }
        val maps = deque.toMaps()
        return seeds.minOf { seed -> maps[seed] }
    }

    fun part2(input: List<String>): Long {
        val deque = ArrayDeque(input)
        val seeds: List<LongRange> = deque.removeFirst().split(' ')
            .drop(1)
            .map { it.toLong() }
            .chunked(2)
            .map { (start, length) -> start..<(start + length) }
        val maps = deque.toMaps()

        return seeds.fold(Long.MAX_VALUE) { min, range ->
            min(min, range.minOf { seed -> maps[seed] })
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == minOf(82L, 43, 86, 35))
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

fun ArrayDeque<String>.toMaps(): Maps {
    fun buildRangeMap(deque: ArrayDeque<String>): List<MapRange> = buildList {
        while (deque.isNotEmpty() && deque.first().isNotBlank()) {
            val line = deque.removeFirst()
            val (to, from, count) = line.split(' ').map { it.toLong() }
            add(MapRange.from(from, to, count))
        }
    }

    fun ArrayDeque<String>.removeFirstTwoElements() {
        removeFirst()
        removeFirst()
    }

    removeFirstTwoElements()
    val seedToSoil = buildRangeMap(this)
    removeFirstTwoElements()
    val soilToFertiliser = buildRangeMap(this)
    removeFirstTwoElements()
    val fertiliserToWater = buildRangeMap(this)
    removeFirstTwoElements()
    val waterToLight = buildRangeMap(this)
    removeFirstTwoElements()
    val lightToTemperature = buildRangeMap(this)
    removeFirstTwoElements()
    val temperatureToHumidity = buildRangeMap(this)
    removeFirstTwoElements()
    val humidityToLocation = buildRangeMap(this)
    return Maps(
        humidityToLocation,
        temperatureToHumidity,
        lightToTemperature,
        waterToLight,
        fertiliserToWater,
        soilToFertiliser,
        seedToSoil
    )
}

operator fun List<MapRange>.get(key: Long): Long =
    key + (find { range -> key in range.keyRange }?.deltaValue ?: 0)

data class MapRange(val keyRange: LongRange, val deltaValue: Long) {
    companion object {
        fun from(to: Long, from: Long, count: Long): MapRange {
            return MapRange(to..<(to + count), from - to)
        }
    }
}

class Maps(
    private val humidityToLocation: List<MapRange>,
    private val temperatureToHumidity: List<MapRange>,
    private val lightToTemperature: List<MapRange>,
    private val waterToLight: List<MapRange>,
    private val fertiliserToWater: List<MapRange>,
    private val soilToFertiliser: List<MapRange>,
    private val seedToSoil: List<MapRange>
) {
    operator fun get(seed: Long): Long {
        return humidityToLocation[
            temperatureToHumidity[
                lightToTemperature[
                    waterToLight[
                        fertiliserToWater[
                            soilToFertiliser[
                                seedToSoil[seed]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }
}
