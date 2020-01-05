package com.met.auth.registration

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.met.auth.R
import com.met.auth.registration.configuration.*
import com.met.impilo.data.*
import com.met.impilo.data.meals.UserMealSet
import com.met.impilo.utils.Constants
import com.met.impilo.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class Registration : AppCompatActivity() , BaseFragment.OnDataSendListener {

    private lateinit var viewModel : RegistrationViewModel
    private val TAG = javaClass.simpleName

    private lateinit var titles: Array<String>
    private lateinit var descriptions: Array<String>
    private var fragments: MutableList<BaseFragment>? = ArrayList()
    private lateinit var onPageChangeListener : OnPageChangeListener
    private lateinit var loadingDialog : Dialog

    private var showOnlyConfiguration : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        showOnlyConfiguration = intent.getBooleanExtra(Constants.ONLY_CONFIGURATION, false)

        viewModel = ViewModelProviders.of(this).get(RegistrationViewModel::class.java)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        supportActionBar?.hide()

        init()

        createFragments()

        initViewPager()

        viewPagerListener()

//        TODO(" przypomnienie hasla potrzebne ? ")

        initOnClickListeners()

        initObservers()
    }

    /**
     * Inicjuje listener ViewPager'a zawierającego Fragmenty z poszczególnymi ekranami rejestracji i konfiguracji
     */
    private fun viewPagerListener() {
        registration_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                Log.e(TAG, "Position : $position")
                when (position) {
                    0 -> {
                        next_step.visibility = View.GONE
                        previous_step.visibility = View.GONE
                    }
                    1 -> {
                        previous_step.visibility = View.GONE
                        next_step.visibility = View.VISIBLE
                    }
                    2 -> {
                        previous_step.visibility = View.VISIBLE
                        next_step.visibility = View.VISIBLE
                    }
                    3 -> {
                        next_step.apply {
                            this.text = resources.getText(com.met.impilo.R.string.next)
                            this.strokeColor = ColorStateList.valueOf(Color.WHITE)
                            this.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                        }
                    }
                    4 -> {
                        next_step.apply {
                            this.text = resources.getText(com.met.impilo.R.string.finish)
                            this.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(context, com.met.impilo.R.color.colorAccent))
                            this.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,com.met.impilo.R.color.colorAccent))
                        }
                    }
                }
                changeListener(position)
                changeTitleAndDescription(position)
            }
        })
    }

    /**
     * Inicjacja listenerów kliknięcia
     */
    private fun initOnClickListeners() {
        next_step.setOnClickListener {
            if (onPageChangeListener.onNextPageClick()) {
                if (registration_view_pager.currentItem != 4)
                    registration_view_pager.currentItem = registration_view_pager.currentItem + 1
                else
                    loadingDialog.show()
            }
            ViewUtils.hideKeyboard(it)
        }

        previous_step.setOnClickListener {
            registration_view_pager.currentItem = registration_view_pager.currentItem - 1
            ViewUtils.hideKeyboard(it)
        }
    }

    private fun closeApp(){
        finish()
        return
    }

    /**
     * Inicjacja obserwatorów
     */
    private fun initObservers() {
        val registrationSuccessObserver = Observer<Boolean> {
            if (it) {
                Log.e(TAG, "Registration success. Finishing activity...")
                loadingDialog.hide()
                closeApp()

            } else ViewUtils.createSnackbar(bg, "Registration failed").show()
        }
        viewModel.getRegistrationSuccess().observe(this, registrationSuccessObserver)

    }


    private fun init() {
        titles = resources.getStringArray(com.met.impilo.R.array.firstConfigTitles)
        descriptions = resources.getStringArray(com.met.impilo.R.array.firstConfigDescriptions)

        loadingDialog = ViewUtils.createLoadingDialog(this)!!

        next_step.visibility = View.GONE
        previous_step.visibility = View.GONE
    }


    private fun initViewPager() {
        val registrationViewPagerAdapter = RegistrationViewPagerAdapter(supportFragmentManager, fragments as ArrayList<BaseFragment>)
        registration_view_pager.adapter = registrationViewPagerAdapter
        indicator.setViewPager(registration_view_pager)

        // W przypadku stworzonego konta, ale niedokonczonej rejestracji pokazywany jest od razu drugi fragment
        if(showOnlyConfiguration){
            next_step.visibility = View.VISIBLE
            registration_view_pager.currentItem = 1
            ((fragments as ArrayList<BaseFragment>)[1] as BasicInformationFragment).showOnlyConfiguration(showOnlyConfiguration)
            changeTitleAndDescription(1)
            changeListener(1)
        }
    }

    private fun createFragments() {
        (fragments as ArrayList<BaseFragment>).add(RegistrationFragment.newInstance())
        (fragments as ArrayList<BaseFragment>).add(BasicInformationFragment.newInstance())
        (fragments as ArrayList<BaseFragment>).add(SomatotypeFragment.newInstance())
        (fragments as ArrayList<BaseFragment>).add(WorkoutStyleFragment.newInstance())
        (fragments as ArrayList<BaseFragment>).add(LifestyleFragment.newInstance())

        fragments?.forEach {
            it.setOnDataSendListener(this)
        }

        setOnPageChangeListener(((fragments as ArrayList<BaseFragment>)[1] as BasicInformationFragment))
    }


    private fun changeListener(position: Int) {
        when (position) {
            1 -> setOnPageChangeListener(((fragments as ArrayList<BaseFragment>)[1] as BasicInformationFragment))
            2 -> setOnPageChangeListener(((fragments as ArrayList<BaseFragment>)[2] as SomatotypeFragment))
            3 -> setOnPageChangeListener(((fragments as ArrayList<BaseFragment>)[3] as WorkoutStyleFragment))
            4 -> setOnPageChangeListener(((fragments as ArrayList<BaseFragment>)[4] as LifestyleFragment))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()
    }

    private fun changeTitleAndDescription(position : Int){
        title_textview.text = titles[position]
        description_textview.text = descriptions[position]
    }

    private fun setOnPageChangeListener(callback : OnPageChangeListener){
        this.onPageChangeListener = callback
    }

    override fun accountCreated() {
        Log.i(TAG, "Account created !")
        registration_view_pager.setCurrentItem(1, true)
    }

    override fun basicInformation(height: Int, weight: Float, waist: Float, gender: Gender, birthDate : Date?) {
        if(showOnlyConfiguration)
            viewModel.sendBasicInformation(height, weight, waist, gender, birthDate)
        else
            viewModel.sendBasicInformation(height, weight, waist, gender)
        ((fragments as ArrayList<BaseFragment>)[2] as SomatotypeFragment).setGender(gender)
    }

    override fun somatotype(somatotype: Somatotype) {
        viewModel.sendSomatotype(somatotype)
    }

    override fun workoutStyle(workoutStyle: WorkoutStyle, trainingsPerWeek: Int, trainingTime: Int) {
        viewModel.sendWorkoutStyle(workoutStyle, trainingsPerWeek, trainingTime)
    }

    override fun lifestyle(lifestyle: Lifestyle, goal: Goal) {
        viewModel.sendDefaultUserMealSet(generateDefaultUserMealSet())
        viewModel.sendLifestyle(lifestyle, goal)
    }

    private fun generateDefaultUserMealSet() : UserMealSet{

        val mealSet = listOf(resources.getString(com.met.impilo.R.string.breakfast), resources.getString(com.met.impilo.R.string.brunch), resources.getString(com.met.impilo.R.string.lunch),
            resources.getString(com.met.impilo.R.string.supper), resources.getString(com.met.impilo.R.string.dinner))

        val mealsString = TextUtils.join(";", mealSet)
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.REF_USER_MEAL_SET, mealsString)
        editor.apply()

        val userMealSet = UserMealSet()
        userMealSet.mealsSet = mealSet
        userMealSet.mealsQuantity = 5

        return userMealSet
    }


    interface OnPageChangeListener {
        fun onNextPageClick() : Boolean
    }
}

