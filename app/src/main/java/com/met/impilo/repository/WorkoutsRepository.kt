package com.met.impilo.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.met.impilo.data.BodyMeasurements
import com.met.impilo.data.Goal
import com.met.impilo.data.workouts.*
import com.met.impilo.data.workouts.generator.ExerciseLoad
import com.met.impilo.data.workouts.generator.GeneratedPlan
import com.met.impilo.data.workouts.generator.PlanGenerator
import com.met.impilo.data.workouts.generator.WorkoutType
import com.met.impilo.utils.Const
import com.met.impilo.utils.idToDate
import java.util.*
import kotlin.collections.HashMap

class WorkoutsRepository(override val firestore: FirebaseFirestore) : FirebaseRepository(firestore) {
    override val TAG = javaClass.simpleName
    override val uid: String = FirebaseAuth.getInstance().uid!!
    private var weekDaysMap: HashMap<Int, String>
    var planGeneratorTrainingDays = mutableListOf<TrainingDay>()
    private var lastDayNumber = 0


    companion object {
        @JvmStatic
        fun newInstance(firestore: FirebaseFirestore) = WorkoutsRepository(firestore)
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

    private fun initWeekDays() {
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

    fun getTrainingDayByDateId(dateId: String, currentWeek: Week, trainingDay: (TrainingDay) -> Unit) {
        initWeekDays()
        getTrainingDay(dateId) { day ->
            if (day != null) {
                trainingDay(day)
            } else {
                getTrainingDayScheme(getCurrentWeekDay(dateId.idToDate()!!), currentWeek) { scheme ->
                    if (scheme != null) {
                        trainingDay(scheme)
                    }
                }
            }
        }
    }

    fun getTrainingDay(dateId: String, trainingDay: (TrainingDay?) -> Unit) {
        val weekDay = getCurrentWeekDay(dateId.idToDate()!!)
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(dateId).collection(Const.REF_TRAINING_DAY)
            .document(weekDaysMap[weekDay]!!).get().addOnSuccessListener {
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

    fun getTrainingDayScheme(dayOfWeek: Int, currentWeek: Week, trainingDay: (TrainingDay?) -> Unit) {
        val currentWeekReference = if (currentWeek == Week.A) Const.REF_WEEK_A
        else Const.REF_WEEK_B

        Log.d(TAG, "getTrainingDayScheme week : $currentWeekReference")

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

    fun getTrainingPlanSystem(trainingSystem: (TrainingSystem) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(Const.REF_TRAINING_PLAN_INFO).get().addOnSuccessListener {
            if (it.exists()) {
                Log.i(TAG, "Current Training System : ${it["trainingSystem"]}")
                trainingSystem(it["trainingSystem"]!! as TrainingSystem)
            } else Log.i(TAG, "Current Training System not found")
        }.addOnFailureListener {
            Log.e(TAG, "Getting current training system error : ${it.message}")
        }
    }

    fun getTrainingPlanInfoByDateOrScheme(dateId: String, trainingPlanInfo: (TrainingPlanInfo) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(dateId).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                Log.i(TAG, "Received TrainingPlanInfo by date : $doc")
                trainingPlanInfo(doc.toObject(TrainingPlanInfo::class.java)!!)
            } else {
                getTrainingPlanInfo {
                    trainingPlanInfo(it)
                }
            }
        }.addOnFailureListener {
            Log.e(TAG, "Getting trainingPlanInfo by date error : ${it.message}")
        }
    }

    fun getTrainingPlanInfo(trainingPlanInfo: (TrainingPlanInfo) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(Const.REF_TRAINING_PLAN_INFO).get().addOnSuccessListener {
            if (it.exists()) {
                Log.i(TAG, "Received TrainingPlanInfo from scheme : $it")
                trainingPlanInfo(it.toObject(TrainingPlanInfo::class.java)!!)
            } else Log.i(TAG, "TrainingPlanInfo in scheme not found")
        }.addOnFailureListener {
            Log.e(TAG, "Getting trainingPlanInfo scheme error : ${it.message}")
        }
    }

    fun addTrainingDone(dateId: String, trainingPlanInfo: TrainingPlanInfo, trainingDay: TrainingDay, success: (Boolean) -> Unit) {
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(dateId).set(trainingPlanInfo).addOnSuccessListener {
            Log.i(TAG, "Training info added successfully")
            addTrainingDay(dateId, trainingDay) {
                success(it)
            }
        }.addOnFailureListener {
            Log.i(TAG, "Adding training info error : ${it.message}")
            success(false)
        }
    }

    fun addTrainingDay(dateId: String, trainingDay: TrainingDay, success: (Boolean) -> Unit) {

        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).document(dateId).collection(Const.REF_TRAINING_DAY)
            .document(weekDaysMap[trainingDay.id]!!).set(trainingDay).addOnSuccessListener {
                Log.i(TAG, "Training day added successfully")
                success(true)
            }.addOnFailureListener {
                Log.i(TAG, "Adding training day error : ${it.message}")
                success(false)
            }

    }

    fun getDatesIdsOfAllCompletedWorkouts(datesIds: (List<String>) -> Unit) {
//        resetUid()
        firestore.collection(Const.REF_USER_DATA).document(uid).collection(Const.REF_ALL_TRAININGS).get().addOnSuccessListener { collection ->
            if (!collection.documents.isNullOrEmpty()) {
                Log.i(TAG, "Founded completed workouts")
                val list = mutableListOf<String>()
                collection.documents.forEach {
                    Log.i(TAG, "Training date : ${it.id}")
                    list.add(it.id)
                }
                datesIds(list)
            } else {
                Log.i(TAG, "No completed workouts found")
            }
        }.addOnFailureListener {
            Log.i(TAG, "Getting dates of all completed workouts error : ${it.message}")
        }
    }

    fun getMaxWeight(weights: List<Pair<Date, Float>>): Float {
        var result = weights[0].second

        weights.forEach {
            if (result < it.second) result = it.second
        }
        return result
    }

    fun getMinWeight(weights: List<Pair<Date, Float>>): Float {
        var result = weights[0].second

        weights.forEach {
            if (result > it.second) result = it.second
        }
        return result
    }


    private fun getCurrentWeekDay(calendar: Calendar): Int {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            2 -> 0
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 5
            1 -> 6
            else -> 0
        }
    }

    private fun getCurrentWeekDay(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            2 -> 0
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 5
            1 -> 6
            else -> 0
        }
    }

