package com.mvvm.esportlogo.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Region
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.imgproc.Imgproc
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

fun Bitmap.getPathFromOpenCV(
    alphaThreshold: Int = 10,
    contourAreaMin: Double = 20.0
): Path {
    val bmp32 = if (this.config != Bitmap.Config.ARGB_8888)
        this.copy(Bitmap.Config.ARGB_8888, false)
    else this

    val rgba = Mat()
    Utils.bitmapToMat(bmp32, rgba)

    val channels = mutableListOf<Mat>()
    Core.split(rgba, channels)
    val alpha = channels[3]

    val binary = Mat()
    Imgproc.threshold(alpha, binary, alphaThreshold.toDouble(), 255.0, Imgproc.THRESH_BINARY)

    val contours = ArrayList<MatOfPoint>()
    Imgproc.findContours(binary, contours, Mat(), Imgproc.FILLED, Imgproc.CHAIN_APPROX_SIMPLE)

    val path = Path()
    val tempMatOfPoint2f = MatOfPoint2f()
    val approx = MatOfPoint2f()

    for (contour in contours) {
        val area = Imgproc.contourArea(contour)
        if (area < contourAreaMin) continue

        contour.convertTo(tempMatOfPoint2f, CvType.CV_32F)
        Imgproc.approxPolyDP(tempMatOfPoint2f, approx, 0.0, true)

        val points = approx.toArray()
        if (points.isNotEmpty()) {
            path.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
            for (i in 1 until points.size) {
                path.lineTo(points[i].x.toFloat(), points[i].y.toFloat())
            }
            path.close()
        }
    }

    rgba.release()
    binary.release()
    alpha.release()
    channels.forEach { it.release() }
    tempMatOfPoint2f.release()
    approx.release()

    return path
}





