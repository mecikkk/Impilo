package com.met.impilo.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    fun dateToString(date: Date): String {
        val myFormat = "dd MMM yyyy"
        val format = SimpleDateFormat(myFormat, Locale.getDefault())
        return format.format(date)
    }

    @Throws(ParseException::class)
    fun stringToDate(date: String): Date? {
        if (!TextUtils.isEmpty(date)) {
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            return format.parse(date)
        }
        return null
    }

    fun isEmailValid(email: String, l: TextInputLayout, errorText: String): Boolean {
        return when (Patterns.EMAIL_ADDRESS.matcher(email.trim().replace("\\s+", "")).matches()) {
            true -> {
                l.isErrorEnabled = false
                true
            }
            false -> {
                ViewUtils.showEditTextError(l, errorText)
                false
            }
        }
    }

    fun isPasswordLengthValid(length: Int, l: TextInputLayout, errorText: String): Boolean {
        return when (length > 6) {
            true -> {
                l.isErrorEnabled = false
                true
            }
            false -> {
                ViewUtils.showEditTextError(l, errorText)
                false
            }
        }
    }

    fun cutWhitespaces(text : String) : String = text.replace("\\s".toRegex(), "")


    fun isEditTextEmpty(e: TextInputEditText, l: TextInputLayout, errorText: String): Boolean {
        return when (TextUtils.isEmpty(e.text.toString().trim())) {
            true -> {
                ViewUtils.showEditTextError(l, errorText)
                true
            }
            false -> {
                l.isErrorEnabled = false
                false
            }
        }
    }

}