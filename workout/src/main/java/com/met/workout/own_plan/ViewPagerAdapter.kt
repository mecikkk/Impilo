package com.met.workout.own_plan

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter (fm : FragmentManager, private val fragments : List<Fragment>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment{
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}