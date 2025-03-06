package com.example.googletaskproject.ui.screens.home

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.googletaskproject.R
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.data.model.TaskRingtoneModel
import com.example.googletaskproject.databinding.ActivityTaskAlarmBinding
import com.example.googletaskproject.presentation.TaskViewmodel
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.extensions.cancelScheduledAlarm
import com.example.googletaskproject.utils.extensions.scheduleTaskWithoutReminder
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TaskAlarmActivity : AppCompatActivity() {
    private lateinit var task: TaskItem
    private lateinit var binding: ActivityTaskAlarmBinding
    private val viewmodel: TaskViewmodel by viewModels()
    private var mediaPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        enableEdgeToEdge()
        binding = ActivityTaskAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        initListener()
        playRingtoneFromRaw()

    }

    private fun initView() {
        intent.getStringExtra(Const.TASK_DATA)?.let {
            task = Gson().fromJson(it, TaskItem::class.java)
            binding.taskTitleTv.text = task.title
            binding.startTimeTv.text = "Starting at ${convertTimestampToTime(task.startTime)}"

            Log.d(TAG, "initView: !task.isReminderShown && task.reminderBefore > 0 = ${!task.isReminderShown && task.reminderBefore > 0}")
            Log.d(TAG, "initView: task.isReminderShown = ${task.isReminderShown}")

            binding.btnStart.visibility = if (task.isReminderShown) View.VISIBLE else View.GONE
            binding.btnReschedule.visibility =
                if (task.isReminderShown) View.VISIBLE else View.GONE

            binding.btnDismiss.visibility =
                if (!task.isReminderShown && task.reminderBefore > 0) View.VISIBLE else View.GONE
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing (blocks back press)
            }
        })

    }

    private fun convertTimestampToTime(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
        return formatter.format(Instant.ofEpochMilli(timestamp))
    }

    private fun initListener() {
        binding.btnDismiss.setOnClickListener {
            stopAlarmSound()
            cancelScheduledAlarm(task.taskId)
            task.isReminderShown = true
            viewmodel.updateTask(task)
            scheduleTaskWithoutReminder(task)
            finish()
        }

        binding.btnStart.setOnClickListener {
            stopAlarmSound() // Stop sound when alarm is dismissed
            cancelScheduledAlarm(task.taskId)
            task.isCompleted = true
            viewmodel.updateTask(task)
            openMainAct(false)

        }

        binding.btnReschedule.setOnClickListener {
            stopAlarmSound() // Stop sound when alarm is dismissed
            cancelScheduledAlarm(task.taskId)
            openMainAct(true)
        }


    }

    private fun openMainAct(isRescheduled: Boolean) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Const.FROM_ALARM, isRescheduled)
            putExtra(Const.TASK_DATA, Gson().toJson(task))
        }
        startActivity(intent)
        finish()
    }

    private fun playRingtoneFromRaw() {
        try {
            val selectedRingtone =
                SessionManager.getObject(Const.RINGTONE_MUSIC, TaskRingtoneModel::class.java)

            selectedRingtone?.let {
                val rawResId = selectedRingtone.assetName
                if (rawResId == -1) return

                val assetFileDescriptor = resources.openRawResourceFd(rawResId) ?: return
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(
                        assetFileDescriptor.fileDescriptor,
                        assetFileDescriptor.startOffset,
                        assetFileDescriptor.length
                    )
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)  // 🔥 Makes it play in silent mode
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
                    )
                    isLooping = true
                    prepare()
                    start()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun stopAlarmSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                release()
            }
        }
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmSound() // Ensure alarm stops if activity is destroyed
    }
}