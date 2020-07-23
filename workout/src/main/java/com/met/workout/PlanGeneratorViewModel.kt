package com.met.workout

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.Goal
import com.met.impilo.data.workouts.*
import com.met.impilo.data.workouts.generator.ExerciseLoad
import com.met.impilo.data.workouts.generator.GeneratedPlan
import com.met.impilo.repository.WorkoutsRepository

class PlanGeneratorViewModel : ViewModel() {

    private val TAG = javaClass.simpleName
    private val workoutRepository = WorkoutsRepository.newInstance(FirebaseFirestore.getInstance())
    private var plan = MutableLiveData<GeneratedPlan>()
    private var trainingPlanInfo = MutableLiveData<TrainingPlanInfo>()

    fun fetchPlan(trainingsPerWeek: Int, trainingInternship: Int, equipment: WhereDoExercise, exerciseLoad: ExerciseLoad) {

        var goal: Goal
        workoutRepository.getPersonalData { personalData ->
            goal = personalData?.goal!!
            workoutRepository.generatePlan(trainingsPerWeek, trainingInternship, equipment, exerciseLoad, goal, null) {
                plan.value = it.first
                trainingPlanInfo.value = it.second
            }

        }
    }

    fun addAllTrainingDays(trainingPlanInfo: TrainingPlanInfo, success: (Int) -> Unit){
        workoutRepository.addWeekATrainingDaysScheme(trainingPlanInfo){ successA ->
            if (trainingPlanInfo.trainingSystem == TrainingSystem.AB)
                workoutRepository.addWeekBTrainingDaysScheme(trainingPlanInfo){ successB ->
                    success(successB)
                }
            else
                success(successA)
        }
    }

    fun addTrainingPlanInfo(trainingPlanInfo: TrainingPlanInfo, success: (Boolean) -> Unit){
        workoutRepository.addTrainingPlanInfo(trainingPlanInfo){
            success(it)
        }
    }

    fun getGeneratedPlan() : LiveData<GeneratedPlan> = plan
    fun getGeneratedTrainingPlanInfo() : LiveData<TrainingPlanInfo> = trainingPlanInfo
}