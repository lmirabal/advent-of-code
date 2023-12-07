import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): Int = input.sumOf { line ->
        val count = line.card().matchCount
        2.0.pow(count - 1).toInt()
    }

    fun part2(input: List<String>): Int {
        fun acc(card: ScratchCard, lookup: Map<CardId, ScratchCard>): List<ScratchCard> {
            if (card.matchCount == 0) {
                return listOf(card)
            }
            return (1..card.matchCount).flatMap { i ->
                val copy = lookup.getValue(card.id + i)
                acc(copy, lookup)
            } + card
        }

        val cards: List<ScratchCard> = input.map { line -> line.card() }
        val lookup: Map<CardId, ScratchCard> = cards.associateBy { it.id }
        return cards.flatMap { acc(it, lookup) }.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 8 + 2 + 2 + 1)
    check(part2(testInput) == 1 + 2 + 4 + 8 + 14 + 1)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

typealias CardId = Int

data class ScratchCard(val id: CardId, val matchCount: Int)

fun String.card(): ScratchCard {
    val (card, allNumbers) = this.split(":\\s+".toRegex())
    val cardId = card.split("\\s+".toRegex())[1].toInt()
    val (winning, numbers) = allNumbers.split("\\s+\\|\\s+".toRegex())
    val count = winning.toSet().intersect(numbers.toSet()).count()
    return ScratchCard(cardId, count)
}

private fun String.toSet(): Set<Int> = split("\\s+".toRegex()).map { it.toInt() }.toSet()