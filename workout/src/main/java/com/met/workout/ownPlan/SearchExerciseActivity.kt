package com.met.workout.ownPlan

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.met.impilo.data.Gender
import com.met.impilo.data.WorkoutStyle
import com.met.impilo.data.workouts.Exercise
import com.met.impilo.utils.Const
import com.met.workout.R
import com.met.workout.adapter.SearchExerciseAdapter
import kotlinx.android.synthetic.main.activity_search_exercise.*

class SearchExerciseActivity : AppCompatActivity() , SearchExerciseAdapter.OnExerciseClickListener , ExerciseBottomSheet.OnAddExerciseListener{

    private lateinit var viewModel : SearchExerciseActivityViewModel
    private val TAG = javaClass.simpleName
    private lateinit var gender : Gender
    private var weight : Float = 0f
    private lateinit var workoutStyle: WorkoutStyle
    private var selectedExerciseMuscleSetName = ""
    private var isBottomSheetInitialized = false

    private lateinit var exerciseBottomSheet: ExerciseBottomSheet

    @SuppressLint("UseSparseArrays")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_exercise)

        val lastMuscleSetIndex = intent.getIntExtra("lastMuscleSetIndex", 0)

        viewModel = ViewModelProvider(this).get(SearchExerciseActivityViewModel::class.java)

        supportActionBar?.hide()

        viewModel.fetchGenderAndWorkoutStyle()

        val musclesNamesArray = resources.getStringArray(com.met.impilo.R.array.muscleSets)

        val muscleSetMap = HashMap<Int, String>().apply {
            put(0, Const.REF_EXERCISE_ABS)
            put(1, Const.REF_EXERCISE_BACK)
            put(2, Const.REF_EXERCISE_BICEPS)
            put(3, Const.REF_EXERCISE_CHEST)
            put(4, Const.REF_EXERCISE_ENDURANCE)
            put(5, Const.REF_EXERCISE_FOREARM)
            put(6, Const.REF_EXERCISE_LEGS)
            put(7, Const.REF_EXERCISE_SHOULDERS)
            put(8, Const.REF_EXERCISE_TRICEPS)
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, musclesNamesArray)
        muscles_spinner.adapter = adapter

        muscles_spinner.setSelection(lastMuscleSetIndex)

        val searchAdapter = SearchExerciseAdapter(this)
        search_list_view.adapter = searchAdapter
        searchAdapter.setOnExerciseClickListener(this)

        muscles_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedExerciseMuscleSetName = musclesNamesArray[position]

                viewModel.fetchExercises(muscleSetMap[position]!!)
            }

        }


        search_action.setOnClickListener {
            if(search_exercise_input_text_layout.visibility == GONE) {
                search_exercise_input_text_layout.visibility = VISIBLE
                search_action.setImageResource(com.met.impilo.R.drawable.ic_close)
            } else {
                search_exercise_input_text_layout.visibility = GONE
                search_action.setImageResource(com.met.impilo.R.drawable.ic_search_black)
                searchAdapter.clearSearchField()
                search_exercise_input_edit_text.setText("")
            }
        }

        search_exercise_input_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchAdapter.filter.filter(s.toString())
            }

        })

        viewModel.getGender().observe(this, Observer {
            gender = it
            viewModel.fetchWeight()
        })

        viewModel.getWorkoutStyle().observe(this, Observer {
            workoutStyle = it
        })

        viewModel.getWeight().observe(this, Observer {
            weight = it
            exerciseBottomSheet = ExerciseBottomSheet(gender, weight, workoutStyle)
            isBottomSheetInitialized = true
        })

        viewModel.getExercises().observe(this, Observer {
            searchAdapter.updateAdapter(it.toMutableList())
        })
    }


    private fun showBottomSheet(exercise: Exercise){
        if(isBottomSheetInitialized) {
            exerciseBottomSheet.exercise = exercise
            exerciseBottomSheet.show(supportFragmentManager, exerciseBottomSheet.tag)
            exerciseBottomSheet.callback = this@SearchExerciseActivity
        }
    }

    override fun onExerciseClick(exercise: Exercise) {
        showBottomSheet(exercise)
    }

    override fun onExerciseAdd(exercise: Exercise) {
        intent.putExtra("exerciseMuscleSetName", selectedExerciseMuscleSetName)
        intent.putExtra("exerciseToAdd", exercise)
        intent.putExtra("lastMuscleSetIndex", muscles_spinner.selectedItemPosition)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
