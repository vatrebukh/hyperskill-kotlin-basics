package com.jhedeen.hyperskill

class CoffeeMachine {
    private val supplies = mutableListOf(400, 540, 120, 9, 550)
    private val headers = listOf("water", "milk", "beans", "cups", "money")
    private val costs = listOf(
        listOf(250, 0, 16, 1, 4),
        listOf(350, 75, 20, 1, 7),
        listOf(200, 100, 12, 1, 6)
    )

    private var state: CoffeeState = CoffeeState.WAITING

    fun command(action: String) {
        when (state) {
            CoffeeState.WAITING -> {
                when(action) {
                    "buy" -> {
                        state = CoffeeState.BUY
                        print("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")
                    }
                    "fill" -> {
                        print("\nWrite how many ml of water do you want to add: ")
                        state = CoffeeState.REFILL_WATER
                    }
                    "take" -> {
                        takeMoney()
                        print("\nWrite action (buy, fill, take, remaining, exit): ")
                    }
                    "remaining" -> {
                        printState()
                        print("\nWrite action (buy, fill, take, remaining, exit): ")
                    }
                }
            }
            CoffeeState.BUY -> {
                buyCoffee(action)
                state = CoffeeState.WAITING
                print("\nWrite action (buy, fill, take, remaining, exit): ")
            }
            in CoffeeState.REFILL_WATER..CoffeeState.REFILL_CUPS -> {
                fillSupplements(action)
            }
            else -> return
        }
    }

    private fun printState() {
        println("\nThe coffee machine has:")
        println("${supplies[0]} ml of water")
        println("${supplies[1]} ml of milk")
        println("${supplies[2]} g of coffee beans")
        println("${supplies[3]} disposable cups")
        println("\$${supplies[4]} of money")
    }

    private fun buyCoffee(input: String) {
        if (input == "back") {
            return
        }

        val cost = costs[input.toInt() - 1]
        if (checkSupplements(cost)) {
            println("I have enough resources, making you a coffee!")
            for (i in 0 .. cost.size - 2) {
                supplies[i] = supplies[i] - cost[i]
            }
            supplies[4] = supplies[4] + cost[4]
        }
    }

    private fun checkSupplements(option: List<Int>): Boolean {
        var isEnough = true
        for (i in 0 .. option.size - 2) {
            if (supplies[i] < option[i]) {
                println("Sorry, not enough ${headers[i]}!")
                isEnough = false
            }
        }
        return isEnough
    }

    private fun takeMoney() {
        println()
        println("I gave you \$${supplies[4]}")
        supplies[4] = 0
    }

    private fun fillSupplements(input: String) {
        when (state) {
            CoffeeState.REFILL_WATER -> {
                supplies[0] = supplies[0] + input.toInt()
                print("Write how many ml of milk do you want to add: ")
                state = CoffeeState.REFILL_MILK
            }
            CoffeeState.REFILL_MILK -> {
                supplies[1] = supplies[1] + input.toInt()
                print("Write how many grams of coffee beans do you want to add: ")
                state = CoffeeState.REFILL_BEANS
            }
            CoffeeState.REFILL_BEANS -> {
                supplies[2] = supplies[2] + input.toInt()
                print("Write how many disposable cups of coffee do you want to add: ")
                state = CoffeeState.REFILL_CUPS
            }
            CoffeeState.REFILL_CUPS -> {
                supplies[3] = supplies[3] + input.toInt()
                print("\nWrite action (buy, fill, take, remaining, exit): ")
                state = CoffeeState.WAITING
            }
            else -> return
        }
    }
}

enum class CoffeeState {
    WAITING,
    BUY,
    REFILL_WATER,
    REFILL_MILK,
    REFILL_BEANS,
    REFILL_CUPS,
}


fun main() {
    val lavazza = CoffeeMachine()

    print("Write action (buy, fill, take, remaining, exit): ")
    var action = readln()

    while (action != "exit") {
        lavazza.command(action)
        action = readln()
    }
}