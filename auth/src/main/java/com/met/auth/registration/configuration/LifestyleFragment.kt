package com.met.auth.registration.configuration


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.met.auth.R
import com.met.auth.registration.Registration
import com.met.impilo.data.Goal
import com.met.impilo.data.Lifestyle
import kotlinx.android.synthetic.main.fragment_lifestyle.*


class LifestyleFragment : BaseFragment(), Registration.OnPageChangeListener {

    private lateinit var lifestyleTitles : List<String>
    private lateinit var lifestyleDescriptions : List<String>
    private lateinit var goalTitles : List<String>

    private lateinit var lifestyle : Lifestyle
    private lateinit var goal : Goal

    companion object {
        @JvmStatic
        fun newInstance() = LifestyleFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lifestyle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifestyleTitles = resources.getStringArray(com.met.impilo.R.array.lifestyleTitles).toList()
        lifestyleDescriptions = resources.getStringArray(com.met.impilo.R.array.lifestyleDescriptions).toList()
        goalTitles = resources.getStringArray(com.met.impilo.R.array.goalTitles).toList()

        sedentary_toggle.setOnClickListener { checkLifestyleToggle(0) }
        light_toggle.setOnClickListener { checkLifestyleToggle(1) }
        active_toggle.setOnClickListener { checkLifestyleToggle(2) }
        very_active_toggle.setOnClickListener { checkLifestyleToggle(3) }

        loss_toggle.setOnClickListener { checkGoalToggle(0) }
        maintain_toggle.setOnClickListener { checkGoalToggle(1) }
        bulking_toggle.setOnClickListener { checkGoalToggle(2) }

        checkLifestyleToggle(1)
        checkGoalToggle(1)

    }

    private fun checkLifestyleToggle(i: Int) {
        if (i == 0) {
            sedentary_toggle.isChecked = true
            lifestyle = Lifestyle.SEDENTARY
        } else sedentary_toggle.isChecked = false
        if (i == 1) {
            light_toggle.isChecked = true
            lifestyle = Lifestyle.LIGHT
        } else light_toggle.isChecked = false
        if (i == 2) {
            active_toggle.isChecked = true
            lifestyle = Lifestyle.ACTIVE
        } else active_toggle.isChecked = false
        if (i == 3) {
            very_active_toggle.isChecked = true
            lifestyle = Lifestyle.VERY_ACTIVE
        } else very_active_toggle.isChecked = false
        changeLifestyleDescription(i)
    }

    private fun checkGoalToggle(i: Int) {
        if (i == 0) {
            loss_toggle.isChecked = true
            goal = Goal.FAT_LOSE
        } else loss_toggle.isChecked = false
        if (i == 1) {
            maintain_toggle.isChecked = true
            goal = Goal.MAINTAIN
        } else maintain_toggle.isChecked = false
        if (i == 2) {
            bulking_toggle.isChecked = true
            goal = Goal.MUSCLE_GAIN
        } else bulking_toggle.isChecked = false

        goal_textview.text = goalTitles[i]
    }

    private fun changeLifestyleDescription(position: Int) {
        lifestyle_title_textview.text = lifestyleTitles[position]
        lifestyle_description_textview.text = lifestyleDescriptions[position]
    }

    override fun onNextPageClick(): Boolean {
        callback.lifestyle(lifestyle, goal)
        return true
    }


}
