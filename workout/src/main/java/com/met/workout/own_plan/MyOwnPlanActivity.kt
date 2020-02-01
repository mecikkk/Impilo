package com.met.workout.own_plan

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.baoyz.expandablelistview.SwipeMenuExpandableCreator
import com.baoyz.swipemenulistview.SwipeMenu
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.met.impilo.data.Gender
import com.met.impilo.data.WorkoutStyle
import com.met.impilo.data.workouts.*
import com.met.impilo.utils.Const
import com.met.impilo.utils.ViewUtils
import com.met.workout.R
import com.met.workout.adapter.OnTrainingWeekClick
import com.met.workout.adapter.TrainingWeekExpandableListAdapter
import kotlinx.android.synthetic.main.activity_my_own_plan.*

class MyOwnPlanActivity : AppCompatActivity(), OnTrainingWeekClick, ExerciseBottomSheet.OnAddExerciseListener {

    private val TAG = javaClass.simpleName
    private var currentWeek: Int = 0
    private var clickedDay: Int = 0
    private lateinit var trainingWeekAdapter: TrainingWeekExpandableListAdapter
    private lateinit var exerciseBottomSheet: ExerciseBottomSheet
    private lateinit var searchViewModel: SearchActivityViewModel
    private lateinit var viewModel : MyOwnPlanActivityViewModel
    private lateinit var gender: Gender
    private var userWeight: Float = 0f
    private lateinit var workoutStyle: WorkoutStyle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_own_plan)

        viewModel = ViewModelProvider(this).get(MyOwnPlanActivityViewModel::class.java)
        searchViewModel = ViewModelProvider(this).get(SearchActivityViewModel::class.java)
        searchViewModel.fetchGenderAndWorkoutStyle()

        training_system_spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(com.met.impilo.R.array.weeks))

        trainingWeekAdapter = TrainingWeekExpandableListAdapter(this, viewModel.weekA, viewModel.weekB)
        trainingWeekAdapter.setOnTrainingWeekClick(this)

        training_system_radio_group.check(R.id.weekly_radio_button)
        training_system_radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.weekly_radio_button -> {
                    training_system_spinner.isEnabled = false
                    training_system_spinner.visibility = View.GONE
                    trainingWeekAdapter.trainingWeek = viewModel.weekA
                    trainingWeekAdapter.isBiweeklySystem = false
                    trainingWeekAdapter.notifyDataSetChanged()
                }
                R.id.biweekly_radio_button -> {
                    training_system_spinner.isEnabled = true
                    training_system_spinner.visibility = View.VISIBLE
                    trainingWeekAdapter.isBiweeklySystem = true
                }
            }
        }

        training_system_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i(TAG, "Spinner position $position")
                when (position) {
                    0 -> {
                        trainingWeekAdapter.showWeekA()
                        currentWeek = position
                    }
                    1 -> {
                        trainingWeekAdapter.showWeekB()
                        currentWeek = position
                    }
                }
            }

        }
        week_list_view.apply {
            setmMenuStickTo(SwipeMenuListView.STICK_TO_SCREEN)
            setAdapter(trainingWeekAdapter)
            setMenuCreator(createSwipeMenu())
        }
        week_list_view.setAdapter(trainingWeekAdapter)

        week_list_view.setOnMenuItemClickListener { group, child, _, index ->
            when (index) {
                0 -> {
                    //editProduct(group, child)
                    val exercise: Exercise = when (currentWeek) {
                        0 -> viewModel.weekA[group].exercises[child]
                        1 -> viewModel.weekB[group].exercises[child]
                        else -> {
                            Exercise()
                        }
                    }
                    showBottomSheet(exercise)
                    true
                }
                1 -> {
                    Log.w(TAG, "Exercise to delete : $group | $child")
                    val builder = AlertDialog.Builder(this).apply {
                        setMessage(getString(com.met.impilo.R.string.want_delete_exercise) + "${trainingWeekAdapter.trainingWeek[group].exercises[child].name} ?")
                        setTitle(getString(com.met.impilo.R.string.delete_exercise))
                        setPositiveButton(getString(com.met.impilo.R.string.yes)) { _, _ ->
                            Log.i(TAG, "Deleting exercise. ListOfMusclesSetNames BEFORE DELETE: ${trainingWeekAdapter.trainingWeek[group].muscleSetsNames}")

                            ViewUtils.createSnackbar(my_own_plan_container, getString(com.met.impilo.R.string.exercise_delete_success)).show()

                            trainingWeekAdapter.trainingWeek[group].exercises.removeAt(child)
                            trainingWeekAdapter.trainingWeek[group].muscleSetsNames.removeAt(child)
                            trainingWeekAdapter.notifyDataSetChanged()

                            Log.i(TAG, "Deleting exercise. ListOfMusclesSetNames AFTER DELETE: ${trainingWeekAdapter.trainingWeek[group].muscleSetsNames}")

                        }
                        setNegativeButton(getString(com.met.impilo.R.string.no)) { _, _ ->
                            Log.e(TAG, "Deleting canceled")
                        }
                    }
                    val dialog = builder.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, com.met.impilo.R.color.colorAccent))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, com.met.impilo.R.color.textColor))
                    true
                }
                else -> false
            }
        }

        week_list_view.setOnScrollListener(fabVisibility())


        searchViewModel.getGender().observe(this, Observer {
            gender = it
            searchViewModel.fetchWeight()
        })

        searchViewModel.getWorkoutStyle().observe(this, Observer {
            workoutStyle = it
        })

        searchViewModel.getWeight().observe(this, Observer {
            userWeight = it
            exerciseBottomSheet = ExerciseBottomSheet(gender, userWeight, workoutStyle)
        })


        finish_extended_fab.setOnClickListener {
            Log.i(TAG,
                "Finishing | Week A - Monday exercises : \nallMusclesSetNames : ${trainingWeekAdapter.weekA[0].muscleSetsNames} \n" + "distinctMusclesSetNames : ${trainingWeekAdapter.weekA[0].muscleSetsNames.distinct()}")
            val trainingPlanInfo = TrainingPlanInfo().apply {
                this.actualWeek = Week.A
                if (trainingWeekAdapter.isBiweeklySystem) this.trainingSystem = TrainingSystem.AB
                else this.trainingSystem = TrainingSystem.A
                this.isConfigurationCompleted = true
                this.weekA = trainingWeekAdapter.weekA.toMutableList()
                this.weekB = trainingWeekAdapter.weekB.toMutableList()
            }
            trainingPlanInfo.weekA.forEach {
                it.muscleSetsNames = it.muscleSetsNames.distinct().toMutableList()
            }
            trainingPlanInfo.weekB.forEach {
                it.muscleSetsNames = it.muscleSetsNames.distinct().toMutableList()
            }
            intent.putExtra("trainingPlanInfo", trainingPlanInfo)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        expandAllGroups()
    }

    private fun expandAllGroups() {
        for (x in 0 until 6) week_list_view.expandGroup(x)
    }

    private fun fabVisibility(): AbsListView.OnScrollListener? = object : AbsListView.OnScrollListener {
        override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            if (firstVisibleItem == 0) {
                finish_extended_fab.show()
                finish_extended_fab.extend()
            } else if (firstVisibleItem >= 1) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) finish_extended_fab.hide()
                else {
                    finish_extended_fab.show()
                    finish_extended_fab.shrink()
                }
            } else finish_extended_fab.hide()
        }

        override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
        }
    }

    private fun createSwipeMenu(): SwipeMenuExpandableCreator = object : SwipeMenuExpandableCreator {
        override fun createGroup(menu: SwipeMenu) {}
        override fun createChild(menu: SwipeMenu) {

            val editItem = SwipeMenuItem(this@MyOwnPlanActivity).apply {
                background = ContextCompat.getDrawable(this@MyOwnPlanActivity, com.met.impilo.R.drawable.menu_item_background)
                width = 200
                setIcon(com.met.impilo.R.drawable.ic_edit)
                icon.setTint(ContextCompat.getColor(this@MyOwnPlanActivity, com.met.impilo.R.color.secondAccent))
            }

            menu.addMenuItem(editItem)

            val deleteItem = SwipeMenuItem(this@MyOwnPlanActivity).apply {
                background = ColorDrawable(ContextCompat.getColor(this@MyOwnPlanActivity, com.met.impilo.R.color.menuIconGradientEnd))
                width = 200
                setIcon(com.met.impilo.R.drawable.ic_delete)
                icon.setTint(ContextCompat.getColor(this@MyOwnPlanActivity, com.met.impilo.R.color.colorAccent))
            }

            menu.addMenuItem(deleteItem)
        }
    }

    private fun showBottomSheet(exercise: Exercise) {
        exerciseBottomSheet.exercise = exercise
        exerciseBottomSheet.show(supportFragmentManager, exerciseBottomSheet.tag)
        exerciseBottomSheet.callback = this@MyOwnPlanActivity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.ADD_EXERCISE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Add exercise result OK")

                val exercise: Exercise = data?.getSerializableExtra("exerciseToAdd") as Exercise
                val muscleSetName = data.getSerializableExtra("exerciseMuscleSetName") as String
                val index = data.getIntExtra("lastMuscleSetIndex", 0)
                viewModel.lastUsedMuscleSetIndex = index
                Log.i(TAG, "Received from searchActivity lastSelectedMuscleSetIndex : $index")

                when (currentWeek) {
                    0 -> {
                        Log.i(TAG, "Adding to weekA | clicked day : $clickedDay | data : $exercise")

                        trainingWeekAdapter.weekA[clickedDay].apply {
                            exercises.add(exercise)
                            isRestDay = viewModel.weekA[clickedDay].exercises.isNullOrEmpty()
                            muscleSetsNames.add(muscleSetName)
                        }
                        trainingWeekAdapter.showWeekA()
                    }
                    1 -> {
                        Log.i(TAG, "Adding to weekB | clicked day : $clickedDay | data : $exercise")

                        trainingWeekAdapter.weekB[clickedDay].apply {
                            exercises.add(exercise)
                            isRestDay = viewModel.weekB[clickedDay].exercises.isNullOrEmpty()
                            muscleSetsNames.add(muscleSetName)
                        }

                        trainingWeekAdapter.showWeekB()
                    }
                }
                expandAllGroups()
            } else Log.w(TAG, "Add exercise result CANCELED / FAILED")

        }
    }

    override fun onDayClick(groupPosition: Int) {
        if (week_list_view.isGroupExpanded(groupPosition)) week_list_view.collapseGroup(groupPosition)
        else week_list_view.expandGroup(groupPosition)
    }

    override fun onAddExerciseClick(groupPosition: Int) {
        clickedDay = groupPosition
        val intent = Intent(this, SearchExerciseActivity::class.java)
        intent.putExtra("lastMuscleSetIndex", viewModel.lastUsedMuscleSetIndex)
        Log.i(TAG, "Sending from viewModel lastSelectedMuscleSetIndex : ${viewModel.lastUsedMuscleSetIndex}")
        startActivityForResult(intent, Const.ADD_EXERCISE_RESULT)
    }

    override fun onExerciseAdd(exercise: Exercise) {
        trainingWeekAdapter.notifyDataSetChanged()
        expandAllGroups()
    }
}
