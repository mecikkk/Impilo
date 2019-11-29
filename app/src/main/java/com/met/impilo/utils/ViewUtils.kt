package com.met.impilo.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*


object ViewUtils {

    fun getDatePicker(view: View?, birthTextField : TextInputEditText): DatePickerDialog {
        val calendar = Calendar.getInstance()

        val date: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                birthTextField.setText(Utils.dateToString(calendar.time))
            }


        val datePickerDialog = DatePickerDialog(
            view!!.context,
            com.met.impilo.R.style.DatePickerTheme,
            date,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Simulate user click on year
        datePickerDialog.datePicker.touchables[0].performClick()
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        return datePickerDialog
    }

    fun createSnackbar(parent: View, message: String): Snackbar {
        val snackbar: Snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG)
            .apply {
                view.setBackgroundColor(ContextCompat.getColor(parent.context, com.met.impilo.R.color.colorAccent))
            }
        val tv = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)

        return snackbar
    }

    fun createInternetConnectionDialog(context: Context): Dialog {
        val internetConnectionDialog =
            Dialog(context, android.R.style.Theme_Material_Dialog_NoActionBar)
        internetConnectionDialog.setContentView(com.met.impilo.R.layout.no_internet_connection)
        internetConnectionDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        internetConnectionDialog.setCanceledOnTouchOutside(false)
        return internetConnectionDialog
    }

    fun showEditTextError(l: TextInputLayout, errorText: String?) {
        l.isErrorEnabled = true
        l.setErrorTextColor(ColorStateList.valueOf(Color.parseColor("#f03750")))
        l.setErrorIconTintList(ColorStateList.valueOf(Color.parseColor("#f03750")))
        l.error = errorText
    }

    fun createLoadingDialog(context: Context?): Dialog? {
        val dialog = Dialog(context!!)
        dialog.setCancelable(false)
        dialog.setContentView(com.met.impilo.R.layout.loading_dialog)
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.attributes.windowAnimations = android.R.anim.fade_in
        return dialog
    }

    fun hideKeyboard(view: View) {
        view.apply {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}