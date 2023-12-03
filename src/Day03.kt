fun main() {

    fun part1(lines: List<String>): Int {
        val (symbols, partNumbers) = lines.toEntries()
        val symbolsPos = symbols.map { it.pos }.toSet()
        val maxX = lines.first().length
        val maxY = lines.size
        return partNumbers
            .filter { partNumber ->
                partNumber.adjacentPositions(maxX, maxY).intersect(symbolsPos).isNotEmpty()
            }
            .sumOf { partNumber -> partNumber.asNumber() }
    }

    fun part2(lines: List<String>): Int {
        val (symbols, partNumbers) = lines.toEntries()
        val maxX = lines.first().length
        val maxY = lines.size
        return symbols
            .flatMap { symbol ->
                val adjacentPartNumbers = partNumbers.filter { partNumber ->
                    symbol.adjacentPositions(maxX, maxY).intersect(partNumber.positions()).isNotEmpty()
                }
                if (adjacentPartNumbers.size == 2) listOf(
                    adjacentPartNumbers.fold(1) { acc, partNumber -> acc * partNumber.asNumber() }
                )
                else listOf()
            }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 467 + 35 + 633 + 617 + 592 + 755 + 664 + 598)
    check(part2(testInput) == 467 * 35 + 755 * 598)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

fun List<String>.toEntries() = mapIndexed { y, line ->
    line.foldIndexed(Acc()) { x, acc, char ->
        when {
            char.isDigit() ->
                when (acc.current) {
                    is PartNumber ->
                        if (x < line.length - 1) acc.withCurrent(acc.current + char)
                        else acc.recordPartNumber(acc.current + char)

                    is Symbol -> acc.withCurrent(PartNumber(char, Pos(x, y))).recordSymbol(acc.current)
                    null -> acc.withCurrent(PartNumber(char, Pos(x, y)))
                }

            char == '.' ->
                when (acc.current) {
                    null -> acc
                    else -> acc.recordEntry(acc.current)
                }
            //Symbol
            else ->
                when (acc.current) {
                    null -> acc.recordSymbol(Symbol(Pos(x, y)))
                    else -> acc.recordEntry(acc.current).recordSymbol(Symbol(Pos(x, y)))
                }
        }
    }.entries()
}.reduce { acc, next -> acc + next }

data class Acc(
    val current: Entry? = null,
    val symbols: List<Symbol> = listOf(),
    val partNumbers: List<PartNumber> = listOf()
) {

    fun withCurrent(entry: Entry) = Acc(entry, symbols, partNumbers)

    fun recordEntry(entry: Entry) = when (entry) {
        is PartNumber -> recordPartNumber(entry)
        is Symbol -> recordSymbol(entry)
    }

    fun recordPartNumber(partNumber: PartNumber) = Acc(null, symbols, partNumbers + partNumber)

    fun recordSymbol(symbol: Symbol) = Acc(null, symbols + symbol, partNumbers)

    fun entries() = Entries(symbols, partNumbers)
}

sealed interface Entry
data class PartNumber(val value: String, val pos: Pos) : Entry {
    constructor(char: Char, pos: Pos) : this(char.toString(), pos)

    operator fun plus(char: Char) = copy(value = value + char)

    fun adjacentPositions(maxX: Int, maxY: Int): List<Pos> =
        positions().let {
            it.flatMap { pos -> pos.adjacentPositions(maxX, maxY) }
                .distinct()
                .minus(it)
        }

    fun positions(): Set<Pos> = value.indices.map { i -> pos.deltaX(i) }.toSet()
    fun asNumber() = value.toInt()
}

data class Symbol(val pos: Pos) : Entry {
    fun adjacentPositions(maxX: Int, maxY: Int) = pos.adjacentPositions(maxX, maxY)
}

data class Entries(val symbols: List<Symbol>, val partNumbers: List<PartNumber>) {
    operator fun plus(that: Entries) = Entries(symbols + that.symbols, partNumbers + that.partNumbers)
}

data class Pos(val x: Int, val y: Int) {

    fun deltaX(delta: Int) = Pos(x + delta, y)
    fun adjacentPositions(maxX: Int, maxY: Int) = setOf(
        delta(-1, -1),
        delta(-1, 0),
        delta(-1, 1),
        delta(0, -1),
        delta(0, 1),
        delta(1, -1),
        delta(1, 0),
        delta(1, 1),
    )
        .filter { pos -> pos.x in 0..<maxX && pos.y in 0..<maxY }

    private fun delta(deltaX: Int, deltaY: Int) = Pos(x + deltaX, y + deltaY)
}
