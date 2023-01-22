package com.jhedeen.hyperskill

import kotlin.random.Random

fun main() {
    print("How many mines do you want on the field?")
    val minesCount = readln().toInt()
    val game = Game(minesCount = minesCount)

    game.printBoard()

    while (!game.isGameOver()) {
        print("Set/unset mines marks or claim a cell as free:")
        val (col, row, action) = readln().split(" ")

        when (action) {
            "free" -> game.guessFree(row.toInt() - 1, col.toInt() - 1)
            "mine" -> game.guessMine(row.toInt() - 1, col.toInt() - 1)
        }
    }
}

class Game(private val size: Int = 9, private val minesCount: Int) {
    private val MINE = "X"
    private val MARK = "*"
    private val FREE = "/"
    private val EMPTY = "."
    private val TEMP = "?"

    private val board: MutableList<MutableList<String>> = emptyBoard()
    private var mineBoard: List<List<String>> = emptyBoard()
    private var gameOver = false
    private var firstMove = true


    fun guessMine(row: Int, col: Int) {
        if (board[row][col] == EMPTY) {
            board[row][col] = MARK
        } else if (board[row][col] == MARK) {
            board[row][col] = EMPTY
        } else {
            println("There is a number here!")
        }
        printBoard()
    }

    fun guessFree(row: Int, col: Int) {
        if (mineBoard[row][col] == MINE) {
            for (r in 0 until size) {
                for (c in 0 until size) {
                    if (mineBoard[r][c] == "X") {
                        board[r][c] = "X"
                    }
                }
            }
            printBoard()
            println("You stepped on a mine and failed!")
            gameOver = true
        } else if (board[row][col] == EMPTY || board[row][col] == MARK) {
            if (firstMove) {
                mineBoard = allocateMines(row, col)
                firstMove = false
            }
            val count = countAround(row, col)
            if (count > 0) {
                board[row][col] = count.toString()
            } else {
                explore(row, col)
                while (!isResolved()) {
                    exploreBoard()
                }
            }
            printBoard()
        } else {
            printBoard()
        }
    }

    private fun explore(row: Int, col: Int) {
        board[row][col] = FREE
        if (row > 0) {
            exploreRow(col, board[row - 1], false)
        }
        if (row < size - 1) {
            exploreRow(col, board[row + 1], false)
        }
        exploreRow(col, board[row], true)
    }

    private fun exploreRow(col: Int, row: MutableList<String>, self: Boolean) {
        if (col > 0 && (row[col - 1] == MARK || row[col - 1] == EMPTY)) {
            row[col - 1] = TEMP
        }
        if (col < size - 1 && (row[col + 1] == MARK || row[col + 1] == EMPTY)) {
            row[col + 1] = TEMP
        }
        if (!self && (row[col] == MARK || row[col] == EMPTY)) {
            row[col] = TEMP
        }
    }

    private fun exploreBoard() {
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (board[row][col] == TEMP) {
                    val count = countAround(row, col)
                    if (count > 0) {
                        board[row][col] = count.toString()
                    } else {
                        explore(row, col)
                    }
                    return
                }
            }
        }
    }

    private fun isResolved(): Boolean {
        for (row in board) {
            for (col in row) {
                if (col == TEMP) {
                    return false
                }
            }
        }
        return true
    }

    fun isGameOver(): Boolean {
        if (firstMove) {
            return false
        }
        if (gameOver) {
            return true
        }
        var remaining = 0
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (board[row][col] == MARK || board[row][col] == EMPTY) {
                    remaining++
                }
            }
        }
        if (remaining == minesCount) {
            println("Congratulations! You found all the mines!")
            return true
        }

        for (row in 0 until size) {
            for (col in 0 until size) {
                if (mineBoard[row][col] == MINE && board[row][col] != MARK) {
                    return false
                }
            }
        }
        println("Congratulations! You found all the mines!")
        return true
    }

    private fun countAround(row: Int, col: Int): Int {
        val upper = if (row > 0) countRow(col, mineBoard[row - 1], false) else 0
        val lower = if (row < size - 1) countRow(col, mineBoard[row + 1], false) else 0
        val current = countRow(col, mineBoard[row], true)
        return upper + lower + current
    }

    private fun countRow(col: Int, row: List<String>, self: Boolean): Int {
        var count = 0
        if (col > 0 && row[col - 1] == MINE) {
            count++
        }
        if (!self && row[col] == MINE) {
            count++
        }
        if (col < size - 1 && row[col + 1] == MINE) {
            count++
        }
        return count
    }

    fun printBoard() {
        print("\n |")
        for (ind in board.indices) {
            print(ind + 1)
        }
        println("|")

        print("—│")
        for (ind in board.indices) {
            print("—")
        }
        println("|")

        for (ind in board.indices) {
            print(ind + 1)
            print("|")
            print(board[ind].joinToString(""))
            println("|")
        }

        print("—│")
        for (ind in board.indices) {
            print("—")
        }
        println("|")
    }

    private fun allocateMines(r: Int, c: Int): List<List<String>> {
        val res: MutableList<List<String>> = mutableListOf()
        val mineList = generateMines(r*size + c)
        for (i in 0 until size) {
            val row = mutableListOf<String>()
            for (j in 0 until size) {
                if (mineList.contains(i*size + j)) {
                    row.add(MINE)
                } else {
                    row.add(EMPTY)
                }
            }
            res.add(row.toList())
        }
        return res.toList()
    }

    private fun generateMines(freeCell: Int): List<Int> {
        val mines = mutableListOf<Int>()
        val cells = size * size
        while (mines.size < minesCount) {
            val rnd = Random.nextInt(cells)
            if (!mines.contains(rnd) && rnd != freeCell) {
                mines.add(rnd)
            }
        }
        return mines.sorted().toList()
    }

    private fun emptyBoard(): MutableList<MutableList<String>> {
        val res: MutableList<MutableList<String>> = mutableListOf()
        for (row in 0 until size) {
            val line = mutableListOf<String>()
            for (col in 0 until size) {
                line.add(EMPTY)
            }
            res.add(line)
        }
        return res
    }
}