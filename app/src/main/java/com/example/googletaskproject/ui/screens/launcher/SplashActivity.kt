package com.example.googletaskproject.ui.screens.launcher

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.databinding.ActivitySplashBinding
import com.example.googletaskproject.domain.UserModel
import com.example.googletaskproject.presentation.EventViewmodel
import com.example.googletaskproject.ui.screens.auth.SignInActivity
import com.example.googletaskproject.ui.screens.home.MainActivity
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.extensions.scheduleEvent
import com.example.googletaskproject.utils.helper.CalendarHelper
import com.example.googletaskproject.utils.helper.CalendarHelper.sortFutureEvents
import com.example.googletaskproject.utils.helper.PermissionHelper.areAllPermissionsGranted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val viewmodel: EventViewmodel by viewModels()

    override fun initViews(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            if (areAllPermissionsGranted(this@SplashActivity)) {
                val userInfo = SessionManager.getObject(Const.USER_INFO, UserModel::class.java)
                userInfo?.let {
                    // Fetch events from Google Calendar and save to database
                    val eventsList = CalendarHelper.fetchGoogleCalendarEvents(this@SplashActivity, userInfo.email)

                    viewmodel.updateAllEvents(eventsList)
                    Log.d(TAG, "initViews: Splash Act fetched eventsList = ${eventsList.size}")
                    val futureEvents = sortFutureEvents(eventsList)

                    futureEvents.forEach { event ->
                        scheduleEvent(event)
                    }
                }
            }
        }

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