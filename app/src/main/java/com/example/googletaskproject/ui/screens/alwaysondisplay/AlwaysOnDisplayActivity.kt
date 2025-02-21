package com.example.googletaskproject.ui.screens.alwaysondisplay

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.googletaskproject.R
import com.example.googletaskproject.databinding.ActivityAlwaysOnDisplayBinding
import com.example.googletaskproject.presentation.EventViewmodel
import com.example.googletaskproject.ui.screens.home.adapter.DayEventListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AlwaysOnDisplayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlwaysOnDisplayBinding
    private var job: Job? = null
    private val viewmodel: EventViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        binding = ActivityAlwaysOnDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // for battery status
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, iFilter)

        startUpdatingTime()

        binding.dateTv.text = LocalDate().toString("EEE, MMMM dd")

        viewmodel.todayEvents.observe(this) {
            binding.rv.adapter = DayEventListAdapter(it) {

            }
        }


    }

    private fun startUpdatingTime() {
        job = CoroutineScope(Dispatchers.Main).launch {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            while (isActive) {
                binding.timeTv.text = timeFormat.format(Date())
                delay(1000) // Update every second
            }
        }
    }

    // To stop updating (when needed)
    private fun stopUpdatingTime() {
        job?.cancel()
    }

    private val batteryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                BatteryManager.BATTERY_STATUS_CHARGING -> binding.chargingIcon.setImageResource(R.drawable.ic_half_charging)
                BatteryManager.BATTERY_STATUS_DISCHARGING -> binding.chargingIcon.setImageResource(R.drawable.ic_half_not_charging)
                BatteryManager.BATTERY_STATUS_FULL -> binding.chargingIcon.setImageResource(R.drawable.ic_full_charged)
                BatteryManager.BATTERY_STATUS_UNKNOWN -> binding.chargingIcon.setImageResource(R.drawable.ic_half_not_charging)
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> binding.chargingIcon.setImageResource(
                    R.drawable.ic_half_not_charging
                )
            }
            binding.batteryLevelTv.text = "${intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)} %"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdatingTime()
    }
}