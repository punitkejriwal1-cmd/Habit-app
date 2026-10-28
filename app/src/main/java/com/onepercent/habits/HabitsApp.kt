package com.onepercent.habits

import android.app.Application
import android.util.Log

class HabitsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val default = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e(TAG, "Uncaught in ${t.name}", e)
            default?.uncaughtException(t, e)
        }
    }

    companion object {
        private const val TAG = "TimetableReview"
    }
}
