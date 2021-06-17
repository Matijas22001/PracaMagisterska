package pl.polsl.MathHelper.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class CircleRed(context: Context?, attrs: AttributeSet?, x:Float, y:Float, r:Float) :
    View(context, attrs) {
    private val drawPaint: Paint = Paint()
    private var xvalue: Float
    private var yvalue: Float
    private var r: Float
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(xvalue, yvalue, r, drawPaint)
    }

    companion object {
        private const val COLOR_HEX = "#E74300"
    }

    init {
        drawPaint.color = Color.parseColor(COLOR_HEX)
        drawPaint.isAntiAlias = true
        this.xvalue = x
        this.yvalue = y
        this.r = r
    }
}