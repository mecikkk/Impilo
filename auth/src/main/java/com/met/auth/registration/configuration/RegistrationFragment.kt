package com.met.auth.registration.configuration

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.met.auth.R
import com.met.impilo.utils.Utils
import com.met.impilo.utils.ViewUtils
import com.met.impilo.utils.stringToDate
import com.met.impilo.utils.toStringDate
import kotlinx.android.synthetic.main.registration_fragment.*
import java.util.*


class RegistrationFragment : BaseFragment() {

    private val TAG = javaClass.simpleName
    private lateinit var loadingDialog : Dialog

    companion object {
        @JvmStatic
        fun newInstance() = RegistrationFragment()
    }

    private lateinit var viewModel: RegistrationFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.registration_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RegistrationFragmentViewModel::class.java)

        loadingDialog = ViewUtils.createLoadingDialog(context)!!

        register_button.setOnClickListener {
            if(validate()) {
                loadingDialog.show()
                viewModel.signUpWithEmail(
                    email_textfield.text.toString(),
                    password_textfield.text.toString(),
                    display_name_textfield.text.toString(),
                    birth_date_textfield.text.toString().stringToDate()!!
                )
            }
        }

        birth_date_textfield.setOnClickListener { clicked_view ->
            ViewUtils.hideKeyboard(clicked_view)
            val dialog = ViewUtils.getDatePicker(view, true, Date()) {
                birth_date_textfield.setText(it.toStringDate())
            }
            dialog.show()
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE)
        }

        val signUpSuccessful = Observer<Boolean> {
            if(it){
                loadingDialog.hide()
                callback.accountCreated()
            }
        }

        viewModel.signUpSuccess.observe(this, signUpSuccessful)

    }

    override fun onPause() {
        super.onPause()
        loadingDialog.dismiss()
    }


    private fun validate(): Boolean {
        val invalidBirthDate: Boolean = Utils.isEditTextEmpty(birth_date_textfield, birth_date_input_layout, resources.getString(com.met.impilo.R.string.field_required))

        val invalidEmail = (Utils.isEditTextEmpty(email_textfield, email_input_layout, resources.getString(com.met.impilo.R.string.field_required)) ||
                !Utils.isEmailValid(email_textfield.text.toString(), email_input_layout, resources.getString(com.met.impilo.R.string.invalid_email_format)))

        val invalidPassword = (Utils.isEditTextEmpty(password_textfield, password_input_layout, resources.getString(com.met.impilo.R.string.field_required)) ||
                !Utils.isPasswordLengthValid(password_textfield.length(), password_input_layout, resources.getString(com.met.impilo.R.string.password_length_error)))

        Log.e(TAG, "birth/name/email/pass : $invalidBirthDate/$invalidEmail/$invalidPassword")

        return !invalidBirthDate && !invalidEmail && !invalidPassword
    }

}
