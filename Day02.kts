import java.io.File
import kotlin.system.exitProcess

// input processing
val inputs = File("inputs/day02.txt").readLines()

/*
Part 1

To make sure you didn't miss any, you scan the likely candidate boxes again, counting the number that have an ID containing exactly two of any letter and then separately counting those with exactly three of any letter. You can multiply those two counts together to get a rudimentary checksum and compare it to what your device predicts.

For example, if you see the following box IDs:

abcdef contains no letters that appear exactly two or three times.
bababc contains two a and three b, so it counts for both.
abbcde contains two b, but no letter appears exactly three times.
abcccd contains three c, but no letter appears exactly two times.
aabcdd contains two a and two d, but it only counts once.
abcdee contains two e.
ababab contains three a and three b, but it only counts once.
Of these box IDs, four of them contain a letter which appears exactly twice, and three of them contain a letter which appears exactly three times. Multiplying these together produces a checksum of 4 * 3 = 12.

What is the checksum for your list of box IDs?
 */
var double = 0
var triple = 0
for (input in inputs) {
    val freqMap = mutableMapOf<Char, Int>()
    for (c in input) {
        freqMap.putIfAbsent(c, 0)
        freqMap.replace(c, freqMap[c]!! + 1)
    }
    if (freqMap.containsValue(2)) { double++ }
    if (freqMap.containsValue(3)) { triple++ }
}
println(double * triple)

/*
Part 2

The boxes will have IDs which differ by exactly one character at the same position in both strings. For example, given the following box IDs:

abcde
fghij
klmno
pqrst
fguij
axcye
wvxyz
The IDs abcde and axcye are close, but they differ by two characters (the second and fourth). However, the IDs fghij and fguij differ by exactly one character, the third (h and u). Those must be the correct boxes.

What letters are common between the two correct box IDs? (In the example above, this is found by removing the differing character from either ID, producing fgij.)
 */
for (i in 0 until inputs.count()-1) {
    for (j in i+1 until inputs.count()) {
        val loc = locFor1Distance(inputs[i], inputs[j])
        if (loc != -1) {
            println(inputs[i].substring(0, loc) + inputs[i].substring(loc+1))
            exitProcess(0)
        }
    }
}

fun locFor1Distance(a: String, b: String): Int {
    assert(a.length == b.length)
    var loc = -1
    for (i in 0 until a.length) {
        if (a[i] != b[i]) {
            if (loc != -1) return -1
            loc = i
        }
    }
    return loc
}