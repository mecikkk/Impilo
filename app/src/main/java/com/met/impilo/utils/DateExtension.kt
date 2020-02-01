package com.met.impilo.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.toId() : String {
    val myFormat = "ddMMMyyyy"
    val format = SimpleDateFormat(myFormat, Locale.getDefault())
    return format.format(this)
}

fun Date.toStringDate() : String {
    val myFormat = "dd MMM yyyy"
    val format = SimpleDateFormat(myFormat, Locale.getDefault())
    return format.format(this)
}

fun Date.getIntDay() : Int {
    val myFormat = "dd"
    val format = SimpleDateFormat(myFormat, Locale.getDefault())
    return format.format(this).toInt()
}

fun Date.getIntMonth() : Int {
    val myFormat = "M"
    val format = SimpleDateFormat(myFormat, Locale.getDefault())
    return format.format(this).toInt()
}

fun Date.getIntYear() : Int {
    val myFormat = "yyyy"
    val format = SimpleDateFormat(myFormat, Locale.getDefault())
    return format.format(this).toInt()
}


fun String.idToDate() : Date? {
    val format = SimpleDateFormat("ddMMMyyyy", Locale.getDefault())
    return format.parse(this)
}

fun String.stringToDate() : Date? {
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.parse(this)
}

fun String.stringFromIntToDate() : Date? {
    val format = SimpleDateFormat("yyyy MM dd", Locale.getDefault())
    return format.parse(this)
}