    fun generatePlan(trainingsPerWeek: Int, trainingInternship: Int, equipment: WhereDoExercise, exerciseLoad: ExerciseLoad, goal: Goal,
                     bodyMeasurements: BodyMeasurements?, plan: (Pair<GeneratedPlan, TrainingPlanInfo>) -> Unit) {

        val generator = PlanGenerator(trainingsPerWeek, trainingInternship, equipment, exerciseLoad, goal, bodyMeasurements)
        val homeOrGym = when (equipment) {
            WhereDoExercise.GYM -> 2
            else -> 1
        }

        getExerciseFromGeneratorPlanScheme(generator.workoutType.nameRef, generator.workoutType.days, homeOrGym) {
            if (it) {
                generator.trainingPlan = planGeneratorTrainingDays
                generator.calculateLoads()
                val g = GeneratedPlan(generator.workoutType, generator.trainingPlan)
                plan(Pair(g,generatedPlanToTrainingPlanInfo(g)))
            }
        }
    }

    private fun generatedPlanToTrainingPlanInfo(generated : GeneratedPlan) : TrainingPlanInfo{
        val weekA = mutableListOf<TrainingDay>()
        val weekB = mutableListOf<TrainingDay>()

        for(x in 0..6) {
            weekA.add(TrainingDay(id = x, isRestDay = true))
            weekB.add(TrainingDay(id = x, isRestDay = true))
        }

        val trainingSystem = when(generated.workoutType){
            WorkoutType.THREE_DAY_FBW -> {

                weekA[0].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[0].exercises)
                    id = 0
                    muscleSetsNames.addAll(generated.trainingDays[0].muscleSetsNames)
                }

                weekA[2].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[1].exercises)
                    id = 2
                    muscleSetsNames.addAll(generated.trainingDays[1].muscleSetsNames)
                }

                weekA[4].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[0].exercises)
                    id = 4
                    muscleSetsNames.addAll(generated.trainingDays[0].muscleSetsNames)
                }

                weekB[0].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[1].exercises)
                    id = 0
                    muscleSetsNames.addAll(generated.trainingDays[1].muscleSetsNames)
                }

                weekB[2].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[0].exercises)
                    id = 2
                    muscleSetsNames.addAll(generated.trainingDays[0].muscleSetsNames)
                }

                weekB[4].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[1].exercises)
                    id = 4
                    muscleSetsNames.addAll(generated.trainingDays[1].muscleSetsNames)
                }

                TrainingSystem.AB
            } WorkoutType.THREE_DAY_FAT_LOSS, WorkoutType.THREE_DAY_SPLIT -> {
                weekA[0].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[0].exercises)
                    id = 0
                    muscleSetsNames.addAll(generated.trainingDays[0].muscleSetsNames)
                }
                weekA[2].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[1].exercises)
                    id = 2
                    muscleSetsNames.addAll(generated.trainingDays[1].muscleSetsNames)
                }
                weekA[4].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[2].exercises)
                    id = 4
                    muscleSetsNames.addAll(generated.trainingDays[2].muscleSetsNames)
                }
                TrainingSystem.A
            } WorkoutType.FOUR_DAY_UP_DOWN, WorkoutType.FOUR_DAY_PUSH_PULL -> {
                weekA[0].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[0].exercises)
                    id = 0
                    muscleSetsNames.addAll(generated.trainingDays[0].muscleSetsNames)
                }
                weekA[1].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[1].exercises)
                    id = 1
                    muscleSetsNames.addAll(generated.trainingDays[1].muscleSetsNames)
                }
                weekA[3].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[2].exercises)
                    id = 3
                    muscleSetsNames.addAll(generated.trainingDays[2].muscleSetsNames)
                }
                weekA[4].apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[3].exercises)
                    id = 4
                    muscleSetsNames.addAll(generated.trainingDays[3].muscleSetsNames)
                }
                TrainingSystem.A
            } else -> {
                weekA[0] = generated.trainingDays[0]
                weekA[1] = generated.trainingDays[1]
                weekA[2] = generated.trainingDays[2]
                weekA[4] .apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[3].exercises)
                    id = 4
                    muscleSetsNames.addAll(generated.trainingDays[3].muscleSetsNames)
                }
                weekA[5] .apply {
                    isRestDay = false
                    exercises.addAll(generated.trainingDays[4].exercises)
                    id = 5
                    muscleSetsNames.addAll(generated.trainingDays[4].muscleSetsNames)
                }
                TrainingSystem.A
            }
        }
        return TrainingPlanInfo(date = Date(), actualWeek = Week.A, trainingSystem = trainingSystem, weekA =  weekA, weekB = weekB)
    }


    fun getExerciseFromGeneratorPlanScheme(planRef: String, daysQuantity: Int, homeEqOrGym: Int, success: (Boolean) -> Unit) {

        for (i in 0 until daysQuantity) {
            planGeneratorTrainingDays.add(TrainingDay(id = i))
        }

        val successDays = mutableListOf<Boolean>()

        var x = 1
        lastDayNumber = daysQuantity - 1

        Log.i(TAG, "PRZED PETLA OGOLNA")

        for (i in 0 until daysQuantity) {
            successDays.add(false)
            Log.i(TAG, "START PETLI OGOLNEJ")
            firestore.collection(Const.REF_EXERCISES).document(planRef).collection("${i + 1}").get().addOnSuccessListener { allExercises ->
                Log.i(TAG, "firebase START POBIERANIA DNIA $i Z PLANU $planRef")

                if (!allExercises.documents.isNullOrEmpty()) {
                    Log.i(TAG, "DOOOOOCCCUUUMMEEENNNTT")
                    getAllDayMusclesSetExercises(i, allExercises, homeEqOrGym) { s ->
                        if (s) {
                            Log.d(TAG, "x == daysQuantity | $x == $daysQuantity")

                            Log.i(TAG, "SUCCES DAY $i")
                            successDays[x - 1] = true

                            if (!successDays.contains(false)) success(s)

                            x++
                        }
                    }

                }
            }
        }
    }

    private fun getAllDayMusclesSetExercises(day: Int, allExercises: QuerySnapshot, variant: Int, success: (Boolean) -> Unit) {

        val allExercisesSuccess = mutableListOf<Boolean>()

        for (x in 0 until allExercises.documents.size) {
            Log.i(TAG, "GETTING EXERCISES FROM $day DAY")
            allExercisesSuccess.add(false)
            // Firebase have problem with cast NUMBER to type Int, Long, Decimal, Float... Solved by cast field -> Any -> String -> Int
            val collectionNumbers: Any? = allExercises.documents[x]["collectionSize"]
            val allColl = collectionNumbers.toString().toInt()
            planGeneratorTrainingDays[day].apply {
                this.isRestDay = false
                muscleSetsNames.add(allExercises.documents[x]["name"] as String)
            }

            getMuscleSetExercises(day, allExercises.documents[x].reference, variant, allColl) {
                if (it) {
                    allExercisesSuccess[x] = true
                    if (!allExercisesSuccess.contains(false)) success(true)
                }
            }
        }
    }

    private fun getMuscleSetExercises(day: Int, exerciseSet: DocumentReference, variant: Int, allColl: Int, success: (Boolean) -> Unit) {
        var x = 1
        var allCollumnsSuccess = mutableListOf<Boolean>()
        for (j in 1..allColl) {
            Log.i(TAG, "GETTING DAY $day MUSCLES SET EXERCISES : $j")
            allCollumnsSuccess.add(false)
            exerciseSet.collection("$j").get().addOnSuccessListener { allVariants ->

                getAllExercisesVariant(day, allVariants, variant) {
                    if (it) {
                        Log.d(TAG, "day $day lastday : $lastDayNumber x == allColl | $x == $allColl")
                        allCollumnsSuccess[x - 1] = true
                        if (!allCollumnsSuccess.contains(false)) success(true)
                        x++
                    }
                }

            }
        }
    }

    private fun getAllExercisesVariant(day: Int, collectionReference: QuerySnapshot, variant: Int, success: (Boolean) -> Unit) {

        Log.d(TAG, "VARIANT $variant  searching ")

        var parent: Int
        var ratio: Float

        val ref: DocumentReference = if (collectionReference.documents.size == 1 && variant == 2) {
            parent = (collectionReference.documents[0]["parent"] as Any).toString().toInt()
            ratio = (collectionReference.documents[0]["ratio"] as Any).toString().toFloat()
            collectionReference.documents[0]["1"] as DocumentReference
        } else if (collectionReference.documents.size == 2 && variant == 2) {
            parent = (collectionReference.documents[1]["parent"] as Any).toString().toInt()
            ratio = (collectionReference.documents[1]["ratio"] as Any).toString().toFloat()
            collectionReference.documents[1]["1"] as DocumentReference

        } else {
            parent = (collectionReference.documents[0]["parent"] as Any).toString().toInt()
            ratio = (collectionReference.documents[0]["ratio"] as Any).toString().toFloat()
            collectionReference.documents[0]["1"] as DocumentReference
        }

        getExerciseAndAddToListByRef(day, variant, parent, ratio, ref) {
            success(it)
        }

    }

    private fun getExerciseAndAddToListByRef(day: Int, variant: Int, parent: Int, ratio: Float, ref: DocumentReference, success: (Boolean) -> Unit) {
        ref.get().addOnSuccessListener {
            if (it.exists()) {
                val exercise = it.toObject(Exercise::class.java) as Exercise
                Log.w(TAG, "Day $day Exercise (variant $variant) : ${exercise.name}")
                exercise.parent = parent
                exercise.ratio = ratio
                planGeneratorTrainingDays[day].exercises.add(exercise)
                success(true)
            } else success(false)
        }
    }

}