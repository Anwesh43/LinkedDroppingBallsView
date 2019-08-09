package com.anwesh.uiprojects.linkeddroppingballsview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.droppingballsview.DroppingBallsView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DroppingBallsView.create(this)
    }
}
