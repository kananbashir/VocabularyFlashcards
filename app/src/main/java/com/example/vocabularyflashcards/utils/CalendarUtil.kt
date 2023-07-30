package com.example.vocabularyflashcards.utils

import android.os.SystemClock
import android.widget.Chronometer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarUtil {

    companion object {
        fun getCurrentDate (): String {
            val currentDate = Calendar.getInstance().time

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return dateFormat.format(currentDate)
        }

        fun getStudyTime (chronometer: Chronometer): String {
            chronometer.stop()

            val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
            val elapsedSecond = elapsedMillis / 1000
            val hours = elapsedSecond / 3600
            val minutes = (elapsedSecond % 3600) / 60
            val second = elapsedSecond % 60

            return String.format("%02d:%02d:%02d", hours, minutes, second)
        }
    }



}