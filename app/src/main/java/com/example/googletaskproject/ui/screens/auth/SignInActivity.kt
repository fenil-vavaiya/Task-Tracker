package com.example.googletaskproject.ui.screens.auth

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.data.model.UserModel
import com.example.googletaskproject.databinding.ActivitySignInBinding
import com.example.googletaskproject.ui.screens.home.MainActivity
import com.example.googletaskproject.ui.screens.launcher.PermissionActivity
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.Const.RC_SIGN_IN
import com.example.googletaskproject.utils.helper.PermissionHelper.areAllPermissionsGranted
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient

class SignInActivity : BaseActivity<ActivitySignInBinding>() {

    private lateinit var googleApiClient: GoogleApiClient

    override fun initViews(view: View) {

    }

    override fun initListeners(view: View) {
        binding.googleLogin.setOnClickListener { googleLogin() }
    }

    private fun googleLogin() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        googleApiClient = GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()

        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(
            intent, RC_SIGN_IN
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                account?.let {
                    SessionManager.putBoolean(Const.IS_LOGGED_IN, true)
                    val userModel =
                        UserModel(it.displayName ?: "", it.email ?: "", it.photoUrl.toString())
                    SessionManager.putObject(
                        Const.USER_INFO, userModel
                    )
                    if (!areAllPermissionsGranted(this)) {
                        startActivity(Intent(this, PermissionActivity::class.java))
                    } else {
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    }
                    finish()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "error" + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivitySignInBinding.inflate(layoutInflater)
}