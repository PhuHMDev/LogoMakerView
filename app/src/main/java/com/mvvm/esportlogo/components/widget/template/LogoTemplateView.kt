package com.mvvm.esportlogo.components.widget.template

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.mvvm.esportlogo.R
import com.mvvm.esportlogo.data.local.model.LogoTemplate
import com.mvvm.esportlogo.extensions.dp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


class LogoTemplateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var logoTemplate: LogoTemplate? = null
    private var textPaint = TextPaint()
    private val gridLinesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isVerticalLine = false
    private var isHorizontalLine = false

    init {
        gridLinesPaint.apply {
            strokeWidth = 2f.dp
            color = ContextCompat.getColor(context, R.color.secondary)
        }
    }

    fun setLogoTemplate(logoTemplate: LogoTemplate) {
        this.logoTemplate = logoTemplate
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackgroundColor()
        drawText(canvas)
        drawGridLines(canvas)
    }

    private fun drawBackgroundColor() {
        try {
            logoTemplate?.let { logo ->
                setBackgroundColor(Color.parseColor(logo.backgroundColor))
            }
        } catch (e: Exception) {
            setBackgroundColor(Color.BLACK)
        }
    }

    private fun drawGridLines(canvas: Canvas) {
        if(isVerticalLine) {
            canvas.drawLine(0f, height.toFloat() / 2f, width.toFloat(), height.toFloat() / 2f, gridLinesPaint)
        }

        if(isHorizontalLine) {
            canvas.drawLine(width.toFloat() / 2f, 0f, height.toFloat() / 2f, height.toFloat(), gridLinesPaint)
        }
    }


    private fun drawText(canvas: Canvas) {
        logoTemplate?.let { logo ->
            val curveValue = logo.textCurve ?: 0f
            if(curveValue == 0.0f) {
                drawTextWithoutCurve(canvas, logo)
            } else {
                drawTextWithCurve(canvas, logo)
            }
        }
    }

    private fun drawTextWithoutCurve(canvas: Canvas, logoTemplate: LogoTemplate) {
        val scaleFactor = width / logoTemplate.previewWidth
        canvas.save()
        canvas.scale(scaleFactor, scaleFactor)
        canvas.save()
        canvas.scale(logoTemplate.textScaleX, logoTemplate.textScaleY, logoTemplate.textCenterX, logoTemplate.textCenterY)
        canvas.rotate(logoTemplate.textAngle, logoTemplate.textCenterX, logoTemplate.textCenterY)

        if (logoTemplate.text3dX != 0.0f) {
            val camera = Camera()
            val matrix = Matrix()

            camera.save()
            camera.rotateX(logoTemplate.text3dX)
            camera.getMatrix(matrix)
            camera.restore()

            matrix.preTranslate(-logoTemplate.textCenterX, -logoTemplate.textCenterY)
            matrix.postTranslate(logoTemplate.textCenterX, logoTemplate.textCenterY)

            canvas.concat(matrix)
        }

        textPaint.apply {
            textSize = logoTemplate.fontSize
            textAlign = Paint.Align.CENTER
            letterSpacing = logoTemplate.letterSpacing
            this.typeface = Typeface.createFromAsset(context.assets, "fonts/${logoTemplate.fontName}")
        }

        val baseLineY = getTextRect(logoTemplate).height() / 2f + logoTemplate.textCenterY

        if (logoTemplate.outlineWidth > 0f) {
            textPaint.apply {
                style = Paint.Style.STROKE
                color = Color.parseColor(logoTemplate.outlineColor)
                strokeWidth = logoTemplate.strokeWidth * 2f + logoTemplate.outlineWidth
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            canvas.drawText(logoTemplate.logoName, logoTemplate.textCenterX, baseLineY, textPaint)
        }

        if (logoTemplate.strokeWidth > 0f) {
            textPaint.apply {
                style = Paint.Style.STROKE
                color = Color.parseColor(logoTemplate.strokeColor)
                strokeWidth = logoTemplate.strokeWidth
            }
            canvas.drawText(logoTemplate.logoName, logoTemplate.textCenterX, baseLineY, textPaint)
        }

        textPaint.apply {
            style = Paint.Style.FILL
            color = Color.parseColor(logoTemplate.textColor)
        }
        canvas.drawText(logoTemplate.logoName, logoTemplate.textCenterX, baseLineY, textPaint)

        canvas.restore()
        canvas.restore()
    }

    private fun drawTextWithCurve(canvas: Canvas, logoTemplate: LogoTemplate) {
        val text = logoTemplate.logoName
        val mCurve = logoTemplate.textCurve ?: 0f
        if (text.isEmpty() || mCurve == 0f) return

        val scaleFactor = width / logoTemplate.previewWidth
        canvas.save()
        canvas.scale(scaleFactor, scaleFactor)

        canvas.save()
        canvas.scale(
            logoTemplate.textScaleX,
            logoTemplate.textScaleY,
            logoTemplate.textCenterX,
            logoTemplate.textScaleY
        )
        canvas.rotate(logoTemplate.textAngle, logoTemplate.textCenterX, logoTemplate.textCenterY)

        if (logoTemplate.text3dX != 0.0f) {
            val camera = Camera()
            val matrix = Matrix()

            camera.save()
            camera.rotateX(logoTemplate.text3dX)
            camera.getMatrix(matrix)
            camera.restore()

            matrix.preTranslate(-logoTemplate.textCenterX, -logoTemplate.textCenterY)
            matrix.postTranslate(logoTemplate.textCenterX, logoTemplate.textCenterY)

            canvas.concat(matrix)
        }

        textPaint.apply {
            textSize = logoTemplate.fontSize
            textAlign = Paint.Align.CENTER
            letterSpacing = logoTemplate.letterSpacing
            typeface = Typeface.createFromAsset(context.assets, "fonts/${logoTemplate.fontName}")
        }

        val path = createCurvedTextPath(
            curveFactor = mCurve,
            centerX = logoTemplate.textCenterX,
            centerY = logoTemplate.textCenterY
        )

        // Vẽ Outline
        if (logoTemplate.outlineWidth > 0f) {
            textPaint.apply {
                style = Paint.Style.STROKE
                color = Color.parseColor(logoTemplate.outlineColor)
                strokeWidth = logoTemplate.strokeWidth * 2f + logoTemplate.outlineWidth
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            canvas.drawTextOnPath(
                text,
                path, 0.0f, (getTextRect(logoTemplate).height() / 2f),
                this.textPaint
            )
        }

        // Vẽ Stroke
        if (logoTemplate.strokeWidth > 0f) {
            textPaint.apply {
                style = Paint.Style.STROKE
                color = Color.parseColor(logoTemplate.strokeColor)
                strokeWidth = logoTemplate.strokeWidth
            }
            canvas.drawTextOnPath(
                text,
                path, 0.0f, (getTextRect(logoTemplate).height() / 2f),
                this.textPaint
            )
        }

        // Vẽ Fill
        textPaint.apply {
            style = Paint.Style.FILL
            color = Color.parseColor(logoTemplate.textColor)
        }
        canvas.drawTextOnPath(
            text,
            path, 0.0f, (getTextRect(logoTemplate).height() / 2f),
            this.textPaint
        )
        canvas.restore()
        canvas.restore()
    }

    private fun createCurvedTextPath(
        curveFactor: Float,
        centerX: Float,
        centerY: Float
    ): Path {
        val textWidth = getTextRect(logoTemplate!!).width()
        val path = Path()

        if (curveFactor == 0f) return path

        val curve = curveFactor.toDouble()
        val radius = abs(textWidth / (2.0 * Math.PI * curve))

        val isCurvingDown = curve > 0
        val startAngle = (-curve * Math.PI) + if (isCurvingDown) -Math.PI / 2 else Math.PI / 2
        val endAngle = (curve * Math.PI) + if (isCurvingDown) -Math.PI / 2 else Math.PI / 2
        val offsetY = centerY + if (isCurvingDown) radius.toFloat() else -radius.toFloat()

        var degrees = Math.toDegrees(startAngle).toFloat()
        val endDegrees = Math.toDegrees(endAngle).toFloat()
        val step = if (isCurvingDown) 0.1f else -0.1f

        val startX = (cos(startAngle) * radius + centerX).toFloat()
        val startY = (sin(startAngle) * radius + offsetY).toFloat()
        path.moveTo(startX, startY)

        while ((isCurvingDown && degrees < endDegrees) || (!isCurvingDown && degrees > endDegrees)) {
            val radian = Math.toRadians(degrees.toDouble())
            val x = (cos(radian) * radius + centerX).toFloat()
            val y = (sin(radian) * radius + offsetY).toFloat()
            path.lineTo(x, y)
            degrees += step
        }

        return path
    }

    private fun getTextRect(logoTemplate: LogoTemplate): RectF {
        val paint = Paint(textPaint).apply {
            style = Paint.Style.FILL
            strokeWidth = 0f
            textSize = logoTemplate.fontSize
            typeface = Typeface.createFromAsset(context.assets, "fonts/${logoTemplate.fontName}")
        }

        val measureText = logoTemplate.letterSpacing + paint.measureText(logoTemplate.logoName)

        val rect = Rect()
        paint.getTextBounds(logoTemplate.logoName, 0, logoTemplate.logoName.length, rect)

        return RectF(0f, 0f, measureText, rect.height().toFloat())
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val logo = logoTemplate ?: return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount == 1) {
                    lastTouchX = event.x
                    lastTouchY = event.y
                    isDragging = true
                    isZooming = false
                }
                return true
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    isDragging = false
                    isZooming = true
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging && event.pointerCount == 1) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY

                    val scaleFactor = width / logo.previewWidth
                    logo.textCenterX += dx / scaleFactor
                    logo.textCenterY += dy / scaleFactor

                    lastTouchX = event.x
                    lastTouchY = event.y

                    invalidate()
                }

                if (isZooming && event.pointerCount == 2) {
                    // TODO: Zoom
                }
                return true
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount - 1 == 1) {
                    val index = if (event.actionIndex == 0) 1 else 0
                    lastTouchX = event.getX(index)
                    lastTouchY = event.getY(index)
                    isDragging = true
                    isZooming = false
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                isZooming = false
                return true
            }
        }

        return super.onTouchEvent(event)
    }
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false
    private var isZooming = false
}