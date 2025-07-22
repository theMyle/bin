package com.example.desamparotourapp

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.Random
import androidx.core.graphics.createBitmap

object Captcha {
    private const val LOWER = "abcdefghijkmnopqrstwxyz"
    private const val UPPER = "ABCDEFGHJKLMNPQRSTWXYZ"
    private const val DIGITS = "123456789"
    private const val SYMBOLS = "!@#$%^&*"
    private const val ALL = LOWER + UPPER + DIGITS + SYMBOLS

    fun generateCaptchaText(): String {
        val rand = Random()
        val captchaString = StringBuilder()

        captchaString.append(LOWER[rand.nextInt(LOWER.length)])
        captchaString.append(UPPER[rand.nextInt(UPPER.length)])
        captchaString.append(DIGITS[rand.nextInt(DIGITS.length)])
        captchaString.append(SYMBOLS[rand.nextInt(SYMBOLS.length)])

        while (captchaString.length < 6) {
            captchaString.append(ALL[rand.nextInt(ALL.length)])
        }

        // shuffle the string
        val chars: MutableList<Char> = ArrayList()
        for (c in captchaString.toString().toCharArray()) chars.add(c)

        chars.shuffle()

        val result = StringBuilder()
        for (c in chars) result.append(c)

        return result.toString()
    }

    fun renderCaptchaImage(text: String, width: Int, height: Int, textSize: Float = 120f): Bitmap {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        canvas.drawColor(Color.LTGRAY)

        paint.color = Color.BLACK
        paint.textSize = textSize
        paint.isAntiAlias = true

        paint.setMaskFilter(BlurMaskFilter(2f, BlurMaskFilter.Blur.NORMAL))

        // Measure text width
        val textWidth = paint.measureText(text)

        // Measure text height using FontMetrics
        val fontMetrics = paint.fontMetrics
        val textHeight = fontMetrics.bottom - fontMetrics.top

        // Calculate exact (x, y) to center text
        val x = (width - textWidth) / 2
        val y = (height + textHeight) / 2 - fontMetrics.bottom

        val rotation = -60

        // Draw the centered text
        canvas.rotate(rotation.toFloat(), width / 2f, height / 2f)
        canvas.drawText(text, x, y, paint)

        return bitmap
    }
}
