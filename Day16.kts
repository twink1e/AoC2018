import java.io.File
import java.util.*
import kotlin.system.exitProcess

// input processing
val inputs = File("inputs/day16.txt").readLines()
val ops = listOf<(Int, Int) -> Int>(::addr, ::addi, ::mulr, ::muli, ::banr, ::bani, ::borr, ::bori, ::setr, ::seti, ::gtir, ::gtri, ::gtrr, ::eqir, ::eqri, ::eqrr)
var registers = mutableListOf<Int>()

fun addr(a: Int, b: Int): Int {
    return registers[a] + registers[b]
}

fun addi(a: Int, b: Int): Int {
    return registers[a] + b
}

fun mulr(a: Int, b: Int): Int {
    return registers[a] * registers[b]
}

fun muli(a: Int, b: Int): Int {
    return registers[a] * b
}

fun banr(a: Int, b: Int): Int {
    return registers[a].and(registers[b])
}

fun bani(a: Int, b: Int): Int {
    return registers[a].and(b)
}

fun borr(a: Int, b: Int): Int {
    return registers[a].or(registers[b])
}

fun bori(a: Int, b: Int): Int {
    return registers[a].or(b)
}

fun setr(a: Int, b: Int): Int {
    return registers[a]
}

fun seti(a: Int, b: Int): Int {
    return a
}

fun gtir(a: Int, b: Int): Int {
    if (a > registers[b]) return 1
    else return 0
}

fun gtri(a: Int, b: Int): Int {
    if (b < registers[a]) return 1
    else return 0
}

fun gtrr(a: Int, b: Int): Int {
    if (registers[a] > registers[b]) return 1
    else return 0
}

fun eqir(a: Int, b: Int): Int {
    if (a == registers[b]) return 1
    else return 0
}

fun eqri(a: Int, b: Int): Int {
    if (b == registers[a]) return 1
    else return 0
}

fun eqrr(a: Int, b: Int): Int {
    if (registers[a] == registers[b]) return 1
    else return 0
}

var ans = 0
var lineNum = 0
val opMap = mutableMapOf<Int, (Int, Int) -> Int>()
while (inputs[lineNum].contains("Before")) {
    registers = inputs[lineNum].replace("Before: [", "").replace("]", "").split(", ").map { it.toInt() }.toMutableList()
    val inst = inputs[lineNum + 1].split(" ").map { it.toInt() }
    if (!opMap.keys.contains(inst[0])) {
        val after = inputs[lineNum + 2].replace("After:  [", "").replace("]", "").split(", ").map { it.toInt() }
        val applicable = ops.filter { it(inst[1], inst[2]) == after[inst[3]] && !opMap.values.contains(it) }
        if (applicable.count() == 1) {
            opMap[inst[0]] = applicable[0]
        }
    }
    lineNum += 4
}
registers = mutableListOf(0, 0, 0, 0)

lineNum+=2
while(lineNum < inputs.count()) {
    val inst = inputs[lineNum].split(" ").map { it.toInt() }
    registers[inst[3]] = opMap[inst[0]]!!(inst[1], inst[2])
    lineNum++
}
print(registers[0])
