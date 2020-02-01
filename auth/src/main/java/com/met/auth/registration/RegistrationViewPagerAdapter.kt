package com.met.auth.registration

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.met.auth.registration.configuration.BaseFragment

class RegistrationViewPagerAdapter(fm : FragmentManager, private val fragments : List<BaseFragment>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): BaseFragment{
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}

