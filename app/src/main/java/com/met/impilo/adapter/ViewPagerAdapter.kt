package com.met.impilo.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.met.impilo.HomeFragment
import com.met.impilo.ProfileFragment

class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), OnEndOfConfigurationListener {

    var isWorkoutConfigurationCompleted = false
    lateinit var onChangeTrainingFragmentListener : OnChangeTrainingFragmentListener
    val homeFragment = HomeFragment.newInstance()

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> homeFragment
            1 -> Class.forName("com.met.diet.DietFragment").newInstance() as Fragment
            2 -> {
                Log.d("ViewPagerAdapter", "Create fragment POSITION 2")
                if(!isWorkoutConfigurationCompleted) {
                    Log.d("ViewPagerAdapter", "Creating configuration workout")
                    val fragment = Class.forName("com.met.workout.WorkoutsConfigurationFragment").newInstance() as Fragment
                    (fragment as WorkoutBaseFragment).onEndOfConfigurationListener = this
                    fragment
                } else {
                    Log.d("ViewPagerAdapter", "Creating workout without configuration")
                    Class.forName("com.met.workout.WorkoutsFragment").newInstance() as Fragment
                }
            }
            3 -> {
                Log.d("ViewPagerAdapter", "Create fragment 3 - ProfileFragment")
                ProfileFragment.newInstance()
            }
            else -> Fragment()
        }
    }

    override fun getCount(): Int = 4

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun onEndOfConfiguration() {
        Log.e("ViewPagerAdapter", "Running listener")
        isWorkoutConfigurationCompleted = true
        Log.e("ViewPagerAdapter", "IsWokroutCOnficompleted : $isWorkoutConfigurationCompleted")
        onChangeTrainingFragmentListener.changeWorkoutConfigFragmentToWorkoutFragment()
    }

    interface OnChangeTrainingFragmentListener {
        fun changeWorkoutConfigFragmentToWorkoutFragment()
    }

}