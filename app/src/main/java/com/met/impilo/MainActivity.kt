package com.met.impilo

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.fxn.OnBubbleClickListener
import com.google.firebase.auth.FirebaseAuth
import com.met.impilo.adapter.ViewPagerAdapter
import com.met.impilo.network.ConnectionInformation
import com.met.impilo.utils.Const
import com.met.impilo.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ViewPagerAdapter.OnChangeTrainingFragmentListener {

    private val TAG = javaClass.simpleName
    private val fragmentManager = supportFragmentManager
    private lateinit var connectionInformation: ConnectionInformation
    private lateinit var receiver: BroadcastReceiver
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var viewPagerAdapter : ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        connectionInformation = ViewModelProviders.of(this)[ConnectionInformation::class.java]
        receiver = connectionInformation.connectionReceiver(this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

        val connectionObserver = Observer<Boolean> {
            if (it) {
                connection_state_bar.text = getString(R.string.back_online)
                connection_state_bar.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.positiveGreen))
                connection_state_bar.animate().setStartDelay(1000).alpha(0f).setDuration(300).setInterpolator(LinearOutSlowInInterpolator()).withEndAction {
                    connection_state_bar.visibility = View.GONE
                }.start()
            } else {
                connection_state_bar.text = getString(R.string.no_internet_connection)
                connection_state_bar.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.actionBarIcon))
                connection_state_bar.visibility = View.VISIBLE
                connection_state_bar.animate().alpha(1f).setDuration(300).setInterpolator(LinearOutSlowInInterpolator()).start()
            }
        }

        connectionInformation.isConnected.observe(this, connectionObserver)

        init()

        initWhenLoggedIn()

        viewModel.isWorkoutConfigCompleted().observe(this, Observer {
            viewPagerAdapter.isWorkoutConfigurationCompleted = it
            Log.i(TAG, "WorkoutConfig state changed to : $it")
        })

    }

    private fun initWhenLoggedIn() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(Intent(this, Class.forName("com.met.auth.login.Login")), Const.LOGIN_REQUEST)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {

            viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
            main_view_pager.adapter = viewPagerAdapter
            viewPagerAdapter.onChangeTrainingFragmentListener = this

            viewModel.checkIsWorkoutConfigCompleted()

            bubbleTabBar.addBubbleListener(object : OnBubbleClickListener {
                override fun onBubbleClick(id: Int) {
                    Log.e(TAG, "Clicked : $id")
                    when (id) {
                        R.id.home -> {
                            main_view_pager.currentItem = 0
                        }
                        R.id.diet -> {
                            main_view_pager.currentItem = 1
                        }
                        R.id.trainings -> {
                            main_view_pager.currentItem = 2
                        }
                    }
                }

            })

//            main_view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                override fun onPageSelected(position: Int) {
//                    super.onPageSelected(position)
//                    bubbleTabBar.setSelected(position)
//                }
//            })

            main_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    bubbleTabBar.setSelected(position)
                }

            })
        }
    }


    private fun init() {
        bubbleTabBar.setSelected(0)
        bubbleTabBar.setSelected(1)
        bubbleTabBar.setSelected(2)
        bubbleTabBar.setSelected(3)
        bubbleTabBar.setSelected(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Const.LOGIN_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "Closing app. Login canceled")
                finish()
            } else if (resultCode == Activity.RESULT_OK) {
                initWhenLoggedIn()
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun changeWorkoutConfigFragmentToWorkoutFragment() {
        viewModel.checkIsWorkoutConfigCompleted()
        viewPagerAdapter.notifyDataSetChanged()
        main_view_pager.currentItem = 2
    }
}
