package com.jhedeen.hyperskill

fun main() {

    val meter = listOf("m", "meter", "meters", "1.0", "L")
    val kilometer = listOf("km", "kilometer", "kilometers", "1000.0", "L")
    val centimeter = listOf("cm", "centimeter", "centimeters", "0.01", "L")
    val millimeter = listOf("mm", "millimeter", "millimeters", "0.001", "L")
    val mile = listOf("mi", "mile", "miles", "1609.35", "L")
    val yard = listOf("yd", "yard", "yards", "0.9144", "L")
    val feet = listOf("ft", "foot", "feet", "0.3048", "L")
    val inch = listOf("in", "inch", "inches", "0.0254", "L")

    val gram = listOf("g", "gram", "grams", "1.0", "W")
    val kilogram = listOf("kg", "kilogram", "kilograms", "1000.0", "W")
    val milligram = listOf("mg", "milligram", "milligrams", "0.001", "W")
    val pound = listOf("lb", "pound", "pounds", "453.592", "W")
    val ounce = listOf("oz", "ounce", "ounces", "28.3495", "W")

    val celsius = listOf("c", "degree Celsius", "degrees Celsius", "1", "T", "dc", "degree celsius", "degrees celsius", "celsius")
    val fahrenheit = listOf("f", "degree Fahrenheit", "degrees Fahrenheit", "1", "T", "df", "degree fahrenheit", "degrees fahrenheit", "fahrenheit")
    val kelvin = listOf("k", "kelvin", "kelvins", "1", "T")

    val uoms = listOf(meter, kilometer, centimeter, millimeter, mile, yard, feet, inch, gram, kilogram, milligram, pound, ounce, celsius, fahrenheit, kelvin)

    print("Enter what you want to convert (or exit): ")
    var inputString = readln()
    while(inputString != "exit") {
        val input = parse(inputString)
        if (input.isEmpty()) {
            println("Parse error")
            print("\nEnter what you want to convert (or exit): ")
            inputString = readln()
            continue
        }

        val number = input[0].toDouble()
        val from = input[1].lowercase()
        val to = input[2].lowercase()

        var fromUom: List<String> = listOf()
        var toUom: List<String> = listOf()

        for (uom in uoms) {
            if (from in uom) {
                fromUom = uom
            }
            if (to in uom) {
                toUom = uom
            }
        }

        if (fromUom.isEmpty() || toUom.isEmpty() || fromUom[4] != toUom[4]) {
            val a = if (fromUom.isEmpty()) "???" else fromUom[2]
            val b = if (toUom.isEmpty()) "???" else toUom[2]
            println("Conversion from $a to $b is impossible")
        } else if (fromUom[4] == "T") {
            val result = if (fromUom == celsius && toUom == kelvin) {
                number + 273.15
            } else if (fromUom == celsius && toUom == fahrenheit) {
                number * 9 / 5 + 32
            } else if (fromUom == fahrenheit && toUom == celsius) {
                (number - 32) * 5 / 9
            } else if (fromUom == fahrenheit && toUom == kelvin) {
                (number + 459.67) * 5 / 9
            } else if (fromUom == kelvin && toUom == celsius) {
                number - 273.15
            } else if (fromUom == kelvin && toUom == fahrenheit) {
                number * 9 / 5 - 459.67
            } else {
                number
            }
            val unit = if (number == 1.0) fromUom[1] else fromUom[2]
            val resultUnit = if (result == 1.0) toUom[1] else toUom[2]
            println("$number $unit is $result $resultUnit")
        } else if (number < 0.0) {
            val unit = if (fromUom[4] == "W") "Weight" else "Length"
            println("$unit shouldn't be negative")
        } else {
            val unit = if (number == 1.0) fromUom[1] else fromUom[2]
            val result = number * fromUom[3].toDouble() / toUom[3].toDouble()
            val resultUnit = if (result == 1.0) toUom[1] else toUom[2]
            println("$number $unit is $result $resultUnit")
        }

        print("\nEnter what you want to convert (or exit): ")
        inputString = readln()
    }
}

fun parse(inputString: String): List<String> {
    val input = inputString.split(" ")
    val result = mutableListOf<String>()

    if (!isDigit(input[0])) {
        return listOf()
    }
    result.add(input[0])

    var index = 3
    if (input[1].lowercase() in listOf("degree", "degrees")) {
        result.add("${input[1]} ${input[2]}")
        index++
    } else {
        result.add(input[1])
    }

    if (input[index].lowercase() in listOf("degree", "degrees")) {
        result.add("${input[index]} ${input[index + 1]}")
    } else {
        result.add(input[index])
    }

    return result
}

fun isDigit(num: String): Boolean {
    return try {
        num.toDouble()
        true
    } catch (ex: NumberFormatException) {
        false
    }
}