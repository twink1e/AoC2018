import java.io.File
import java.util.*
import kotlin.system.exitProcess

// input processing
val inputs = File("inputs/day17.txt").readLines()

var minX = Int.MAX_VALUE
var maxX = Int.MIN_VALUE
var minY = Int.MAX_VALUE
var maxY = Int.MIN_VALUE

val records = inputs.map {
    val r = IntArray(4)
    val parts = it.split(", ")
    val part1 = parts[0].split("=")
    val v1 = part1[1].toInt()
    val parts2 = parts[1].split("=")[1].split("..")
    val v2 = parts2[0].toInt()
    val v3 = parts2[1].toInt()
    if (part1[0] == "x") {
        updateX(v1)
        updateY(v2)
        updateY(v3)
    } else {
        r[0] = 1
        updateY(v1)
        updateX(v2)
        updateX(v3)
    }
    r[1] = v1
    r[2] = v2
    r[3] = v3
    r
}

val map = Array(maxY + 1, { CharArray(maxX + 1, { '.' } ) })
map[0][500] = '+'
for (r in records) {
    for (i in r[2]..r[3]) {
        if (r[0] == 1) {
            map[r[1]][i] = '#'
        } else {
            map[i][r[1]] = '#'
        }
    }
}
printMap()

fun printMap() {
    for (i in 0..maxY) {
        for (j in minX..maxX) {
            print(map[i][j])
        }
        println()
    }
}
fun updateX(v: Int) {
    minX = Math.min(minX, v)
    maxX = Math.max(maxX, v)
}

fun updateY(v: Int) {
    minY = Math.min(minY, v)
    maxY = Math.max(maxY, v)
}