fun main() {

    fun part1(input: List<String>): Int {
        return totalWinnings(input, comparator())
    }

    fun part2(input: List<String>): Int {
        return totalWinnings(input, jokerComparator())
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 765 * 1 + 220 * 2 + 28 * 3 + 684 * 4 + 483 * 5)
    check(part2(testInput) == 765 * 1 + 28 * 2 + 684 * 3 + 483 * 4 + 220 * 5)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

fun totalWinnings(input: List<String>, comparator: Comparator<Hand>) = input
    .map { line -> line.toHand() }
    .sortedWith(comparator)
    .mapIndexed { index, hand -> hand.winnings(index) }
    .sum()

fun String.toHand(): Hand {
    val (cards, bid) = split(' ')
    return Hand(cards.map { Card(it) }, bid.trim().toInt())
}

fun comparator() = comparator({ hand -> hand.strength() }, { card -> card.strength() })

fun jokerComparator() = comparator({ hand -> hand.jokerStrength() }, { card -> card.jokerStrength() })

fun comparator(handStrength: (Hand) -> Int, cardStrength: (Card) -> Int): Comparator<Hand> =
    Comparator { hand, otherHand ->
        val strength = handStrength(hand)
        val otherStrength = handStrength(otherHand)
        if (strength > otherStrength) 1
        else if (strength < otherStrength) -1
        else {
            val (card, otherCard) = hand.cards.zip(otherHand.cards).first { (card, otherCard) -> card != otherCard }
            if (cardStrength(card) > cardStrength(otherCard)) 1
            else -1
        }
    }

fun Hand.strength(): Int = when (cards.toSet().size) {
    1 -> 7 //five of a kind
    2 -> if (maxCardCount() == 4) 6 //four of a kind
    else 5 //full house
    3 -> if (maxCardCount() == 3) 4 //three of a kind
    else 3 //two pair
    4 -> 2 //one pair
    else -> 1
}

fun Hand.jokerStrength(): Int {
    val counts: Map<Card, Int> = cardCount()
    val jokers: Int = counts.getOrDefault(Card.JOKER, 0)
    return if (jokers > 0 && jokers < cards.count()) {
        counts.keys
            .filter { it != Card.JOKER }
            .maxOf { card -> replaceCards(Card.JOKER, card).strength() }
    } else strength()
}

fun Card.strength(): Int = when (char) {
    'A' -> 14
    'K' -> 13
    'Q' -> 12
    'J' -> 11
    'T' -> 10
    else -> char.digitToInt()
}

fun Card.jokerStrength(): Int = when (this) {
    Card.JOKER -> 1
    else -> strength()
}

data class Hand(val cards: List<Card>, val bid: Int) {

    fun winnings(zeroBasedRank: Int) = (zeroBasedRank + 1) * bid

    fun maxCardCount() = cardCount().values.max()

    fun cardCount(): Map<Card, Int> = cards.fold(mapOf()) { acc, card ->
        acc + (card to (acc.getOrDefault(card, 0) + 1))
    }

    fun replaceCards(cardToReplace: Card, replacement: Card): Hand = copy(
        cards = cards.map { card -> if (card == cardToReplace) replacement else card }
    )
}

data class Card(val char: Char) {

    override fun toString() = char.toString()

    companion object {
        val JOKER = Card('J')
    }
}

