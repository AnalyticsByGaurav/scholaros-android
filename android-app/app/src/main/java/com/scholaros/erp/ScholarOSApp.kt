package com.scholaros.erp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ScholarOSApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            listOf(
                NotificationChannel(
                    "scholaros_default",
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Notices, homework, and general updates" },

                NotificationChannel(
                    "scholaros_attendance",
                    "Attendance Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Absent notifications for parents" },

                NotificationChannel(
                    "scholaros_fees",
                    "Fee Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Fee due reminders" }
            ).forEach { manager.createNotificationChannel(it) }
        }
    }
}
