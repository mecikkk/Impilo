package com.met.workout.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.met.impilo.data.workouts.Exercise
import com.met.impilo.data.workouts.ExerciseType
import com.met.workout.R

class TrainingDayExercisesDetailsAdapter(var exercises: MutableList<Exercise> = mutableListOf()) : RecyclerView.Adapter<TrainingDayExerciseDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingDayExerciseDetailsViewHolder {
        return TrainingDayExerciseDetailsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.training_day_exercise_details, parent, false))
    }

    override fun getItemCount(): Int = exercises.size

    override fun onBindViewHolder(holder: TrainingDayExerciseDetailsViewHolder, position: Int) {
        holder.updateInformation(exercises[position])
    }


}

class TrainingDayExerciseDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun updateInformation(exercise: Exercise) {
        val name = itemView.findViewById<TextView>(R.id.exercise_name_text_view)
        val sets = itemView.findViewById<TextView>(R.id.number_of_sets_text_view)
        val reps = itemView.findViewById<TextView>(R.id.reps_range_text_view)
        val weight = itemView.findViewById<TextView>(R.id.weight_text_view)

        name.text = exercise.name

        when (exercise.exerciseType) {
            ExerciseType.WITH_WEIGHT -> {
                reps.visibility = View.VISIBLE
                weight.visibility = View.VISIBLE

                var repsList = itemView.context.getString(com.met.impilo.R.string.repetition_range)
                for (x in 0 until exercise.details.reps.size) repsList += if (exercise.details.reps.size-1 == x) "${exercise.details.reps[x]}"
                else "${exercise.details.reps[x]} / "

                var weightList = itemView.context.getString(com.met.impilo.R.string.exercise_weight) + " : "
                for (x in 0 until exercise.details.weight.size) weightList += if (exercise.details.weight.size-1 == x) "${exercise.details.weight[x]}kg"
                else "${exercise.details.weight[x]}kg / "

                reps.text = repsList
                sets.text = StringBuilder(itemView.context.getString(com.met.impilo.R.string.number_of_sets) + " ${exercise.details.sets}")
                weight.text = weightList
            }
            ExerciseType.ONLY_REPS -> {
                reps.visibility = View.GONE
                weight.visibility = View.GONE

                var repsList = itemView.context.getString(com.met.impilo.R.string.number_of_reps) +  " : "
                for (x in 0 until exercise.details.reps.size) repsList += if (exercise.details.reps.size-1 == x) "${exercise.details.reps[x]}"
                else "${exercise.details.reps[x]} / "

                sets.text = repsList
            }
            ExerciseType.TIME -> {
                reps.visibility = View.GONE
                weight.visibility = View.GONE

                val timeTitle = itemView.context.getString(com.met.impilo.R.string.time) + " : ${exercise.details.time} min"

                sets.text = timeTitle
            }
            ExerciseType.TIME_WITH_DISTANCE -> {
                reps.visibility = View.VISIBLE
                weight.visibility = View.GONE

                val timeTitle = itemView.context.getString(com.met.impilo.R.string.time) + " : ${exercise.details.time} min"
                val distanceTitle = itemView.context.getString(com.met.impilo.R.string.distance) + " : ${exercise.details.distance} km"

                sets.text = timeTitle
                reps.text = distanceTitle
            }
        }

    }


}