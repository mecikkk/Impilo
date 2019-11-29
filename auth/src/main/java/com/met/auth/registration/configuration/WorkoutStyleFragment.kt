package com.met.auth.registration.configuration


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.auth.R
import com.met.auth.registration.Registration
import com.met.impilo.data.WorkoutStyle
import com.met.impilo.utils.Utils
import kotlinx.android.synthetic.main.fragment_workout_style.*


class WorkoutStyleFragment : BaseFragment(), Registration.OnPageChangeListener {

    private lateinit var workoutDescriptions: Array<String>
    private lateinit var workoutStyle: WorkoutStyle
    private var trainingsPerWeek: Int = 0

    companion object {
        @JvmStatic
        fun newInstance() = WorkoutStyleFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_style, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutDescriptions = resources.getStringArray(com.met.impilo.R.array.workoutDescriptions)

        days_recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CircularDaysAdapter()
        days_recyclerview.adapter = adapter

        adapter.setOnDayItemClickListener(object : DayItemClickListener {
            override fun onDayItemClick(position: Int) {
                trainingsPerWeek = position + 1
            }
        })

        light_toggle.setOnClickListener { checkToggle(0) }
        moderate_toggle.setOnClickListener { checkToggle(1) }
        heavy_toggle.setOnClickListener { checkToggle(2) }

        checkToggle(1)

    }

    private fun checkToggle(i: Int) {
        if (i == 0) {
            light_toggle.isChecked = true
            workoutStyle = WorkoutStyle.LIGHT
        } else light_toggle.isChecked = false
        if (i == 1) {
            moderate_toggle.isChecked = true
            workoutStyle = WorkoutStyle.MODERATE
        } else moderate_toggle.isChecked = false
        if (i == 2) {
            heavy_toggle.isChecked = true
            workoutStyle = WorkoutStyle.HEAVY
        } else heavy_toggle.isChecked = false

        workout_description_textview.text = workoutDescriptions[i]
    }

    private fun validator(): Boolean =
        !(Utils.isEditTextEmpty(training_time_textfield, training_time_textlayout, resources.getString(com.met.impilo.R.string.field_required)) || !Utils.isRangeCorrect(
            training_time_textfield, training_time_textlayout, 1.0f, 1140.0f, resources.getString(com.met.impilo.R.string.invalid_value)))


    override fun onNextPageClick(): Boolean = if (validator()) {
        callback.workoutStyle(workoutStyle, trainingsPerWeek, Utils.toInt(training_time_textfield))
        true
    } else false


}
