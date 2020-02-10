package com.met.workout

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.met.impilo.data.workouts.TrainingDay
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.utils.ViewUtils
import com.met.workout.adapter.ExerciseProgressDetailsAdapter
import kotlinx.android.synthetic.main.activity_my_own_plan.*
import kotlinx.android.synthetic.main.activity_start_workout.*
import kotlinx.android.synthetic.main.finish_workout_dialog.view.*
import java.util.*


class StartWorkoutActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private lateinit var viewModel: StartWorkoutActivityViewModel
    private lateinit var clockHandler: Handler
    private lateinit var visibilityHandler : Handler
    private lateinit var exerciseFragmentAdapter : ExerciseProgressDetailsAdapter
    private lateinit var loadingDialog: Dialog
    private var isClockVisible = true
    private var paused = false

    // TODO : Dialog przy kliknieciu na zakonczenie z informacja o czasie treningu i pytaniem o to czy na pewno zakonczyc (nadpisac tez onBackPress)
    // TODO : Po zatwierdzeniu jakis loading i w tym czasie wysylanie danych na serwer
    // TODO : Po wyslaniu back do WorkoutsFragment i tam walnac jakis update

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_workout)

        viewModel = ViewModelProvider(this)[StartWorkoutActivityViewModel::class.java]

        viewModel.trainingDay = intent.getSerializableExtra("trainingDay") as TrainingDay
        viewModel.trainingPlanInfo = intent.getSerializableExtra("trainingPlanInfo") as TrainingPlanInfo
        viewModel.workoutDate = intent.getSerializableExtra("trainingDate") as Date
        viewModel.trainingPlanInfo.date = viewModel.workoutDate

        loadingDialog = createLoadingDialog()

        exerciseFragmentAdapter = ExerciseProgressDetailsAdapter(supportFragmentManager, viewModel.trainingDay)
        exercises_progress_view_pager.adapter = exerciseFragmentAdapter
        indicator.setViewPager(exercises_progress_view_pager)

        viewModel.startTime = SystemClock.uptimeMillis()
        clockHandler = Handler()
        visibilityHandler = Handler()

        clockHandler.postDelayed(clockRunnable, 0)

        play_pause_button.setOnClickListener {
            Log.i(TAG, "${viewModel.trainingDay.exercises[0].details.weight}")
            if(!paused){
                viewModel.timeBuff += viewModel.milliSecTime
                clockHandler.removeCallbacks(clockRunnable)
                visibilityHandler.postDelayed(visibilityRunnable, 0)
                play_pause_button.setImageResource(com.met.impilo.R.drawable.ic_play_circle)
                paused = true
            } else {
                minutes_timer_text_view.visibility = View.VISIBLE
                seconds_timer_text_view.visibility = View.VISIBLE
                play_pause_button.setImageResource(com.met.impilo.R.drawable.ic_pause_circle)
                viewModel.startTime = SystemClock.uptimeMillis()
                clockHandler.postDelayed(clockRunnable, 0)
                visibilityHandler.removeCallbacks(visibilityRunnable)
                paused = false
            }
        }

        finish_workout_fab.setOnClickListener {
            showFinishDialog()
        }

    }

    fun showFinishDialog() {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.finish_workout_dialog, null)

        viewModel.timeBuff += viewModel.milliSecTime
        clockHandler.removeCallbacks(clockRunnable)

        var seconds = (viewModel.updateTime/ 1000)
        val minutes = (seconds / 60)
        seconds %= 60

        var avg = 0
        val allKcal = mutableListOf<Int>()
        viewModel.trainingDay.exercises.forEach {
            allKcal.add(it.kcalPerHour)
            Log.i(TAG, "allKcal : ${it.kcalPerHour}")
        }

        allKcal.distinct().forEach {
            avg += it
            Log.i(TAG, "kcal distinct : $it")
        }
        Log.i(TAG, "kcal distinct size : ${allKcal.distinct().size}")
        var kcal = (avg/allKcal.distinct().size)

        val timeFormat = String.format("%d.%02d", minutes, seconds).toFloat()
        Log.i(TAG, "Formated time : $timeFormat")

        kcal = ((kcal * timeFormat) / 60).toInt()

        view.findViewById<TextView>(R.id.workout_duration_data_text_view).text = StringBuilder("$timeFormat min")
        view.findViewById<TextView>(R.id.workout_kcal_data_text_view).text = StringBuilder("$kcal kcal")


        val builder = AlertDialog.Builder(this).apply {
            setTitle(getString(com.met.impilo.R.string.end_of_training))
            setView(view)
            setPositiveButton(getString(com.met.impilo.R.string.finish)) { _, _ ->
                loadingDialog.show()
                viewModel.trainingDay.caloriesBurned = kcal
                viewModel.trainingDay.trainingDuration = timeFormat
                viewModel.addTrainingDone {
                    if(it){
                        loadingDialog.dismiss()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        loadingDialog.dismiss()
                        ViewUtils.createSnackbar(start_workout_container, getString(com.met.impilo.R.string.finishing_wokrout_error))
                    }
                }

                // TODO : zablokowac przycisk + jakies info ze dany trening byl zrobiony i tam gdzie jest historia dac wykres (jak bedzie szybki do zrobienia).
                // TODO : Ogarnac UI  z treningiem na ekranie glownym + ekran profilu/dodawanie poiarow ciala

                Log.e(TAG, "Finishing workout")

            }
            setNegativeButton(getString(com.met.impilo.R.string.cancel)) { _, _ ->
                viewModel.startTime = SystemClock.uptimeMillis()
                clockHandler.postDelayed(clockRunnable, 0)
                Log.e(TAG, "Workout finishing canceled")
            }
            setNeutralButton(getString(com.met.impilo.R.string.reject_training)) { _, _ ->
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
        val dialog = builder.create()
        dialog.show()
        dialog.setOnCancelListener {
            viewModel.startTime = SystemClock.uptimeMillis()
            clockHandler.postDelayed(clockRunnable, 0)
        }
        dialog.window?.setBackgroundDrawableResource(com.met.impilo.R.drawable.dialog_rounded_corner)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, com.met.impilo.R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, com.met.impilo.R.color.textColor))
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(this, com.met.impilo.R.color.actionBarIcon))
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).gravity = Gravity.START
    }

    fun createLoadingDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(com.met.impilo.R.layout.loading_dialog)
        dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.attributes.windowAnimations = android.R.anim.fade_in
        return dialog
    }

    override fun onBackPressed() {
        showFinishDialog()
    }


    private var clockRunnable : Runnable = object : Runnable {
        override fun run() {
            viewModel.milliSecTime = SystemClock.uptimeMillis() - viewModel.startTime
            viewModel.updateTime = viewModel.timeBuff + viewModel.milliSecTime

            var seconds = (viewModel.updateTime/ 1000)
            val minutes = (seconds / 60)
            seconds %= 60

            if(minutes < 10)
                minutes_timer_text_view.text = StringBuilder("0$minutes")
            else
                minutes_timer_text_view.text = StringBuilder("$minutes")

            if(seconds < 10)
                seconds_timer_text_view.text = StringBuilder("0$seconds")
            else
                seconds_timer_text_view.text = StringBuilder("$seconds")

            clockHandler.postDelayed(this, 0)
        }

    }

    private var visibilityRunnable : Runnable = object : Runnable {
        override fun run() {

            if(isClockVisible) {
                minutes_timer_text_view.visibility = View.INVISIBLE
                seconds_timer_text_view.visibility = View.INVISIBLE
                isClockVisible = false
            } else {
                minutes_timer_text_view.visibility = View.VISIBLE
                seconds_timer_text_view.visibility = View.VISIBLE
                isClockVisible = true
            }

            visibilityHandler.postDelayed(this, 800)
        }

    }

}

