package com.jhedeen.hyperskill

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.experimental.xor
import kotlin.math.pow

const val stopWord = "000000000000000000000011"

fun main() {
    start()
}

fun start() {
    println("Task (hide, show, exit):")
    var task = readln()
    while (task != "exit") {
        when(task) {
            "hide" -> hide()
            "show" -> show()
            else -> println("Wrong task: $task")
        }
        println("Task (hide, show, exit):")
        task = readln()
    }
    println("Bye!")
}

fun hide() {
    println("Input image file:")
    val input = readln()
    println("Output image file:")
    val output = readln()
    println("Message to hide:")
    val rawMessage = readln()
    println("Password:")
    val pwd = readln()
    val message = encode(rawMessage.encodeToByteArray(), pwd.encodeToByteArray()).joinToString("", postfix = stopWord, transform = { toBits(it.toInt()) })

    try {
        val origin = ImageIO.read(File(input))
        if (origin.width * origin.height < message.length) {
            println("The input image is not large enough to hold this message.")
            return
        }
        val image = BufferedImage(origin.width, origin.height, BufferedImage.TYPE_INT_RGB)
        var count = 0
        for (y in 0 until origin.height) {
            for (x in 0 until origin.width) {
                val color = Color(origin.getRGB(x, y))
                if (count < message.length) {
                    val blue = if (message[count] == '1') color.blue or 1 else color.blue and 254
                    val newColor = Color(color.red, color.green, blue)
                    image.setRGB(x, y, newColor.rgb)
                    count++
                } else {
                    image.setRGB(x, y, color.rgb)
                }
            }
        }
        ImageIO.write(image, "png", File(output))
        println("Message saved in $output image.")
    } catch (ex: IOException) {
        println("Can't read input file!")
    }
}

fun show() {
    println("Input image file:")
    val input = readln()
    println("Password:")
    val pwd = readln()
    var bytes = ""
    try {
        val origin = ImageIO.read(File(input))
        for (r in 0 until origin.height) {
            for (c in 0 until origin.width) {
                val color = Color(origin.getRGB(c, r)).blue
                bytes += color and 1
                if (bytes.endsWith(stopWord)) {
                    val message = bytes.substring(0, bytes.indexOf(stopWord))
                    val decoded = encode(toByteArray(message), pwd.encodeToByteArray())
                    println("Message:")
                    println(decoded.toString(Charsets.UTF_8))
                }
            }
        }
    } catch (ex: IOException) {
        println("Can't read input file!")
    }
}

fun encode(msg: ByteArray, pwd: ByteArray): ByteArray {
    val res = mutableListOf<Byte>()
    val len = pwd.size
    for (i in msg.indices) {
        res.add(msg[i] xor pwd[i % len])
    }
    return res.toByteArray()
}

fun toBits(decimal: Int): String {
    val base = 2
    val result = arrayOf(0,0,0,0,0,0,0,0)
    var count = 7
    var input = decimal
    while (input >= base) {
        result[count] = input.mod(base)
        input = input.div(base)
        count--
    }
    result[count] = input
    return result.joinToString("")
}

fun toByteArray(input: String): ByteArray {
    var str = input
    val result = mutableListOf<Byte>()
    while (str.length > 8) {
        result.add(toByte(str.substring(0, 8)))
        str = str.substring(8)
    }
    result.add(toByte(str))
    return result.toByteArray()
}

fun toByte(input: String): Byte {
    val source = input.reversed()
    val base = 2
    var result = 0
    var pos = 0
    for (i in source) {
        result += (i.toString().toInt() * base.toDouble().pow(pos++)).toInt()
    }
    return result.toByte()
}