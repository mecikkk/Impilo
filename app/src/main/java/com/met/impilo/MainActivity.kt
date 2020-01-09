package com.met.impilo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import com.fxn.OnBubbleClickListener
import com.google.firebase.auth.FirebaseAuth
import com.met.impilo.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        init()

        initWhenLoggedIn()

    }

    private fun initWhenLoggedIn() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(Intent(this, Class.forName("com.met.auth.login.Login")), Constants.LOGIN_REQUEST)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {

            fragmentManager.beginTransaction().replace(R.id.framelayout, HomeFragment()).commit()

            bubbleTabBar.addBubbleListener(object : OnBubbleClickListener {
                override fun onBubbleClick(id: Int) {
                    Log.e(TAG, "Clicked : $id")
                    when (id) {
                        R.id.home -> {
                            val fragment = HomeFragment()
                            fragment.enterTransition = Fade()
                            fragment.exitTransition = Fade()
                            fragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit()
                        }
                        R.id.diet -> {
                            val fragment: Fragment = Class.forName("com.met.diet.DietFragment").newInstance() as Fragment
                            fragment.enterTransition = Fade()
                            fragment.exitTransition = Fade()
                            fragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit()
                        }
                        R.id.trainings -> {
                            val fragment: Fragment = Class.forName("com.met.workout.WorkoutsFragment").newInstance() as Fragment
                            fragment.enterTransition = Fade()
                            fragment.exitTransition = Fade()
                            fragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit()
                        }
                    }
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

        if (requestCode == Constants.LOGIN_REQUEST){
            if(resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "Closing app. Login canceled")
                finish()
            } else if (resultCode == Activity.RESULT_OK){
                initWhenLoggedIn()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings -> {
                FirebaseAuth.getInstance().signOut()
                com.met.impilo.utils.ViewUtils.createSnackbar(main_container, "Settings clicked").show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
