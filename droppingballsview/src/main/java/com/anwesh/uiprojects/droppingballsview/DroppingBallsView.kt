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
val sizeFactor : Float = 2f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int)  : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.updateValue(dir : Float) : Float = dir * scGap / balls

fun Canvas.drawBalls(size : Float, hDest : Float, scale : Float, paint : Paint) {
    for (j in 0..(balls - 1)) {
        val scj : Float = scale.divideScale(j, balls)
        drawCircle(j * 2 * size + size, hDest * scj, size, paint)
    }
}

fun Canvas.drawDBNode(i  : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = (h / hFactor) / (nodes)
    val size : Float = gap / sizeFactor
    val destH : Float = h - (i + 1) * gap
    paint.color = foreColor
    save()
    drawBalls(size, destH, scale, paint)
    restore()
}

class DroppingBallsView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class DBNode(var i : Int, val state : State = State()) {

        private var next : DBNode? = null
        private var prev : DBNode? = null

        init {
            addNeighbor()
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawDBNode(i, state.scale, paint)
            prev?.draw(canvas, paint)
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = DBNode(i + 1)
                next?.prev = this
            }
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : DBNode {
            var curr : DBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }

    }

    data class DroppingBalls(var i : Int) {

        private var curr : DBNode = DBNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : DroppingBallsView) {

        private val droppingBalls : DroppingBalls = DroppingBalls(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            droppingBalls.draw(canvas, paint)
            animator.animate {
                droppingBalls.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            droppingBalls.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : DroppingBallsView {
            val view : DroppingBallsView = DroppingBallsView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
