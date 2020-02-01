package com.met.impilo


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.met.impilo.data.Demand
import com.met.impilo.data.Gender
import com.met.impilo.data.MusclesSet
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.utils.MarkMuscles
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.StringBuilder


class HomeFragment : Fragment() {

    private val TAG = javaClass.simpleName

    private lateinit var viewModel : HomeFragmentViewModel
    private lateinit var demand : Demand
    private lateinit var mealsSummary: MealsSummary

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)

        var muscleMapImageView : Int = R.drawable.muscles_man_front

        viewModel.getGender().observe(this, Observer {
            muscleMapImageView = if(it == Gender.MALE) R.drawable.muscles_man_front
            else R.drawable.muscles_woman_front

        })

       // muscle_map.setImageResource(muscleMapImageView)

        //MarkMuscles.set(context!!, muscleMapImageView, muscle_map, MusclesSet.ABDOMINALS, R.color.colorAccent)

        viewModel.getMyDemand()

        val demandObserver = Observer<Demand> {
            if(it != null) {
                demand = it
                viewModel.getMyMealsSummary()
            }
        }

        val mealsSummaryObserver = Observer<MealsSummary> {
            mealsSummary = it ?: MealsSummary()
            updateProgress()
        }

        viewModel.getDemand().observe(this, demandObserver)
        viewModel.getMealsSummary().observe(this, mealsSummaryObserver)
    }

    private fun updateProgress(){
        Log.e(TAG, "Demand : $demand")
        Log.e(TAG, "MealSummary : $mealsSummary")
        val caloriesPercent : Int = ((mealsSummary.kcalSum.toFloat() / demand.calories.toFloat()) * 100).toInt()
        val carboPercent = (mealsSummary.carbohydratesSum / demand.carbohydares) * 100
        val proteinsPercent = (mealsSummary.proteinsSum / demand.proteins) * 100
        val fatsPercent = (mealsSummary.fatsSum / demand.fats) * 100

        calories_percent_progress_text_view.text = StringBuilder("$caloriesPercent%")
        calories_progress.setProgress(caloriesPercent.toFloat(), true)
        calories_progress_text_view.text = StringBuilder("" + mealsSummary.kcalSum + " / " + demand.calories + " kcal")

        carbohydrates_progress.progress = carboPercent
        proteins_progress.progress = proteinsPercent
        fats_progress.progress = fatsPercent

    }

}
