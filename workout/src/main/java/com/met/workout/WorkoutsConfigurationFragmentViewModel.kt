package com.met.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.met.impilo.data.BodyRating
import com.met.impilo.data.Goal
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.data.workouts.TrainingSystem
import com.met.impilo.repository.BodyMeasurementsRepository
import com.met.impilo.repository.PersonalDataRepository
import com.met.impilo.repository.WorkoutsRepository

class WorkoutsConfigurationFragmentViewModel : ViewModel() {

    private val TAG = javaClass.simpleName

    private val firestore = FirebaseFirestore.getInstance()
    private val bodyMeasurementsRepository = BodyMeasurementsRepository.newInstance(firestore)
    private val personalDataRepository = PersonalDataRepository.newInstance(firestore)
    private val workoutRepository = WorkoutsRepository.newInstance(firestore)

    var bf = MutableLiveData<Float>()
    var thumbPosition = MutableLiveData<Float>()
    var bodyRating = MutableLiveData<BodyRating>()


    fun fetchBF(){
        bodyMeasurementsRepository.getBF { bfResult ->
            bf.value = bfResult
        }
    }

    fun fetchThumbPosition(bf : Float){
        bodyMeasurementsRepository.getThumbPosition(bf){
            thumbPosition.value = it
        }
    }

    fun fetchBodyRating(bf : Float){
        bodyMeasurementsRepository.getBodyRating(bf){
            bodyRating.value = it
        }
    }

    fun changeGoal(goal : Goal, success : (Boolean) -> Unit) {
        personalDataRepository.updateGoal(goal){
            success(it)
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

    fun getBF() : LiveData<Float> = bf
    fun getThumbPosition() : LiveData<Float> = thumbPosition
    fun getBodyRating() : LiveData<BodyRating> = bodyRating

}