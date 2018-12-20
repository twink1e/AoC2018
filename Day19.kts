import java.io.File
import java.util.*
import kotlin.system.exitProcess

// input processing
val inputs = File("inputs/day19.txt").readLines()
val ops = mapOf<String, (Int, Int) -> Int>("addr" to ::addr, "addi" to ::addi, "mulr" to ::mulr, 
        "muli" to ::muli, "banr" to ::banr, "bani" to ::bani, "borr" to ::borr, "bori" to ::bori, "setr" to ::setr,
        "seti" to ::seti, "gtir" to ::gtir, "gtri" to ::gtri, "gtrr" to ::gtrr, "eqir" to ::eqir, "eqri" to ::eqri, "eqrr" to ::eqrr)
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

val ip = inputs[0].replace("#ip ", "").toInt()
val ins = inputs.toMutableList()
ins.removeAt(0)
val instr = ins.map {
    val details = it.split(" ")
    val ins = ops[details[0]]!!
    val params = details.toMutableList()
    params.removeAt(0)
    Pair(ins, params.map { it.toInt() })
}

registers = mutableListOf(1, 0, 0, 0, 0, 0)

var instNum = 0

instr.withIndex().forEach {(idx, c) -> println("$idx ${inputs[idx + 1]}")}
var r = 0
println()
while (instNum >= 0 && instNum < instr.count() && r<300000) {
    println("$instNum ${inputs[instNum + 1]}")
    val cur = instr[instNum]
    registers[cur.second[2]] = cur.first(cur.second[0], cur.second[1])
    for (i in 0..5) print("${registers[i]} ")
    println()
    registers[ip] = registers[ip] + 1
    instNum = registers[ip]
    r++
}
println(registers[0])
println(registers[ip])

/*
 * Part 2
 * R5 = 10551367
 * for (R1 = 1; R1 <= R5; R++) {
 *      For (R4 = 1; R4 <= R5; R4++) {
 *          if (R1*R4 == R5) R0 += R1
 *      }
 *      
 * }
 * 
 */