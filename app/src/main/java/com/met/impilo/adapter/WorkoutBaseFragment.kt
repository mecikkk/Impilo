package com.met.impilo.adapter

import androidx.fragment.app.Fragment

abstract class WorkoutBaseFragment : Fragment() {

    lateinit var onEndOfConfigurationListener : OnEndOfConfigurationListener

}