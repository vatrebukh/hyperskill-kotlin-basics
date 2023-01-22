package com.jhedeen.hyperskill

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.pow

val hex = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')

fun main() {
    print("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
    var command = readln()
    while (command != "/exit") {
        val sourceBase = command.split(" ")[0].toInt()
        val targetBase = command.split(" ")[1].toInt()
        print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
        var input = readln()
        while (input != "/back") {
            val dot = input.indexOf('.')
            if (dot > 0) {
                val number = input.substring(0, dot)
                val fraction = input.substring((dot + 1))

                val tmpNumber = toDecimal(number, sourceBase)
                val result = fromDecimal(tmpNumber, targetBase)

                val tmpFract = toDecimalFraction(fraction, sourceBase)
                val resultFractional = fromDecimalFraction(tmpFract, targetBase)

                println("Conversion result: $result.$resultFractional \n")
                print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
                input = readln()
            } else {
                val tmp = toDecimal(input, sourceBase)
                val result = fromDecimal(tmp, targetBase)
                println("Conversion result: $result \n")
                print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
                input = readln()
            }
        }
        print("\nEnter two numbers in format: {source base} {target base} (To quit type /exit)")
        command = readln()
    }
}

fun toDecimal(input: String, base: Int): BigInteger {
    if (base == 10) {
        return input.toBigInteger()
    }

    val source = input.reversed().uppercase()

    var result = BigInteger.ZERO
    var pos = 0
    for (i in source) {
        result += (hex.indexOf(i) * base.toDouble().pow(pos++)).toBigDecimal().toBigInteger()
    }
    return result
}

fun fromDecimal(input: BigInteger, base: Int): String {
    if (base == 10) {
        return input.toString()
    }
    var result = ""
    var bdinput = input
    val bdBase = BigInteger.valueOf(base.toLong())
    while (bdinput >= bdBase) {
        result += hex[bdinput.mod(bdBase).toInt()]
        bdinput = bdinput.div(bdBase)
    }
    result += hex[bdinput.toInt()]
    return result.reversed()
}

fun toDecimalFraction(input: String, base: Int): BigDecimal {
    if (base == 10) {
        return "0.$input".toBigDecimal()
    }

    val source = input.uppercase()

    var result = BigDecimal.ZERO
    var pos = 1
    for (i in source) {
        result += (hex.indexOf(i) * base.toDouble().pow(-pos++)).toBigDecimal()
    }
    return result
}

fun fromDecimalFraction(input: BigDecimal, base: Int): String {
    if (base == 10) {
        return input.toString().substring(2, 7)
    }
    var result = ""
    var bdInput = input
    val bdBase = BigDecimal.valueOf(base.toDouble())
    while (bdInput != BigDecimal.ZERO && result.length < 5) {
        val tmp = bdInput.multiply(bdBase)
        result += hex[tmp.toInt()]
        bdInput = tmp.remainder(BigDecimal.ONE)
    }
    return result
}