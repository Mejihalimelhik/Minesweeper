package minesweeper

var minesList: MutableSet<Pair<Int, Int>> = mutableSetOf()
var numbersList: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()
var numbersRevealed: MutableSet<Pair<Int, Int>> = mutableSetOf()
var exploredNoAround: MutableSet<Pair<Int, Int>> = mutableSetOf()
var unexploredList: MutableSet<Pair<Int, Int>> = mutableSetOf()
var markedList: MutableSet<Pair<Int, Int>> = mutableSetOf()
var state = false

fun main() {
    print("How many mines do you want on the field? ")
    readLine()!!.toInt().let { while (minesList.size < it) minesList.add(Pair((1..9).random(), (1..9).random())) }
    for (l in 1..9) {  for (w in 1..9) { checkNeighbours(l, w, ::onCheckNeighbours) } }
    minesList.forEach{ numbersList.remove(it) }
    printBoard()
    while(!state && minesList != markedList && minesList != unexploredList) {
        unexploredList = mutableSetOf()
        print("Set/unset mines marks or claim a cell as free: ")
        readLine()!!.split(" ").let { input ->
            Pair(input[0].toInt(), input[1].toInt()).let { p ->
                when {
                    input[2] == "free" && minesList.contains(p) -> state = true
                    input[2] == "free" && numbersList.contains(p) -> numbersRevealed.add(p)
                    input[2] == "mine" -> if (markedList.contains(p)) markedList.remove(p) else markedList.add(p)
                    else -> { freeBoard(p) }
                }
                printBoard()
            }
        }
    }
    if (state) println("stepped on a mine and failed!")
    else println("Congratulations! You found all the mines!")
}

fun printBoard() {
    println("\n" + " |123456789|\n" + "-|---------|")
    for (w in 1..9) { print("${w}|")
        for (l in 1..9) {
            when {
                state && minesList.contains(Pair(l, w)) -> "X"
                numbersRevealed.contains(Pair(l, w)) -> "${numbersList[Pair(l, w)]}"
                markedList.contains(Pair(l, w)) -> "*"
                exploredNoAround.contains(Pair(l, w)) -> "/"
                else -> { unexploredList.add(Pair(l, w)); "."}
            }.run(::print)
        }
        println("|")
    }
    println("-|---------|")
}

fun freeBoard(p: Pair<Int, Int>) =
    if (p.first in 1..9 && p.second in 1..9) checkNeighbours(p.first, p.second, ::onFreeBoard) else null

fun onFreeBoard(p: Pair<Int, Int>, a1: Any) {
    if (markedList.contains(p)) markedList.remove(p)
    if (numbersList.contains(p)) numbersRevealed.add(p)
    else if (!exploredNoAround.contains(p)) { exploredNoAround.add(p); freeBoard(p) }
}

fun checkNeighbours(l: Int, w: Int, kFun: (Pair<Int, Int>, Pair<Int, Int>) -> Unit) {
    for (i in (l - 1)..(l + 1)) {
        for (j in (w - 1)..(w + 1)) {
            kFun(Pair(i, j), Pair(l, w))
        }
    }
}

fun onCheckNeighbours(p1: Pair<Int, Int>, p2: Pair<Int, Int>) {
    numbersList.let { l ->
        if (minesList.contains(p1)) if (l.contains(p2)) l[p2] = l[p2]!!.plus(1) else l[p2] = 1
    }
}