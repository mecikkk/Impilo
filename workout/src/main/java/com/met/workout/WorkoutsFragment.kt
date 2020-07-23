package com.met.workout


import android.app.Activity
import android.app.Dialog
import android.content.Intent
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
import com.met.impilo.data.workouts.*
import com.met.impilo.utils.*
import com.met.workout.adapter.TrainingDayExercisesDetailsAdapter
import kotlinx.android.synthetic.main.fragment_workouts.*
import java.util.*


class WorkoutsFragment : Fragment() {

    private val TAG = javaClass.simpleName
    private lateinit var viewModel: WorkoutsFragmentViewModel
    private var selectedDate = Date()
    private lateinit var selectedTrainingDay: TrainingDay
    private lateinit var traininPlanInfo : TrainingPlanInfo
    private lateinit var registrationDate: Date
    private lateinit var currentWeek: Week
    private var selectedWeek : Week = Week.A
    private var wasWorkoutDone = false
    private lateinit var trainingSystem: TrainingSystem
    private lateinit var loadingDialog: Dialog
    private lateinit var gender: Gender
    private lateinit var exercisesAdapter : TrainingDayExercisesDetailsAdapter
    private var shouldShowFab = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = ViewUtils.createLoadingDialog(context!!)!!
        loadingDialog.dismiss()

        viewModel = ViewModelProvider(this).get(WorkoutsFragmentViewModel::class.java)
        viewModel.fetchRegistrationDate()
        viewModel.fetchTrainingPlanInfo(selectedDate)
        viewModel.fetchGender()

        exercisesAdapter = TrainingDayExercisesDetailsAdapter()
        training_day_exercises_details_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exercisesAdapter
        }

        workout_date_picker.timelineView.setBackgroundColor(ContextCompat.getColor(context!!, com.met.impilo.R.color.colorPrimaryDark))

        workout_date_picker.monthView.colorBeforeSelection = ContextCompat.getColor(context!!, com.met.impilo.R.color.actionBarIcon)
        workout_date_picker.monthView.defaultColor = ContextCompat.getColor(context!!, com.met.impilo.R.color.actionBarIcon)

        workout_date_picker.setOnDateSelectedListener { year, month, day, _ ->
            selectedDate = "$year ${month + 1} $day".stringFromIntToDate()!!

            val calendar = Calendar.getInstance()
            calendar.time = selectedDate

            selectedWeek = if (::trainingSystem.isInitialized && trainingSystem == TrainingSystem.AB) {
                if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) Week.B
                else Week.A
            } else Week.A

            if (selectedDate.toId() == Date().toId()) {
                action_bar_title.text = getString(com.met.impilo.R.string.today)
                shouldShowFab = true
            } else {
                action_bar_title.text = selectedDate.toStringDate()
                shouldShowFab = false
            }
            loadingDialog.show()
            viewModel.fetchTrainingPlanInfo(selectedDate)
        }

        nested_scroll_view.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            if(scrollY > 300)
                start_workout_fab.shrink()
            else
                start_workout_fab.extend()
        })

        start_workout_fab.setOnClickListener {
            val intent = Intent(context, StartWorkoutActivity::class.java)
            intent.putExtra("trainingDay", selectedTrainingDay)
            intent.putExtra("trainingPlanInfo", traininPlanInfo)
            intent.putExtra("trainingDate", selectedDate)
            startActivityForResult(intent, Const.START_WORKOUT_RESULT)
        }

        viewModel.getRegistrationDate().observe(this, androidx.lifecycle.Observer {
            workout_date_picker.setFirstVisibleDate(it.getIntYear(), it.getIntMonth() - 1, it.getIntDay())
            registrationDate = it

            val calendar = Calendar.getInstance()
            calendar.time = registrationDate

            viewModel.fetchCompletedWorkoutsDates()
        })

        viewModel.getTrainingPlanInfo().observe(this, androidx.lifecycle.Observer {
            val calendar = Calendar.getInstance()
            calendar.time = Date()

            traininPlanInfo = it

            trainingSystem = it.trainingSystem

            currentWeek = if (trainingSystem == TrainingSystem.AB) if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) Week.B else Week.A
            else Week.A

            shouldShowFab = (selectedDate.toId() != it.date.toId()) && (selectedDate.toId() == Date().toId())
            wasWorkoutDone = !((selectedDate.toId() != it.date.toId()) && (selectedDate.toId() == Date().toId()))

            viewModel.fetchTrainingDay(selectedDate, selectedWeek)
        })

        viewModel.getTrainingDay().observe(this, androidx.lifecycle.Observer {
            if (it.isRestDay) {
                rest_day_layout.visibility = View.VISIBLE
                workout_day_layout.visibility = View.GONE
                start_workout_fab.visibility = View.GONE
                loadingDialog.dismiss()
            } else {
                rest_day_layout.visibility = View.GONE
                workout_day_layout.visibility = View.VISIBLE
                // should show fab ?
                if(shouldShowFab)
                    start_workout_fab.visibility = View.VISIBLE
                else
                    start_workout_fab.visibility = View.GONE

                var title = ""
                for (x in 0 until it.muscleSetsNames.size) {
                    title += if (x != it.muscleSetsNames.size - 1) "${it.muscleSetsNames[x].toUpperCase()} & "
                    else it.muscleSetsNames[x].toUpperCase()
                }

                workout_day_title.text = title
                markMuscles(it)

                exercisesAdapter.exercises = it.exercises
                exercisesAdapter.notifyDataSetChanged()

                selectedTrainingDay = it
                loadingDialog.dismiss()
            }
        })

        viewModel.getGender().observe(this, androidx.lifecycle.Observer {
            gender = it
        })

        viewModel.getCompletedWorkoutsDates().observe(this, androidx.lifecycle.Observer {
            setTimeLineData(it)
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

    private fun setTimeLineData(completedWorkouts: List<String>) {
        workout_date_picker.setLastVisibleDate(Date().getIntYear(), Date().getIntMonth() + 2, Date().getIntDay())

        workout_date_picker.setSelectedDate(Date().getIntYear(), Date().getIntMonth() - 1, Date().getIntDay())
        workout_date_picker.centerOnSelection()

        workout_date_picker.setDateLabelAdapter { calendar, _ ->
            val date = calendar.time
            if (calendar.get(Calendar.MONTH) == registrationDate.getIntMonth() - 1 && calendar.get(Calendar.DAY_OF_MONTH) == registrationDate.getIntDay()) "Registered"
            else if (::trainingSystem.isInitialized && trainingSystem == TrainingSystem.AB) {
                if(!completedWorkouts.contains(date.toId())) {
                    if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) setLabel("B", com.met.impilo.R.color.actionBarIcon)
                    else setLabel("A", android.R.color.white)
                } else {
                    setLabel("\u2713", com.met.impilo.R.color.colorAccent)
                }
            }
            else if(completedWorkouts.contains(date.toId())) setLabel("\u2713", com.met.impilo.R.color.colorAccent)
            else ""


        }

    }


    fun setLabel(text: String, colorRef: Int): SpannableString {
        val span = SpannableString(text)
        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, colorRef)), 0, span.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return span
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
                Const.START_WORKOUT_RESULT -> {
                    if(resultCode == Activity.RESULT_OK){
                        //REFRESH
                        viewModel.fetchTrainingPlanInfo(selectedDate)
                    }
                }
        }

    }

}
