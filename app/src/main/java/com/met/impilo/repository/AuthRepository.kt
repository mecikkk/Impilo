package com.met.impilo.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepository {

    private val TAG = javaClass.simpleName

    companion object {
        fun newInstance() = AuthRepository()
    }

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun signInWithEmail(email: String, pass: String, signInSuccess: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            signInSuccess(true)
        }.addOnFailureListener {
                signInSuccess(false)
            }
    }

    fun signUpWithEmail(email: String, pass: String, signUpSuccess: (Boolean) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener {
            //sendDataToDatabase(it.user!!.uid, name, birthDate)
            signUpSuccess(true)
        }.addOnFailureListener {
                signUpSuccess(false)
            }
    }

    fun signUpWithGoogle(acct: GoogleSignInAccount, signInSuccess: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult?> ->
            if (task.isSuccessful) { // Sign in success
                val user = firebaseAuth.currentUser
                signInSuccess(true)
            } else { // Sign in fails
                signInSuccess(false)
            }
        }
    }

}