package com.jhedeen.hyperskill

import java.io.File

fun main() {
    val abc = getAlphaBet()
    val medium = readMedium()
    val large = readLarge()

    print("Enter name and surname:")
    val name = readln()
    print("Enter person's status:")
    val status = readln()

    val bigName = mutableListOf<MutableList<String>>()
    val bigStatus = mutableListOf<MutableList<String>>()

    for(i in 0 until large[0].size) {
        bigName.add(mutableListOf())
        for(c in name) {
            bigName[i].add(large[abc.indexOf(c)][i])
        }
    }

    for(i in 0 until medium[0].size) {
        bigStatus.add(mutableListOf())
        for(c in status) {
            bigStatus[i].add(medium[abc.indexOf(c)][i])
        }
    }

    printCard(bigName, bigStatus)

}

fun readMedium(): List<List<String>>  {
    val letters = readFonts("src/main/resources/medium.txt")
    letters.add(getMediumSpace())
    return letters.toList()
}

fun readLarge(): List<List<String>>  {
    val letters = readFonts("src/main/resources/roman.txt")
    letters.add(getLargeSpace())
    return letters.toList()
}

fun getMediumSpace(): List<String> {
    val space = " ".repeat(5)
    return listOf(space, space, space)
}

fun getLargeSpace(): List<String> {
    val result = mutableListOf<String>()
    val space = " ".repeat(10)
    for (i in 1..10) {
        result.add(space)
    }
    return result
}

fun readFonts(path: String): MutableList<List<String>> {
    val result = mutableListOf<List<String>>()

    val lines = File(path).readLines()
    val meta = lines[0].split(" ")
    val fontSize = meta[0].toInt()
    val letters = meta[1].toInt()
    for (i in 0 until letters) {
        val letter = mutableListOf<String>()
        for (j in i * (fontSize + 1) until fontSize + i * (fontSize + 1)) {
            letter.add(lines[j + 2])
        }
        result.add(letter.toList())
    }

    return result
}


fun getAlphaBet(): List<Char> {
    val abc = mutableListOf<Char>()
    abc.addAll(('a'..'z').toMutableList())
    abc.addAll(('A'..'Z').toMutableList())
    abc.add(' ')
    return abc.toList()
}

fun printCard(name: List<MutableList<String>>, status: List<MutableList<String>>) {
    val bigNameLen = name[0].joinToString("").length
    val bigStatusLen = status[0].joinToString("").length
    var namePrefix = ""
    var nameSuffix = ""
    var statusPrefix = ""
    var statusSuffix = ""
    val totalLen = if (bigNameLen > bigStatusLen) bigNameLen + 6 else bigStatusLen + 6

    if (bigNameLen > bigStatusLen) {
        statusPrefix = " ".repeat((bigNameLen - bigStatusLen) / 2)
        statusSuffix = " ".repeat(bigNameLen - statusPrefix.length - bigStatusLen)
    } else {
        namePrefix = " ".repeat((bigStatusLen - bigNameLen) / 2)
        nameSuffix = " ".repeat(bigStatusLen - namePrefix.length - bigNameLen)
    }

    println("8".repeat(totalLen + 2))
    for(s in name) {
        println("88  $namePrefix${s.joinToString("")}$nameSuffix  88")
    }
    for(s in status) {
        println("88  $statusPrefix${s.joinToString("")}$statusSuffix  88")
    }
    println("8".repeat(totalLen + 2))
}