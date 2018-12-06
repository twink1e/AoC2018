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
val map = Array(maxRow+1, { Array(maxCol+1, { Pair(EMPTY, maxRow * maxCol) }) })

for ((idx, value) in points.withIndex()) {
    var dist = 2
    val localMap = Array(maxRow+1, { IntArray(maxCol+1) })
    var queue = ArrayDeque<IntArray>()
    var nextQueue = ArrayDeque<IntArray>()
    queue.add(intArrayOf(value[1], value[0]))
    localMap[value[1]][value[0]] = 1
    while (!queue.isEmpty() || !nextQueue.isEmpty()) {
        val curr = queue.poll()
        walk(curr[0], curr[1], localMap, dist, nextQueue)
        if (queue.isEmpty()) {
            dist++
            queue = nextQueue
            nextQueue = ArrayDeque<IntArray>()

        }
    }
    for (i in 0..maxRow) {
        for (j in 0..maxCol) {
            if (map[i][j].second > localMap[i][j]) {
                map[i][j] = Pair(idx+1, localMap[i][j])
            } else if (map[i][j].second == localMap[i][j]) {
                map[i][j] = Pair(SAME_DIST, localMap[i][j])
            }
        }
    }
}

fun walk(row: Int, col: Int, map: Array<IntArray>, dist: Int, nextQueue: Queue<IntArray>) {
    for (direction in directions) {
        val newRow = row + direction[0]
        val newCol = col + direction[1]
        if (newRow < 0 || newCol < 0 || newRow > maxRow || newCol > maxCol) continue
        if (map[newRow][newCol] == 0) {
            map[newRow][newCol] = dist
            nextQueue.add(intArrayOf(newRow, newCol))
        }
    }
}
val counter = mutableMapOf<Int, Int>()
map.map { it.map { it.first } }
        .forEach { it
            .filter { it > EMPTY }
            .forEach {
                counter.putIfAbsent(it, 0)
                counter[it] = counter[it]!! + 1
} }
for (i in 0..maxRow) {
    counter.remove(map[i][0].first)
    counter.remove(map[i][maxCol].first)
}
for (j in 0..maxCol) {
    counter.remove(map[0][j].first)
    counter.remove(map[maxRow][j].first)
}
println(counter.maxBy { it.value }!!.value)
/*
Part 2

On the other hand, if the coordinates are safe, maybe the best you can do is try to find a region near as many coordinates as possible.

For example, suppose you want the sum of the Manhattan distance to all of the coordinates to be less than 32. For each location, add up the distances to all of the given coordinates; if the total of those distances is less than 32, that location is within the desired region. Using the same coordinates as above, the resulting region looks like this:

..........
.A........
..........
...###..C.
..#D###...
..###E#...
.B.###....
..........
..........
........F.
In particular, consider the highlighted location 4,3 located at the top middle of the region. Its calculation is as follows, where abs() is the absolute value function:

Distance to coordinate A: abs(4-1) + abs(3-1) =  5
Distance to coordinate B: abs(4-1) + abs(3-6) =  6
Distance to coordinate C: abs(4-8) + abs(3-3) =  4
Distance to coordinate D: abs(4-3) + abs(3-4) =  2
Distance to coordinate E: abs(4-5) + abs(3-5) =  3
Distance to coordinate F: abs(4-8) + abs(3-9) = 10
Total distance: 5 + 6 + 4 + 2 + 3 + 10 = 30
Because the total distance to all coordinates (30) is less than 32, the location is within the region.

This region, which also includes coordinates D and E, has a total size of 16.

Your actual region will need to be much larger than this example, though, instead including all locations with a total distance of less than 10000.

What is the size of the region containing all locations which have a total distance to all given coordinates of less than 10000?
*/
fun totalDist(row: Int, col: Int) = points.fold(0) {acc, it -> acc + Math.abs(row - it[1]) + Math.abs(col - it[0])}
var ans = 0
for (i in 0..maxRow) {
    for (j in 0..maxCol) {
        if (totalDist(i, j) < 10000) ans++
    }
}

println(ans)