package com.jhedeen.hyperskill

fun main() {
    val rows = getIntegerInput("Enter the number of rows:")
    val seats = getIntegerInput("Enter the number of seats in each row:")

    val iskra = Cinema(rows, seats)
    var input = getIntegerInput("\n1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Exit")
    while (input != 0) {
        when(input) {
            1 -> iskra.printSeats()
            2 -> {
                var result = false
                while (!result) {
                    result = iskra.buySeat()
                }
            }
            3 -> iskra.statistic()
            else -> print("Wrong input!")
        }
        input = getIntegerInput("\n1. Show the seats\n2. Buy a ticket\n3. Statistics\n0. Exit")
    }
}


fun getIntegerInput(prompt: String, min: Int = 0, max: Int = Int.MAX_VALUE): Int {
    var result = -1
    while (result !in min..max) {
        println(prompt)
        val input = readln()
        try {
            result = input.toInt()
        } catch (e: NumberFormatException) {
            println("Wrong input!")
        }
    }
    return result
}


class Cinema(private val rows: Int, private val seats: Int) {
    private val cinemaSeats = mutableListOf<MutableList<String>>()
    private var soldTickets = 0
    private var currentIncome = 0
    init {
        val head = (0..seats).map{ it.toString() }.toMutableList()
        head[0] = " "
        cinemaSeats.add(head)
        for (i in 1..rows) {
            val row = mutableListOf<String>()
            for(j in 0..seats) {
                if (j == 0) {
                    row.add("$i")
                } else {
                    row.add("S")
                }
            }
            cinemaSeats.add(row)
        }
    }

    fun statistic() {
        println()
        println("Number of purchased tickets: $soldTickets")
        println("Percentage: ${percentage()}%")
        println("Current income: \$$currentIncome")
        println("Total income: \$${totalIncome()}")
    }

    fun printSeats() {
        println("\nCinema:")
        for (r in cinemaSeats) {
            println(r.joinToString(" "))
        }
    }

    fun buySeat(): Boolean {
        println()
        val row = getIntegerInput("Enter a row number:")
        val seat = getIntegerInput("Enter a seat number in that row:")

        if (row < 1 || row > rows || seat < 1 || seat > seats) {
            println("\nWrong input!")
            return false
        } else if (cinemaSeats[row][seat] == "B") {
            println("\nThat ticket has already been purchased!")
            return false
        } else {
            cinemaSeats[row][seat] = "B"
            soldTickets++
            ticketPrice(row)
            return true
        }
    }

    private fun ticketPrice(row: Int) {
        val price = if (row > rows / 2 && rows * seats >= 60) 8 else 10
        currentIncome += price
        println("\nTicket price: \$$price")
    }

    private fun totalIncome(): Int {
        val totalSeats = rows * seats
        return if (totalSeats < 60) {
            totalSeats * 10
        } else {
            rows / 2 * seats * 10 + (rows - rows / 2) * seats * 8
        }
    }

    private fun percentage(): String {
        val perc = 100.0 * soldTickets / (seats * rows)
        return "%.2f".format(perc)
    }
}