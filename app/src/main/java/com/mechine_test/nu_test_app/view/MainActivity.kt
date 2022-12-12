package com.mechine_test.nu_test_app.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.mechine_test.nu_test_app.R
import com.mechine_test.nu_test_app.databinding.ActivityMainBinding
import com.mechine_test.nu_test_app.session.sharedPreferences
import com.mechine_test.nu_test_app.util.commonUtils


class MainActivity : AppCompatActivity() {

    private val SIGN_IN_RESULT_CODE = 10000
    private var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        initAuth()
        initListeners()
        if (sharedPreferences.getTokenPreferences(this) != "") {
            if (commonUtils.isNetworkAvailable(this))
                silentSignIn()
            else
                signOut()
        }
    }

    private fun initListeners() {
        _binding.btnSignIn.setOnClickListener {
            if (commonUtils.isNetworkAvailable(this))
                signIn()
            else
                signOut()
        }
    }

    private fun initAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SIGN_IN_RESULT_CODE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.d("Sign In: ", completedTask.toString())
        try {
            sharedPreferences.setTokenPreferences(
                this,
                completedTask.getResult(ApiException::class.java).idToken
            )
            startActivity(Intent(this, DashBoardActivity::class.java))
        } catch (e: ApiException) {
            Log.w("Sign In: Error", "statusCode :- " + e.statusCode)
            signOut()
        }
    }

    private fun signIn() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        Log.d("Sign In: Acc", account.toString())
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, SIGN_IN_RESULT_CODE)
    }

    private fun signOut() {
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) {
                sharedPreferences.setTokenPreferences(this, sharedPreferences.__NULL)
                revokeAccess()
            }
        sharedPreferences.setTokenPreferences(this, sharedPreferences.__NULL)
    }

    private fun revokeAccess() {
        mGoogleSignInClient!!.revokeAccess()
            .addOnCompleteListener(this) {
                Log.d("Sign In: Revoke", "success")
            }
    }

    private fun silentSignIn() {
        val task: Task<GoogleSignInAccount> = mGoogleSignInClient!!.silentSignIn()
        if (task.isSuccessful)
            startActivity(Intent(this, DashBoardActivity::class.java))
        else
            signOut()
    }

}