fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line -> line.calibrationValue() }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line -> line.translateSpelledOutNumbers().calibrationValue() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11 + 12 + 22 + 33 + 42 + 24 + 77 + 99)
    check(part2(testInput) == 29 + 83 + 13 + 24 + 42 + 14 + 76 + 58)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

val translationTable = mapOf(
    "one" to "1",
    "two" to "2",
    "three" to "3",
    "four" to "4",
    "five" to "5",
    "six" to "6",
    "seven" to "7",
    "eight" to "8",
    "nine" to "9"
)

private fun String.translateSpelledOutNumbers(): String =
    translationTable.entries.fold(this) { result, (string, number) ->
        result.replace(string, string.first() + number + string.last())
    }

private fun String.calibrationValue(): Int {
    val digits = filter { char -> char.isDigit() }
    return (digits.first().toString() + digits.last()).toInt()
}
