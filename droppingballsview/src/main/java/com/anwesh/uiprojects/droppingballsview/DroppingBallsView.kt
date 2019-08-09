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
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int)  : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.updateValue(dir : Float) : Float = dir * scGap / balls
