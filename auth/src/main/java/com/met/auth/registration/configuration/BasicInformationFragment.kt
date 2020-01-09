package com.met.auth.registration.configuration

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.met.auth.R
import com.met.auth.registration.Registration
import com.met.impilo.data.Gender
import com.met.impilo.utils.*
import com.polyak.iconswitch.IconSwitch
import kotlinx.android.synthetic.main.basic_information_fragment.*
import java.util.*


class BasicInformationFragment : BaseFragment(), Registration.OnPageChangeListener{

    companion object {
        @JvmStatic
        fun newInstance() = BasicInformationFragment()
    }

    private var gender = Gender.MALE
    private var onlyConfiguration : Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.basic_information_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        gender = Gender.MALE

        gender_switch.setCheckedChangeListener {
            if(it == IconSwitch.Checked.LEFT){
                gender_textview.text = resources.getString(com.met.impilo.R.string.male)
                gender = Gender.MALE
            } else {
                gender_textview.text = resources.getString(com.met.impilo.R.string.female)
                gender = Gender.FEMALE
            }
        }

        birth_date_textfield2.setOnClickListener { clicked_view ->
            ViewUtils.hideKeyboard(clicked_view)
            val dialog = ViewUtils.getDatePicker(view, true, Date()) {
                birth_date_textfield2.setText(it.toStringDate())
            }
            dialog.show()
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE)
        }
    }

    fun showOnlyConfiguration(onlyConfiguration : Boolean){
        this.onlyConfiguration = onlyConfiguration
    }

    private fun validate(): Boolean {
        var validate = true
        if (Utils.isEditTextEmpty(height_textfield, height_input_layout, resources.getString(com.met.impilo.R.string.field_required)) ||
            !Utils.isRangeCorrect(height_textfield, height_input_layout, 110.0f, 270.0f, resources.getString(com.met.impilo.R.string.invalid_value)))
            validate = false
        if (Utils.isEditTextEmpty(weight_textfield, weight_input_layout, resources.getString(com.met.impilo.R.string.field_required)) ||
            !Utils.isRangeCorrect(weight_textfield, weight_input_layout, 10.0f, 650.0f, resources.getString(com.met.impilo.R.string.invalid_value)))
            validate = false
        if (Utils.isEditTextEmpty(waist_textfield, waist_input_layout, resources.getString(com.met.impilo.R.string.field_required)) ||
            !Utils.isRangeCorrect(waist_textfield, waist_input_layout, 50.0f, 300.0f, resources.getString(com.met.impilo.R.string.invalid_value)))
            validate = false

        if(onlyConfiguration){
            if(Utils.isEditTextEmpty(birth_date_textfield2, birth_date_input_layout2, resources.getString(com.met.impilo.R.string.field_required))){
                validate = false
            }
        }

        return validate
    }

    override fun onResume() {
        super.onResume()
        if(onlyConfiguration)
            birth_date_input_layout2.visibility = View.VISIBLE

    }

    override fun onNextPageClick(): Boolean {
        if(validate()){

            if(onlyConfiguration)
                callback.basicInformation(Utils.toInt(height_textfield), Utils.toFloat(weight_textfield), Utils.toFloat(waist_textfield), gender, birth_date_textfield2.text.toString().stringToDate())
            else
                callback.basicInformation(Utils.toInt(height_textfield), Utils.toFloat(weight_textfield), Utils.toFloat(waist_textfield), gender)

            return true
        }
        return false
    }

}
