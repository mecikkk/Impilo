package com.met.auth.registration.configuration

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.met.auth.R
import com.met.impilo.utils.Utils
import com.met.impilo.utils.ViewUtils
import kotlinx.android.synthetic.main.registration_fragment.*


class RegistrationFragment : Fragment() {

    private val TAG = javaClass.simpleName

    companion object {
        fun newInstance() = RegistrationFragment()
    }

    private lateinit var viewModel: RegistrationFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.registration_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RegistrationFragmentViewModel::class.java)

        register_button.setOnClickListener {
            validator()
        }

        birth_date_textfield.setOnClickListener {
            var dialog = ViewUtils.getDatePicker(view, birth_date_textfield)
            dialog.show()
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE)
        }

        //TODO : rejestracja + kolejne fragmenty

    }

    private fun validator(): Boolean {
        val birth: Boolean = Utils.isEditTextEmpty(birth_date_textfield, birth_date_input_layout, resources.getString(com.met.impilo.R.string.field_required))

        val invalidEmail = (Utils.isEditTextEmpty(email_textfield, email_input_layout, resources.getString(com.met.impilo.R.string.field_required)) ||
                !Utils.isEmailValid(email_textfield.text.toString(), email_input_layout, resources.getString(com.met.impilo.R.string.invalid_email_format)))

        val invalidPassword = (Utils.isEditTextEmpty(password_textfield, password_input_layout, resources.getString(com.met.impilo.R.string.field_required)) ||
                !Utils.isPasswordLengthValid(password_textfield.length(), password_input_layout, resources.getString(com.met.impilo.R.string.password_length_error)))

        Log.e(TAG, "birth/name/email/pass : $birth/$invalidEmail/$invalidPassword")

        return birth && !invalidEmail && !invalidPassword
    }

}
