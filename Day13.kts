import java.io.File
import kotlin.system.exitProcess

// input processing
val inputs = File("inputs/day131.txt").readLines()
val map = inputs.map { it.map { it }.toMutableList() }.toMutableList()
val row = map.count()
val col = map[0].count()
/*
Part 1
You map out the tracks (your puzzle input) and see where you can help.

Tracks consist of straight paths (| and -), curves (/ and \), and intersections (+). Curves connect exactly two perpendicular pieces of track; for example, this is a closed loop:

/----\
|    |
|    |
\----/
Intersections occur when two perpendicular paths cross. At an intersection, a cart is capable of turning left, turning right, or continuing straight. Here are two loops connected by two intersections:

/-----\
|     |
|  /--+--\
|  |  |  |
\--+--/  |
   |     |
   \-----/
Several carts are also on the tracks. Carts always face either up (^), down (v), left (<), or right (>). (On your initial map, the track under each cart is a straight path matching the direction the cart is facing.)

Each time a cart has the option to turn (by arriving at any intersection), it turns left the first time, goes straight the second time, turns right the third time, and then repeats those directions starting again with left the fourth time, straight the fifth time, and so on. This process is independent of the particular intersection at which the cart has arrived - that is, the cart has no per-intersection memory.

Carts all move at the same speed; they take turns moving a single step at a time. They do this based on their current location: carts on the top row move first (acting from left to right), then carts on the second row move (again from left to right), then carts on the third row, and so on. Once each cart has moved one step, the process repeats; each of these loops is called a tick.

For example, suppose there are two carts on a straight track:

|  |  |  |  |
v  |  |  |  |
|  v  v  |  |
|  |  |  v  X
|  |  ^  ^  |
^  ^  |  |  |
|  |  |  |  |
First, the top cart moves. It is facing down (v), so it moves down one square. Second, the bottom cart moves. It is facing up (^), so it moves up one square. Because all carts have moved, the first tick ends. Then, the process repeats, starting with the first cart. The first cart moves down, then the second cart moves up - right into the first cart, colliding with it! (The location of the crash is marked with an X.) This ends the second and last tick.

Here is a longer example:

/->-\
|   |  /----\
| /-+--+-\  |
| | |  | v  |
\-+-/  \-+--/
  \------/

/-->\
|   |  /----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \->--/
  \------/

/---v
|   |  /----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \-+>-/
  \------/

/---\
|   v  /----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \-+->/
  \------/

/---\
|   |  /----\
| /->--+-\  |
| | |  | |  |
\-+-/  \-+--^
  \------/

/---\
|   |  /----\
| /-+>-+-\  |
| | |  | |  ^
\-+-/  \-+--/
  \------/

/---\
|   |  /----\
| /-+->+-\  ^
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /----<
| /-+-->-\  |
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /---<\
| /-+--+>\  |
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /--<-\
| /-+--+-v  |
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /-<--\
| /-+--+-\  |
| | |  | v  |
\-+-/  \-+--/
  \------/

/---\
|   |  /<---\
| /-+--+-\  |
| | |  | |  |
\-+-/  \-<--/
  \------/

/---\
|   |  v----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \<+--/
  \------/

/---\
|   |  /----\
| /-+--v-\  |
| | |  | |  |
\-+-/  ^-+--/
  \------/

/---\
|   |  /----\
| /-+--+-\  |
| | |  X |  |
\-+-/  \-+--/
  \------/
After following their respective paths for a while, the carts eventually crash. To help prevent crashes, you'd like to know the location of the first crash. Locations are given in X,Y coordinates, where the furthest left column is X=0 and the furthest top row is Y=0:

           111
 0123456789012
0/---\
1|   |  /----\
2| /-+--+-\  |
3| | |  X |  |
4\-+-/  \-+--/
5  \------/
In this example, the location of the first crash is 7,3.
*/

val cars = charArrayOf('v', '>', '^', '<')
val oldValues= mutableMapOf<Pair<Int, Int>, Char>()
val turns = mutableMapOf<Pair<Int, Int>, Int>()

for (i in 0 until row)
    for (j in 0 until col)
        if (map[i][j] in cars) {
            turns[Pair(i, j)] = 0
            oldValues[Pair(i, j)] = when (map[i][j]) {
                '>', '<' -> '-'
                else -> '|'
            }
        }
val ignore = mutableSetOf<Pair<Int, Int>>()
var clashes = 0
var x = 0
var y = 0
while (true) {
    ignore.clear()
    clashes = 0
    for (i in 0 until row) {
        for (j in 0 until col) {
            if (map[i][j] in cars && !ignore.contains(Pair(i, j))) {
                moveCar(i, j)
            }
           // print(map[i][j])
        }
        println()
    }
    println()
    if (clashes == 0) {
        println("$y,$x")
        exitProcess(0)
    }
}

fun moveCar(row: Int, col: Int) {
    val nextRow: Int
    val nextCol: Int
    val currentChar = map[row][col]
    when (currentChar) {
        'v' -> {
            nextRow = row + 1
            nextCol = col
        }
        '>' -> {
            nextRow = row
            nextCol = col + 1
        }
        '^' -> {
            nextRow = row - 1
            nextCol = col
        }
        else -> {
            nextRow = row
            nextCol = col - 1
        }
    }
    val nextChar = map[nextRow][nextCol]
    println("$row, $col, $currentChar $nextRow, $nextCol, $nextChar")

    if (nextChar in cars) {
        clashes++
        map[row][col] = oldValues[Pair(row, col)]!!
        println("set $row, $col, ${map[row][col]}")
        map[nextRow][nextCol] = oldValues[Pair(nextRow, nextCol)]!!
        println("set $nextRow, $nextCol, ${oldValues[Pair(nextRow, nextCol)]!!}")
        return
    }

    oldValues[Pair(nextRow, nextCol)] = nextChar
 

    var newNextChar: Char
    if (nextChar == '+') {
        newNextChar = turn(row, col, nextRow, nextCol)
    } else if (nextChar == '/') {
        turns[Pair(nextRow, nextCol)] = turns[Pair(row, col)]!!
        newNextChar = when (currentChar) {
            '^' -> '>'
            '<' -> 'v'
            '>' -> '^'
            else -> '<'
        }
    } else if (nextChar == '\\') {
        turns[Pair(nextRow, nextCol)] = turns[Pair(row, col)]!!
        newNextChar = when (currentChar) {
            '^' -> '<'
            '<' -> '^'
            '>' -> 'v'
            else -> '>'
        }
    } else {
        turns[Pair(nextRow, nextCol)] = turns[Pair(row, col)]!!
        newNextChar = currentChar
    }
    map[row][col] = oldValues[Pair(row, col)]!!
    println("set $row, $col, ${map[row][col]}")
    map[nextRow][nextCol] = newNextChar
    println("set $nextRow, $nextCol, $newNextChar")
    ignore.add(Pair(nextRow, nextCol))
}

fun turn(row: Int, col: Int, nextRow: Int, nextCol: Int): Char {
    val currentCar = map[row][col]
    val currentTurn = turns[Pair(row, col)]!!
    val idx = currentTurn % 3
    turns[Pair(nextRow, nextCol)] = currentTurn + 1
    return when (idx) {
        0 -> cars[(cars.indexOf(currentCar) + 1) % 4]
        1 -> currentCar
        else -> cars[(cars.indexOf(currentCar) + 3) % 4]
    }
}
/*
Part 2

What is the location of the last cart at the end of the first tick where it is the only cart left?
*/

