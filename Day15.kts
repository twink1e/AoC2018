import java.io.File
import java.util.*
import kotlin.system.exitProcess

// input processing
val inputs = File("inputs/day15.txt").readLines()

class Unit(row: Int, col: Int) {
    var row = row
    var col = col
    var life = 200
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Unit) return false
        return row == other.row && col == other.col
    }
}

val elfs = mutableListOf<Unit>()
val goblins = mutableListOf<Unit>()
val rowNum = inputs.count()
val colNum = inputs[0].count()
val rowDir = intArrayOf(-1, 0, 0, 1)
val colDir = intArrayOf(0, -1, 1, 0)

val map = Array(rowNum, { CharArray(colNum) })
val ignore = mutableListOf<Pair<Int, Int>>()
for (i in 0 until rowNum) {
    for (j in 0 until colNum) {
        val c = inputs[i][j]
        map[i][j] = c
        if (c == 'G') {
            goblins.add(Unit(i, j))
        } else if (c == 'E') {
            elfs.add(Unit(i, j))
        }
    }
}

var rounds = 1
val hurt = 3
var elfPower = 12
val elfCount = elfs.count()
while(true) {
    ignore.clear()
    //println("================== Round $rounds ====================")
    for (i in 0 until rowNum) {
        for (j in 0 until colNum) {
            if (Pair(i, j) in ignore) continue
            round(i, j, map[i][j])
        }
    }
    rounds++
}

fun round(row: Int, col: Int, c: Char) {
    if (c != 'G' && c != 'E') return
    if (c == 'G' && elfs.count() == 0 || c == 'E' && goblins.count() == 0) getResult()
    if (attack(row, col, c)) return
    val newPos = move(row, col, c)
    attack(newPos.first, newPos.second, c)
}

fun move(row: Int, col: Int, c: Char): Pair<Int, Int> {
    val targets = if (c == 'E') goblins else elfs
    var minDist = Int.MAX_VALUE
    var distMap: Array<IntArray>? = null
    val squares = mutableListOf<Pair<Int, Int>>()
     for (unit in targets) {
        for (i in 0..3) {
            val currRow = unit.row+rowDir[i]
            val currCol = unit.col+colDir[i]
            if (map[currRow][currCol] == '.') squares.add(Pair(currRow, currCol))
        }
    } 
    squares.sortWith(compareBy({ it.first }, { it.second }))
    for (t in squares) {
        val d = reachable(row, col, t.first, t.second)
        if (d[row][col] < minDist) {
            minDist = d[row][col]
            distMap = d
        }
    }
    
    if (distMap == null) return Pair(row, col)
    
    var closetRow = 0
    var closetCol = 0
    var dist = Int.MAX_VALUE
    for (i in 0..3) {
        val currRow = row+rowDir[i]
        val currCol = col+colDir[i]
        val currDist = distMap[currRow][currCol]
        if (currDist < dist) {
            dist = currDist
            closetRow = currRow
            closetCol = currCol
        }
    }
    val enemy = if (c == 'E') 'G' else 'E'
    val life = removeUnit(enemy, row, col)
    map[closetRow][closetCol] = c
    val newUnit = Unit(closetRow, closetCol)
    newUnit.life = life
    if (c == 'E') {
        elfs.add(newUnit)
    } else {
        goblins.add(newUnit)
    }
    ignore.add(Pair(closetRow, closetCol))
    return Pair(closetRow, closetCol)
    println("$row, $col $c moved to $closetRow, $closetCol")
}

fun reachable(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Array<IntArray> {
    //println("reaching $toRow, $toCol from $fromRow, $fromCol")
    var q = LinkedList<Pair<Int, Int>>()
    var nextQ = LinkedList<Pair<Int, Int>>()
    val distMap = Array(rowNum, { IntArray(colNum, { Int.MAX_VALUE }) })

    q.add(Pair(toRow, toCol))
    distMap[toRow][toCol] = 0
    while (!q.isEmpty() || !nextQ.isEmpty()) {
        if (q.isEmpty()) {
            q = nextQ
            nextQ = LinkedList<Pair<Int, Int>>()
        }
        val cur = q.pop()
        val curRow = cur.first
        val curCol = cur.second
        for (i in 0..3) {
            val newRow = curRow + rowDir[i]
            val newCol = curCol + colDir[i]
            if (distMap[newRow][newCol] != Int.MAX_VALUE) continue
            if (newRow == fromRow && newCol == fromCol || map[newRow][newCol] == '.') {
                distMap[newRow][newCol] = distMap[curRow][curCol] + 1
                nextQ.add(Pair(newRow, newCol))
                //println("$curRow, $curCol ${distMap[curRow][curCol]} add $newRow, $newCol ${map[newRow][newCol]} ${distMap[newRow][newCol]}")
                if (newRow == fromRow && newCol == fromCol) return distMap
            }
        }
    }
 
    return distMap
}

fun attack(row: Int, col: Int, c: Char): Boolean {
    var attacked = false
    var minRow: Int? = null
    var minCol: Int? = null
    var minLife = Int.MAX_VALUE
    for (i in 0..3) {
        val newRow = row + rowDir[i]
        val newCol = col + colDir[i]
        val neighbor = map[newRow][newCol]
        if ( c == 'E' && neighbor == 'G' || c == 'G' && neighbor == 'E') {
            attacked = true
            val target = getTarget(c, newRow, newCol)
            if (target.life < minLife) {
                minLife = target.life
                minRow = newRow
                minCol = newCol
            }
        }
    }
    if (attacked) {
        val target = getTarget(c, minRow!!, minCol!!)
        target.life = target.life - if (c == 'G') hurt else elfPower
        if (target.life <= 0) {
            removeUnit(c, minRow, minCol)
        }
        //println("$row, $col $c attack $minRow, $minCol ${map[minRow][minCol]} life ${target.life}")
    } else {
        //println("$row, $col $c no attack")
    }
    return attacked
}

fun removeUnit(c: Char, row: Int, col: Int): Int {
    val mock = Unit(row, col)
    map[row][col] = '.'
    if (c == 'E') {
        return goblins.removeAt(goblins.indexOf(mock)).life
    }else {
        return elfs.removeAt(elfs.indexOf(mock)).life
    }
}
fun getTarget(c: Char, row: Int, col: Int): Unit {
    val mock = Unit(row, col)
    if (c == 'E') { 
        return goblins[goblins.indexOf(mock)]
    } else { 
        return elfs[elfs.indexOf(mock)]
    }
}

fun getResult(): Int {
    var life = elfs.fold(0) { acc, unit -> acc + unit.life }
    life += goblins.fold(0) { acc, unit -> acc + unit.life }
    printMap()
    println("${rounds - 1} $life")
    println((rounds - 1) * life)
    println(elfCount - elfs.count())
    exitProcess(0)
}

fun printMap() {
    for (ii in 0 until rowNum) {
        for (jj in 0 until colNum) {
            print(map[ii][jj])
        }
        println()
    }
    println()
}