fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val values = parseElements(line)
            val history = buildHistory(values)
            predictNext(previous = history.last(), next = 0).value
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val values = parseElements(line)
            val history = buildHistory(values)
            predictPrevious(previous = 0, next = history.first()).value
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 18 + 28 + 68)
    check(part2(testInput) == -3 + 0 + 5)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

fun parseElements(line: String) = line.split(' ').map { HistoryLeaf(it.toInt()) }

/**
    10  13  16  21  30  45
      03  03  05  09  15
        00  02  04  06
          02  02  02
            00  00
 */
tailrec fun buildHistory(elements: List<HistoryElement>): List<HistoryNode> {
    val next: List<HistoryNode> = elements.windowed(2)
        .map { (left, right) ->
            HistoryNode(right.value - left.value, left, right)
        }
    return if (next.distinct().all { it.value == 0 }) next
    else buildHistory(next)
}

/**
    02  02,   -> 04  06,   -> 09  15,   -> 30  45,   -> 45,68 -> 68
      00   00      02   02      06   08      15   23
 */
fun predictNext(previous: HistoryElement, next: Int): HistoryLeaf {
    return when (previous) {
        is HistoryLeaf -> HistoryLeaf(next)
        is HistoryNode -> predictNext(previous.right, next + previous.right.value)
    }
}

/**
      ,02  02   ->   ,00  02   ->   ,03  13   ->   ,10  13 -> 5,10 -> 5
    00   00        02   02        -2   00        05   03
 */
fun predictPrevious(previous: Int, next: HistoryElement): HistoryLeaf {
    return when (next) {
        is HistoryLeaf -> HistoryLeaf(previous)
        is HistoryNode -> predictPrevious(next.left.value - previous, next.left)
    }
}

sealed interface HistoryElement {
    val value: Int
}

data class HistoryLeaf(override val value: Int) : HistoryElement

data class HistoryNode(override val value: Int, val left: HistoryElement, val right: HistoryElement) : HistoryElement