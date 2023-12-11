import PipeDirection.*

fun main() {

    fun part1(input: List<String>): Int {
        val sketch = parseSketch(input)
        return sketch.stepsToFarthestPoint()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput).also { println(it) } == 8)
//    val test2Input = readInput("Day10_part2_test")
//    check(part2(test2Input).also { println(it) } == 10)

    val input = readInput("Day10")
    check(part1(input).also { println(it) } == 6951)
//    part2(input).println()
}

fun parseSketch(input: List<String>): Sketch {
    val tiles: List<List<SketchTile>> = input.mapIndexed { row, line ->
        line.mapIndexed { col, char -> tileOf(Location(row, col), char) }
    }
    val sketch = Sketch.from(tiles)
    return sketch
}

fun tileOf(location: Location, char: Char) = when (char) {
    'S' -> StartingTile(location)
    '.' -> GroundTile(location)
    else -> Pipe(location, char.toDirection())
}

fun Char.toDirection(): List<PipeDirection> = when (this) {
    '|' -> listOf(NORTH, SOUTH)
    'L' -> listOf(NORTH, EAST)
    'J' -> listOf(NORTH, WEST)
    '7' -> listOf(SOUTH, WEST)
    'F' -> listOf(SOUTH, EAST)
    '-' -> listOf(WEST, EAST)
    else -> throw IllegalStateException("No directions possible for $this")
}

class Sketch private constructor(private val rows: Int, private val cols: Int, private val tiles: List<SketchTile>) {
    fun stepsToFarthestPoint(): Int {
        fun farthestPoint(tiles: List<SketchTile>, seen: Set<SketchTile>, steps: Int): Int {
            val neighbours = tiles.flatMap { tile -> tile.neighbours() }.minus(seen)
            return if (neighbours.isEmpty()) steps
            else farthestPoint(neighbours, seen + tiles, steps + 1)
        }
        val startingTile = tiles.first { it is StartingTile }
        return farthestPoint(listOf(startingTile), setOf(), 0)
    }

    private fun SketchTile.neighbours(): List<SketchTile> {
        return when (this) {
            is Pipe -> directions.map { tileAt(it.next(location)) }
            is StartingTile -> expectedNeighbours()
                .filter { (_, location) -> location.isValid() }
                .flatMap { (direction, location) ->
                    val neighbour = tileAt(location)
                    if (neighbour is Pipe && direction in neighbour.directions) listOf(neighbour)
                    else listOf()
                }

            is GroundTile -> throw IllegalStateException("Should never be a ground tile")
        }
    }

    private fun tileAt(location: Location): SketchTile = tiles[indexOf(location)]

    private fun indexOf(location: Location): Int =
        location.row * (cols - 1) + (location.col + location.row)

    private fun Location.isValid(): Boolean {
        return row in 0..<rows && col in 0..<cols
    }

    companion object {
        fun from(tiles: List<List<SketchTile>>): Sketch {
            val rows = tiles.size
            val cols = tiles.first().size
            return Sketch(rows, cols, tiles.flatten())
        }
    }
}

enum class PipeDirection(private val deltaRow: Int, private val deltaCol: Int) {
    NORTH(-1, 0),
    SOUTH(+1, 0),
    WEST(0, -1),
    EAST(0, +1);

    fun next(location: Location) = location.move(deltaRow, deltaCol)
    fun previous(location: Location) = location.move(-deltaRow, -deltaCol)
}

data class Location(val row: Int, val col: Int) {
    fun move(row: Int, col: Int) = copy(row = this.row + row, col = this.col + col)
}

sealed interface SketchTile {
    val location: Location
}

data class StartingTile(override val location: Location) : SketchTile {
    fun expectedNeighbours(): List<Pair<PipeDirection, Location>> {
        return PipeDirection.entries
            .map { direction -> direction to direction.previous(location) }
    }
}

data class GroundTile(override val location: Location) : SketchTile

data class Pipe(override val location: Location, val directions: List<PipeDirection>) : SketchTile