package com.met.workout

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.workouts.TrainingDay
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.repository.WorkoutsRepository
import com.met.impilo.utils.toId
import java.util.*

class StartWorkoutActivityViewModel : ViewModel() {

    private val workoutRepository = WorkoutsRepository.newInstance(FirebaseFirestore.getInstance())
    lateinit var trainingPlanInfo : TrainingPlanInfo
    lateinit var trainingDay: TrainingDay
    lateinit var workoutDate: Date
    var startTime = 0L
    var updateTime = 0L
    var timeBuff = 0L
    var milliSecTime = 0L

    fun addTrainingDone(success : (Boolean) -> Unit){
        workoutRepository.addTrainingDone(workoutDate.toId(), trainingPlanInfo, trainingDay){
            success(it)
        }
    }
}