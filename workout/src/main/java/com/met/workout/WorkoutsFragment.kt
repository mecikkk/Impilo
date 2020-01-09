package com.met.workout


import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.fragment_workouts.*


class WorkoutsFragment : Fragment() {

    private var TAG = javaClass.simpleName

    companion object {
        @JvmStatic
        fun newInstance() = WorkoutsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bf_seekbar.setCustomSectionTextArray { _, array ->
            array.clear()
            array.put(1, "Niedowaga")
            array.put(3, "W normie")
            array.put(5, "Nadwaga")
            array.put(7, "Otyłość")

            array
        }

        bf_seekbar.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
                Log.e(TAG, "Slide : $progressFloat")
                val color: Int = when {
                    progressFloat <= 2f -> {
                        ContextCompat.getColor(context!!, com.met.impilo.R.color.proteinsColor)
                    }
                    progressFloat <= 4f -> {
                        ContextCompat.getColor(context!!, com.met.impilo.R.color.positiveGreen)
                    }
                    progressFloat <= 6f -> {
                        ContextCompat.getColor(context!!, com.met.impilo.R.color.fatsColor)
                    }
                    progressFloat <= 8f -> {
                        ContextCompat.getColor(context!!, com.met.impilo.R.color.colorAccent)
                    }
                    else -> ContextCompat.getColor(context!!, com.met.impilo.R.color.colorAccent)
                }

                bf_seekbar.setSecondTrackColor(color)
                bf_seekbar.setThumbColor(color)
            }

            override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
            }

            override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
            }

        }


    }

}
