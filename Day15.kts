import java.io.File
import java.util.*
import kotlin.system.exitProcess

// Unit class for a goblin or elf
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

// constants
val elf = 'E'
val goblin = 'G'
val space = '.'
val hit = 3
// following the reading order: up, left, right, down
val rowDir = intArrayOf(-1, 0, 0, 1)
val colDir = intArrayOf(0, -1, 1, 0)

// input processing
val inputs = File("inputs/day15.txt").readLines()

val rowNum = inputs.count()
val colNum = inputs[0].count()
val map = Array(rowNum, { CharArray(colNum) })
val elfs = mutableListOf<Unit>()
val goblins = mutableListOf<Unit>()
val ignored = mutableListOf<Pair<Int, Int>>()

for (i in 0 until rowNum) {
    for (j in 0 until colNum) {
        val c = inputs[i][j]
        map[i][j] = c
        if (c == goblin) {
            goblins.add(Unit(i, j))
        } else if (c == elf) {
            elfs.add(Unit(i, j))
        }
    }
}

// Simulation
var rounds = 1

while(true) {
    ignored.clear()
    // println("================== Round $rounds ====================")
    for (i in 0 until rowNum) {
        for (j in 0 until colNum) {
            // ignore the already moved unit in this round
            if (Pair(i, j) in ignored) continue
            takeAction(i, j, map[i][j])
        }
    }
    rounds++
}

fun takeAction(row: Int, col: Int, c: Char) {
    if (c != goblin && c != elf) return
    if (c == goblin && elfs.count() == 0 || c == elf && goblins.count() == 0) getResult()
    if (attack(row, col, c)) return
    val newPos = move(row, col, c)
    attack(newPos.first, newPos.second, c)
}

fun move(row: Int, col: Int, c: Char): Pair<Int, Int> {
    val targets = if (c == elf) goblins else elfs
    var minDist = Int.MAX_VALUE
    var distMap: Array<IntArray>? = null
    
    // all possible destination squares in reading order
    val squares = mutableListOf<Pair<Int, Int>>()
     for (unit in targets) {
        for (i in 0..3) {
            val currRow = unit.row+rowDir[i]
            val currCol = unit.col+colDir[i]
            if (map[currRow][currCol] == space) squares.add(Pair(currRow, currCol))
        }
    } 
    squares.sortWith(compareBy({ it.first }, { it.second }))

    for (s in squares) {
        val d = reachable(row, col, s.first, s.second)
        if (d[row][col] < minDist) {
            minDist = d[row][col]
            distMap = d
        }
    }
    
    // no reachable target
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
    addUnit(c, closetRow, closetCol, removeUnit(c, row, col))
    ignored.add(Pair(closetRow, closetCol))
    // println("$row, $col $c moved to $closetRow, $closetCol")
    return Pair(closetRow, closetCol)
}

// BFS
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
            if (newRow == fromRow && newCol == fromCol || map[newRow][newCol] == space) {
                distMap[newRow][newCol] = distMap[curRow][curCol] + 1
                nextQ.add(Pair(newRow, newCol))
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
        if (c == elf && neighbor == goblin || c == goblin && neighbor == elf) {
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
        target.life = target.life - hit
        if (target.life <= 0) {
            removeEnemyUnit(c, minRow, minCol)
        }
        //println("$row, $col $c attack $minRow, $minCol ${map[minRow][minCol]} life ${target.life}")
    } else {
        //println("$row, $col $c no attack")
    }
    return attacked
}

fun addUnit(c: Char, row: Int, col: Int, life: Int) {
    map[row][col] = c
    val newUnit = Unit(row, col)
    newUnit.life = life
    if (c == goblin) {
        goblins.add(newUnit)
    }else {
        elfs.add(newUnit)
    }
}

fun removeEnemyUnit(c: Char, row: Int, col: Int): Int {
    if (c == elf) {
        return removeUnit(goblin, row, col)
    } else {
        return removeUnit(elf, row, col)
    }
}

fun removeUnit(c: Char, row: Int, col: Int): Int {
    val mock = Unit(row, col)
    map[row][col] = space
    if (c == goblin) {
        return goblins.removeAt(goblins.indexOf(mock)).life
    }else {
        return elfs.removeAt(elfs.indexOf(mock)).life
    }
}

fun getTarget(c: Char, row: Int, col: Int): Unit {
    val mock = Unit(row, col)
    if (c == elf) { 
        return goblins[goblins.indexOf(mock)]
    } else { 
        return elfs[elfs.indexOf(mock)]
    }
}

fun getResult() {
    var life = elfs.fold(0) { acc, unit -> acc + unit.life }
    life += goblins.fold(0) { acc, unit -> acc + unit.life }
    printMap()
    // only count completed round
    println("${rounds - 1} $life")
    println((rounds - 1) * life)
    exitProcess(0)
}

fun printMap() {
    for (i in 0 until rowNum) {
        for (j in 0 until colNum) {
            print(map[i][j])
        }
        println()
    }
    println()
}