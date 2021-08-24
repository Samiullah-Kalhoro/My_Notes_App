package com.example.personalnotes.utils

import org.joda.time.LocalDateTime

fun dateTimeToString(dateTime: LocalDateTime, today: LocalDateTime): String {
    val year: Int? = if(dateTime.year == today.year) null else dateTime.year
    val monthDay =  "${dateTime.monthOfYear().asShortText} ${dateTime.dayOfMonth}"

    val date = year?.let { "$monthDay, $it" } ?: monthDay
    val time = if(year == null) dateTime.toString(", HH:mm") else ""

    return "$date$time"
}