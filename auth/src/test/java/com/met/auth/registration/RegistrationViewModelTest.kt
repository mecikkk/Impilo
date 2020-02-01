package com.met.auth.registration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.met.impilo.data.*
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*


class RegistrationViewModelTest {

//    @get:Rule
//    val rule = InstantTaskExecutorRule()
//
//    private lateinit var viewModel : RegistrationViewModel
//
//    @Before
//    fun setUp() {
//        viewModel = RegistrationViewModel()
//        viewModel.personalData = PersonalData(uid = "testUser", birthDate = stringToDate("28 cze 1995"), gender = Gender.MALE, somatotype = Somatotype.ECTO_MESOMORPH,
//            workoutStyle = WorkoutStyle.MODERATE, workoutQuantity = 5, workoutTime = 90, lifestyle = Lifestyle.SEDENTARY, goal = Goal.MUSCLE_GAIN)
//        viewModel.bodyMeasurements = BodyMeasurements(uid = "testUser", height = 183, weight = 85f)
//    }
//
//    fun stringToDate(date: String): Date? {
//        if (date != "") {
//            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
//            return format.parse(date)
//        }
//        return null
//    }
//
//    @Test
//    fun getCaloricDemand() {
//        val tdee = viewModel.getCaloricDemand()
//        Assert.assertEquals(3529, tdee)
//    }
//
//    @Test
//    fun calculateBmr() {
//        assertEquals(1874, viewModel.calculateBmr().toInt())
//    }
//
//    @Test
//    fun calculateTea() {
//        assertEquals(608, viewModel.calculateTea(viewModel.calculateBmr()).toInt())
//    }
//
//    @Test
//    fun calculateNeat() {
//        assertEquals(600, viewModel.calculateNeat())
//    }
//
//    @Test
//    fun calculateTfe() {
//        assertEquals(246, viewModel.calculateTfe(viewModel.calculateBmr(), viewModel.calculateNeat().toFloat(), viewModel.calculateTea(viewModel.calculateBmr())).toInt())
//    }
//
//    @Test
//    fun calculateDemand(){
//        viewModel.calculateDemand()
//        assertEquals(3529, viewModel.demand.calories)
//        assertEquals(170, viewModel.demand.proteins)
//        assertEquals(85, viewModel.demand.fats)
//        assertEquals(521, viewModel.demand.carbohydares)
//    }
}