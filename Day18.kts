import java.io.File
import java.util.*

// input processing
val inputs = File("inputs/day18.txt").readLines()
val row = inputs.count()
val col = inputs[0].length
var map = Array(row + 2, { CharArray(col + 2) })
for (i in 0 until row) 
    for (j in 0 until col)
        map[i+1][j+1] = inputs[i][j]
var nextMap = Array(row + 2, { CharArray(col + 2) })
printMap(0)

for (round in 1..10) {
    changed = false
    for (i in 0 until row)
        for (j in 0 until col) 
            evolve(i+1, j+1)
    map = nextMap
    nextMap = Array(row + 2, { CharArray(col + 2) })
}

val wood = map.fold(0) { acc, chars -> acc + chars.filter { it == '|' }.count() }
val lumber = map.fold(0) { acc, chars -> acc + chars.filter { it == '#' }.count() }
println(wood * lumber)

fun evolve(i: Int, j: Int) {
    val c = map[i][j]
    when (c) {
        '.' -> {
            if (count(i, j, '|') >= 3) nextMap[i][j] = '|'
            else nextMap[i][j] = '.'
        }
        '|' -> {
            if (count(i, j, '#') >= 3) nextMap[i][j] = '#'
            else nextMap[i][j] = '|'
        }
        else -> {
            if (count(i, j, '#') >= 1 && count(i, j, '|') >= 1) nextMap[i][j] = '#'
            else nextMap[i][j] = '.'
        }
    }
}

fun count(i: Int, j: Int, c: Char): Int {
    var ans = 0
    val rowDir = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
    val colDir = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
    for (k in 0 until rowDir.count()) {
        if (map[i + rowDir[k]][j + colDir[k]] == c) ans++
    }
    return ans
}

fun printMap(round: Int) {
    println("================== Round $round ================== ")
    for (i in 0 until row) {
        for (j in 0 until col)
            print(map[i + 1][j + 1])
        println()
    }
}