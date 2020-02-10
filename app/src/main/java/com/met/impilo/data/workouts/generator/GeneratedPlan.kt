package com.met.impilo.data.workouts.generator

import com.met.impilo.data.workouts.TrainingDay

data class GeneratedPlan(var workoutType : WorkoutType, var trainingDays : MutableList<TrainingDay>)