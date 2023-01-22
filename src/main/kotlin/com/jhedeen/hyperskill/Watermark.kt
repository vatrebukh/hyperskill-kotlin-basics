package com.jhedeen.hyperskill

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.system.exitProcess


var alfa = false
var transparentColor: Color? = null
var posX = 0
var posY = 0
var position = "grid"

fun main() {
    val origin = getOriginImage()
    val watermark = getWatermark()
    if (origin.width < watermark.width || origin.height < watermark.height) {
        println("The watermark's dimensions are larger.")
        exitProcess(1)
    }
    if (watermark.transparency == 1) {
        println("Do you want to set a transparency color?")
        if (readln().lowercase() == "yes") {
            setTransparentColor()
        }
    }
    val percentage = getPercentage()
    val dx = origin.width - watermark.width
    val dy = origin.height - watermark.height
    setPosition(dx, dy)
    val result = getResultImage()
    if (position == "single") {
        applyWatermark(origin, watermark, percentage, result)
    } else {
        blendImages(origin, watermark, percentage, result)
    }
}

fun blendImages(origin: BufferedImage, watermark: BufferedImage, percentage: Int, result: String) {
    val image = BufferedImage(origin.width, origin.height, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until origin.height) {
        for (x in 0 until origin.width) {
            val oColor = Color(origin.getRGB(x, y))
            val wColor = Color(watermark.getRGB(x % watermark.width, y % watermark.height), alfa)
            val color = if (wColor.alpha == 0 || wColor == transparentColor) {
                Color(oColor.red, oColor.green, oColor.blue)
            } else {
                Color(
                    (percentage * wColor.red + (100 - percentage) * oColor.red) / 100,
                    (percentage * wColor.green + (100 - percentage) * oColor.green) / 100,
                    (percentage * wColor.blue + (100 - percentage) * oColor.blue) / 100
                )
            }
            image.setRGB(x, y, color.rgb)
        }
    }
    ImageIO.write(image, "png", File(result))
    println("The watermarked image $result has been created.")
}

fun applyWatermark(origin: BufferedImage, watermark: BufferedImage, percentage: Int, result: String) {
    val image = BufferedImage(origin.width, origin.height, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until origin.height) {
        for (x in 0 until origin.width) {
            val oColor = Color(origin.getRGB(x, y))
            if (!(y in posY until posY + watermark.height && x in posX until posX + watermark.width)) {
                image.setRGB(x, y, oColor.rgb)
                continue
            }

            val wColor = Color(watermark.getRGB(x - posX, y - posY), alfa)
            val color = if (wColor.alpha == 0 || wColor == transparentColor) {
                oColor
            } else {
                Color(
                    (percentage * wColor.red + (100 - percentage) * oColor.red) / 100,
                    (percentage * wColor.green + (100 - percentage) * oColor.green) / 100,
                    (percentage * wColor.blue + (100 - percentage) * oColor.blue) / 100
                )
            }
            image.setRGB(x, y, color.rgb)
        }
    }
    ImageIO.write(image, "png", File(result))
    println("The watermarked image $result has been created.")
}

fun getOriginImage(): BufferedImage {
    println("Input the image filename:")
    return getFile("image")
}

fun getWatermark(): BufferedImage {
    println("Input the watermark image filename:")
    val image = getFile("watermark")
    if (image.transparency == 3) {
        println("Do you want to use the watermark's Alpha channel?")
        if (readln().lowercase() == "yes") {
            alfa = true
        }
    }
    return image
}

fun getResultImage(): String {
    println("Input the output image filename (jpg or png extension):")
    val result = readln()
    if (!result.endsWith(".jpg") && !result.endsWith(".png")) {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(1)
    }
    return result
}

fun getFile(type: String): BufferedImage {
    val imageName = readln()
    try {
        val image = ImageIO.read(File(imageName))
        if (image.colorModel.numComponents !in listOf(3, 4)) {
            println("The number of $type color components isn't 3.")
            exitProcess(1)
        }
        if (image.colorModel.pixelSize !in listOf(24, 32)) {
            println("The $type isn't 24 or 32-bit.")
            exitProcess(1)
        }
        return image
    } catch (ex: IOException) {
        println("The file $imageName doesn't exist.")
        exitProcess(1)
    }
}

fun getPercentage(): Int {
    println("Input the watermark transparency percentage (Integer 0-100):")
    val percentage = readln().toIntOrNull()
    if (percentage == null) {
        println("The transparency percentage isn't an integer number.")
        exitProcess(1)
    }
    if (percentage < 0 || percentage > 100) {
        println("The transparency percentage is out of range.")
        exitProcess(1)
    }
    return percentage
}

fun setPosition(dx: Int, dy: Int) {
    println("Choose the position method (single, grid):")
    val pos = readln()
    if (pos !in listOf("single", "grid")) {
        println("The position method input is invalid.")
        exitProcess(1)
    }
    if (pos == "single") {
        println("Input the watermark position ([x 0-$dx] [y 0-$dy]):")
        val xy = readln().split(" ").mapNotNull { it.toIntOrNull() }.toList()
        if (xy.size != 2) {
            println("The position input is invalid.")
            exitProcess(1)
        }
        if (xy[0] !in 0..dx || xy[1] !in 0..dy) {
            println("The position input is out of range.")
            exitProcess(1)
        }
        posX = xy[0]
        posY = xy[1]
        position = pos
    }
}

fun setTransparentColor() {
    println("Input a transparency color ([Red] [Green] [Blue]):")
    val rgb = readln().split(" ").mapNotNull { it.toIntOrNull() }.filter { it in 0..255 }.toList()
    if (rgb.size != 3) {
        println("The transparency color input is invalid.")
        exitProcess(1)
    }
    transparentColor = Color(rgb[0], rgb[1], rgb[2])
}
