package com.met.impilo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, Class.forName("com.met.auth.login.Login")))

        login_btn.setOnClickListener {
            startActivity(Intent(this, Class.forName("com.met.auth.login.Login")))
        }

        logout_fab.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }
    }
}
