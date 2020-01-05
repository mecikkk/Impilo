package com.met.auth.login

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.met.auth.R
import com.met.auth.registration.Registration
import com.met.impilo.network.ConnectionInformation
import com.met.impilo.utils.Constants
import com.met.impilo.utils.ScreenUtils
import com.met.impilo.utils.Utils
import com.met.impilo.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Aktywność Logowania
 */
class Login : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private var impiloDefaultPosition = IntArray(2)
    private var shouldStartAnim: Boolean = true

    private lateinit var connectionInformation: ConnectionInformation
    private lateinit var vModel: LoginViewModel
    private lateinit var networkDialog: Dialog
    private lateinit var loadingDialog: Dialog
    private lateinit var receiver: BroadcastReceiver
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private lateinit var firebaseAuth: FirebaseAuth

    private var googleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        supportActionBar?.hide()

        vModel = ViewModelProviders.of(this)[LoginViewModel::class.java]
        connectionInformation = ViewModelProviders.of(this)[ConnectionInformation::class.java]

        initGoogleSignIn()

        init()
        setImpiloPositionInCenter()

        onClickListeners()
        initObservers()

        authListener = FirebaseAuth.AuthStateListener {
            if (firebaseAuth.currentUser != null) {
                vModel.isAccountConfigured {
                    if (!it) {
                        Log.e(TAG, "Start only configuration...")
                        startRegistrationActivity(true)
                    } else {
                        setResult(Activity.RESULT_OK)
                        finish()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                }
            }
        }

    }

    /**
     * W zależnosci od parametru :
     * true - Uruchamia aktywnosc rejestracji przechodząć bezpośrednio do konfiguracji
     * false - Uruchamia pełną rejestrację wraz z konfiguracją
     */
    private fun startRegistrationActivity(showOnlyConfiguration: Boolean) {
        Log.e(TAG, "Starting registrationnn")
        val intent = Intent(applicationContext, Registration::class.java)
        intent.putExtra(Constants.ONLY_CONFIGURATION, showOnlyConfiguration)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     * Inicjacja obserwatorów z ViewModelu
     */
    private fun initObservers() {
        val connectionObserver = Observer<Boolean> {
            if (it) {
                networkDialog.hide()
                if (shouldStartAnim) {
                    startAnimation()
                    shouldStartAnim = false
                }
            } else {
                networkDialog = createNetworkDialog()
                networkDialog.show()
            }
        }

        val signInWithEmailObserver = Observer<Boolean> {
            if (it) ViewUtils.createSnackbar(bg, bg.resources.getString(com.met.impilo.R.string.success_login)).show()
            else {
                loadingDialog.hide()
                ViewUtils.createSnackbar(bg, bg.resources.getString(com.met.impilo.R.string.failed_signing_in)).show()
            }
        }

        val isGoogleAccountConfiguredObserver = Observer<Boolean> {
            Log.e(TAG, "isGoogleAccountConfigured : $it")
            loadingDialog.hide()
            if(it) {
                setResult(Activity.RESULT_OK)
                finish()
            }else {
                Log.e(TAG, "Google account not configured.")

            }
//            if (!it) {
//                startRegistrationActivity(true)
//            } else ViewUtils.createSnackbar(bg, bg.resources.getString(com.met.impilo.R.string.success_login)).show()
        }

        connectionInformation.isConnected.observe(this, connectionObserver)
        vModel.signInWithEmailSuccess.observe(this, signInWithEmailObserver)
        vModel.isGoogleAccountConfigured.observe(this, isGoogleAccountConfiguredObserver)
    }

    /**
     * Inicjacja listenerów kliknięć
     */
    private fun onClickListeners() {
        login_button.setOnClickListener {
            if (validate()) {
                loadingDialog.show()
                ViewUtils.hideKeyboard(it)
                vModel.signInWithEmail(email_textfield.text.toString(), password_textfield.text.toString())
            }
        }

        google_login.setOnClickListener {
            loadingDialog.show()
            signInWithGoogle()
        }

        create_account_button.setOnClickListener {
            startRegistrationActivity(false)
        }
    }

    /**
     * Integracja aplikacji z funkcją logowania za pomocą konta Google
     */
    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            "544302128215-e1p5sc69at8eiulrc7lf1rqofafdlk4n.apps.googleusercontent.com").requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * Wywołanie aktywności logowania za pomocą konta Google
     */
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, Constants.RC_GOOGLE_SIGN_IN)

    }

    /**
     * Inicjacja podstawowych elementów aktywnosci
     */
    private fun init() {
        // Tworzenie BroadcastReceiver'a z informacją o połączeniu z internetem
        receiver = connectionInformation.connectionReceiver(this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

        firebaseAuth = FirebaseAuth.getInstance()

        networkDialog = createNetworkDialog()

        loadingDialog = ViewUtils.createLoadingDialog(bg.context)!!

    }

    /**
     * Zwraca obiekt typu Dialog z informacją o braku połączenia z internetem
     */
    private fun createNetworkDialog(): Dialog {
        val networkDialog = ViewUtils.createInternetConnectionDialog(this)
        networkDialog.setOnCancelListener {
            finish()
        }
        return networkDialog
    }

    /**
     * Walidacja wprowadzonych informacji.
     */
    private fun validate(): Boolean {
        val invalidEmail = (Utils.isEditTextEmpty(email_textfield, email_input_layout, resources.getString(com.met.impilo.R.string.field_required)) || !Utils.isEmailValid(
            email_textfield.text.toString(), email_input_layout, resources.getString(com.met.impilo.R.string.invalid_email_format)))

        val invalidPassword =
            (Utils.isEditTextEmpty(password_textfield, password_input_layout, resources.getString(com.met.impilo.R.string.field_required)) || !Utils.isPasswordLengthValid(
                password_textfield.length(), password_input_layout, resources.getString(com.met.impilo.R.string.password_length_error)))

        Log.e(TAG, "email : $invalidEmail  pass : $invalidPassword")

        return (!invalidEmail && !invalidPassword)

    }

    /**
     * Uruchamia animację pojawiania się elementów na ekranie
     */
    private fun startAnimation() {
        val views = ArrayList<View>()
        views.add(email_input_layout)
        views.add(password_input_layout)
        views.add(login_button)
        views.add(or_you_can_layout)
        views.add(create_account_button)
        views.add(google_login)
        views.add(forget_password_button)

        app_name_textview.animate().translationY(impiloDefaultPosition[1].toFloat()).setStartDelay(2000).setDuration(500).setInterpolator(LinearOutSlowInInterpolator()).start()
        bg.animate().alpha(0.5f).setStartDelay(2000).setDuration(500).setInterpolator(LinearOutSlowInInterpolator()).start()

        var i = 2200L
        for (v in views) {
            v.animate().alpha(1f).translationYBy(30f).setDuration(250).setStartDelay(i).start()
            i += 50L
        }

        // Hide Impilo text if layout with login fileds takes up more than 75% space
        if (login_fields_layout.height / ScreenUtils.getScreenHeight(this) > 0.75) {
            app_name_textview.animate().alpha(0f).setStartDelay(2100).setDuration(500).setInterpolator(LinearOutSlowInInterpolator()).start()
        }
    }

    /**
     * Ustala wyśrodkowaną pozycję względem ekranu dla napisu "Impilo"
     */
    private fun setImpiloPositionInCenter() {
        app_name_textview.getLocationInWindow(impiloDefaultPosition)

        val point = Point()
        val manager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.defaultDisplay.getSize(point)

        val centerY = point.y / 3

        app_name_textview.translationY = centerY - (app_name_textview.height / 2).toFloat()
    }

    /**
     * Rejestruje nasluchiwanie stanu polączenia z internetem, oraz listenera autoryzacji. Metoda jest wywoływana w momencie uruchomienia aktywności
     */
    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        firebaseAuth.addAuthStateListener(authListener)
    }

    /**
     * Odłącza nasłuchiwanie stanu połączenia z internetem, oraz odłącza listener autoryzacji. Metoda wywoływana jest w momencie pojawienia się innej aktywności.
     */
    override fun onPause() {
        super.onPause()
        shouldStartAnim = false
        unregisterReceiver(receiver)
        networkDialog.dismiss()
        loadingDialog.dismiss()
        firebaseAuth.removeAuthStateListener(authListener)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    /**
     * Metoda przechwytuje wynik przesłany z innej aktywności.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                vModel.signInWithGoogle(account!!)
                Log.e(TAG, "Google sign was successful!")
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed : ", e)
                // ...
            }
        }
    }

}
