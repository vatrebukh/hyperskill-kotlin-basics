package com.jhedeen.hyperskill

import kotlin.math.abs

fun main() {
    val game = ChessGame()
    println("Pawns-Only Chess")
    println("First Player's name:")
    val fp = readln()
    println("Second Player's name:")
    val sp = readln()
    game.printBoard()

    var turnFirst = true

    while (true) {
        println("${if (turnFirst) fp else sp}'s turn:")
        val move = readln()
        if (move == "exit") {
            break
        }
        if (!game.isValidMove(move, turnFirst)) {
            continue
        }
        game.makeMove(move, turnFirst)
        game.printBoard()
        if (game.isOver()) {
            println("${if (turnFirst) "White" else "Black"} wins!")
            break
        }
        if (game.isDraw(turnFirst)) {
            println("Stalemate!")
            break
        }

        turnFirst = turnFirst xor true
    }
    println("Bye!")
}

class ChessGame {
    private val border = "  +---+---+---+---+---+---+---+---+"
    private val size = 8
    private val EMPTY = ' '
    private val WHITE = 'W'
    private val BLACK = 'B'
    private val cols = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    private val board = initBoard()
    private var phantom = ""

    fun isOver(): Boolean {
        if (board[0].joinToString().contains(BLACK) || board[size - 1].joinToString().contains(WHITE)) {
            return true
        }
        var whites = 0
        var blacks = 0
        for (line in board) {
            val str = line.joinToString()
            if (str.contains(WHITE)) {
                whites++
            }
            if (str.contains(BLACK)) {
                blacks++
            }
        }
        if (whites == 0 || blacks == 0) {
            return true
        }

        return false
    }

    fun isDraw(isWhite: Boolean): Boolean {
        return if (isWhite) !hasBlackMoves() else !hasWhiteMoves()
    }

    private fun hasWhiteMoves(): Boolean {
        for (r in 1 until size - 2) {
            for (c in 0 until board[r].size) {
                if (board[r][c] == WHITE) {
                    if (board[r + 1][c] == EMPTY) {
                        return true
                    }
                    if (c < size - 1 && (board[r + 1][c + 1] == BLACK || phantom != "" && cols.indexOf(phantom[0]) == c + 1 && phantom[1].toString().toInt() - 1 == r + 1)) {
                        return true
                    }
                    if (c > 0 && (board[r + 1][c - 1] == BLACK || phantom != "" && cols.indexOf(phantom[0]) == c - 1 && phantom[1].toString().toInt() - 1 == r + 1)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun hasBlackMoves(): Boolean {
        for (r in 2 until size - 1) {
            for (c in 0 until board[r].size) {
                if (board[r][c] == BLACK) {
                    if (board[r - 1][c] == EMPTY) {
                        return true
                    }
                    if (c < size - 1 && (board[r - 1][c + 1] == WHITE || phantom != "" && cols.indexOf(phantom[0]) == c + 1 && phantom[1].toString().toInt() - 1 == r - 1)) {
                        return true
                    }
                    if (c > 0 && (board[r - 1][c - 1] == WHITE || phantom != "" && cols.indexOf(phantom[0]) == c - 1 && phantom[1].toString().toInt() - 1 == r - 1)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun isValidMove(move: String, isWhite: Boolean):Boolean {
        if (move.length != 4) {
            println("Invalid Input")
            return false
        }

        val withinBoard = "[a-h][1-8][a-h][1-8]".toRegex().matches(move)
        if (!withinBoard) {
            println("Invalid Input")
            return false
        }
        if (abs(cols.indexOf(move[0]) - cols.indexOf(move[2])) > 1) {
            println("Invalid Input")
            return false
        }
        val origin = move[1].toString().toInt()
        val destination = move[3].toString().toInt()
        val delta = if (isWhite) destination - origin else origin - destination
        if (delta < 0 || delta > 2) {
            println("Invalid Input")
            return false
        }
        if (delta == 2 && (isWhite && origin != 2 || !isWhite && origin != 7)) {
            println("Invalid Input")
            return false
        }
        if (isWhite && board[origin - 1][cols.indexOf(move[0])] != WHITE) {
            println("No white pawn at ${move.substring(0,2)}")
            return false
        }
        if (!isWhite && board[origin - 1][cols.indexOf(move[0])] != BLACK) {
            println("No black pawn at ${move.substring(0,2)}")
            return false
        }
        if (delta == 0) {
            println("Invalid Input")
            return false
        }
        if (move[0] == move[2] && board[destination - 1][cols.indexOf(move[2])] != EMPTY) {
            println("Invalid Input")
            return false
        }
        if (move[0] == move[2] && delta == 2 && board[(origin + destination) / 2 - 1][cols.indexOf(move[2])] != EMPTY) {
            println("Invalid Input")
            return false
        }

        if (abs(cols.indexOf(move[0]) - cols.indexOf(move[2])) == 1) {
            if (isWhite && (board[destination - 1][cols.indexOf(move[2])] != BLACK && move.substring(2) != phantom)) {
                println("Invalid Input")
                return false
            }
            if (!isWhite && (board[destination - 1][cols.indexOf(move[2])] != WHITE && move.substring(2) != phantom)) {
                println("Invalid Input")
                return false
            }
        }

        return true
    }

    fun makeMove(move: String, isWhite: Boolean) {
        if (move[0] == move[2]) {
            moveForward(move, isWhite)
        } else {
            capture(move, isWhite)
        }
    }

    private fun capture(move: String, isWhite: Boolean) {
        val or = move[1].toString().toInt() - 1
        val oc = cols.indexOf(move[0])
        val dr = move[3].toString().toInt() - 1
        val dc = cols.indexOf(move[2])

        if (board[dr][dc] == EMPTY) {
            val rr = if (isWhite) dr - 1 else dr + 1
            board[rr][dc] = EMPTY
        }
        board[or][oc] = EMPTY
        board[dr][dc] = if (isWhite) WHITE else BLACK
    }

    private fun moveForward(move: String, isWhite: Boolean) {
        val or = move[1].toString().toInt() - 1
        val oc = cols.indexOf(move[0])
        board[or][oc] = EMPTY

        val dr = move[3].toString().toInt() - 1
        val dc = cols.indexOf(move[2])
        board[dr][dc] = if (isWhite) WHITE else BLACK

        if (abs(or - dr) == 2) {
            val fr = if (isWhite) dr else dr + 2
            phantom = "${move[0]}$fr"
        } else {
            phantom = ""
        }
    }

    fun printBoard() {
        println(border)
        var rowNum = 8
        for (line in board.reversed()) {
            println(line.joinToString(" | ", prefix = "${rowNum--} | ", postfix = " |"))
            println(border)
        }
        println(cols.joinToString("   ", prefix = "    "))
        println()
    }

    private fun initBoard(): MutableList<MutableList<Char>> {
        val board = mutableListOf<MutableList<Char>>()
        for (r in 0 until size) {
            val pawn = if (r == 1) WHITE else if (r == size - 2) BLACK else EMPTY
            board.add(CharArray(size){pawn}.toMutableList())
        }
        return board
    }
}