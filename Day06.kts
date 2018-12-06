import java.io.File
import java.util.*
import kotlin.system.exitProcess

// input processing
val inputs = File("inputs/day06.txt").readLines()
val points = inputs.map { it.split(", ").map { it.toInt() } }

/*
Part 1

Using only the Manhattan distance, determine the area around each coordinate by counting the number of integer X,Y locations that are closest to that coordinate (and aren't tied in distance to any other coordinate).

Your goal is to find the size of the largest area that isn't infinite. For example, consider the following list of coordinates:

1, 1
1, 6
8, 3
3, 4
5, 5
8, 9
If we name these coordinates A through F, we can draw them on a grid, putting 0,0 at the top left:

..........
.A........
..........
........C.
...D......
.....E....
.B........
..........
..........
........F.
This view is partial - the actual grid extends infinitely in all directions. Using the Manhattan distance, each location's closest coordinate can be determined, shown here in lowercase:

aaaaa.cccc
aAaaa.cccc
aaaddecccc
aadddeccCc
..dDdeeccc
bb.deEeecc
bBb.eeee..
bbb.eeefff
bbb.eeffff
bbb.ffffFf
Locations shown as . are equally far from two or more coordinates, and so they don't count as being closest to any.

In this example, the areas of coordinates A, B, C, and F are infinite - while not shown here, their areas extend forever outside the visible grid. However, the areas of coordinates D and E are finite: D is closest to 9 locations, and E is closest to 17 (both including the coordinate's location itself). Therefore, in this example, the size of the largest area is 17.

What is the size of the largest area that isn't infinite?
 */
var maxRow = 0
var maxCol = 0
val EMPTY = 0
val SAME_DIST = -1
val directions = arrayOf(intArrayOf(0, -1), intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(-1, 0))
points.forEach {
    maxCol = maxOf(maxCol, it[0])
    maxRow = maxOf(maxRow, it[1])
}
val map = Array(maxRow+1, { IntArray(maxCol+1) })
val protected = Array(maxRow+1, { BooleanArray(maxCol+1) })
var queue = ArrayDeque<IntArray>()
var nextQueue = ArrayDeque<IntArray>()
for ((idx, value) in points.withIndex()) {
    map[value[1]][value[0]] = idx + 1
    queue.add(intArrayOf(value[1], value[0]))
}
while (!queue.isEmpty() || !nextQueue.isEmpty()) {
    val curr = queue.poll()
    walk(curr[0], curr[1])
    if (queue.isEmpty()) {
        protect()
        queue = nextQueue
        nextQueue = ArrayDeque<IntArray>()

    }
}
fun protect() {
    for (i in 0..maxRow) {
        for (j in 0..maxCol) {
            if (map[i][j] > EMPTY) protected[i][j] = true
        }
    }
}
fun walk(row: Int, col: Int) {
    println("walk $row, $col, ${map[row][col]}")
    if (map[row][col] <= EMPTY) return
    for (direction in directions) {
        val newRow = row + direction[0]
        val newCol = col + direction[1]
        if (newRow < 0 || newCol < 0 || newRow > maxRow || newCol > maxCol) continue
        if (map[newRow][newCol] == EMPTY) {
            map[newRow][newCol] = map[row][col]
            println("add $newRow, $newCol, ${map[newRow][newCol]}")
            nextQueue.add(intArrayOf(newRow, newCol))
        }
        else if (map[newRow][newCol] != map[row][col] && !protected[newRow][newCol]) {
            map[newRow][newCol] = SAME_DIST
            println("same $newRow, $newCol, ${map[newRow][newCol]}")
        }
    }
}
val counter = mutableMapOf<Int, Int>()
map.forEach {
    it
            .filter { it > EMPTY }
            .forEach {
    counter.putIfAbsent(it, 0)
    counter[it] = counter[it]!! + 1
} }
map.forEach {
    println(it.fold("") {acc, o -> acc + o + " "})
}
println(counter.maxBy { it.value }!!.value)
/*
Part 2

Amidst the chaos, you notice that exactly one claim doesn't overlap by even a single square inch of fabric with any other claim. If you can somehow draw attention to it, maybe the Elves will be able to make Santa's suit after all!

For example, in the claims above, only claim 3 is intact after all claims are made.

What is the ID of the only claim that doesn't overlap?
*/
