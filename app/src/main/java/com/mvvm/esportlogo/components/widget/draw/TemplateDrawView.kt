package com.mvvm.esportlogo.components.widget.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.mvvm.esportlogo.R
import com.mvvm.esportlogo.data.local.model.LogoTemplate
import com.mvvm.esportlogo.extensions.dp
import com.mvvm.esportlogo.extensions.getBitmapFromAssets
import com.mvvm.esportlogo.extensions.getSolidPathFromBitmap
import com.mvvm.esportlogo.extensions.toMatrix
import com.mvvm.esportlogo.extensions.toMatrixString
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TemplateDrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var backgroundColor: String = "#ffffff"
    private var fontName: String = "Space Obsessed.otf"
    private var fontSize: Float = 80f
    private var imageName: String = "esport_logo_0205.png"
    private var imagePosition: String = ""
    private var isSwap: Boolean = false
    private var letterSpacing: Float = 0.0f
    private var logoName: String = "Esport"
    private var outlineColor: String = "#000000"
    private var outlineWidth: Float = 40f
    private var previewWidth: Float = 1600f
    private var strokeColor: String = "#ffffff"
    private var strokeWidth: Float = 40f
    private var text3dX: Float = 0.0f
    private var textAngle: Float = 0.0f
    private var textCenterX: Float = 0.0f
    private var textCenterY: Float = 0.0f
    private var textColor: String = "#333333"
    private var textCurve: Float = 0.0f
    private var textScaleX: Float = 0.0f
    private var textScaleY: Float = 0.0f

    private val guideLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isVerticalGuideVisible = false
    private var isHorizontalGuideVisible = false

    private val imageBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val imageOutlineBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var imageBitmap: Bitmap? = null
    private val imageMatrix = Matrix()
    private var imageOutlineBitmapPath: Path? = null

    private var textPaint = TextPaint()
    private var textTypeface = Typeface.DEFAULT

    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f

    private var currentViewType = ViewType.NONE
    private var mGestureDetector: GestureDetector? = null
    var isTouchEnable: Boolean = false

    private var logoTemplate: LogoTemplate? = null

    enum class ViewType {
        NONE,
        IMAGE,
        TEXT
    }


    init {
        guideLinePaint.apply {
            strokeWidth = 2f.dp
            color = ContextCompat.getColor(context, R.color.secondary)
        }

        imageBitmapPaint.apply {
            isFilterBitmap = true
            isDither = true
        }

        imageOutlineBitmapPaint.apply {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }

        textPaint.apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        centerX = width / 2f
        centerY = height / 2f

        mGestureDetector = GestureDetector(context, ViewGestureListener())
    }

    fun initTemplate(logoTemplate: LogoTemplate) {
        this.logoTemplate = logoTemplate
        backgroundColor = logoTemplate.backgroundColor
        fontName = logoTemplate.fontName
        fontSize = logoTemplate.fontSize
        imageName = logoTemplate.imageName
        imagePosition = logoTemplate.imagePosition
        isSwap = logoTemplate.isSwap
        letterSpacing = logoTemplate.letterSpacing
        logoName = logoTemplate.logoName
        outlineColor = logoTemplate.outlineColor
        outlineWidth = logoTemplate.outlineWidth
        previewWidth = logoTemplate.previewWidth
        strokeColor = logoTemplate.strokeColor
        strokeWidth = logoTemplate.strokeWidth
        text3dX = logoTemplate.text3dX
        textAngle = logoTemplate.textAngle
        textCenterX = logoTemplate.textCenterX
        textCenterY = logoTemplate.textCenterY
        textColor = logoTemplate.textColor
        textCurve = logoTemplate.textCurve
        textScaleX = logoTemplate.textScaleX
        textScaleY = logoTemplate.textScaleY

        imageMatrix.set(imagePosition.toMatrix())
        imageBitmap = context.getBitmapFromAssets("logo/$imageName")
        imageOutlineBitmapPath = null

        textTypeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
        textPaint.apply {
            textSize = fontSize
            typeface = textTypeface
            letterSpacing = this@TemplateDrawView.letterSpacing
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackgroundColor()
        drawOutlineImage(canvas)
        drawOutlineText(canvas)

        if (isSwap) {
            drawText(canvas)
            drawImage(canvas)
        } else {
            drawImage(canvas)
            drawText(canvas)
        }
        drawGuideLines(canvas)
    }

    private fun drawBackgroundColor() {
        try {
            setBackgroundColor(Color.parseColor(backgroundColor))
        } catch (e: Exception) {
            setBackgroundColor(Color.WHITE)
        }
    }

    private fun drawOutlineImage(canvas: Canvas) {
        imageBitmap?.let { bitmap ->
            if (imageOutlineBitmapPath == null) {
                imageOutlineBitmapPath = bitmap.getSolidPathFromBitmap()
            }

            imageOutlineBitmapPath?.let { path ->
                val transformedPath = Path(path)
                transformedPath.transform(imageMatrix)

                canvas.save()
                canvas.scale(getScaleFactor(), getScaleFactor())

                imageOutlineBitmapPaint.apply {
                    color = Color.parseColor(outlineColor)
                    strokeWidth = outlineWidth * 3f
                }

                canvas.drawPath(transformedPath, imageOutlineBitmapPaint)
                canvas.restore()
            }
        }
    }

    private fun drawImage(canvas: Canvas) {
        imageBitmap?.let { bitmap ->
            canvas.save()
            canvas.scale(getScaleFactor(), getScaleFactor())
            canvas.drawBitmap(bitmap, imageMatrix, imageBitmapPaint)
            canvas.restore()
        }
    }

    private fun drawOutlineText(canvas: Canvas) {
        canvas.save()
        canvas.scale(getScaleFactor(), getScaleFactor())

        canvas.save()
        canvas.scale(textScaleX, textScaleY, textCenterX, textCenterY)

        canvas.rotate(textAngle, textCenterX, textCenterY)

        if (text3dX != 0.0f) {
            val camera = Camera()
            val matrix = Matrix()

            camera.save()
            camera.rotateX(text3dX)
            camera.getMatrix(matrix)
            camera.restore()

            matrix.preTranslate(-textCenterX, -textCenterY)
            matrix.postTranslate(textCenterX, textCenterY)

            canvas.concat(matrix)
        }

        val centerX = textCenterX
        val centerY = if (textCurve == 0.0f) textCenterY + getTextRect().height() / 2f else textCenterX

        val path = if (textCurve == 0.0f) {
            null
        } else {
            createCurvedTextPath()
        }

        if (outlineWidth > 0f) {
            textPaint.apply {
                style = Paint.Style.STROKE
                color = Color.parseColor(outlineColor)
                strokeWidth = this@TemplateDrawView.strokeWidth + outlineWidth * 3f
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }

            path?.let {
                val offset = getTextRect().height() / 2f
                canvas.drawTextOnPath(logoName, it, 0f, offset, textPaint)
            } ?: run {
                canvas.drawText(logoName, centerX, centerY, textPaint)
            }
        }

        canvas.restore()
        canvas.restore()
    }

    private fun drawText(canvas: Canvas) {
        if (textCurve == 0.0f) {
            drawTextWithoutCurve(canvas)
        } else {
            drawTextWithCurve(canvas)
        }
    }

    private fun drawTextWithoutCurve(canvas: Canvas) {
        canvas.save()
        canvas.scale(getScaleFactor(), getScaleFactor())

        canvas.save()
        canvas.scale(textScaleX, textScaleY, textCenterX, textCenterY)

        canvas.rotate(textAngle, textCenterX, textCenterY)

        if (text3dX != 0.0f) {
            val camera = Camera()
            val matrix = Matrix()

            camera.save()
            camera.rotateX(text3dX)
            camera.getMatrix(matrix)
            camera.restore()

            matrix.preTranslate(-textCenterX, -textCenterY)
            matrix.postTranslate(textCenterX, textCenterY)

            canvas.concat(matrix)
        }

        val centerX = textCenterX
        val centerY = textCenterY + getTextRect().height() / 2f

        if (strokeWidth > 0f) {
            textPaint.apply {
                style = Paint.Style.STROKE
                color = Color.parseColor(strokeColor)
                strokeWidth = this@TemplateDrawView.strokeWidth
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            canvas.drawText(logoName, centerX, centerY, textPaint)
        }

        textPaint.apply {
            style = Paint.Style.FILL
            color = Color.parseColor(textColor)
        }

        canvas.drawText(logoName, centerX, centerY, textPaint)

        canvas.restore()
        canvas.restore()
    }

    private fun drawTextWithCurve(canvas: Canvas) {
        canvas.save()
        canvas.scale(getScaleFactor(), getScaleFactor())

        canvas.save()
        canvas.scale(textScaleX, textScaleY, textCenterX, textCenterY)

        canvas.rotate(textAngle, textCenterX, textCenterY)

        if (text3dX != 0.0f) {
            val camera = Camera()
            val matrix = Matrix()

            camera.save()
            camera.rotateX(text3dX)
            camera.getMatrix(matrix)
            camera.restore()

            matrix.preTranslate(-textCenterX, -textCenterY)
            matrix.postTranslate(textCenterX, textCenterY)

            canvas.concat(matrix)
        }

        val textPath = createCurvedTextPath()
        val offset = getTextRect().height() / 2f

        if (strokeWidth > 0f) {
            textPaint.apply {
                style = Paint.Style.STROKE
                color = Color.parseColor(strokeColor)
                strokeWidth = this@TemplateDrawView.strokeWidth
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            canvas.drawTextOnPath(logoName, textPath, 0f, offset, textPaint)
        }

        textPaint.apply {
            style = Paint.Style.FILL
            color = Color.parseColor(textColor)
        }

        canvas.drawTextOnPath(logoName, textPath, 0f, offset, textPaint)

        canvas.restore()
        canvas.restore()
    }

    private fun createCurvedTextPath(): Path {
        val textWidth = getTextRect().width()
        val path = Path()

        val curve = textCurve.toDouble()
        val radius = abs(textWidth / (2.0 * Math.PI * curve))

        val isCurvingDown = curve > 0
        val startAngle = (-curve * Math.PI) + if (isCurvingDown) -Math.PI / 2 else Math.PI / 2
        val endAngle = (curve * Math.PI) + if (isCurvingDown) -Math.PI / 2 else Math.PI / 2
        val offsetY = textCenterY + if (isCurvingDown) radius.toFloat() else -radius.toFloat()

        var degrees = Math.toDegrees(startAngle).toFloat()
        val endDegrees = Math.toDegrees(endAngle).toFloat()
        val step = if (isCurvingDown) 0.1f else -0.1f

        val startX = (cos(startAngle) * radius + textCenterX).toFloat()
        val startY = (sin(startAngle) * radius + offsetY).toFloat()
        path.moveTo(startX, startY)

        while ((isCurvingDown && degrees < endDegrees) || (!isCurvingDown && degrees > endDegrees)) {
            val radian = Math.toRadians(degrees.toDouble())
            val x = (cos(radian) * radius + textCenterX).toFloat()
            val y = (sin(radian) * radius + offsetY).toFloat()
            path.lineTo(x, y)
            degrees += step
        }

        return path
    }

    private fun getTextRect(): RectF {
        val paint = Paint(textPaint).apply {
            style = Paint.Style.FILL
            strokeWidth = 0f
            textSize = fontSize
            typeface = Typeface.createFromAsset(context.assets, "fonts/${fontName}")
        }

        val measureText = letterSpacing + paint.measureText(logoName)

        val rect = Rect()
        paint.getTextBounds(logoName, 0, logoName.length, rect)

        return RectF(0f, 0f, measureText, rect.height().toFloat())
    }

    private fun drawGuideLines(canvas: Canvas) {
        if (isVerticalGuideVisible) {
            canvas.drawLine(centerX, 0f, centerX, height.toFloat(), guideLinePaint)
        }

        if (isHorizontalGuideVisible) {
            canvas.drawLine(0f, centerY, width.toFloat(), centerY, guideLinePaint)
        }
    }

    private fun getScaleFactor() = width / previewWidth

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        centerX = width / 2f
        centerY = height / 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        centerX = width / 2f
        centerY = height / 2f
    }

    fun swapLayer() {
        isSwap = !isSwap
        invalidate()
    }

    fun setTextCurve(value: Float) {
        textCurve = value
        invalidate()
    }

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var oldDistance = 0f
    private var oldAngle = 0f
    private var isDragging = false
    private var isZooming = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isTouchEnable || event == null) return false

        mGestureDetector?.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount == 1) {
                    lastTouchX = event.x
                    lastTouchY = event.y
                    isDragging = true
                    isZooming = false
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    oldDistance = calculateDistance(event)
                    oldAngle = calculateAngle(event)
                    isDragging = false
                    isZooming = true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging && event.pointerCount == 1) {
                    val dx = (event.x - lastTouchX) / getScaleFactor()
                    val dy = (event.y - lastTouchY) / getScaleFactor()

                    when (currentViewType) {
                        ViewType.IMAGE -> imageMatrix.postTranslate(dx, dy)
                        ViewType.TEXT -> {
                            textCenterX += dx
                            textCenterY += dy
                        }

                        else -> {}
                    }

                    invalidate()
                    lastTouchX = event.x
                    lastTouchY = event.y
                }


                if (isZooming && event.pointerCount == 2) {
                    val newDistance = calculateDistance(event)
                    val scale = newDistance / oldDistance

                    val newAngle = calculateAngle(event)
                    val angle = newAngle - oldAngle

                    when (currentViewType) {
                        ViewType.IMAGE -> {
                            val center = getImageCenter()
                            imageMatrix.postScale(scale, scale, center.x, center.y)
                            imageMatrix.postRotate(angle, center.x, center.y)
                        }

                        ViewType.TEXT -> {
                            textScaleX *= scale
                            textScaleY *= scale
                            textAngle += angle
                        }

                        else -> {}
                    }

                    oldDistance = newDistance
                    oldAngle = newAngle
                    invalidate()
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount > 1) {
                    val newIndex = if (event.actionIndex == 0) 1 else 0
                    lastTouchX = event.getX(newIndex)
                    lastTouchY = event.getY(newIndex)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isVerticalGuideVisible = false
                isHorizontalGuideVisible = false
                invalidate()

                isDragging = false
                isZooming = false
            }
        }

        return true
    }

    private inner class ViewGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            val touchX = e.x
            val touchY = e.y

            val touchOnImage = isTouchOnImage(touchX, touchY)
            val touchOnText = isTouchOnText(touchX, touchY)

            currentViewType = when {
                touchOnImage && touchOnText -> {
                    if (isSwap) ViewType.IMAGE else ViewType.TEXT
                }

                touchOnImage -> ViewType.IMAGE
                touchOnText -> ViewType.TEXT
                else -> currentViewType
            }

            return true
        }
    }

    private fun isTouchOnImage(x: Float, y: Float): Boolean {
        val inverse = Matrix()
        imageMatrix.invert(inverse)

        val touchPoint = floatArrayOf(x / getScaleFactor(), y / getScaleFactor())
        inverse.mapPoints(touchPoint)

        imageBitmap?.let {
            val bmpWidth = it.width
            val bmpHeight = it.height

            return touchPoint[0] in 0f..bmpWidth.toFloat() && touchPoint[1] in 0f..bmpHeight.toFloat()
        }

        return false
    }

    private fun isTouchOnText(x: Float, y: Float): Boolean {
        val scale = getScaleFactor()
        val scaledX = x / scale
        val scaledY = y / scale

        val textBounds = getTextRect()

        val rect = if (textCurve in -0.25f..0.25f) {
            RectF(
                textCenterX - textBounds.width() / 2f,
                textCenterY - textBounds.height() / 2f,
                textCenterX + textBounds.width() / 2f,
                textCenterY + textBounds.height() / 2f,
            )
        } else {
            RectF(
                textCenterX - textBounds.width() / 2f,
                textCenterY - textBounds.height() * 2f,
                textCenterX + textBounds.width() / 2f,
                textCenterY + textBounds.height() * 2F,
            )
        }
        return rect.contains(scaledX, scaledY)
    }

    private fun calculateDistance(event: MotionEvent): Float {
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        return sqrt(dx * dx + dy * dy)
    }

    private fun calculateAngle(event: MotionEvent): Float {
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    }

    private fun getImageCenter(): PointF {
        imageBitmap?.let { bitmap ->
            val imageWidth = bitmap.width.toFloat()
            val imageHeight = bitmap.height.toFloat()

            val points = floatArrayOf(imageWidth / 2f, imageHeight / 2f)
            imageMatrix.mapPoints(points)

            return PointF(points[0], points[1])
        }
        return PointF(0f, 0f)
    }


    fun getLogoTemplateSaved(): LogoTemplate? {
        logoTemplate = logoTemplate?.copy(
            backgroundColor = this@TemplateDrawView.backgroundColor,
            fontName = this@TemplateDrawView.fontName,
            fontSize = this@TemplateDrawView.fontSize,
            imageName = this@TemplateDrawView.imageName,
            imagePosition = this@TemplateDrawView.imageMatrix.toMatrixString(),
            isSwap = this@TemplateDrawView.isSwap,
            letterSpacing = this@TemplateDrawView.letterSpacing,
            logoName = this@TemplateDrawView.logoName,
            outlineColor = this@TemplateDrawView.outlineColor,
            outlineWidth = this@TemplateDrawView.outlineWidth,
            previewWidth = this@TemplateDrawView.previewWidth,
            strokeColor = this@TemplateDrawView.strokeColor,
            strokeWidth = this@TemplateDrawView.strokeWidth,
            text3dX = this@TemplateDrawView.text3dX,
            textAngle = this@TemplateDrawView.textAngle,
            textCenterX = this@TemplateDrawView.textCenterX,
            textCenterY = this@TemplateDrawView.textCenterY,
            textColor = this@TemplateDrawView.textColor,
            textCurve = this@TemplateDrawView.textCurve,
            textScaleX = this@TemplateDrawView.textScaleX,
            textScaleY = this@TemplateDrawView.textScaleY
        )
        return logoTemplate
    }
}