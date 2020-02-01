package com.met.workout.adapter


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.met.impilo.data.workouts.ExerciseDetails
import com.met.impilo.data.workouts.ExerciseType
import com.met.workout.R

class ExerciseDetailsAdapter(var exerciseDetails: ExerciseDetails, private val exerciseType: ExerciseType) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = javaClass.simpleName
    private lateinit var context: Context
    var isOneWeightMode: Boolean = false
    private var firstWeight: Float = 0f
    private var existDataChecked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (exerciseType) {
            ExerciseType.ONLY_REPS, ExerciseType.TIME -> OnlyRepsOrTimeViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.exercise_details_only_reps_list_item, parent, false))
            ExerciseType.WITH_WEIGHT -> WithWeightViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_details_with_weight_list_item, parent, false))
            ExerciseType.TIME_WITH_DISTANCE -> TimeWithDistanceViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.exercise_details_time_with_distance_list_item, parent, false))
            else -> WithWeightViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_details_with_weight_list_item, parent, false))
        }
    }

    override fun getItemCount(): Int = exerciseDetails.sets

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (exerciseType) {
            ExerciseType.WITH_WEIGHT -> {
                Log.w(TAG, "Weight list : ${exerciseDetails.weight} | isNullOrEmpty : ${exerciseDetails.weight.isNullOrEmpty()}")
                Log.w(TAG, "ExistDataChecked : $existDataChecked | SetsNumber : ${exerciseDetails.sets}")
                Log.w(TAG, "Binding position : $position")


                if (!existDataChecked) {
                    if (isOneWeightMode) {
                        if (!exerciseDetails.weight.isNullOrEmpty() && exerciseDetails.weight.size > position) {
                            if (position == 0) {
                                (holder as WithWeightViewHolder).setWeightEditText(exerciseDetails.weight[position])
                                firstWeight = exerciseDetails.weight[position]
                            }

                            (holder as WithWeightViewHolder).setWeightTextView(exerciseDetails.weight[position])
                        }
                    } else {
                        if (!exerciseDetails.weight.isNullOrEmpty() && exerciseDetails.weight.size > position) (holder as WithWeightViewHolder).setWeightEditText(
                            exerciseDetails.weight[position])
                    }

                    if (!exerciseDetails.reps.isNullOrEmpty() && exerciseDetails.reps.size > position) {
                        (holder as WithWeightViewHolder).getSpinner().setSelection(exerciseDetails.reps[position])
                    }
                    if (position == exerciseDetails.sets - 1) existDataChecked = true

                }

                (holder as WithWeightViewHolder).getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, spinnerPosition: Int, id: Long) {
                        if (exerciseDetails.reps.size > position) exerciseDetails.reps[position] = spinnerPosition
                        else exerciseDetails.reps.add(spinnerPosition)
                    }

                }

                if (isOneWeightMode) {

                    if (position == 0) {
                        holder.setEditTextVisibility(View.VISIBLE)
                        holder.setWeightTextViewVisibility(View.GONE)
                        holder.setTextWatcher(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                if (!s.isNullOrEmpty()) {
                                    firstWeight = s.toString().toFloat()

                                    if (exerciseDetails.weight.size > position) exerciseDetails.weight[position] = firstWeight
                                    else exerciseDetails.weight.add(firstWeight)

                                    for (x in 1 until exerciseDetails.sets) notifyItemChanged(x)
                                }
                                Log.i(TAG, "Entered weight : $firstWeight | text from watcher : ${s.toString()}")
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            }

                        })
                    } else {

                        holder.setEditTextVisibility(View.INVISIBLE)
                        holder.setWeightTextViewVisibility(View.VISIBLE)
                        holder.setWeightTextView(firstWeight)
                    }
                    if (exerciseDetails.weight.size > position) exerciseDetails.weight[position] = firstWeight
                    else exerciseDetails.weight.add(firstWeight)
                } else {

                    holder.setTextWatcher(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            if (!s.isNullOrEmpty()) {
                                if (exerciseDetails.weight.size > position) exerciseDetails.weight[position] = holder.getWeight()
                                else exerciseDetails.weight.add(holder.getWeight())
                            }

                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                    })

                    holder.setEditTextVisibility(View.VISIBLE)
                    holder.setWeightTextViewVisibility(View.GONE)
                }

                holder.setSetNumber(position + 1)
            }
            ExerciseType.ONLY_REPS -> {
                if (!exerciseDetails.reps.isNullOrEmpty() && exerciseDetails.reps.size > position) (holder as OnlyRepsOrTimeViewHolder).setEditTextValue(exerciseDetails.reps[0])

                (holder as OnlyRepsOrTimeViewHolder).setTextWatcher(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (!s.isNullOrEmpty()) {
                            if (exerciseDetails.reps.size > position) exerciseDetails.reps[position] = s.toString().toInt()
                            else exerciseDetails.reps.add(s.toString().toInt())
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                })

                holder.changeTextInfo(com.met.impilo.R.string.enter_number_of_repetitions)
            }
            ExerciseType.TIME -> {
                if (exerciseDetails.time != 0f) (holder as OnlyRepsOrTimeViewHolder).setEditTextValue(exerciseDetails.time)

                (holder as OnlyRepsOrTimeViewHolder).setTextWatcher(setTimeTextWatcher())
                holder.changeTextInfo(com.met.impilo.R.string.enter_exercise_duration)
                holder.changeTextInputToTime()
            }
            ExerciseType.TIME_WITH_DISTANCE -> {

                if (exerciseDetails.time != 0f) (holder as TimeWithDistanceViewHolder).setTimeEditText(exerciseDetails.time)

                if (exerciseDetails.distance != 0f) (holder as TimeWithDistanceViewHolder).setDistanceEditText(exerciseDetails.distance)

                (holder as TimeWithDistanceViewHolder).setTimeTextWatcher(setTimeTextWatcher())

                holder.setDistanceTextWatcher(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (!s.isNullOrEmpty()) exerciseDetails.distance = s.toString().toFloat()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
            }
            else -> {
            }
        }
    }

    private fun setTimeTextWatcher(): TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!s.isNullOrEmpty()) exerciseDetails.time = s.toString().toFloat()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    class WithWeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val num: MutableList<Int> = mutableListOf()
            for (i in 0..25) num.add(i)

            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_dropdown_item, num)
            itemView.findViewById<AppCompatSpinner>(R.id.reps_spinner).adapter = adapter
        }

        fun setSetNumber(num: Int) {
            itemView.findViewById<TextView>(R.id.set_num_text_view).text = StringBuilder("$num.")
        }

        fun setEditTextVisibility(visibility: Int) {
            itemView.findViewById<TextInputEditText>(R.id.weight_input_edit_text).visibility = visibility
            itemView.findViewById<TextInputLayout>(R.id.weight_input_text_layout).visibility = visibility
            itemView.findViewById<TextInputEditText>(R.id.weight_input_edit_text).isEnabled = visibility != View.INVISIBLE
        }

        fun setWeightTextViewVisibility(visibility: Int) {
            itemView.findViewById<TextView>(R.id.weight_text_view).visibility = visibility
        }

        fun setWeightTextView(weight: Float) {
            itemView.findViewById<TextView>(R.id.weight_text_view).text = StringBuilder("$weight kg")
        }

        fun setTextWatcher(textWatcher: TextWatcher) {
            itemView.findViewById<TextInputEditText>(R.id.weight_input_edit_text).addTextChangedListener(textWatcher)
        }

        fun setWeightEditText(weight: Float) {
            itemView.findViewById<TextInputEditText>(R.id.weight_input_edit_text).setText("$weight")
        }

        fun getWeight(): Float {
            val et = itemView.findViewById<TextInputEditText>(R.id.weight_input_edit_text)

            return if (et.text.isNullOrEmpty()) 0f
            else et.text.toString().toFloat()

        }

        fun getSpinner(): AppCompatSpinner = itemView.findViewById(R.id.reps_spinner)

    }

    class OnlyRepsOrTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setEditTextValue(value: Float) {
            itemView.findViewById<TextInputEditText>(R.id.reps_input_edit_text).setText("$value")
        }

        fun setEditTextValue(value: Int) {
            itemView.findViewById<TextInputEditText>(R.id.reps_input_edit_text).setText("$value")
        }

        fun changeTextInfo(textRef: Int) {
            itemView.findViewById<TextView>(R.id.set_num_text_view).text = itemView.context.getText(textRef)
        }

        fun setTextWatcher(watcher: TextWatcher) {
            itemView.findViewById<TextInputEditText>(R.id.reps_input_edit_text).addTextChangedListener(watcher)
        }

        fun changeTextInputToTime() {
            val hintText = itemView.context.getText(com.met.impilo.R.string.time_min)
            itemView.findViewById<TextInputLayout>(R.id.reps_input_text_layout).apply {
                hint = hintText
                startIconDrawable = ContextCompat.getDrawable(context, com.met.impilo.R.drawable.ic_clock)
            }
        }
    }

    class TimeWithDistanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setDistanceEditText(distance: Float) {
            itemView.findViewById<TextInputEditText>(R.id.distance_input_edit_text).setText("$distance")
        }

        fun setTimeEditText(time: Float) {
            itemView.findViewById<TextInputEditText>(R.id.time_input_edit_text).setText("$time")
        }

        fun setDistanceTextWatcher(watcher: TextWatcher) {
            itemView.findViewById<TextInputEditText>(R.id.distance_input_edit_text).addTextChangedListener(watcher)
        }

        fun setTimeTextWatcher(watcher: TextWatcher) {
            itemView.findViewById<TextInputEditText>(R.id.time_input_edit_text).addTextChangedListener(watcher)
        }
    }
}

