package com.example.googletaskproject.ui.screens.launcher

import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import com.example.googletaskproject.ui.screens.home.MainActivity
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.databinding.ActivitySplashBinding
import com.example.googletaskproject.ui.screens.auth.SignInActivity
import com.example.googletaskproject.utils.Const
import kotlinx.coroutines.Runnable

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun initViews(view: View) {
        Handler().postDelayed(Runnable {
            if (SessionManager.getBoolean(Const.IS_LOGGED_IN)) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, SignInActivity::class.java))
            }
            finish()
        }, 1000)

    }

    override fun initListeners(view: View) {

    }

    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivitySplashBinding.inflate(layoutInflater)


}