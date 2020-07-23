package com.met.workout.ownPlan

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.met.impilo.data.Gender
import com.met.impilo.data.WorkoutStyle
import com.met.impilo.data.workouts.BodySide
import com.met.impilo.data.workouts.Exercise
import com.met.impilo.data.workouts.ExerciseType
import com.met.impilo.utils.MarkMuscles
import com.met.workout.R
import com.met.workout.adapter.ExerciseDetailsAdapter
import kotlinx.android.synthetic.main.exercise_bottom_sheet.*

class ExerciseBottomSheet(private val gender: Gender, private val weight: Float, private val workoutIntensity: WorkoutStyle) : BottomSheetDialogFragment() {

    private val TAG = javaClass.simpleName

    lateinit var callback: OnAddExerciseListener
    private lateinit var behavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetView: View
    private lateinit var exerciseDetailsAdapter: ExerciseDetailsAdapter

    lateinit var exercise: Exercise

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet: BottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetView = View.inflate(context, R.layout.exercise_bottom_sheet, null)

        bottomSheet.setContentView(bottomSheetView)
        behavior = BottomSheetBehavior.from(bottomSheetView.parent as View)

        behavior.peekHeight = (Resources.getSystem().displayMetrics.heightPixels)
        bottomSheetView.layoutParams.height = (Resources.getSystem().displayMetrics.heightPixels)

        behavior.addBottomSheetCallback(getBottomSheetBehavior())

        bottomSheet.action_bar_title.text = exercise.name

        exercise.kcalPerHour = setCaloriesInfo(bottomSheet)

        setMainMusclesInfo(bottomSheet)

        setSupportMusclesInfo(bottomSheet)

        setExercisePlaceInfo(bottomSheet)

        markMuscles(bottomSheet)

        initExerciseDetails(bottomSheet)

        bottomSheet.add_exercise_toolbar_button.setOnClickListener {

            if (exercise.exerciseType == ExerciseType.WITH_WEIGHT) {
                for (x in (exercise.details.reps.size - 1) downTo exercise.details.sets) {
                    if ((exercise.details.reps.size - 1) <= x) exercise.details.reps.removeAt(x)
                }

                for (x in (exercise.details.weight.size - 1) downTo exercise.details.sets) {
                    if ((exercise.details.weight.size - 1) <= x) exercise.details.weight.removeAt(x)
                }
            }
            try {
                callback.onExerciseAdd(exercise)
                dismiss()
            } catch (e: Exception) {
                Log.e(TAG, "OnAddExerciseListener must be initialized !")
            }
        }

