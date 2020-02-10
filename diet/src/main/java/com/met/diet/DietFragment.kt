package com.met.diet


import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.data.food.ServingType
import com.met.impilo.data.meals.Meal
import com.met.impilo.data.meals.MealsSummary
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.utils.*
import kotlinx.android.synthetic.main.fragment_diet.*
import java.util.*


class DietFragment : Fragment() {

    private val TAG = javaClass.simpleName

    private lateinit var viewModel: DietFragmentViewModel
    private lateinit var loadingDialog: Dialog

    private var selectedDate = Date()

    private lateinit var mealsAdapter: MealsExpandableListAdapter

    private lateinit var mealsSummary: MealsSummary
    private var percentageDemand: Demand? = null
    private var fullDemand : Demand? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diet, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.window!!.statusBarColor = ContextCompat.getColor(view.context, com.met.impilo.R.color.colorPrimaryDark)

        viewModel = ViewModelProviders.of(this).get(DietFragmentViewModel::class.java)

        loadingDialog = ViewUtils.createLoadingDialog(view.context)!!

        mealsAdapter = MealsExpandableListAdapter(view.context, getMealsFromSharedPreferences())


        meals_list_view.apply {
            setmMenuStickTo(SwipeMenuListView.STICK_TO_SCREEN)
            setAdapter(mealsAdapter)
            setChildDivider(null)
            divider = null
            setMenuCreator(createSwipeMenu())
            closeInterpolator = FastOutSlowInInterpolator()
        }

        mealsAdapter.meals.forEach {
            meals_list_view.expandGroup(it.id)
        }

//        viewModel.getAllMealsByDateId(selectedDate.toId())
//        viewModel.getMyMealsSummary(selectedDate)
//        Log.e("GET_DEMAND_ONCREATE ", "--------------------------------------------")


        mealsAdapter.setOnMealClickListener(object : OnMealClickListener {
            override fun onMealClick(groupPosition: Int) {
                Log.e("MEAL CLICKED", "Meal num : $groupPosition")
                if (!meals_list_view.isGroupExpanded(groupPosition)) meals_list_view.expandGroup(groupPosition)
                else meals_list_view.collapseGroup(groupPosition)
            }

            override fun onAddProductClick(groupPosition: Int, mealName: String) {
                Log.e("MEAL NEW PRODUCT", "Meal num : $groupPosition")
                val intent = Intent(context, SearchActivity::class.java)
                intent.putExtra(Const.MEAL_ID_REQUEST, groupPosition)
                intent.putExtra(Const.MEAL_NAME_REQUEST, mealName)
                startActivity(intent)
            }

            override fun onProductClick(groupPosition: Int, productPosition: Int) {
                editProduct(groupPosition, productPosition)
            }

        })

