package com.jhedeen.hyperskill

import java.io.File
import java.util.Scanner

fun main(args: Array<String>) {
    var sortingType = "natural"
    var dataType = "word"
    var inputFile = ""
    var outputFile = ""

    val sortTypeIndex = args.indexOf("-sortingType")
    if (sortTypeIndex > -1) {
        if (sortTypeIndex + 1 >= args.size) {
            println("No sorting type defined!")
            return
        }
        sortingType = args[sortTypeIndex + 1]
    }

    val dataTypeIndex = args.indexOf("-dataType")
    if (dataTypeIndex > -1) {
        if (dataTypeIndex + 1 >= args.size) {
            println("No sorting type defined!")
            return
        }
        dataType = args[dataTypeIndex + 1]
    }

    val inputIndex = args.indexOf("-inputFile")
    if (inputIndex > -1) {
        if (inputIndex + 1 >= args.size) {
            println("No input file defined!")
            return
        }
        inputFile = args[inputIndex + 1]
    }

    val outputIndex = args.indexOf("-outputFile")
    if (outputIndex > -1) {
        if (outputIndex + 1 >= args.size) {
            println("No output file defined!")
            return
        }
        outputFile = args[outputIndex + 1]
        if (!File(outputFile).exists()) {
            File(outputFile).createNewFile()
        }
    }

    args.forEach {
        run {
            if (it.startsWith("-") && it !in listOf("-sortingType", "-dataType", "-inputFile", "-outputFile")) {
                println("\"$it\" is not a valid parameter. It will be skipped.")
            }
        }
    }

    when(dataType) {
        "long" -> numbers(sortingType == "natural", inputFile, outputFile)
        "line" -> lines(sortingType == "natural", inputFile, outputFile)
        "word" -> words(sortingType == "natural", inputFile, outputFile)
        else -> println("No data type defined!")
    }
}

fun numbers(natural: Boolean, input: String, output: String) {
    val ints: MutableList<Int> = mutableListOf()
    val outFile = File(output)
    if (input.isEmpty() || !File(input).exists()) {
        val sc = Scanner(System.`in`)
        while (sc.hasNext()) {
            ints.addAll(sc.nextLine().split(" ").filter { it.isNotEmpty() && isNumber(it)}.map { it.toInt() })
        }
    } else {
        File(input).readLines().forEach {line ->  ints.addAll(line.split(" ").filter { it.isNotEmpty() && isNumber(it)}.map { it.toInt() }) }
    }

    val header = "Total numbers: ${ints.size}."
    printString(header, outFile)
    if (natural) {
        printString(ints.sorted().joinToString(separator = " ", prefix = "Sorted data: "), outFile)
    } else {
        val intMap = ints.groupBy { item -> ints.count { it == item } }
        intMap.keys.sorted().forEach { key -> intMap[key]!!.toSet().sorted().forEach { printString("$it: $key time(s), ${key * 100 / ints.size}%", outFile) } }
    }
}

private fun printString(s: String, file: File) {
    if (file.exists()) {
        file.appendText("$s\n")
    } else {
        println(s)
    }
}

private fun isNumber(str: String): Boolean {
    return try {
        str.toLong()
        true
    } catch (ex: java.lang.NumberFormatException) {
        println("\"$str\" is not a long. It will be skipped.")
        false
    }
}

fun words(natural: Boolean, input: String, output: String) {
    val words: MutableList<String> = mutableListOf()
    val outFile = File(output)
    if (input.isEmpty() || !File(input).exists()) {
        val sc = Scanner(System.`in`)
        while (sc.hasNext()) {
            words.addAll(sc.nextLine().split(" ").filter { it.isNotEmpty() })
        }
    } else {
        File(input).readLines().forEach { words.addAll(it.split(" ").filter { s ->  s.isNotEmpty() }) }
    }


    printString("Total words: ${words.size}.", outFile)
    if (natural) {
        printString(words.sorted().joinToString(separator = " ", prefix = "Sorted data: "), outFile)
    } else {
        val intMap = words.groupBy { item -> words.count { it == item } }
        intMap.keys.sorted().forEach { key -> intMap[key]!!.toSet().sorted().forEach { printString("$it: $key time(s), ${key * 100 / words.size}%", outFile) } }
    }
}

fun lines(natural: Boolean, input: String, output: String) {
    val lines: MutableList<String> = mutableListOf()
    val outFile = File(output)
    if (input.isEmpty() || !File(input).exists()) {
        val sc = Scanner(System.`in`)
        while (sc.hasNext()) {
            lines.add(sc.nextLine())
        }
    } else {
        lines.addAll(File(input).readLines())
    }

    printString("Total lines: ${lines.size}.", outFile)
    if (natural) {
        printString(lines.sorted().joinToString(separator = "\n", prefix = "Sorted data:"), outFile)
    } else {
        val intMap = lines.groupBy { item -> lines.count { it == item } }
        intMap.keys.sorted().forEach { key -> intMap[key]!!.toSet().sorted().forEach { printString("$it: $key time(s), ${key * 100 / lines.size}%", outFile) } }
    }
}