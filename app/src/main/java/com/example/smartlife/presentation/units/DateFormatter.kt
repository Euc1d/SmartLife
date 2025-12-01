package com.example.smartlife.presentation.units

import android.icu.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.logging.SimpleFormatter

object DateFormatter {
    private val millisInHour = TimeUnit.HOURS.toMillis(1)
    private val millisInDay = TimeUnit.DAYS.toMillis(1)
    private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)
    fun today(): String = formatter.format(System.currentTimeMillis())

    fun formatDateTimeToString(timestamp: Long): String{
        val now = System.currentTimeMillis()
        var diff = now - timestamp

        return when {
            diff < millisInHour -> "Just now"
            diff < millisInDay ->{
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours h ago"
            }
            else ->{
                formatter.format(timestamp)
            }
        }
    }
}