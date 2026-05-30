package ru.mirea.vakhrushevra.mireaproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

object AuthHelper {

    fun signOut(context: Context) {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)

        if (context is Activity) {
            context.finish()
        }
    }

    fun currentUserEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }
}
