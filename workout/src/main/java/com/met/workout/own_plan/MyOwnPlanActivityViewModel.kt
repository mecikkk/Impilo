package com.met.workout.own_plan

import androidx.lifecycle.ViewModel
import com.met.impilo.data.workouts.TrainingDay

class MyOwnPlanActivityViewModel : ViewModel() {

    var weekA = mutableListOf(TrainingDay(id = 0), TrainingDay(id = 1), TrainingDay(id = 2), TrainingDay(id = 3), TrainingDay(id = 4), TrainingDay(id = 5), TrainingDay(id = 6))
    var weekB = mutableListOf(TrainingDay(id = 0), TrainingDay(id = 1), TrainingDay(id = 2), TrainingDay(id = 3), TrainingDay(id = 4), TrainingDay(id = 5), TrainingDay(id = 6))
    var lastUsedMuscleSetIndex = 0
}