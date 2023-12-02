fun main() {

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val (game, play) = line.toGameDetails()
            val possible = play.toSets()
                .map { set -> set.toColours() }
                .fold(true) { possible, colours ->
                    possible && colours.all { it.isPossible() }
                }
            if (possible) game.id() else 0
        }
    }

    fun part2(lines: List<String>): Int {
        return lines.sumOf { line ->
            val (_, play) = line.toGameDetails()
            play.toSets()
                .map { set ->
                    set.toColours()
                        .fold(GameCount(red = 1, green = 1, blue = 1)) { gameCount, colourCount ->
                            gameCount.with(colourCount)
                        }
                }
                .reduce { result, colourCount -> result.acc(colourCount) }
                .multiplyCounts()
        }
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 1 + 2 + 5)
    val testInput2 = readInput("Day02_part2_test")
    check(part2(testInput2) == 4 * 2 * 6 + 1 * 3 * 4 + 20 * 13 * 6 + 14 * 3 * 15 + 6 * 3 * 2)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}

fun String.toGameDetails() = split(": ")
fun String.id() = split(' ')[1].toInt()
fun String.toSets() = split("; ")
fun String.toColours(): List<ColourCount> {
    return split(", ")
        .map { value ->
            val (count, colourName) = value.split(' ')
            ColourCount(Colour.valueOf(colourName), count.toInt())
        }
}

data class ColourCount(val colour: Colour, val count: Int) {
    fun isPossible(): Boolean = count <= colour.max
}

enum class Colour(val max: Int) {
    red(12), green(13), blue(14)
}

data class GameCount(val red: Int, val green: Int, val blue: Int) {
    fun with(colourCount: ColourCount): GameCount = when (colourCount.colour) {
        Colour.red -> copy(red = colourCount.count)
        Colour.green -> copy(green = colourCount.count)
        Colour.blue -> copy(blue = colourCount.count)
    }

    fun acc(that: GameCount) = GameCount(
        if (red < that.red) that.red else red,
        if (green < that.green) that.green else green,
        if (blue < that.blue) that.blue else blue
    )

    fun multiplyCounts(): Int {
        return red * green * blue
    }
}
