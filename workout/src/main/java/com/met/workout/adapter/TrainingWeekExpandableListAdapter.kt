package com.met.workout.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import com.baoyz.expandablelistview.BaseSwipeMenuExpandableListAdapter
import com.baoyz.swipemenulistview.ContentViewWrapper
import com.met.impilo.data.workouts.Exercise
import com.met.impilo.data.workouts.ExerciseType
import com.met.impilo.data.workouts.TrainingDay
import com.met.workout.R

class TrainingWeekExpandableListAdapter(private val context: Context, var weekA: List<TrainingDay>, var weekB: List<TrainingDay>, var isBiweeklySystem: Boolean = false) :
    BaseSwipeMenuExpandableListAdapter() {

    private val TAG = javaClass.simpleName
    private lateinit var callback: OnTrainingWeekClick
    var trainingWeek: List<TrainingDay> = weekA

    fun setOnTrainingWeekClick(onTrainingWeekClick: OnTrainingWeekClick) {
        this.callback = onTrainingWeekClick
    }

    override fun getChildViewAndReUsable(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup?): ContentViewWrapper {
        var reUseable = true
        val convertView: View

        if (view == null) {
            convertView = View.inflate(context, R.layout.exercise_list_item, null)
            reUseable = false
        } else convertView = view

        val exercise = getChild(groupPosition, childPosition)

        val name = convertView.findViewById<TextView>(R.id.exercise_name_text_view)
        val setAndReps = convertView.findViewById<TextView>(R.id.set_reps_info_text_view)
        val weight = convertView.findViewById<TextView>(R.id.weight_text_view)

        val set = context.resources.getString(com.met.impilo.R.string.number_of_sets)
        val reps = context.resources.getString(com.met.impilo.R.string.reps)
        val time = context.resources.getString(com.met.impilo.R.string.time)
        val distance = context.resources.getString(com.met.impilo.R.string.distance)

        var setsDetails = ""

        when (exercise.exerciseType) {
            ExerciseType.WITH_WEIGHT -> {
                for (x in 0 until (exercise.details.sets)) {
                    setsDetails += "${exercise.details.reps[x]}×${exercise.details.weight[x]}kg"
                    if (x < (exercise.details.sets - 1)) setsDetails += " | "
                }

                weight.visibility = View.VISIBLE
                setAndReps.text = StringBuilder("$set ${exercise.details.sets}")
                weight.text = StringBuilder(setsDetails)

            }
            ExerciseType.ONLY_REPS -> {
                weight.visibility = View.GONE
                setAndReps.text = StringBuilder("${exercise.details.reps[0]} $reps")
            }
            ExerciseType.TIME -> {
                weight.visibility = View.GONE
                setAndReps.text = StringBuilder("$time : ${exercise.details.time} min")
            }
            ExerciseType.TIME_WITH_DISTANCE -> {
                weight.visibility = View.VISIBLE
                setAndReps.text = StringBuilder("$time : ${exercise.details.time} min")
                weight.text = StringBuilder("$distance : ${exercise.details.distance} km")
            }
        }

        name.text = exercise.name

        return ContentViewWrapper(convertView, reUseable)
    }

    override fun getGroup(groupPosition: Int): TrainingDay = trainingWeek[groupPosition]

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun hasStableIds(): Boolean = false

    override fun isGroupSwipable(p0: Int): Boolean = false

    override fun getChildrenCount(groupPosition: Int): Int = if (trainingWeek[groupPosition].exercises.isNullOrEmpty()) 0
    else trainingWeek[groupPosition].exercises.size


    override fun isChildSwipable(p0: Int, p1: Int): Boolean = true

    override fun getChild(groupPosition: Int, childPosition: Int): Exercise = trainingWeek[groupPosition].exercises[childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int = trainingWeek.size


    override fun getGroupViewAndReUsable(groupPosition: Int, isExpanded: Boolean, view: View?, parent: ViewGroup?): ContentViewWrapper {
        var reuseable = true
        val convertView: View

        if (view == null) {
            convertView = View.inflate(context, R.layout.day_list_item, null)
            reuseable = false
        } else convertView = view

        val trainingDay: TrainingDay = getGroup(groupPosition)

        val name = convertView.findViewById<TextView>(R.id.day_name_text_view)
        val summary = convertView.findViewById<TextView>(R.id.day_summary_info_text_view)
        val addExerciseButton = convertView.findViewById<AppCompatImageButton>(R.id.add_exercise_button)
        val expandBg = convertView.findViewById<LinearLayout>(R.id.some_bg)

        addExerciseButton.setOnClickListener {
            callback.onAddExerciseClick(groupPosition)
        }

        convertView.setOnClickListener {
            if (!isExpanded && !trainingDay.exercises.isNullOrEmpty()) {
                expandBg.visibility = View.VISIBLE
            } else {
                expandBg.visibility = View.GONE
            }

            callback.onDayClick(groupPosition)
        }

        val weekDaysNames = context.resources.getStringArray(com.met.impilo.R.array.weekDays)

        name.text = weekDaysNames[trainingDay.id]

        if (trainingDay.isRestDay) {
            summary.text = StringBuilder(context.resources.getString(com.met.impilo.R.string.rest_day))
        } else {
            when (trainingDay.exercises.size) {
                in 2..4 -> summary.text = StringBuilder("${trainingDay.exercises.size} ${context.resources.getString(com.met.impilo.R.string.two_four_exercises_for_pl)}")
                1 -> summary.text = StringBuilder("${trainingDay.exercises.size} ${context.resources.getString(com.met.impilo.R.string.one_exercise)}")
                0 -> trainingDay.isRestDay = true
                else -> summary.text = StringBuilder("${trainingDay.exercises.size} ${context.resources.getString(com.met.impilo.R.string.exercises)}")
            }
        }

        return ContentViewWrapper(convertView, reuseable)
    }

    fun showWeekA() {
        this.trainingWeek = weekA
        notifyDataSetChanged()
    }

    fun showWeekB() {
        this.trainingWeek = weekB
        notifyDataSetChanged()
    }
}

interface OnTrainingWeekClick {
    fun onDayClick(groupPosition: Int)
    fun onAddExerciseClick(groupPosition: Int)
}