package com.example.codefix.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.timestampFormatted(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(this))
}
