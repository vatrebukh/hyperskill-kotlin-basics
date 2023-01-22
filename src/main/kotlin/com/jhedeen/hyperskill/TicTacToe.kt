package com.jhedeen.hyperskill

import kotlin.math.abs

fun main() {
    var activeMark = 'X'
    var state = "Game not finished"
    createEmptyBoard()
    printBoard()

    while (state == "Game not finished") {
        updateBoard(activeMark)
        printBoard()
        state = getState()
        activeMark = if (activeMark == 'X') 'O' else 'X'
    }
    println(state)
}

val board = mutableListOf<MutableList<Char>>()

fun createEmptyBoard() {
    board.add(mutableListOf(' ', ' ', ' '))
    board.add(mutableListOf(' ', ' ', ' '))
    board.add(mutableListOf(' ', ' ', ' '))
}

fun printBoard() {
    println("---------")
    for(i in board) {
        print("| ")
        print(i.joinToString(" "))
        println(" |")

    }
    println("---------")
}

fun updateBoard(mark: Char) {
    var done = false
    while(!done) {
        val input = readln().split(" ").toList()
        try {
            val row = input[0].toInt()
            val col = input[1].toInt()
            val cell = board[row - 1][col - 1]
            if (cell == 'X' || cell == 'O') {
                println("This cell is occupied! Choose another one!")
            } else {
                board[row - 1][col - 1] = mark
                done = true
            }
        } catch (e: NumberFormatException) {
            println("You should enter numbers!")
        } catch (e: IndexOutOfBoundsException) {
            println("Coordinates should be from 1 to 3!")
        }
    }
}

fun getState(): String {
    var winner = ""
    for(i in board) {
        if (i[0] == i[1] && i[0] == i[2]) {
            winner += i[0]
        }
    }
    for (i in 0 until board[0].size) {
        if (board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
            winner += board[0][i]
        }
    }
    if(board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
        winner += board[1][1]
    }
    if(board[2][0] == board[1][1] && board[2][0] == board[0][2]) {
        winner += board[1][1]
    }

    winner = winner.replace(" ", "").replace("_", "")
    if(winner.length == 1) {
        return "${winner[0]} wins"
    } else if (winner.length > 1) {
        return "Impossible"
    }

    val x = count(board, 'X')
    val o = count(board, 'O')
    return if (abs(x - o) > 1) {
        "Impossible"
    } else if (x + o == 9) {
        "Draw"
    } else {
        "Game not finished"
    }
}

fun count(board: List<List<Char>>, c: Char): Int {
    var counter = 0
    for (row in board) {
        for (j in row) {
            if (c == j) {
                counter++
            }
        }
    }
    return counter
}