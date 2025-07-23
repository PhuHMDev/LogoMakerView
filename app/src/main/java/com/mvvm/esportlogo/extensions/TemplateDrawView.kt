package com.mvvm.esportlogo.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Region
import java.io.IOException

fun Context.getBitmapFromAssets(fileName: String): Bitmap? {
    return try {
        val inputStream = assets.open(fileName)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        bitmap
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun String.toMatrix(): Matrix {
    val values = split(",").map { it.toFloat() }.toFloatArray()
    val matrix = Matrix()
    matrix.setValues(values)
    return matrix
}

fun Bitmap.getSolidPathFromBitmap(): Path {
    val width = width
    val height = height
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)

    val region = Region()
    val tempRegion = Region()
    val rect = Rect()

    for (y in 0 until height) {
        var startX = -1
        for (x in 0 until width) {
            val alpha = pixels[y * width + x] ushr 24
            if (alpha > 0) {
                if (startX == -1) startX = x
            } else if (startX != -1) {
                rect.set(startX, y, x, y + 1)
                tempRegion.set(rect)
                region.op(tempRegion, Region.Op.UNION)
                startX = -1
            }
        }

        if (startX != -1) {
            rect.set(startX, y, width, y + 1)
            tempRegion.set(rect)
            region.op(tempRegion, Region.Op.UNION)
        }
    }

    val path = Path()
    region.getBoundaryPath(path)
    return path
}
