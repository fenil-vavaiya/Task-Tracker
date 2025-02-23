package com.example.googletaskproject.ui.screens.launcher

import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.databinding.ActivitySplashBinding
import com.example.googletaskproject.presentation.TaskViewmodel
import com.example.googletaskproject.ui.screens.auth.SignInActivity
import com.example.googletaskproject.ui.screens.home.MainActivity
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.helper.PermissionHelper.areAllPermissionsGranted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Runnable

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val viewmodel: TaskViewmodel by viewModels()

    override fun initViews(view: View) {
        Handler().postDelayed(Runnable {
            if (!SessionManager.getBoolean(Const.IS_LOGGED_IN)) {
                startActivity(Intent(this, SignInActivity::class.java))
            } else if (!areAllPermissionsGranted(this)) {
                startActivity(Intent(this, PermissionActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 1000)

    }

    override fun initListeners(view: View) {

    }

    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivitySplashBinding.inflate(layoutInflater)


}