fun main() {
    fun part1(input: List<String>): Int {
        val durations = input[0].findEntries().map { it.toLong() }
        val distances = input[1].findEntries().map { it.toLong() }
        return durations.zip(distances)
            .map { (duration, distance) -> buttonPressTime(duration, distance) }
            .reduce { acc, count -> acc * count }
    }

    fun part2(input: List<String>): Int {
        val duration = input[0].findEntries().joinToString("").toLong()
        val distance = input[1].findEntries().joinToString("").toLong()
        return buttonPressTime(duration, distance)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 4 * 8 * 9)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

fun String.findEntries() = replace("\\w+:\\s+".toRegex(), "").split("\\s+".toRegex())

fun buttonPressTime(raceDuration: Long, recordDistance: Long): Int =
    (1..raceDuration)
        .count { time -> time * (raceDuration - time) > recordDistance }
