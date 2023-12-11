import kotlin.math.abs

fun main() {

    fun part1(input: List<String>): Long {
        return sumOfShortestPaths(input, 2)
    }

    fun part2(input: List<String>): Long {
        return sumOfShortestPaths(input, 1_000_000)
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}

fun sumOfShortestPaths(input: List<String>, expansion: Int): Long {
    val universe = parseUniverse(input)
    val galaxies = universe.galaxiesAfterExpansion(expansion)
    return galaxies.possiblePairs()
        .sumOf { (galaxy1, galaxy2) -> shortestPath(galaxy1, galaxy2) }
}

fun parseUniverse(input: List<String>) = input.map { it.toList() }
    .mapIndexed { row, line ->
        line.mapIndexed { col, element -> Space(row, col, SpaceType.of(element)) }
    }

fun Universe.galaxiesAfterExpansion(expansion: Int): List<Space> {
    val (colsToExpand, rowsToExpand) = colRowsToExpand()
    return flatten()
        .filter { it.isGalaxy() }
        .map { space ->
            Space(
                space.row + rowsToExpand.count { it < space.row } * (expansion - 1),
                space.col + colsToExpand.count { it < space.col } * (expansion - 1),
                space.type
            )
        }
}

fun Universe.colRowsToExpand(): Pair<List<Int>, List<Int>> {
    val cols = first().indices
    val rows = indices
    val colsToExpand = cols.filter { col -> rows.map { row -> this[row][col] }.all { it.isBlank() } }
    val rowsToExpand = rows.filter { row -> cols.map { col -> this[row][col] }.all { it.isBlank() } }
    return colsToExpand to rowsToExpand
}

fun List<Space>.possiblePairs() = flatMapIndexed { i, galaxy1 ->
    drop(i + 1).map { galaxy2 -> galaxy1 to galaxy2 }
}

fun shortestPath(source: Space, target: Space): Long {
    return abs(source.row - target.row) + abs(source.col - target.col)
}

typealias Universe = List<List<Space>>

enum class SpaceType(val char: Char) {
    BLANK('.'), GALAXY('#');

    companion object {
        fun of(char: Char) = entries.first { it.char == char }
    }
}

data class Space(val row: Long, val col: Long, val type: SpaceType) {
    constructor(row: Int, col: Int, type: SpaceType) : this(row.toLong(), col.toLong(), type)

    fun isBlank() = type == SpaceType.BLANK
    fun isGalaxy() = type == SpaceType.GALAXY
}
