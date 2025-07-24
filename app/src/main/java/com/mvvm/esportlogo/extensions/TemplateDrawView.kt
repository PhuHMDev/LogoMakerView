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

fun Matrix.toMatrixString(): String {
    val values = FloatArray(9)
    this.getValues(values)
    return values.joinToString(",")
}


fun Bitmap.getSolidPathFromBitmap(): Path {
    val downscaleFactor = 0.6f
    val scaledBitmap = Bitmap.createScaledBitmap(
        this,
        (width * downscaleFactor).toInt(),
        (height * downscaleFactor).toInt(),
        true
    )

    val width = scaledBitmap.width
    val height = scaledBitmap.height
    val pixels = IntArray(width)
    val region = Region()
    val rect = Rect()

    for (y in 0 until height) {
        scaledBitmap.getPixels(pixels, 0, width, 0, y, width, 1)

        var startX = -1
        for (x in 0 until width) {
            val alpha = pixels[x] ushr 24
            if (alpha > 0) {
                if (startX == -1) startX = x
            } else if (startX != -1) {
                rect.set(startX, y, x, y + 1)
                region.op(rect, Region.Op.UNION)
                startX = -1
            }
        }

        if (startX != -1) {
            rect.set(startX, y, width, y + 1)
            region.op(rect, Region.Op.UNION)
        }
    }

    val path = Path()
    region.getBoundaryPath(path)

    val matrix = Matrix().apply {
        setScale(1 / downscaleFactor, 1 / downscaleFactor)
    }
    path.transform(matrix)

    return path
}

