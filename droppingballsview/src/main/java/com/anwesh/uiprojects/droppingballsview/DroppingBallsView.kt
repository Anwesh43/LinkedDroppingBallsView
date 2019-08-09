package com.anwesh.uiprojects.droppingballsview

/**
 * Created by anweshmishra on 09/08/19.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.content.Context
import android.app.Activity
import android.graphics.Color

val nodes : Int = 5
val balls : Int = 5
val hFactor : Float = 3f
val scGap : Float = 0.1f
val strokeFactor : Int = 90
val sizeFactor : Float = 2f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int)  : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.updateValue(dir : Float) : Float = dir * scGap / balls

fun Canvas.drawBalls(size : Float, xGap : Float, h : Float, hDest : Float, scale : Float, paint : Paint) {
    for (j in 0..(balls - 1)) {
        val scj : Float = scale.divideScale(j, balls)
        drawCircle(j * xGap + size, h + (hDest - h) * scj, size, paint)
    }
}

fun Canvas.drawDBNode(i  : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = (h / hFactor) / (nodes)
    val size : Float = gap / sizeFactor
    val xGap : Float = w / (2 * size)
    val destH : Float = h - (i + 1) * gap
    paint.color = foreColor
    save()
    drawBalls(size, xGap, h, destH, scale, paint)
    restore()
}

class DroppingBallsView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateValue(dir)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false 
            }
        }
    }
}
