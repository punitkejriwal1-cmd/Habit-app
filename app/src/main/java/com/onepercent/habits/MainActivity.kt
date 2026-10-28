package com.onepercent.habits

import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compose-only diagnostic: no ViewModel, no icons, no navigation, no receivers.
 * If this crashes → Compose/Material3 itself is the problem on this device.
 * If this shows OK → next we add ViewModel carefully.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContent {
                MaterialTheme(colorScheme = darkColorScheme()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "v1.1.5-compose-diag OK\n\nCompose works on this phone.",
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Log.i(TAG, "Compose diag opened")
        } catch (t: Throwable) {
            Log.e(TAG, "Compose diag failed", t)
            setContentView(
                TextView(this).apply {
                    text = "Compose failed:\n${t.javaClass.name}\n${t.message}"
                    setTextColor(AndroidColor.RED)
                    setBackgroundColor(AndroidColor.BLACK)
                    setPadding(48, 48, 48, 48)
                    textSize = 14f
                },
            )
        }
    }

    companion object {
        private const val TAG = "TimetableReview"
    }
}
