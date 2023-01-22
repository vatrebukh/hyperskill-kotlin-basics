package com.jhedeen.hyperskill

import kotlin.random.Random

val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
val suits = listOf("♦", "♥", "♠", "♣")
var wonLast = Player("")

fun main() {
    val player = Player("Player")
    val computer = ComputerPlayer("Computer")

    println("Indigo Card Game")
    val deck = Deck()
    val playerStarted = isFirst()
    val cardsOnDesk = deck.getTopCards(4)
    player.takeCards(deck.getTopCards(6))
    computer.takeCards(deck.getTopCards(6))
    wonLast = if (playerStarted) player else computer
    var activePlayer = if (playerStarted) player else computer

    println(cardsOnDesk.joinToString(" ", prefix = "Initial cards on the table:"))

    while (true) {
        println()
        if (cardsOnDesk.isEmpty()) {
            println("No cards on the table")
        } else {
            println("${cardsOnDesk.size} cards on the table, and the top card is ${cardsOnDesk.last()}")
        }

        if (player.hand.isEmpty() && computer.hand.isEmpty()) {
            wonLast.winCards(cardsOnDesk)

            if (player.cardsCount > computer.cardsCount || player.cardsCount == computer.cardsCount && playerStarted) {
                player.score += 3
            } else {
                computer.score += 3
            }
            printScore(player, computer)
            break
        }

        val card = activePlayer.getCard(cardsOnDesk)
        if (card == "exit") {
            break
        }
        if (cardsOnDesk.isEmpty()) {
            cardsOnDesk.add(card)
        } else {
            val lastCard = cardsOnDesk.last()
            cardsOnDesk.add(card)
            if (isWinCard(lastCard, card)) {
                wonLast = activePlayer
                activePlayer.winCards(cardsOnDesk)
                println("${activePlayer.name} wins cards")
                printScore(player, computer)
            }
        }
        if (activePlayer.hand.isEmpty()) {
            activePlayer.takeCards(deck.getTopCards(6))
        }

        activePlayer = if (activePlayer == player) computer else player
    }
    println("Game Over")
}

fun printScore(p: Player, c: Player) {
    println("Score: ${p.name} ${p.score} - ${c.name} ${c.score}")
    println("Cards: ${p.name} ${p.cardsCount} - ${c.name} ${c.cardsCount}")
}

fun isWinCard(card: String, candidate: String): Boolean {
    return candidate.last() == card.last() || candidate.substring(0, candidate.length - 1) == card.substring(0, candidate.length - 1)
}

fun isFirst(): Boolean {
    while (true) {
        println("Play first?")
        val input = readln()
        when (input.lowercase()) {
            "yes" -> return true
            "no" -> return false
        }
    }
}

open class Player(val name: String) {
    var score = 0
    var cardsCount = 0
    val hand = mutableListOf<String>()

    fun winCards(cards: MutableList<String>) {
        cardsCount += cards.size
        score += cards.count { it.substring(0, it.length - 1) in listOf("10", "J", "Q", "K", "A") }
        cards.clear()
        wonLast = this
    }

    fun takeCards(cards: MutableList<String>) {
        hand.addAll(cards)
    }

    open fun getCard(desk: MutableList<String>): String {
        printCards()
        val number = readNumber(hand.size)
        return if (number == 0) "exit" else hand.removeAt(number - 1)
    }

    private fun printCards() {
        var i = 1
        print("Cards in hand: ")
        hand.forEach { print("${i++})$it ") }
        println()
    }

    private fun readNumber(max: Int): Int {
        while (true) {
            println("Choose a card to play (1-$max):")
            val input = readln()
            if (input == "exit") {
                return 0
            }
            val num = input.toIntOrNull()
            if (num != null && num >= 1 && num <= max) {
                return num
            }
        }
    }
}

class ComputerPlayer(name: String): Player(name) {
    override fun getCard(desk: MutableList<String>): String {
        println(hand.joinToString(" "))

        val card = if (hand.size == 1) {
            hand[0]
        } else if (desk.isEmpty()) {
            getCandidate(hand)
        } else {
            val candidates = hand.filter { isWinCard(it, desk.last()) }
            if (candidates.size == 1) {
                candidates[0]
            } else if (candidates.isEmpty()) {
                getCandidate(hand)
            } else {
                getCandidate(candidates)
            }
        }

        hand.remove(card)
        println("Computer plays $card")
        return card
    }

    private fun getCandidate(candidates: List<String>): String {
        val sameSuitCandidates = candidates.groupBy { it.last() }.filter { it.value.size > 1 }.flatMap { it.value }
        val sameRankCandidates = candidates.groupBy { it.substring(0, it.length - 1) }.filter { it.value.size > 1 }.flatMap { it.value }
        return if (sameSuitCandidates.isNotEmpty()) {
            sameSuitCandidates[Random.nextInt(sameSuitCandidates.size)]
        } else if (sameRankCandidates.isNotEmpty()) {
            sameRankCandidates[Random.nextInt(sameRankCandidates.size)]
        } else {
            candidates[Random.nextInt(candidates.size)]
        }
    }
}

class Deck {
    var deck = mutableListOf<String>()
    init {
        initDeck()
    }

    fun getTopCards(num: Int): MutableList<String> {
        val cards = mutableListOf<String>()
        if (num > deck.size) {
            return cards
        }
        for (i in 0 until num) {
            cards.add(deck.removeFirst())
        }
        return cards
    }

    private fun initDeck() {
        for (suit in suits) {
            for (rank in ranks) {
                deck.add("$rank$suit")
            }
        }
        deck.shuffle()
    }
}