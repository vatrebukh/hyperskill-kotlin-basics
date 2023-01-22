package com.jhedeen.hyperskill

fun main() {
    println("Connect Four")
    println("First player's name:")
    val fp = readln()
    println("Second player's name:")
    val sp = readln()

    val game = createGame()
    val games = getNumberOfGames()
    var gameNumber = 1
    var firstScore = 0
    var secondScore = 0

    println("$fp VS $sp")
    println("${game.rows} X ${game.columns} board")
    if (games > 1) {
        println("Total $games games")
    }

     while (gameNumber <= games) {
        if (games > 1) {
            println("Game #$gameNumber")
        } else {
            println("Single game")
        }
        game.printBoard()
        var isFirst = gameNumber % 2 == 1

        while (true) {
            val player = if (isFirst) fp else sp
            val move = game.getMove(player)
            if (move == 0) {
                gameNumber = games
                break
            }
            game.makeMove(move - 1, isFirst)
            if (game.hasWinner()) {
                println("Player $player won")
                if (games > 1) {
                    if (isFirst) {
                        firstScore += 2
                    } else {
                        secondScore += 2
                    }
                    println("Score")
                    println("$fp: $firstScore $sp: $secondScore")
                }
                break
            }
            if (game.isDraw()) {
                println("It is a draw")
                if (games > 1) {
                    firstScore++
                    secondScore++
                    println("Score")
                    println("$fp: $firstScore $sp: $secondScore")
                }
                break
            }
            isFirst = isFirst xor true
        }
         gameNumber++
         game.resetBoard()
    }
    println("Game over!")
}

fun getNumberOfGames(): Int {
    while (true) {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        val g = readln()
        if (g.isEmpty()) {
            return 1
        }
        if ("[1-9]\\d*".toRegex().matches(g)) {
            return g.toInt()
        }
        println("Invalid input")
    }
}

fun createGame(): ConnectFourGame {
    while (true) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        val size = readln().lowercase()

        if (size.isEmpty()) {
            return ConnectFourGame()
        }
        if ("\\s*\\d+\\s*x\\s*\\d+\\s*".toRegex().matches(size)) {
            val (r, c) = size.replace("\\s".toRegex(), "").split("x").map { it.toInt() }
            if (r < 5 || r > 9) {
                println("Board rows should be from 5 to 9")
                continue
            }
            if (c < 5 || c > 9) {
                println("Board columns should be from 5 to 9")
                continue
            }
            return ConnectFourGame(r, c)
        }
        println("Invalid input")
    }
}

class ConnectFourGame(val rows: Int = 6, val columns: Int = 7) {

    private var board = Array(rows){CharArray(columns){' '}.toMutableList()}.toMutableList()
    private val firstMark = 'o'
    private val secondMark = '*'
    private val winner1 = "oooo"
    private val winner2 = "****"

    fun printBoard() {
        println(board[0].indices.map { it + 1 }.joinToString(" ", prefix = " ", postfix = " "))
        for (line in board) {
            println(line.joinToString("║", prefix = "║", postfix = "║"))
        }
        println(CharArray(columns){'═'}.joinToString("╩", prefix = "╚", postfix = "╝"))
    }

    fun resetBoard() {
        board = Array(rows){CharArray(columns){' '}.toMutableList()}.toMutableList()
    }

    fun makeMove(col: Int, isFirst: Boolean) {
        board[getRow(col)][col] = if (isFirst) firstMark else secondMark
        printBoard()
    }

    private fun getRow(col: Int): Int {
        return rows - board.count { row -> row[col] != ' ' } - 1
    }

    fun getMove(player: String): Int {
        while (true) {
            println("${player}'s turn:")
            val c = readln()
            if (c == "end") {
                return 0
            }
            if (!"\\d+".toRegex().matches(c)) {
                println("Incorrect column number")
                continue
            }
            val c2 = c.toInt()
            if (c2 > columns || c2 == 0) {
                println("The column number is out of range (1 - $columns)")
                continue
            }
            val freeRow = getRow(c2 - 1)
            if (freeRow == -1) {
                println("Column $c2 is full")
                continue
            }
            return c2
        }
    }

    fun hasWinner(): Boolean {
        val hasRow = board.any { checkRow(it) }
        if (hasRow || checkColumns() || checkDiagonals()) {
            return true
        }

        return false
    }

    private fun checkRow(row: List<Char>): Boolean {
        val res = row.joinToString("")
        return res.contains(winner1) || res.contains(winner2)
    }

    private fun checkColumns(): Boolean {
        for (i in 0 until columns) {
            var res = ""
            for (row in board) {
                res += row[i]
            }
            if (res.contains(winner1) || res.contains(winner2)) {
                return true
            }
        }
        return false
    }

    private fun checkDiagonals(): Boolean {
        for (r in 0..rows - 4) {
            for (c in 3 until columns) {
                var i = r
                var j = c
                var res = ""
                while (i < rows && j >= 0) {
                    res += board[i++][j--]
                }
                if (res.contains(winner1) || res.contains(winner2)) {
                    return true
                }
            }
            for (c in 0..columns-4) {
                var i = r
                var j = c
                var res = ""
                while (i < rows && j < columns) {
                    res += board[i++][j++]
                }
                if (res.contains(winner1) || res.contains(winner2)) {
                    return true
                }
            }
        }
        return false
    }

    fun isDraw(): Boolean {
        return board.sumOf { row -> row.count { it == ' ' } } == 0
    }
}