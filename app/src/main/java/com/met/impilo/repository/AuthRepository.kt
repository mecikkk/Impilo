package com.met.impilo.repository

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepository : FirebaseRepository() {

    override val TAG = javaClass.simpleName

    companion object {
        fun newInstance() = AuthRepository()
    }

    private val firebaseAuth = FirebaseAuth.getInstance()

    // Wykorzystanie typu funkcyjnego (signInSuccess) - (w Javie parametrem bylby interfejs)
    fun signInWithEmail(email: String, pass: String, signInSuccess: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            signInSuccess(true)
        }.addOnFailureListener {
                signInSuccess(false)
            }
    }

    fun signUpWithEmail(email: String, pass: String, signUpSuccess: (Boolean) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener {
            Log.i(TAG, "Account created. UID : " + it.user!!.uid)
            //sendDataToDatabase(it.user!!.uid, name, birthDate)
            signUpSuccess(true)
        }.addOnFailureListener {
                Log.e(TAG, "Account creating failure. Caused by :" + it.cause + " | " + it.message)
                signUpSuccess(false)
            }
    }

    fun signUpWithGoogle(acct: GoogleSignInAccount, signInSuccess: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult?> ->
            if (task.isSuccessful) { // Sign in success
                val user = firebaseAuth.currentUser
                Log.d(TAG, "signInWithCredential: success")
                Log.d(TAG, "Google User : " + user!!.uid + " " + user.displayName + " " + user.email)
                signInSuccess(true)
            } else { // Sign in fails
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                signInSuccess(false)
            }
        }
    }

}