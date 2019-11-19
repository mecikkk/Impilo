package com.met.impilo.repository

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthRepository {

    private val TAG: String = javaClass.simpleName
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Wykorzystanie fukcji rezszerzającej (w Javie parametrem bylby interfejs)
    fun signInWithEmail(email: String, pass: String, signInSuccess: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                signInSuccess(true)
            }
            .addOnFailureListener {
                signInSuccess(false)
            }
    }
}