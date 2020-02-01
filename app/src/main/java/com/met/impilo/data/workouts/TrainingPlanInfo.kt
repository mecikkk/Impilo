package com.met.impilo.data.workouts

import java.io.Serializable
import java.util.*

data class TrainingPlanInfo(
    var date: Date = Date(),
    var actualWeek : Week = Week.A,
    var trainingSystem : TrainingSystem = TrainingSystem.A,
    var isConfigurationCompleted : Boolean = false,
    var weekA : MutableList<TrainingDay> = mutableListOf(),
    var weekB : MutableList<TrainingDay> = mutableListOf()
) : Serializable {


    override fun toString(): String {
        return "${javaClass.simpleName} : \n" +
                "date = $date\nactualWeek : $actualWeek\ntrainingSystem : $trainingSystem\nisConfigurationCompleted : $isConfigurationCompleted\n" +
                "TrainingDays : \n" +
                "WeekA : $weekA\nWeekB : $weekB "
    }
}