        meals_list_view.setOnMenuItemClickListener { group, child, _, index ->
            when (index) {
                0 -> {
                    editProduct(group, child)
                    true
                }
                1 -> {
                    Log.e(TAG, "Product to delete : $group | $child")
                    val builder = AlertDialog.Builder(context!!).apply {
                        setMessage(getString(com.met.impilo.R.string.want_delete_product))
                        setTitle(getString(com.met.impilo.R.string.delete_product))
                        setPositiveButton(getString(com.met.impilo.R.string.yes)) { _, _ ->
                            loadingDialog.show()
                            Log.e(TAG, "Deleting product")
                            viewModel.removeMealProduct(selectedDate.toId(), group, child) {
                                if (it) {
                                    loadingDialog.hide()
                                    ViewUtils.createSnackbar(diet_content, getString(com.met.impilo.R.string.product_delete_success)).show()
                                    viewModel.getAllMealsByDateId(selectedDate.toId())
                                    viewModel.getMyMealsSummary(selectedDate)
                                    Log.e("GET_DEMAND_REMOVE", "--------------------------------------------")
                                } else {
                                    loadingDialog.hide()
                                    ViewUtils.createSnackbar(diet_content, getString(com.met.impilo.R.string.product_delete_error)).show()
                                }
                            }
                        }
                        setNegativeButton(getString(com.met.impilo.R.string.no)) { _, _ ->
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

        calendar_action.setOnClickListener {
            val dialog = ViewUtils.getDatePicker(view, false, selectedDate){ date ->
                selectedDate = date

                if(selectedDate.toId() == Date().toId())
                    action_bar_title.text = getString(com.met.impilo.R.string.today)
                else
                    action_bar_title.text = selectedDate.toStringDate()

                Log.e(TAG, "Selected Date : $selectedDate")

                viewModel.getAllMealsByDateId(selectedDate.toId())

                mealsAdapter.meals.forEach {
                    meals_list_view.expandGroup(it.id)
                }

                // Update UI
                viewModel.getMyMealsSummary(selectedDate)
                Log.e("GET_DEMAND_CALENDAR", "--------------------------------------------")
            }
            dialog.show()
            dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
            dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE)

        }


        val allMealsObserver = Observer<List<Meal>> {
            Log.e(TAG, "Getting allMeals")
            if (!it.isNullOrEmpty()) {
                mealsAdapter.meals = it
                mealsAdapter.notifyDataSetChanged()
                loadingDialog.dismiss()
            } else {
                viewModel.getUserMealSet()
                loadingDialog.dismiss()
            }
        }

        val userMealSetObserver = Observer<UserMealSet> {
            if (it != null) {
                mealsAdapter.meals = viewModel.generateMeals(it)
                mealsAdapter.notifyDataSetChanged()
                viewModel.saveMealsSetInSharedPreferences(activity!!.getSharedPreferences("meals", Context.MODE_PRIVATE), it)
            }
        }

        viewModel.getAllMeals().observe(this, allMealsObserver)
        viewModel.getMealSet().observe(this, userMealSetObserver)

        val mealsSummaryObserver = Observer<MealsSummary> {
            mealsSummary = it ?: MealsSummary()
            viewModel.getMyDemand()
        }

        val fullDemandObserver = Observer<Demand> {
            if(it != null){
                fullDemand = it
                viewModel.getCalculatedDemand(selectedDate)
            }
        }

        val calculatedDemandObserver = Observer<Demand> {
            if (it != null) {
                percentageDemand = it
                updateProgress()
            }
        }

        viewModel.getMealsSummary().observe(this, mealsSummaryObserver)
        viewModel.getFullDemand().observe(this, fullDemandObserver)
        viewModel.getPercentageDemand().observe(this, calculatedDemandObserver)


    }

    private fun editProduct(mealPosition: Int, productPosition: Int) {
        loadingDialog.show()
        val productBottomSheet = ProductBottomSheet(ViewUtils.isLightModeOn(context!!))

        viewModel.getProductById(mealsAdapter.meals[mealPosition].mealProducts[productPosition].productId) { foodProduct ->

            productBottomSheet.servingType = if (mealsAdapter.meals[mealPosition].mealProducts[productPosition].servingName == "gram") ServingType.GRAM
            else ServingType.PORTION
            productBottomSheet.servingSize = mealsAdapter.meals[mealPosition].mealProducts[productPosition].servingSize
            productBottomSheet.product = foodProduct
            productBottomSheet.shouldBeExpanded = true

            productBottomSheet.show(activity!!.supportFragmentManager, productBottomSheet.tag)
            loadingDialog.hide()
            productBottomSheet.onAddProductListener = object : ProductBottomSheet.OnAddProductListener {
                override fun onAddProduct(foodProduct: FoodProduct, servingSize: Float, servingType: ServingType) {

                    viewModel.updateMealProduct(selectedDate.toId(), mealPosition, productPosition, foodProduct, servingType, servingSize) {
                        Log.e(TAG, "Editing product result : $it")
                        if (it) {
                            productBottomSheet.dismiss()
                            ViewUtils.createSnackbar(diet_content, getString(com.met.impilo.R.string.product_update_success)).show()
                            viewModel.getAllMealsByDateId(selectedDate.toId())
                            viewModel.getMyMealsSummary(selectedDate)
                            Log.e("GET_DEMAND_ADD", "--------------------------------------------")
                        } else ViewUtils.createSnackbar(diet_content, getString(com.met.impilo.R.string.product_update_error)).show()
                    }
                }
            }
        }
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

        val list = viewModel.getMealsSetFromSharedPreferences(activity!!.getSharedPreferences("meals", Context.MODE_PRIVATE))
        if (list.isNullOrEmpty()) loadingDialog.show()

        return list
    }



    private fun updateProgress() {
        Log.e(TAG, "Updating progress")

        kcal_progress.progress = 0f
        carbohydrates_progress.progress = 0f
        proteins_progress.progress = 0f
        fats_progress.progress = 0f


        kcal_progress.labelText = StringBuilder("" + mealsSummary.kcalSum + " / " + fullDemand?.calories + " kcal").toString()
        kcal_progress.progress = percentageDemand!!.calories.toFloat()

        carbohydrates_progress.progress = percentageDemand!!.carbohydares.toFloat()
        carbohydrates_progress.labelText = StringBuilder("" + mealsSummary.carbohydratesSum.toInt() + " / " + fullDemand?.carbohydares + " g").toString()
        proteins_progress.progress = percentageDemand!!.proteins.toFloat()
        proteins_progress.labelText = StringBuilder("" + mealsSummary.proteinsSum.toInt() + " / " + fullDemand?.proteins + " g").toString()
        fats_progress.progress = percentageDemand!!.fats.toFloat()
        fats_progress.labelText = StringBuilder("" + mealsSummary.fatsSum.toInt() + " / " + fullDemand?.fats + " g").toString()

        Log.e(TAG, "Calories from viewmodel ${mealsSummary.kcalSum}")

    }

    override fun onResume() {
        super.onResume()
        selectedDate = Date()
        viewModel.getAllMealsByDateId(selectedDate.toId())
        Log.e(TAG, "Runned onResume. PercentageDemand : $percentageDemand")
        viewModel.getMyMealsSummary(selectedDate)
        loadingDialog.show()
    }

}
