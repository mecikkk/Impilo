package com.met.impilo


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hadiidbouk.charts.BarData
import com.hadiidbouk.charts.ChartProgressBar
import com.met.impilo.data.Demand
import com.met.impilo.data.Gender
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.workouts.BodySide
import com.met.impilo.data.workouts.TrainingDay
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.utils.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue
import kotlin.text.StringBuilder


class HomeFragment : Fragment() {

    private val TAG = javaClass.simpleName

    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var demand: Demand
    private lateinit var mealsSummary: MealsSummary
    private lateinit var gender: Gender
    private  var loadingDialog: Dialog? = null
    private lateinit var chart: ChartProgressBar
    private lateinit var trainingPlanInfo: TrainingPlanInfo
    private lateinit var trainingDay: TrainingDay
    lateinit var setUpWorkoutsListener: SetUpWorkoutsListener

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = view.findViewById(R.id.chart_weight_progress)

        loadingDialog?.dismiss()
        loadingDialog = createLoadingDialog()

        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        viewModel.fetchGender()
        viewModel.fetchCompletedWorkouts()
        viewModel.fetchAllWeights()
        viewModel.fetchTrainingPlanInfo()

        loadingDialog?.show()

        start_workout_fab.setOnClickListener {
            val startActivity = Class.forName("com.met.workout.StartWorkoutActivity")
            val intent = Intent(context, startActivity)
            intent.putExtra("trainingDay", trainingDay)
            intent.putExtra("trainingPlanInfo", trainingPlanInfo)
            intent.putExtra("trainingDate", Date())
            startActivityForResult(intent, Const.START_WORKOUT_RESULT)
        }

        viewModel.isWorkoutConfigurationCompleted{
            loadingDialog?.dismiss()
            if (!it)
                configure_trainings_layout?.visibility = View.VISIBLE
             else
                configure_trainings_layout?.visibility = View.GONE

        }

        start_workout_configuration_fab.setOnClickListener {
            setUpWorkoutsListener.runConfiguration()
        }

        viewModel.getGender().observe(this, Observer {
            gender = it
        })

        viewModel.fetchDemand()

        val demandObserver = Observer<Demand> {
            if (it != null) {
                demand = it
                viewModel.fetchMealsSummary()
                loadingDialog?.dismiss()
            }
        }

        val mealsSummaryObserver = Observer<MealsSummary> {
            mealsSummary = it ?: MealsSummary()
            updateProgress()
        }

        viewModel.getDemand().observe(this, demandObserver)
        viewModel.getMealsSummary().observe(this, mealsSummaryObserver)

        viewModel.getTrainingPlanInfo().observe(this, Observer {
            trainingPlanInfo = it
            viewModel.fetchTrainingDay(it.trainingSystem)
        })

        viewModel.getTrainingDay().observe(this, Observer { trainingDay ->
            if (trainingDay.isRestDay) {
                training_day_layout.visibility = View.GONE
                rest_day_layout.visibility = View.VISIBLE


            } else {
                rest_day_layout.visibility = View.GONE
                training_day_layout.visibility = View.VISIBLE

                markMuscles(trainingDay)

                var muscleSetNames = ""
                trainingDay.muscleSetsNames.forEach {
                    muscleSetNames += "\u2022 ${it}\n"
                }

                training_muscles_set_data_text_view.text = muscleSetNames
            }

            this.trainingDay = trainingDay
            loadingDialog?.dismiss()
        })

        viewModel.getCompletedWorkouts().observe(this, Observer {
            if (it.contains(Date().toId())) {
                start_workout_fab.apply {
                    text = getString(R.string.workout_completed)
                    icon = ContextCompat.getDrawable(context!!, R.drawable.ic_accept)
                    isClickable = false
                }
            } else {
                start_workout_fab.apply {
                    text = getString(R.string.start)
                    icon = ContextCompat.getDrawable(context!!, R.drawable.ic_play)
                    isClickable = true
                }
            }
        })

