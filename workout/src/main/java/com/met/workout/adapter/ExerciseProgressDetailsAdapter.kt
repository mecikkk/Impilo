package com.met.workout.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.met.impilo.data.workouts.TrainingDay
import com.met.workout.ExerciseProgressFragment

class ExerciseProgressDetailsAdapter(fm: FragmentManager, var trainingDay: TrainingDay) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return ExerciseProgressFragment.newInstance(trainingDay.exercises[position])
    }

    override fun getCount(): Int = trainingDay.exercises.size

}
