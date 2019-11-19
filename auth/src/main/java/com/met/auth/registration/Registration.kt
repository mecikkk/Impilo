package com.met.auth.registration

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.met.auth.R
import com.met.auth.registration.configuration.RegistrationFragment
import kotlinx.android.synthetic.main.activity_registration.*


class Registration : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private var titles: Array<String>? = null
    private var descriptions: Array<String>? = null
    private var fragments: MutableList<Fragment>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        supportActionBar?.hide()

        titles = resources.getStringArray(com.met.impilo.R.array.firstConfigTitles)
        descriptions = resources.getStringArray(com.met.impilo.R.array.firstConfigDescriptions)

        (fragments as ArrayList<Fragment>).add(RegistrationFragment())

        val registrationViewPagerAdapter = RegistrationViewPagerAdapter(supportFragmentManager, fragments as ArrayList<Fragment>)
        registration_view_pager.adapter = registrationViewPagerAdapter
        indicator.setViewPager(registration_view_pager)

        next_step.visibility = View.GONE
        previous_step.visibility = View.GONE

        registration_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                Log.e(TAG, "Position : $position")
                when(position){
                    0 -> {
                        next_step.visibility = View.GONE
                        previous_step.visibility = View.GONE
                    }
                    1 -> {
                        next_step.visibility = View.VISIBLE
                    }
                }
            }

        })
    }
}