        viewModel.getAllWeights().observe(this, Observer {
            updateWeightProgress(it)
            loadingDialog?.dismiss()
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
        }

        when {
            onlyFront -> {
                val drawableRes = if (gender == Gender.MALE) R.drawable.muscles_man_front
                else R.drawable.muscles_woman_front

                MarkMuscles.setOnlyMainMusclesByTrainingDay(context!!, drawableRes, muscle_map_left_image_view, trainingDay)
                MarkMuscles.setOnlyMainMusclesByTrainingDay(context!!, drawableRes, muscle_map_right_image_view, trainingDay)
            }
            onlyBack -> {
                val drawableRes = if (gender == Gender.MALE) R.drawable.muscles_man_back
                else R.drawable.muscles_woman_back

                muscle_map_left_image_view.visibility = View.GONE
                MarkMuscles.setOnlyMainMusclesByTrainingDay(context!!, drawableRes, muscle_map_left_image_view, trainingDay)
                MarkMuscles.setOnlyMainMusclesByTrainingDay(context!!, drawableRes, muscle_map_right_image_view, trainingDay)
            }
            else -> {
                val drawableLeftRes = if (gender == Gender.MALE) R.drawable.muscles_man_front
                else R.drawable.muscles_woman_front

                val drawableRightRes = if (gender == Gender.MALE) R.drawable.muscles_man_back
                else R.drawable.muscles_woman_back

                muscle_map_left_image_view.visibility = View.VISIBLE
                MarkMuscles.setOnlyMainMusclesByTrainingDay(context!!, drawableLeftRes, muscle_map_left_image_view, trainingDay)
                MarkMuscles.setOnlyMainMusclesByTrainingDay(context!!, drawableRightRes, muscle_map_right_image_view, trainingDay)
            }
        }
    }

    private fun updateWeightProgress(weights: List<Pair<Date, Float>>) {
        val data = ArrayList<BarData>()
        val months = DateFormatSymbols.getInstance().shortMonths
        val max = viewModel.getMaxWeight(weights)
        val min = viewModel.getMinWeight(weights)

        chart.setMaxValue(max - min + 0.2f)
        weights.forEach {
            val chartValue = it.second - min + 0.1f
            data.add(BarData("${it.first.getIntDay()} ${months[it.first.getIntMonth() - 1].toUpperCase()}", chartValue, "${it.second} kg"))
        }

        actual_weight_text_view.text = StringBuilder("${weights[weights.size - 1].second} kg")

        if (weights.size > 2) {
            if ((weights[weights.size - 1].second - weights[weights.size - 2].second) <= 0) ic_arrow_weight_diff.apply {
                setImageResource(R.drawable.ic_arrow_drop_down)
                imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.positiveGreen))
            }
            else ic_arrow_weight_diff.apply {
                setImageResource(R.drawable.ic_arrow_drop_up)
                imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
            }

            val weightDiff = BigDecimal((weights[weights.size - 1].second - weights[weights.size - 2].second).absoluteValue.toDouble()).setScale(1, RoundingMode.HALF_UP)
            weight_diff_text_view.text = StringBuilder("$weightDiff kg")

        } else {
            ic_arrow_weight_diff.apply {
                setImageResource(R.drawable.ic_arrow_drop_down)
                imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.positiveGreen))
            }
            weight_diff_text_view.text = StringBuilder("0 kg")
        }

        chart.setDataList(data)
        chart.build()

    }

    override fun onPause() {
        super.onPause()
        loadingDialog?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchDemand()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Const.START_WORKOUT_RESULT -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.getTrainingDay()
                    viewModel.getCompletedWorkouts()
                }
            }
        }
    }

    fun createLoadingDialog(): Dialog {
        val dialog = Dialog(context!!)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.attributes.windowAnimations = android.R.anim.fade_in
        return dialog
    }

    private fun updateProgress() {
        val caloriesPercent: Int = ((mealsSummary.kcalSum.toFloat() / demand.calories.toFloat()) * 100).toInt()
        val carboPercent = (mealsSummary.carbohydratesSum / demand.carbohydares) * 100
        val proteinsPercent = (mealsSummary.proteinsSum / demand.proteins) * 100
        val fatsPercent = (mealsSummary.fatsSum / demand.fats) * 100

        calories_percent_progress_text_view.text = StringBuilder("$caloriesPercent%")
        calories_progress.setProgress(caloriesPercent.toFloat(), true)
        calories_progress_text_view.text = StringBuilder("" + mealsSummary.kcalSum + " / " + demand.calories + " kcal")

        carbohydrates_progress.progress = carboPercent
        proteins_progress.progress = proteinsPercent
        fats_progress.progress = fatsPercent

    }

    interface SetUpWorkoutsListener {
        fun runConfiguration()
    }
}
