package com.example.googletaskproject.utils.extensions.context

import android.content.Context
import android.provider.Settings

fun Context.getAndroidId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }