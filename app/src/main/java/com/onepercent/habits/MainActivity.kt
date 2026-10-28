package com.onepercent.habits

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

/**
 * Absolute minimal activity — no Compose, no ViewModel, no notifications.
 * If this still crashes, the problem is install/signing/device policy, not app logic.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "Timetable Review\n\nHello — app is running."
        tv.textSize = 22f
        tv.setPadding(48, 48, 48, 48)
        setContentView(tv)
    }
}
