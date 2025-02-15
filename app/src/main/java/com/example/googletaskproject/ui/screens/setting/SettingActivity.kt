package com.example.googletaskproject.ui.screens.setting

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.databinding.ActivitySettingBinding
import com.example.googletaskproject.domain.UserModel
import com.example.googletaskproject.ui.screens.auth.SignInActivity
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.extensions.showLogoutDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SettingActivity : BaseActivity<ActivitySettingBinding>() {


    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivitySettingBinding.inflate(layoutInflater)


    override fun initViews(view: View) {
        val userInfo = SessionManager.getObject(Const.USER_INFO, UserModel::class.java)

        userInfo?.let {
            binding.userName.text = it.name
            Glide.with(this).load(userInfo.photoUrl.toUri()).circleCrop().into(binding.userImage)
        }
    }

    override fun initListeners(view: View) {
        binding.timeZoneSettingSegment.setOnClickListener {
            startActivity(
                Intent(
                    this, TimeZoneActivity::class.java
                )
            )
        }

        binding.logoutSegment.setOnClickListener {
            showLogoutDialog {
                googleLogout()
            }
        }

        binding.chooseTaskMusicSegment.setOnClickListener {
            startActivity(Intent(this, TaskMusicActivity::class.java))
        }
    }


    private fun googleLogout() {
        GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut().addOnCompleteListener {
            // Clear session data
            SessionManager.putBoolean(Const.IS_LOGGED_IN, false)
            SessionManager.removeKey(Const.USER_INFO)

            // Navigate back to SignInActivity
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }


}