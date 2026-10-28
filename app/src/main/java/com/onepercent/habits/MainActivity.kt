package com.onepercent.habits

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView

/**
 * Diagnostic entry point — NO Jetpack Compose, NO ViewModel.
 * If this still crashes, the problem is manifest/theme/Application/packaging.
 * If this opens and shows the green message, Compose/VM is the culprit.
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val tv = TextView(this).apply {
                text = "Timetable Review\n\nv1.1.3-diag OK\n\nIf you see this, the app can open.\nNext step: re-enable Compose UI."
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#0F172A"))
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                setPadding(48, 48, 48, 48)
            }
            val root = FrameLayout(this).apply {
                setBackgroundColor(Color.parseColor("#0F172A"))
                addView(
                    tv,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    ),
                )
            }
            setContentView(root)
            Log.i(TAG, "MainActivity opened successfully (diag)")
        } catch (t: Throwable) {
            Log.e(TAG, "diag onCreate failed", t)
            val err = TextView(this).apply {
                text = "FATAL: ${t.javaClass.name}\n${t.message}"
                setTextColor(Color.RED)
                setBackgroundColor(Color.BLACK)
                setPadding(32, 32, 32, 32)
            }
            setContentView(err)
        }
    }

    companion object {
        private const val TAG = "TimetableReview"
    }
}
