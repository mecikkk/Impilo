package com.met.workout.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.met.impilo.data.workouts.Exercise
import com.met.workout.R


class SearchExerciseAdapter(private val context: Context) : BaseAdapter(), Filterable{

    var exercisesOrigin : MutableList<Exercise> = mutableListOf()
    var exercisesFiltered : MutableList<Exercise> = exercisesOrigin
    lateinit var callback : OnExerciseClickListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = LayoutInflater.from(context).inflate(R.layout.search_exercise_list_item, null)

        val exerciseName = v.findViewById<TextView>(R.id.exercise_name_text_view)
        val musclesName = v.findViewById<TextView>(R.id.main_muscles_text_view)

        exerciseName.text = exercisesFiltered[position].name

        var mainMuscles = ""
        exercisesFiltered[position].mainMuscle.forEach {
            if(exercisesFiltered[position].mainMuscle.size <= 1)
                mainMuscles += context.getString(it.nameRef)
            else
                mainMuscles += "${context.getString(it.nameRef)}, "
        }

        musclesName.text = mainMuscles

        v.setOnClickListener {
            Log.e("SearchActivityAdapter", "Clicked product : ${exerciseName.text} | ${musclesName.text}}")
            callback.onExerciseClick(exercisesFiltered[position])
        }

        return v
    }

    fun setOnExerciseClickListener(onExerciseClickListener: OnExerciseClickListener){
        this.callback = onExerciseClickListener
    }

    fun updateAdapter(exercises : MutableList<Exercise>){
        this.exercisesOrigin = exercises
        this.exercisesFiltered = exercises
        notifyDataSetChanged()
    }

    fun clearSearchField(){
        this.exercisesFiltered = exercisesOrigin
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any = exercisesFiltered[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = exercisesFiltered.size


    interface OnExerciseClickListener {
        fun onExerciseClick(exercise: Exercise)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                exercisesFiltered = results.values as MutableList<Exercise> // has the filtered values
                notifyDataSetChanged() // notifies the data with new filtered values
            }

            @SuppressLint("DefaultLocale")
            override fun performFiltering(constraint: CharSequence): FilterResults {
                var constraint: CharSequence? = constraint
                val results = FilterResults() // Holds the results of a filtering operation in values
                val filteredArrList: MutableList<Exercise> = mutableListOf()

                Log.d("SearchAdapterFilter", "CharSequence : $constraint")

                if (constraint == null || constraint.isEmpty()) { // set the Original result to return
                    results.count = exercisesOrigin.size
                    results.values = exercisesOrigin
                    Log.d("SearchAdapterFilter", "CharSequence is empty, showing original data")
                } else {
                    constraint = constraint.toString().toLowerCase()
                    Log.d("SearchAdapterFilter", "CharSequenceLowerCase $constraint")

                    for (i in exercisesOrigin.indices) {
                        Log.d("SearchAdapterFilter", "Index $i : of : ${exercisesOrigin.indices}")
                        val data: String = exercisesOrigin[i].name
                        if (data.toLowerCase().contains(constraint.toString())) {
                            Log.d("SearchAdapterFilter", "Data in lowercase : ${data.toLowerCase()}  | constraint : ${constraint}")
                            filteredArrList.add(exercisesOrigin[i])
                        }
                    }
                    // set the Filtered result to return
                    results.count = filteredArrList.size
                    results.values = filteredArrList
                }
                return results
            }
        }
    }
}