package com.met.workout


import android.app.Dialog
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.impilo.data.Gender
import com.met.impilo.data.workouts.BodySide
import com.met.impilo.data.workouts.TrainingDay
import com.met.impilo.data.workouts.TrainingSystem
import com.met.impilo.data.workouts.Week
import com.met.impilo.utils.*
import com.met.workout.adapter.TrainingDayExercisesDetailsAdapter
import kotlinx.android.synthetic.main.fragment_workouts.*
import java.util.*


class WorkoutsFragment : Fragment() {

    private val TAG = javaClass.simpleName
    private lateinit var viewModel: WorkoutsFragmentViewModel
    private val selectedDate = Date()
    private lateinit var registrationDate: Date
    private lateinit var currentWeek: Week
    private lateinit var trainingSystem: TrainingSystem
    private lateinit var loadingDialog: Dialog
    private lateinit var gender: Gender
    private lateinit var exercisesAdapter : TrainingDayExercisesDetailsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = ViewUtils.createLoadingDialog(context!!)!!

        viewModel = ViewModelProvider(this).get(WorkoutsFragmentViewModel::class.java)
        viewModel.fetchRegistrationDate()
        viewModel.fetchTrainingPlanInfo()
        viewModel.fetchGender()

        exercisesAdapter = TrainingDayExercisesDetailsAdapter()
        training_day_exercises_details_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exercisesAdapter
        }

        workout_date_picker.timelineView.setBackgroundColor(ContextCompat.getColor(context!!, com.met.impilo.R.color.colorPrimaryDark))

        workout_date_picker.monthView.colorBeforeSelection = ContextCompat.getColor(context!!, com.met.impilo.R.color.actionBarIcon)
        workout_date_picker.monthView.defaultColor = ContextCompat.getColor(context!!, com.met.impilo.R.color.actionBarIcon)

        workout_date_picker.setOnDateSelectedListener { year, month, day, index ->
            val date: Date = "$year ${month + 1} $day".stringFromIntToDate()!!
            val dayOfWeek = viewModel.getCurrentWeekDay(date)

            val calendar = Calendar.getInstance()
            calendar.time = date

            val selectedWeek = if (::trainingSystem.isInitialized && trainingSystem == TrainingSystem.AB) {
                if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) Week.B
                else Week.A
            } else Week.A

            action_bar_title.text = if (date.toId() == selectedDate.toId()) getString(com.met.impilo.R.string.today)
            else date.toStringDate()
            loadingDialog.show()
            viewModel.fetchTrainingDay(date, dayOfWeek, selectedWeek)
        }

        nested_scroll_view.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.i(TAG, "ScrollX : $scrollX | ScrollY : $scrollY")
            if(scrollY > 300)
                start_workout_fab.shrink()
            else
                start_workout_fab.extend()
        })

        viewModel.getRegistrationDate().observe(this, androidx.lifecycle.Observer {
            Log.i(TAG, "Registration date : year ${it.getIntYear()}, month ${it.getIntMonth()} , day ${it.getIntDay()}")
            workout_date_picker.setFirstVisibleDate(it.getIntYear(), it.getIntMonth() - 1, it.getIntDay())
            registrationDate = it

            val calendar = Calendar.getInstance()
            calendar.time = registrationDate

            setTimeLineData(it)
        })

        viewModel.getTrainingPlanInfo().observe(this, androidx.lifecycle.Observer {
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate

            trainingSystem = it.trainingSystem

            currentWeek = if (trainingSystem == TrainingSystem.AB) if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) Week.B else Week.A
            else Week.A

            action_bar_title.text = getString(com.met.impilo.R.string.today)

            viewModel.fetchTrainingDay(selectedDate, viewModel.getCurrentWeekDay(selectedDate), currentWeek)
        })

        viewModel.getTrainingDay().observe(this, androidx.lifecycle.Observer {
            Log.i(TAG, "Clicked trainingDay : $it")
            if (it.isRestDay) {
                rest_day_layout.visibility = View.VISIBLE
                workout_day_layout.visibility = View.GONE
                start_workout_fab.visibility = View.GONE
                loadingDialog.dismiss()
            } else {
                rest_day_layout.visibility = View.GONE
                workout_day_layout.visibility = View.VISIBLE
                start_workout_fab.visibility = View.VISIBLE

                var title = ""
                for (x in 0 until it.muscleSetsNames.size) {
                    title += if (x != it.muscleSetsNames.size - 1) "${it.muscleSetsNames[x].toUpperCase()} & "
                    else it.muscleSetsNames[x].toUpperCase()
                }

                workout_day_title.text = title
                markMuscles(it)

                exercisesAdapter.exercises = it.exercises
                exercisesAdapter.notifyDataSetChanged()

                loadingDialog.dismiss()
            }
        })

        viewModel.getGender().observe(this, androidx.lifecycle.Observer {
            gender = it
        })

    }

    private fun markMuscles(trainingDay: TrainingDay) {
        var onlyFront = true
        var onlyBack = true

        trainingDay.exercises.forEach { exercise ->
            exercise.mainMuscle.forEach {
                if (it.bodySide == BodySide.BACK) onlyFront = false
                else onlyBack = false
            }

            exercise.supportMuscles?.forEach {
                if (it.bodySide == BodySide.BACK) onlyFront = false
                else onlyBack = false
            }
        }

        when {
            onlyFront -> {
                val drawableRes = if (gender == Gender.MALE) com.met.impilo.R.drawable.muscles_man_front
                else com.met.impilo.R.drawable.muscles_woman_front

                muscle_map_left_image_view.visibility = View.GONE
                MarkMuscles.setByTrainingDay(context!!, drawableRes, muscle_map_right_image_view, trainingDay)
            }
            onlyBack -> {
                val drawableRes = if (gender == Gender.MALE) com.met.impilo.R.drawable.muscles_man_back
                else com.met.impilo.R.drawable.muscles_woman_back

                muscle_map_left_image_view.visibility = View.GONE
                MarkMuscles.setByTrainingDay(context!!, drawableRes, muscle_map_right_image_view, trainingDay)
            }
            else -> {
                val drawableLeftRes = if (gender == Gender.MALE) com.met.impilo.R.drawable.muscles_man_front
                else com.met.impilo.R.drawable.muscles_woman_front

                val drawableRightRes = if (gender == Gender.MALE) com.met.impilo.R.drawable.muscles_man_back
                else com.met.impilo.R.drawable.muscles_woman_back

                muscle_map_left_image_view.visibility = View.VISIBLE
                MarkMuscles.setByTrainingDay(context!!, drawableLeftRes, muscle_map_left_image_view, trainingDay)
                MarkMuscles.setByTrainingDay(context!!, drawableRightRes, muscle_map_right_image_view, trainingDay)
            }
        }
    }

    private fun setTimeLineData(registrationDate: Date) {
        workout_date_picker.setLastVisibleDate(selectedDate.getIntYear(), selectedDate.getIntMonth() + 2, selectedDate.getIntDay())

        workout_date_picker.setSelectedDate(selectedDate.getIntYear(), selectedDate.getIntMonth() - 1, selectedDate.getIntDay())
        workout_date_picker.centerOnSelection()

        workout_date_picker.setDateLabelAdapter { calendar, index ->
            if (calendar.get(Calendar.MONTH) == registrationDate.getIntMonth() - 1 && calendar.get(Calendar.DAY_OF_MONTH) == registrationDate.getIntDay()) "Registered"
            else if (::trainingSystem.isInitialized && trainingSystem == TrainingSystem.AB) if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) setLabel("B", com.met.impilo.R.color.secondAccent)
            else setLabel("A", com.met.impilo.R.color.colorAccent)
            else ""
        }

    }


    fun setLabel(text: String, colorRef: Int): SpannableString {
        val span = SpannableString(text)
        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, colorRef)), 0, span.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return span
    }

}
