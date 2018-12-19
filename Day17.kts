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
val rowNum = maxY + 1
val colNum = maxX + 1
val map = Array(rowNum, { CharArray(colNum, { '.' } ) })
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

val source: Queue<Pair<Int, Int>> = LinkedList<Pair<Int, Int>>()
source.add(Pair(0, 500))
while(!source.isEmpty()) {
    val curr = source.poll()
    move(curr.first, curr.second)
}

fun move(row: Int, col: Int) {
    val touch = goDown(row, col)
    if (touch != null) {
        for (p in spread(touch.first, touch.second)) source.add(p)
    }
}

fun spread(r: Int, c: Int): List<Pair<Int, Int>> {
    if (map[r][c] == '~') return emptyList()
    var cLeft = c
    var cRight = c
    map[r][c] = '~'
    while(map[r][cLeft - 1] != '#' && map[r+1][cLeft - 1] != '.' && map[r+1][cLeft - 1] != '|') {
        cLeft--
        map[r][cLeft] = '~'
    }
    while(map[r][cRight + 1] != '#' && map[r+1][cRight + 1] != '.' && map[r+1][cRight + 1] != '|') {
        cRight++
        map[r][cRight] = '~'
    }
    if (map[r][cLeft-1] == '#' && map[r][cRight+1] == '#') return spread(r-1, c)
    val ans = mutableListOf<Pair<Int, Int>>()
    if (map[r][cLeft-1] == '.') {
        for (i in cLeft-1..cRight) map[r][i] = '|'
        map[r][cLeft-1] = '|'
        ans.add(Pair(r, cLeft-1))
    }
    if (map[r][cRight+1] == '.') {
        for (i in cLeft..cRight+1) map[r][i] = '|'
        map[r][cRight+1] = '|'
        ans.add(Pair(r, cRight+1))
    }
    return ans
}

fun goDown(r: Int, c: Int): Pair<Int, Int>? {
    var row = r
    while(row+1 < rowNum && map[row+1][c] != '#'){
        map[row+1][c] = '|'
        row++
    }
    if (row+1 == rowNum) return null
    else return Pair(row, c)
}

printMap()

fun printMap() {
    var ans = 0

    for (i in minY..maxY) {
        for (j in (minX -2)..maxX) {
            print(map[i][j])
            if (map[i][j] == '~' || map[i][j] == '|') ans++
        }
        println()
    }
    println(ans)
}

count()
fun count() {
    var ans = 0
    for (i in minY..maxY) {
        var j = minX
        var started = false
        var idx = 0
        while (j<= maxX) {
            if (map[i][j] != '#') {
                if (map[i][j] != '~') started = false
                j++
                continue
            }
            else if (started) {
                ans += j-idx
                idx = j+1
            } else {
                started = true
                idx = j+1
            }
 
            j++
        }
    }
    println(ans)
    
}
fun updateX(v: Int) {
    minX = Math.min(minX, v)
    maxX = Math.max(maxX, v)
}

fun updateY(v: Int) {
    minY = Math.min(minY, v)
    maxY = Math.max(maxY, v)
}