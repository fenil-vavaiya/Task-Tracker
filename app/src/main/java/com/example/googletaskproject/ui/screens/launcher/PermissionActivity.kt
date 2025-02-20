package com.example.googletaskproject.ui.screens.launcher

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Switch
import androidx.core.content.ContextCompat
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.databinding.ActivityPermissionBinding
import com.example.googletaskproject.ui.screens.home.MainActivity
import com.example.googletaskproject.utils.helper.PermissionHelper
import com.example.googletaskproject.utils.helper.PermissionHelper.areAllPermissionsGranted
import com.example.googletaskproject.utils.helper.PermissionHelper.canScheduleExactAlarms

class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {
    private val permissionsStatus = hashMapOf<String, Boolean>()
    override fun inflateBinding(layoutInflater: LayoutInflater)=  ActivityPermissionBinding.inflate(layoutInflater)


    override fun initViews(view: View) {
        // 14 ma badhi j 3
        // 13 ma khali 2 calendar & notification
        // -13 ma only calendar
        initializePermissions()
    }

    override fun initListeners(view: View) {
        setupPermissionButton(
            binding.btnCalendarAccess,
            Manifest.permission.READ_CALENDAR,
            binding.switchCalendarAccess
        )
        setupPermissionButton(
            binding.btnAlarmAccess,
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            binding.switchAlarm
        )
        setupPermissionButton(
            binding.btnNotificationAccess,
            Manifest.permission.POST_NOTIFICATIONS,
            binding.switchNotification
        )

        binding.btnNext.setOnClickListener {
            if (areAllPermissionsGranted(this)) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }


    private fun initializePermissions() {
        when (android.os.Build.VERSION.SDK_INT) {

            android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                permissionsStatus[Manifest.permission.READ_CALENDAR] =
                    isPermissionGranted(Manifest.permission.READ_CALENDAR)
                permissionsStatus[Manifest.permission.SCHEDULE_EXACT_ALARM] =
                    canScheduleExactAlarms(this)
                permissionsStatus[Manifest.permission.POST_NOTIFICATIONS] =
                    isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)
            }

            android.os.Build.VERSION_CODES.TIRAMISU -> {
                permissionsStatus[Manifest.permission.READ_CALENDAR] =
                    isPermissionGranted(Manifest.permission.READ_CALENDAR)
                permissionsStatus[Manifest.permission.POST_NOTIFICATIONS] =
                    isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)
                binding.btnAlarmAccess.visibility = View.GONE
            }

            else -> {
                permissionsStatus[Manifest.permission.READ_CALENDAR] =
                    isPermissionGranted(Manifest.permission.READ_CALENDAR)
                binding.btnAlarmAccess.visibility = View.GONE
                binding.btnNotificationAccess.visibility = View.GONE
            }

        }

        binding.switchCalendarAccess.isChecked =
            permissionsStatus[Manifest.permission.READ_CALENDAR] ?: false
        binding.switchAlarm.isChecked =
            permissionsStatus[Manifest.permission.SCHEDULE_EXACT_ALARM] ?: false
        binding.switchNotification.isChecked =
            permissionsStatus[Manifest.permission.POST_NOTIFICATIONS] ?: false

        updateNextButtonVisibility()

    }

    private fun setupPermissionButton(button: View, permission: String, switchCompat: Switch) {

        button.setOnClickListener {
            if (permission == Manifest.permission.SCHEDULE_EXACT_ALARM) {
                startActivity(
                    Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:$packageName")
                    )
                )
                return@setOnClickListener
            }
            requestPermissionIfNeeded(listOf(permission)) { allGranted, _, _ ->
                permissionsStatus[permission] = allGranted
                switchCompat.isChecked = allGranted
                updateNextButtonVisibility()
            }
        }
    }

    private fun updateNextButtonVisibility() {
        val allGranted = areAllPermissionsGranted(this)
        binding.btnNext.visibility = if (allGranted) View.VISIBLE else View.GONE
    }


    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        if (canScheduleExactAlarms(this)) {
            binding.switchAlarm.isChecked = true
            updateNextButtonVisibility()
        }
    }


}