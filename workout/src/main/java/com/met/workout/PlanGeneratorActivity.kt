package com.met.workout

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.impilo.adapter.CircularDaysAdapter
import com.met.impilo.adapter.DayItemClickListener
import com.met.impilo.data.MusclesSet
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.data.workouts.WhereDoExercise
import com.met.impilo.data.workouts.generator.ExerciseLoad
import com.met.impilo.data.workouts.generator.GeneratedPlan
import com.met.impilo.data.workouts.generator.WorkoutType
import com.met.impilo.utils.Const
import com.met.impilo.utils.ShortTextWatcher
import com.met.workout.ownPlan.PlanCreatorActivity
import kotlinx.android.synthetic.main.activity_generate_plan.*

class PlanGeneratorActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    private lateinit var viewModel: PlanGeneratorViewModel
    private var trainingsPerWeek: Int = 0
    private var trainingInternship = -1
    private var equipment: WhereDoExercise = WhereDoExercise.HOME
    private var maxLoad = ExerciseLoad()
    private lateinit var trainingPlanInfo: TrainingPlanInfo
    private lateinit var generatedPlan: GeneratedPlan

    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_plan)


        viewModel = ViewModelProvider(this)[PlanGeneratorViewModel::class.java]
        loadingDialog = createLoadingDialog()

        days_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CircularDaysAdapter(this)
        days_recycler_view.adapter = adapter
        adapter.setDayNightStyle()

        adapter.setOnDayItemClickListener(object : DayItemClickListener {
            override fun onDayItemClick(position: Int) {
                trainingsPerWeek = position + 1
            }
        })

        equipment_radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.no_eq_radio_button -> {
                    equipment = WhereDoExercise.HOME
                    workout_load_layout.visibility = View.GONE
                }
                R.id.have_eq_radio_button -> {
                    workout_load_layout.visibility = View.VISIBLE
                    equipment = WhereDoExercise.HOME_WITH_EQ
                }
                R.id.gym_radio_button -> {
                    workout_load_layout.visibility = View.VISIBLE
                    equipment = WhereDoExercise.GYM
                }
            }
        }

        beginner_toggle.setOnClickListener { checkToggle(0) }
        middle_toggle.setOnClickListener { checkToggle(1) }
        long_toggle.setOnClickListener { checkToggle(2) }

        initTextWatchers()


        finish_generation_button.setOnClickListener {
            loadingDialog.show()
            viewModel.fetchPlan(trainingsPerWeek, trainingInternship, equipment, maxLoad)
        }

        viewModel.getGeneratedPlan().observe(this, Observer {
            generatedPlan = it
        })

        viewModel.getGeneratedTrainingPlanInfo().observe(this, Observer { trainingPlanInfo ->
            Log.w(TAG, "GENERATED TRAINING PLAN INFO")
            Log.w(TAG,
                "ActualWeek : ${trainingPlanInfo.actualWeek}\nTrainingSystem : ${trainingPlanInfo.trainingSystem}\nConfigFinished : ${trainingPlanInfo.isConfigurationCompleted}")
            trainingPlanInfo.weekA.forEach { trainingDay ->
                for (x in 0 until trainingDay.muscleSetsNames.size) {
                    trainingDay.muscleSetsNames[x] = getString(MusclesSet.valueOf(trainingDay.muscleSetsNames[x]).nameRef)
                }
                Log.d(TAG, "DAY ${trainingDay.id} | MusclesSetName : ${trainingDay.muscleSetsNames}")
                trainingDay.exercises.forEach {
                    Log.i(TAG, "Exercise : $it")
                }
            }

            this.trainingPlanInfo = trainingPlanInfo
            loadingDialog.hide()
            showFinishDialog()
        })
    }



    private fun showFinishDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.generated_plan_dialog, null)

        view.findViewById<TextView>(R.id.plan_name_text_view).text = getString(generatedPlan.workoutType.titleRef)

        view.findViewById<TextView>(R.id.description).text = when (generatedPlan.workoutType) {
            WorkoutType.FOUR_DAY_PUSH_PULL -> getString(com.met.impilo.R.string.push_pull_explanation)
            WorkoutType.FIVE_DAY_SPLIT, WorkoutType.THREE_DAY_SPLIT -> getString(com.met.impilo.R.string.split_explanation)
            WorkoutType.THREE_DAY_FBW -> getString(com.met.impilo.R.string.fbw_explanation)
            WorkoutType.FOUR_DAY_UP_DOWN -> getString(com.met.impilo.R.string.split_explanation)
            WorkoutType.THREE_DAY_FAT_LOSS -> "${getString(com.met.impilo.R.string.split_explanation)} ${getString(com.met.impilo.R.string.and_cardio)}"
        }

        val builder = AlertDialog.Builder(this).apply {
            setTitle(getString(com.met.impilo.R.string.your_plan))
            setView(view)
            setPositiveButton(getString(com.met.impilo.R.string.next)) { _, _ ->
                val intent = Intent(context, PlanCreatorActivity::class.java)
                intent.putExtra("generatedTrainingPlan", trainingPlanInfo)
                startActivityForResult(intent, Const.EDIT_GENERATED_PLAN_REQUEST)

                Log.e(TAG, "Finishing generator")

            }
            setNegativeButton(getString(com.met.impilo.R.string.reject_training)) { _, _ ->

                Log.e(TAG, "Finishing canceled")
            }
        }
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(com.met.impilo.R.drawable.dialog_rounded_corner)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, com.met.impilo.R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, com.met.impilo.R.color.textColor))
    }

    private fun createLoadingDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(com.met.impilo.R.layout.loading_dialog)
        dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.attributes.windowAnimations = android.R.anim.fade_in
        return dialog
    }

    private fun initTextWatchers() {
        bench_press_input_edit_text.addTextChangedListener(setChestTextWatcher())
        squat_input_edit_text.addTextChangedListener(setLegsTextWatcher())
        dumbbell_curl_input_edit_text.addTextChangedListener(setBicepsTextWatcher())
        dumbbell_front_raise_input_edit_text.addTextChangedListener(setShouldersTextWatcher())
        dumbbell_triceps_extension_input_edit_text.addTextChangedListener(setTricepsTextWatcher())
    }

    private fun setChestTextWatcher(): TextWatcher = object : ShortTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            setFinishButtonVisibility()
            if (!s.isNullOrEmpty() && s.toString() != ".") {
                maxLoad.barbellBenchPress = s.toString().toFloat()
            }
        }
    }

    private fun setBicepsTextWatcher(): TextWatcher = object : ShortTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            setFinishButtonVisibility()
            if (!s.isNullOrEmpty() && s.toString() != ".") {
                maxLoad.dumbbellCurl = s.toString().toFloat()
            }
        }
    }

    private fun setLegsTextWatcher(): TextWatcher = object : ShortTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            setFinishButtonVisibility()
            if (!s.isNullOrEmpty() && s.toString() != ".") {
                maxLoad.barbellSquat = s.toString().toFloat()
            }
        }
    }

    private fun setShouldersTextWatcher(): TextWatcher = object : ShortTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            setFinishButtonVisibility()
            if (!s.isNullOrEmpty() && s.toString() != ".") {
                maxLoad.dumbbellFrontRaise = s.toString().toFloat()
            }
        }
    }

    private fun setTricepsTextWatcher(): TextWatcher = object : ShortTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            setFinishButtonVisibility()
            if (!s.isNullOrEmpty() && s.toString() != ".") {
                maxLoad.dumbbellTricepsExtension = s.toString().toFloat()
            }
        }
    }

    private fun setFinishButtonVisibility() {
        if (bench_press_input_edit_text.text.isNullOrEmpty() || squat_input_edit_text.text.isNullOrEmpty() || dumbbell_curl_input_edit_text.text.isNullOrEmpty() || dumbbell_front_raise_input_edit_text.text.isNullOrEmpty() || dumbbell_triceps_extension_input_edit_text.text.isNullOrEmpty()) finish_generation_button.hide()
        else finish_generation_button.show()
    }

    private fun checkToggle(i: Int) {

        trainingInternship = i

        beginner_toggle.isChecked = i == 0
        middle_toggle.isChecked = i == 1
        long_toggle.isChecked = i == 2

        equipment_layout.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Const.EDIT_GENERATED_PLAN_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val trainingPlanInfo = data?.getSerializableExtra("trainingPlanInfo") as TrainingPlanInfo

                    Log.d(TAG, "Received planInfo : $trainingPlanInfo")

                    viewModel.addAllTrainingDays(trainingPlanInfo) {
                        if (it == 6) viewModel.addTrainingPlanInfo(trainingPlanInfo) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                }
            }
        }
    }

}
