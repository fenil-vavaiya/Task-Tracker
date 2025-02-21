package com.example.googletaskproject.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.util.Calendar
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.googletaskproject.R
import com.example.googletaskproject.background.OverlayService
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.databinding.ActivityMainBinding
import com.example.googletaskproject.domain.UserModel
import com.example.googletaskproject.presentation.EventViewmodel
import com.example.googletaskproject.ui.components.monthview.MonthFragment
import com.example.googletaskproject.ui.screens.setting.SettingActivity
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.extensions.showOverlayPermissionDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var intentService: Intent? = null

    private val viewmodel: EventViewmodel by viewModels()

    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)


    override fun initViews(view: View) {
        window.statusBarColor = getColor(R.color.Theme1BgColor)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        val permission = listOf(Manifest.permission.READ_CALENDAR)
        requestPermissionIfNeeded(
            permission
        ) { allGranted, _, _ ->
            if (allGranted) {

                val userInfo = SessionManager.getObject(Const.USER_INFO, UserModel::class.java)
                userInfo?.let {

                    val calendar = Calendar.getInstance()
                    val monthFragment = MonthFragment.newInstance(
                        calendar[java.util.Calendar.YEAR], calendar[java.util.Calendar.MONTH]
                    )
                    replaceFragment(monthFragment)

                    viewmodel.allEvents.observe(this) {
                        Log.d(TAG, "initViews: Main Act eventsList.size = ${it?.size}")
                    }


//                    startOverlayService()


                }
            }
        }


        LocalBroadcastManager.getInstance(this)
            .registerReceiver(titleReceiver, IntentFilter(Const.TITLE_EVENT))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(clickReceiver, IntentFilter(Const.CLICK_EVENT))

    }

    private fun startOverlayService() {
        intentService = Intent(this@MainActivity, OverlayService::class.java)
        if (!Settings.canDrawOverlays(this)) {
            showOverlayPermissionDialog {
                resultLauncher.launch(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                            "package:$packageName"
                        )
                    )
                )
            }
        } else {
            startService(intentService)
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != Activity.RESULT_OK) {
            startService(intentService)
        }
    }
    private val titleReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getStringExtra(Const.TITLE)?.let {
                binding.title.text = it
            }
        }
    }

    private val clickReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getLongExtra(Const.DO_CLICK, 0).let {

                startActivity(
                    Intent(
                        this@MainActivity, DayEventActivity::class.java
                    ).putExtra(Const.DATA, it)
                )
            }
        }
    }

    override fun initListeners(view: View) {
        binding.btnSetting.setOnClickListener {
            startActivity(
                Intent(
                    this, SettingActivity::class.java
                )
            )
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(titleReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(clickReceiver)
    }

}