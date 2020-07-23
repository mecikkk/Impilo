package com.met.workout


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.impilo.data.workouts.Exercise
import com.met.impilo.data.workouts.ExerciseType
import com.met.workout.adapter.ExerciseDetailsAdapter
import kotlinx.android.synthetic.main.activity_start_workout.*
import kotlinx.android.synthetic.main.exercise_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_exercise_progress.*
import kotlinx.android.synthetic.main.fragment_exercise_progress.exercise_details_recycler_view
import kotlinx.android.synthetic.main.fragment_exercise_progress.radio_group
import kotlinx.android.synthetic.main.fragment_exercise_progress.reps_text_view
import kotlinx.android.synthetic.main.fragment_exercise_progress.sets_spinner
import kotlinx.android.synthetic.main.fragment_exercise_progress.sets_text_view


class ExerciseProgressFragment(var exercise: Exercise) : Fragment() {

    private lateinit var exerciseDetailsAdapter : ExerciseDetailsAdapter
    private var editMode = false

    companion object {
        @JvmStatic
        fun newInstance(exercise: Exercise) : ExerciseProgressFragment {
            return ExerciseProgressFragment(exercise)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exercise_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercise_name_text_view.text = exercise.name

        initExerciseDetails()
        checkEditMode()

        edit_button.setOnClickListener {
            editMode = !editMode
            checkEditMode()
        }
    }



    private fun checkEditMode() {
        if(exercise.exerciseType == ExerciseType.WITH_WEIGHT) {
            if (!editMode) {
                radio_group.visibility = View.GONE
                sets_spinner.visibility = View.GONE
                sets_text_view.visibility = View.GONE
            } else {
                radio_group.visibility = View.VISIBLE
                sets_spinner.visibility = View.VISIBLE
                sets_text_view.visibility = View.VISIBLE
            }
        } else {
            edit_button.visibility = View.INVISIBLE
        }
    }


    private fun initExerciseDetails() {

        exerciseDetailsAdapter = ExerciseDetailsAdapter(exercise.details, exercise.exerciseType)
        exercise_details_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseDetailsAdapter
        }

        val num: MutableList<Int> = mutableListOf()
        for (i in 0..25) num.add(i)

        sets_spinner.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, num)
        if (exercise.exerciseType == ExerciseType.WITH_WEIGHT) sets_spinner.setSelection(exerciseDetailsAdapter.exerciseDetails.sets)

        sets_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when {
                    position != 0 -> {
                        reps_text_view.visibility = View.VISIBLE
                        radio_group.visibility = View.VISIBLE
                        exerciseDetailsAdapter.exerciseDetails.sets = position
                    }
                    exercise.exerciseType == ExerciseType.WITH_WEIGHT -> {
                        reps_text_view.visibility = View.VISIBLE
                        radio_group.visibility = View.VISIBLE
                        sets_spinner.visibility = View.VISIBLE
                        sets_text_view.visibility = View.VISIBLE
                        exerciseDetailsAdapter.exerciseDetails.sets = position
                    }
                    else -> {
                        reps_text_view.visibility = View.GONE
                        radio_group.visibility = View.GONE
                        sets_spinner.visibility = View.GONE
                        sets_text_view.visibility = View.GONE
                        exerciseDetailsAdapter.exerciseDetails.sets = 1
                    }
                }
                exerciseDetailsAdapter.notifyDataSetChanged()
            }
        }

        if (!checkWeightHasOneValue()) {
            radio_group.check(R.id.different_weights_radio_button)
            exerciseDetailsAdapter.isOneWeightMode = false
            exerciseDetailsAdapter.notifyDataSetChanged()
        } else {
            radio_group.check(R.id.one_weight_radio_button)
            exerciseDetailsAdapter.isOneWeightMode = true
            exerciseDetailsAdapter.notifyDataSetChanged()
        }

        radio_group.setOnCheckedChangeListener { _, checkedId ->
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

        when(exercise.exerciseType){
                ExerciseType.WITH_WEIGHT -> {
                    reps_text_view.visibility = View.VISIBLE
                }
                else -> {
                    reps_text_view.visibility = View.GONE
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

}
