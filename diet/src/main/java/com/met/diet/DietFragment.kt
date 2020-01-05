package com.met.diet


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.baoyz.expandablelistview.SwipeMenuExpandableCreator
import com.baoyz.swipemenulistview.SwipeMenu
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.met.diet.adapter.MealsExpandableListAdapter
import com.met.diet.adapter.OnMealClickListener
import com.met.impilo.data.Demand
import com.met.impilo.data.meals.Meal
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.utils.Constants
import com.met.impilo.utils.Utils
import com.met.impilo.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_diet.*
import java.util.*


class DietFragment : Fragment() {

    private val TAG = javaClass.simpleName

    lateinit var viewModel: DietFragmentViewModel
    lateinit var loadingDialog: Dialog
    lateinit var mealsSummary: MealsSummary
    var percentageDemand: Demand? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diet, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.window!!.statusBarColor = ContextCompat.getColor(view.context, com.met.impilo.R.color.colorPrimaryDark)

        viewModel = ViewModelProviders.of(this).get(DietFragmentViewModel::class.java)

        loadingDialog = ViewUtils.createLoadingDialog(view.context)!!

        val adapter = MealsExpandableListAdapter(view.context, getMealsFromSharedPreferences())

        meals_list_view.apply {
            setmMenuStickTo(SwipeMenuListView.STICK_TO_SCREEN)
            setAdapter(adapter)
            setChildDivider(null)
            divider = null
            setMenuCreator(createSwipeMenu())
            closeInterpolator = FastOutSlowInInterpolator()
        }

        adapter.meals.forEach {
            meals_list_view.expandGroup(it.id)
        }

        viewModel.getAllMealsByDateId(Utils.dateToId(Date()))
        viewModel.getMyMealsSummary()


        adapter.setOnMealClickListener(object : OnMealClickListener {
            override fun onMealClick(groupPosition: Int) {
                Log.e("MEAL CLICKED", "Meal num : $groupPosition")
                if (!meals_list_view.isGroupExpanded(groupPosition)) meals_list_view.expandGroup(groupPosition)
                else meals_list_view.collapseGroup(groupPosition)
            }

            override fun onAddProductClick(groupPosition: Int, mealName : String) {
                Log.e("MEAL NEW PRODUCT", "Meal num : $groupPosition")
                val intent = Intent(context, SearchActivity::class.java)
                intent.putExtra(Constants.MEAL_ID_REQUEST, groupPosition)
                intent.putExtra(Constants.MEAL_NAME_REQUEST, mealName)
                startActivity(intent)
            }

        })

        meals_list_view.setOnMenuItemClickListener { group, child, menu, index ->
            when(index){
                1 -> {
                    val builder = AlertDialog.Builder(context!!).apply {
                        setMessage("Do you want to delete this products ?")
                        setTitle("Delete products")
                        setPositiveButton("Yes") { dialog, which ->
                            Log.e(TAG, "Deleting product")
                        }
                        setNegativeButton("No") { dialog, which ->
                            Log.e(TAG, "Deleting canceled")
                        }
                    }
                    val dialog = builder.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, com.met.impilo.R.color.colorAccent))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, com.met.impilo.R.color.textColor))
                    true
                }
                else -> false
            }
        }

        val allMealsObserver = Observer<List<Meal>> {
            if (!it.isNullOrEmpty()) {
                adapter.meals = it
                adapter.notifyDataSetChanged()
                loadingDialog.dismiss()
            } else {
                viewModel.getUserMealSet()
                loadingDialog.dismiss()
            }
        }

        val userMealSetObserver = Observer<UserMealSet> {
            if(it != null){
                adapter.meals = viewModel.generateMeals(it)
                adapter.notifyDataSetChanged()
                viewModel.storeStringListSharedPreferences(activity!!.getSharedPreferences("meals",Context.MODE_PRIVATE), it)
            }
        }

        viewModel.getAllMeals().observe(this, allMealsObserver)
        viewModel.getMealSet().observe(this, userMealSetObserver)

        val mealsSummaryObserver = Observer<MealsSummary> {
            mealsSummary = it ?: MealsSummary()
            viewModel.getMyDemand()
        }

        val calculatedDemandObserver = Observer<Demand> {
            if(it != null){
                percentageDemand = it
                updateProgress()
            }
        }

        viewModel.getMealsSummary().observe(this, mealsSummaryObserver)
        viewModel.getPercentegeDemand().observe(this, calculatedDemandObserver)


    }


    private fun createSwipeMenu(): SwipeMenuExpandableCreator = object : SwipeMenuExpandableCreator {
        override fun createGroup(menu: SwipeMenu) {}
        override fun createChild(menu: SwipeMenu) {

            val editItem = SwipeMenuItem(context).apply {
                background = ContextCompat.getDrawable(activity!!, com.met.impilo.R.drawable.menu_item_background)
                width = 200
                setIcon(com.met.impilo.R.drawable.ic_edit)
                icon.setTint(ContextCompat.getColor(activity!!, com.met.impilo.R.color.secondAccent))
            }

            menu.addMenuItem(editItem)

            val deleteItem = SwipeMenuItem(context).apply {
                background = ColorDrawable(ContextCompat.getColor(activity!!, com.met.impilo.R.color.menuIconGradientEnd))
                width = 200
                setIcon(com.met.impilo.R.drawable.ic_delete)
                icon.setTint(ContextCompat.getColor(activity!!, com.met.impilo.R.color.colorAccent))
            }

            menu.addMenuItem(deleteItem)
        }
    }

    private fun getMealsFromSharedPreferences(): List<Meal> {

        val list = viewModel.getMealsSetFromSharedPreferences(activity!!.getSharedPreferences("meals",Context.MODE_PRIVATE))
        if(list.isNullOrEmpty())
            loadingDialog.show()

        return list
    }


    private fun updateProgress(){
        Log.e(TAG, "Updating progress")

        kcal_progress.progress = 0f
        carbohydrates_progress.progress = 0f
        proteins_progress.progress = 0f
        fats_progress.progress = 0f

        kcal_progress.labelText = StringBuilder("" + mealsSummary.kcalSum + " / " + viewModel.demand.calories + " kcal").toString()
        kcal_progress.progress = percentageDemand!!.calories.toFloat()

        carbohydrates_progress.progress = percentageDemand!!.carbohydares.toFloat()
        carbohydrates_progress.labelText = StringBuilder("" + mealsSummary.carbohydratesSum.toInt() + " / " + viewModel.demand.carbohydares + " g").toString()
        proteins_progress.progress = percentageDemand!!.proteins.toFloat()
        proteins_progress.labelText = StringBuilder("" + mealsSummary.proteinsSum.toInt() + " / " + viewModel.demand.proteins + " g").toString()
        fats_progress.progress = percentageDemand!!.fats.toFloat()
        fats_progress.labelText = StringBuilder("" + mealsSummary.fatsSum.toInt() + " / " + viewModel.demand.fats + " g").toString()

        Log.e(TAG, "Calories from viewmodel ${mealsSummary.kcalSum}")

    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllMealsByDateId(Utils.dateToId(Date()))
        Log.e(TAG, "Runned onResume. PercentageDemand : $percentageDemand")
        viewModel.getMyMealsSummary()
        loadingDialog.show()
    }

}
