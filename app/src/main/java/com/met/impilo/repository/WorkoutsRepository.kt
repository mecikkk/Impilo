package com.met.impilo.repository

import android.util.Log
import com.met.impilo.data.workouts.*
import com.met.impilo.utils.Const

class WorkoutsRepository : FirebaseRepository() {
    override val TAG = javaClass.simpleName
    private var weekDaysMap: HashMap<Int, String>

    companion object {
        @JvmStatic
        fun newInstance() = WorkoutsRepository()
    }

    init {
        weekDaysMap = HashMap()
        weekDaysMap[0] = Const.REF_MONDAY
        weekDaysMap[1] = Const.REF_TUESDAY
        weekDaysMap[2] = Const.REF_WEDNESDAY
        weekDaysMap[3] = Const.REF_THURSDAY
        weekDaysMap[4] = Const.REF_FRIDAY
        weekDaysMap[5] = Const.REF_SATURDAY
        weekDaysMap[6] = Const.REF_SUNDAY
    }

    private fun initWeekDays(){
        weekDaysMap = HashMap()
        weekDaysMap[0] = Const.REF_MONDAY
        weekDaysMap[1] = Const.REF_TUESDAY
        weekDaysMap[2] = Const.REF_WEDNESDAY
        weekDaysMap[3] = Const.REF_THURSDAY
        weekDaysMap[4] = Const.REF_FRIDAY
        weekDaysMap[5] = Const.REF_SATURDAY
        weekDaysMap[6] = Const.REF_SUNDAY
    }

    fun getExercises(exerciseReference: String, exercises: (List<Exercise>) -> Unit) {
        val result = mutableListOf<Exercise>()
        firestore.collection(Const.REF_EXERCISES).document(Const.REF_GYM_EXERCISES).collection(exerciseReference).get().addOnSuccessListener { collection ->
            Log.i(TAG, "Getting exercises for : $exerciseReference")
            collection.documents.forEach {
                result.add(it.toObject(Exercise::class.java)!!)
                Log.i(TAG, "Found : ${it["name"]}")
            }
            exercises(result)
        }.addOnFailureListener {
            Log.e(TAG, "Getting exercises error : ${it.cause} | ${it.message}")
        }
    }

    fun addWeekATrainingDaysScheme(trainingPlanInfo: TrainingPlanInfo, success: (Int) -> Unit) {
        for (x in 0..6) {
            firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(Const.REF_TRAINING_PLAN_INFO).collection(Const.REF_WEEK_A)
                .document(weekDaysMap[x]!!).set(trainingPlanInfo.weekA[x]).addOnSuccessListener {
                    Log.i(TAG, "Week A TrainingDay $x added successful")
                    success(x)
                }.addOnFailureListener {
                    Log.i(TAG, "Week A TrainingDay $x adding error : ${it.message}")
                    success(-1)
                }
        }
    }

    fun addWeekBTrainingDaysScheme(trainingPlanInfo: TrainingPlanInfo, success: (Int) -> Unit) {
        for (x in 0..6) {
            firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(Const.REF_TRAINING_PLAN_INFO).collection(Const.REF_WEEK_B)
                .document(weekDaysMap[x]!!).set(trainingPlanInfo.weekB[x]).addOnSuccessListener {
                    Log.i(TAG, "Week B TrainingDay $x added successful")
                    success(x)
                }.addOnFailureListener {
                    Log.i(TAG, "Week B TrainingDay $x adding error : ${it.message}")
                    success(-1)
                }
        }
    }

    fun addTrainingPlanInfo(trainingPlanInfo: TrainingPlanInfo, success: (Boolean) -> Unit) {
        trainingPlanInfo.weekA.clear()
        trainingPlanInfo.weekB.clear()
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(Const.REF_TRAINING_PLAN_INFO).set(trainingPlanInfo)
            .addOnSuccessListener {
                Log.i(TAG, "Training Info added successful")
                success(true)
            }.addOnFailureListener {
                Log.i(TAG, "Training Info adding error : ${it.message}")
                success(false)
            }
    }

    fun getTrainingDayByDateId(dateId: String, dayOfWeek: Int, currentWeek: Week, trainingDay: (TrainingDay) -> Unit) {
        initWeekDays()
        getTrainingDay(dateId, dayOfWeek) { day ->
            if (day != null){
                trainingDay(day)
            } else {
                getTrainingDaySheme(dayOfWeek, currentWeek){ scheme ->
                    if(scheme != null) {
                        trainingDay(scheme)
                    }
                }
            }
        }
    }

    fun getTrainingDay(dateId: String, dayOfWeek: Int, trainingDay: (TrainingDay?) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(dateId)
            .collection(Const.REF_TRAINING_DAY)
            .document(
                weekDaysMap[dayOfWeek]!!
            )
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val day = it.toObject(TrainingDay::class.java)
                    Log.i(TAG, "TrainingDay by date founded : $day")
                    trainingDay(day!!)
                } else {
                    Log.i(TAG, "TrainingDay by date not found")
                    trainingDay(null)
                }
            }.addOnFailureListener {
                Log.i(TAG, "Getting training day by date error : ${it.message}")
                trainingDay(null)
            }
    }

    fun getTrainingDaySheme(dayOfWeek: Int, currentWeek : Week,  trainingDay: (TrainingDay?) -> Unit) {
        val currentWeekReference = if(currentWeek == Week.A) Const.REF_WEEK_A
        else Const.REF_WEEK_B

        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(Const.REF_TRAINING_PLAN_INFO).collection(currentWeekReference)
            .document(weekDaysMap[dayOfWeek]!!).get().addOnSuccessListener {
                if (it.exists()) {
                    val day = it.toObject(TrainingDay::class.java)
                    Log.i(TAG, "TrainingDay from scheme founded : $day")
                    trainingDay(day!!)
                } else {
                    Log.i(TAG, "TrainingDay from scheme not found")
                    trainingDay(null)
                }
            }.addOnFailureListener {
                Log.i(TAG, "Getting training day from scheme error : ${it.message}")
                trainingDay(null)
            }
    }

    fun getTrainingPlanSystem(trainingSystem: (TrainingSystem) -> Unit){
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(Const.REF_TRAINING_PLAN_INFO).get()
            .addOnSuccessListener {
                if(it.exists()){
                    Log.i(TAG, "Current Training System : ${it["trainingSystem"]}")
                    trainingSystem(it["trainingSystem"]!! as TrainingSystem)
                } else
                    Log.i(TAG, "Current Training System not found")
            }. addOnFailureListener {
                Log.e(TAG, "Getting current training system error : ${it.message}")
            }
    }

    fun getTrainingPlanInfo(trainingPlanInfo: (TrainingPlanInfo) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(Const.REF_TRAINING_PLAN_INFO).get()
            .addOnSuccessListener {
                if(it.exists()){
                    Log.i(TAG, "Received TrainingPlanInfo : $it")
                    trainingPlanInfo(it.toObject(TrainingPlanInfo::class.java)!!)
                } else
                    Log.i(TAG, "TrainingPlanInfo not found")
            }. addOnFailureListener {
                Log.e(TAG, "Getting trainingPlanInfo error : ${it.message}")
            }
    }
}