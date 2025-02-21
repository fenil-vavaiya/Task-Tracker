package com.example.googletaskproject.core.app

import android.app.Application
import com.example.googletaskproject.core.SessionManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}
