package com.example.googletaskproject.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.googletaskproject.R
import com.example.googletaskproject.background.OverlayService
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.data.model.UserModel
import com.example.googletaskproject.databinding.ActivityMainBinding
import com.example.googletaskproject.presentation.TaskViewmodel
import com.example.googletaskproject.ui.components.showAddTask
import com.example.googletaskproject.ui.components.showUserDetails
import com.example.googletaskproject.ui.screens.home.adapter.TaskCallback
import com.example.googletaskproject.ui.screens.home.adapter.TaskListAdapter
import com.example.googletaskproject.ui.screens.setting.SettingActivity
import com.example.googletaskproject.utils.Const
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.extensions.scheduleEvent
import com.example.googletaskproject.utils.extensions.showOverlayPermissionDialog
import com.example.googletaskproject.utils.helper.CalendarHelper
import com.example.googletaskproject.utils.helper.CalendarHelper.sortFutureEvents
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var intentService: Intent? = null
    private val viewmodel: TaskViewmodel by viewModels()
    private val adapter = TaskListAdapter()
    private var job: Job? = null

    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)


    override fun initViews(view: View) {
        window.statusBarColor = getColor(R.color.Theme1BgColor)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        arePermissionGranted {
            val userInfo = SessionManager.getObject(Const.USER_INFO, UserModel::class.java)
            userInfo?.let {
                if (userInfo.userId.isEmpty()) {
                    showUserDetails(userInfo) {
                        // Fetch events from Google Calendar and save to database
                        CoroutineScope(Dispatchers.IO).launch {
                            SessionManager.putObject(Const.USER_INFO, it)

                            val eventsList = CalendarHelper.fetchGoogleCalendarEvents(
                                this@MainActivity, userInfo.email
                            )
                            Log.d(TAG, "initViews: eventsList.size = ${eventsList.size}")

                            if (it.parentId.isEmpty()) {
                                viewmodel.addTaskList(Const.TASK_GROUP_ID, eventsList)
                            }

                            val futureEvents = sortFutureEvents(eventsList)

                            futureEvents.forEach { event ->
                                scheduleEvent(event)
                            }

                        }
                    }
                }

                viewmodel.tasksLiveData.observe(this) {
                    Log.d(TAG, "initViews: $it")
                    adapter.setData(it)
                }
//                    startOverlayService()
                viewmodel.fetchTasks(Const.TASK_GROUP_ID)

                binding.rv.adapter = adapter
                startUpdatingTime()
                binding.dateTv.text = LocalDate().toString("EEE, MMMM dd")

            }
        }

    }


    private fun arePermissionGranted(callback: () -> Unit) {
        val permission = listOf(Manifest.permission.READ_CALENDAR)
        requestPermissionIfNeeded(
            permission
        ) { allGranted, _, _ ->
            if (allGranted) {
                callback.invoke()
            }
        }
    }

    override fun initListeners(view: View) {

        binding.btnSyncTask.setOnClickListener {

        }
        binding.btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        binding.btnAddTask.setOnClickListener {
            showAddTask {
                viewmodel.addTask(Const.TASK_GROUP_ID, it)
                scheduleEvent(it)
            }
        }

        adapter.setCallback {
            when (it) {
                is TaskCallback.OnDeleteClick -> {
                    deleteDialog {
                        viewmodel.deleteTask(Const.TASK_GROUP_ID, it.task.taskId)
                        adapter.removeItem(it.position)
                    }
                }

                is TaskCallback.OnEditClick -> {
                    Log.d(TAG, "initListeners: click task")
                    showAddTask(it.task) { newItem ->
                        adapter.editItem(it.position, newItem)
                        viewmodel.updateTask(Const.TASK_GROUP_ID, newItem)
                        scheduleEvent(it.task)
                    }
                }


                is TaskCallback.OnTaskClick -> {}
            }
        }
    }

    private fun deleteDialog(callback: () -> Unit) {
        MaterialAlertDialogBuilder(this).setCancelable(true).setTitle("Are you sure?")
            .setMessage("This action will remove the task forever").setNegativeButton(
                getString(android.R.string.cancel)
            ) { _, _ -> }.setPositiveButton("Yes") { _, _ ->
                callback()
            }.show()
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



    override fun onDestroy() {
        super.onDestroy()
        stopUpdatingTime()
    }
}