        return bottomSheet
    }

    private fun initExerciseDetails(bottomSheet: BottomSheetDialog) {

        exerciseDetailsAdapter = ExerciseDetailsAdapter(exercise.details, exercise.exerciseType)
        bottomSheet.exercise_details_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@ExerciseBottomSheet.context!!)
            adapter = exerciseDetailsAdapter
        }

        val num: MutableList<Int> = mutableListOf()
        for (i in 0..25) num.add(i)

        bottomSheet.sets_spinner.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, num)
        if (exercise.exerciseType == ExerciseType.WITH_WEIGHT) bottomSheet.sets_spinner.setSelection(exerciseDetailsAdapter.exerciseDetails.sets)

        bottomSheet.sets_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when {
                    position != 0 -> {
                        bottomSheet.reps_text_view.visibility = View.VISIBLE
                        bottomSheet.radio_group.visibility = View.VISIBLE
                        exerciseDetailsAdapter.exerciseDetails.sets = position
                    }
                    exercise.exerciseType == ExerciseType.WITH_WEIGHT -> {
                        bottomSheet.reps_text_view.visibility = View.VISIBLE
                        bottomSheet.radio_group.visibility = View.VISIBLE
                        bottomSheet.sets_spinner.visibility = View.VISIBLE
                        bottomSheet.sets_text_view.visibility = View.VISIBLE
                        exerciseDetailsAdapter.exerciseDetails.sets = position
                    }
                    else -> {
                        bottomSheet.reps_text_view.visibility = View.GONE
                        bottomSheet.radio_group.visibility = View.GONE
                        bottomSheet.sets_spinner.visibility = View.GONE
                        bottomSheet.sets_text_view.visibility = View.GONE
                        exerciseDetailsAdapter.exerciseDetails.sets = 1
                    }
                }
                exerciseDetailsAdapter.notifyDataSetChanged()
            }
        }

        if (!checkWeightHasOneValue()) {
            bottomSheet.radio_group.check(R.id.different_weights_radio_button)
            exerciseDetailsAdapter.isOneWeightMode = false
            exerciseDetailsAdapter.notifyDataSetChanged()
        } else {
            bottomSheet.radio_group.check(R.id.one_weight_radio_button)
            exerciseDetailsAdapter.isOneWeightMode = true
            exerciseDetailsAdapter.notifyDataSetChanged()
        }

        bottomSheet.radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.one_weight_radio_button -> {
                    exerciseDetailsAdapter.isOneWeightMode = true
                    exerciseDetailsAdapter.notifyDataSetChanged()
                }
                R.id.different_weights_radio_button -> {
                    exerciseDetailsAdapter.isOneWeightMode = false
                    exerciseDetailsAdapter.notifyDataSetChanged()
                }
            }
        }

    }

    private fun checkWeightHasOneValue(): Boolean {
        if (exercise.details.weight.isNullOrEmpty() || exercise.details.weight.size == 1) return true

        val tmp = exercise.details.weight[0]

        for (x in 1 until exercise.details.weight.size) {
            if (exercise.details.weight[x] != tmp) return false
        }

        return true
    }

    private fun markMuscles(bottomSheet: BottomSheetDialog) {
        var onlyFront = true
        var onlyBack = true
        exercise.mainMuscle.forEach {
            if (it.bodySide == BodySide.BACK) onlyFront = false
            else onlyBack = false
        }
        exercise.supportMuscles?.forEach {
            if (it.bodySide == BodySide.BACK) onlyFront = false
            else onlyBack = false
        }

        when {
            onlyFront -> {
                val drawableRes = if (gender == Gender.MALE) com.met.impilo.R.drawable.muscles_man_front
                else com.met.impilo.R.drawable.muscles_woman_front

                MarkMuscles.set(context!!, drawableRes, bottomSheet.muscle_map_left_image_view, exercise)
                MarkMuscles.set(context!!, drawableRes, bottomSheet.muscle_map_right_image_view, exercise)
                bottomSheet.muscle_map_divider.visibility = View.GONE
            }
            onlyBack -> {
                val drawableRes = if (gender == Gender.MALE) com.met.impilo.R.drawable.muscles_man_back
                else com.met.impilo.R.drawable.muscles_woman_back

                MarkMuscles.set(context!!, drawableRes, bottomSheet.muscle_map_left_image_view, exercise)
                MarkMuscles.set(context!!, drawableRes, bottomSheet.muscle_map_right_image_view, exercise)
                bottomSheet.muscle_map_divider.visibility = View.GONE
            }
            else -> {
                val drawableLeftRes = if (gender == Gender.MALE) com.met.impilo.R.drawable.muscles_man_front
                else com.met.impilo.R.drawable.muscles_woman_front

                val drawableRightRes = if (gender == Gender.MALE) com.met.impilo.R.drawable.muscles_man_back
                else com.met.impilo.R.drawable.muscles_woman_back

                MarkMuscles.set(context!!, drawableLeftRes, bottomSheet.muscle_map_left_image_view, exercise)
                MarkMuscles.set(context!!, drawableRightRes, bottomSheet.muscle_map_right_image_view, exercise)
                bottomSheet.muscle_map_divider.visibility = View.VISIBLE
            }
        }
    }

    private fun setExercisePlaceInfo(bottomSheet: BottomSheetDialog) {
        if (exercise.exercisePlace.isNullOrEmpty()) bottomSheet.where_do_data_tex_view.text = getString(com.met.impilo.R.string.no_place_exercise)
        else bottomSheet.where_do_data_tex_view.text = getString(exercise.exercisePlace!![0].nameRef)
    }

    private fun setSupportMusclesInfo(bottomSheet: BottomSheetDialog) {
        if (exercise.supportMuscles.isNullOrEmpty()) {
            bottomSheet.support_muscles_set_text_view.visibility = View.GONE
            bottomSheet.support_muscles_data_tex_view.visibility = View.GONE
        } else {
            var supportMuscles = ""
            exercise.supportMuscles?.forEach {
                supportMuscles += if (exercise.supportMuscles!!.size <= 1) context?.getString(it.nameRef)
                else "${context?.getString(it.nameRef)}, "
            }
            bottomSheet.support_muscles_data_tex_view.text = supportMuscles
        }
    }

    private fun setMainMusclesInfo(bottomSheet: BottomSheetDialog) {
        var mainMuscles = ""
        exercise.mainMuscle.forEach {
            mainMuscles += if (exercise.mainMuscle.size <= 1) context?.getString(it.nameRef)
            else "${context?.getString(it.nameRef)}, "
        }
        bottomSheet.main_muscles_data_tex_view.text = mainMuscles
    }

    private fun setCaloriesInfo(bottomSheet: BottomSheetDialog): Int {
        val calories = if (exercise.kcalPerHour != 0) ((exercise.kcalPerHour).toFloat() * (weight / 100)).toInt()
        else {
            when (workoutIntensity) {
                WorkoutStyle.LIGHT -> ((350).toFloat() * (weight / 100)).toInt()
                WorkoutStyle.MODERATE -> ((700).toFloat() * (weight / 100)).toInt()
                WorkoutStyle.HEAVY -> ((850).toFloat() * (weight / 100)).toInt()
            }
        }
        bottomSheet.calories_burned_data_tex_view.text = StringBuilder("$calories kcal / h")
        return calories
    }


    private fun getBottomSheetBehavior() = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(view: View, i: Int) {

            when (BottomSheetBehavior.STATE_HIDDEN) {
                i -> dismiss()
            }
        }

        override fun onSlide(view: View, v: Float) {
        }
    }


    /**
     * Zostala nadpisana metoda show() aby aktywnsoc nie zapisywala stanu (commitAllowinStateLoss)
     */
    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val transaction = manager.beginTransaction()
            transaction.add(this, tag)
            transaction.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }


    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    interface OnAddExerciseListener {
        fun onExerciseAdd(exercise: Exercise)
    }
}