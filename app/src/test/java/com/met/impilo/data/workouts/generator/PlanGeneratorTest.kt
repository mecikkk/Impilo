package com.met.impilo.data.workouts.generator

import com.met.impilo.data.BodyMeasurements
import com.met.impilo.data.Goal
import com.met.impilo.data.workouts.WhereDoExercise
import org.junit.Assert.assertEquals
import org.junit.Test

class PlanGeneratorTest {

    private val planGenerator = PlanGenerator(5, 2, WhereDoExercise.HOME_WITH_EQ, ExerciseLoad(65f, 60f,
        20f, 10f, 15f), Goal.MUSCLE_GAIN, BodyMeasurements())

    @Test
    fun `one max rep when max load for 5 reps is 65kg should equals 75,82kg`() {
        assertEquals(75.82f, planGenerator.calculate1RM(planGenerator.exerciseLoad.barbellBenchPress))
    }

    @Test
    fun `selected workout type should be FIVE_DAY_SPLIT`() {
        assertEquals(WorkoutType.FIVE_DAY_SPLIT, planGenerator.selectWorkoutType())
    }

    @Test
    fun `calculated exercise load for 1RM 65 kg when goal is MUSCLE GAIN should be 52kg`() {
        assertEquals(52f, planGenerator.calculateExerciseLoad(65f))
    }

}