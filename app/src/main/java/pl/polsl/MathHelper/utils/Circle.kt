package pl.polsl.MathHelper.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class Circle(context: Context?, attrs: AttributeSet?, x:Float, y:Float, r:Float) :
    View(context, attrs) {
    private val drawPaint: Paint = Paint()
    private var xvalue: Float
    private var yvalue: Float
    private var r: Float
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(xvalue, yvalue, r, drawPaint)
    }

    //private fun setOnMeasureCallback() {
    //    val vto = viewTreeObserver
    //    vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
    //        override fun onGlobalLayout() {
    //            removeOnGlobalLayoutListener(this)
    //        }
    //    })
    //}
//
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    //private fun removeOnGlobalLayoutListener(listener: OnGlobalLayoutListener) {
    //    if (Build.VERSION.SDK_INT < 16) {
    //        viewTreeObserver.removeGlobalOnLayoutListener(listener)
    //    } else {
    //        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    //    }
    //}

    companion object {
        private const val COLOR_HEX = "#E74300"
    }

    init {
        drawPaint.color = Color.parseColor(COLOR_HEX)
        drawPaint.isAntiAlias = true
        this.xvalue = x
        this.yvalue = y
        this.r = r
        //setOnMeasureCallback()
    }
}