fun main() {
    fun part1(input: List<String>): Steps {
        val (instructions, connections) = parse(input)
        return navigate(
            instructions,
            connections,
            start = Node("AAA"),
            reachEnd = { node -> node == Node("ZZZ") }
        )
    }

    fun part2(input: List<String>): Steps {
        val (instructions, connections) = parse(input)
        return connections.keys
            .filter { node -> node.isStart }
            .map { startNode ->
                navigate(
                    instructions,
                    connections,
                    startNode,
                    reachEnd = { node -> node.isEnd }
                )
            }
            .reduce { acc, next -> leastCommonMultiple(acc, next) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 6L)
    val test2Input = readInput("Day08_part2_test")
    check(part2(test2Input) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}

fun parse(input: List<String>): Pair<List<Instruction>, Map<Node, Connection>> {
    val deque = ArrayDeque(input)
    val instructions = deque.removeFirst().map { Instruction.from(it) }
    deque.removeFirst()
    val connections = deque.associate { line ->
        val matchResult = """(\w{3}) = \((\w{3}), (\w{3})\)""".toRegex().find(line)
            ?: throw IllegalStateException()
        val (node, left, right) = matchResult.destructured
        Node(node) to Connection(Node(left), Node(right))
    }
    return instructions to connections
}

fun navigate(
    instructions: List<Instruction>,
    connections: Map<Node, Connection>,
    start: Node,
    reachEnd: (Node) -> Boolean
): Steps {
    tailrec fun find(start: Node, acc: Steps): Steps {
        val index = (acc % instructions.size).toInt()
        val instruction = instructions[index]
        val connection = connections.getValue(start)
        val next = when (instruction) {
            Instruction.LEFT -> connection.left
            Instruction.RIGHT -> connection.right
        }
        return if (reachEnd(next)) acc + 1
        else find(next, acc + 1)
    }

    return find(start, 0)
}

fun leastCommonMultiple(num1: Long, num2: Long): Long {
    val larger = if (num1 > num2) num1 else num2
    val maxLcm = num1 * num2
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % num1 == 0L && lcm % num2 == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

typealias Steps = Long

data class Node(val name: String) {
    val isStart = name.endsWith('A')
    val isEnd = name.endsWith('Z')
}

data class Connection(val left: Node, val right: Node)

enum class Instruction(val id: Char) {
    LEFT('L'), RIGHT('R');

    companion object {
        fun from(id: Char): Instruction {
            return entries.first { instruction -> instruction.id == id }
        }
    }
}
