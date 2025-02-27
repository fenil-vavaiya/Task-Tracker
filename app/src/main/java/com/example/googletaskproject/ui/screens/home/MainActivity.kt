package com.example.googletaskproject.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import com.example.googletaskproject.R
import com.example.googletaskproject.background.OverlayService
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.core.state.TypeState
import com.example.googletaskproject.data.model.GroupItem
import com.example.googletaskproject.data.model.GroupMember
import com.example.googletaskproject.data.model.TaskItem
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
import com.example.googletaskproject.utils.extensions.cancelScheduledAlarm
import com.example.googletaskproject.utils.extensions.context.getAndroidId
import com.example.googletaskproject.utils.extensions.context.showToast
import com.example.googletaskproject.utils.extensions.scheduleTask
import com.example.googletaskproject.utils.extensions.showOverlayPermissionDialog
import com.example.googletaskproject.utils.helper.CalendarHelper
import com.example.googletaskproject.utils.helper.CalendarHelper.filterEventsByExactDate
import com.example.googletaskproject.utils.helper.getUserInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
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

    private var groupMembers: List<GroupMember> = emptyList()
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
            userInfo?.let { it ->
                if (userInfo.userId.isEmpty()) {
                    viewmodel.getUser(it.email).addOnSuccessListener {
                        Log.d(TAG, "initViews: user model = $it")
                        SessionManager.putObject(Const.USER_INFO, it)
                        initializeAppEnvironment(it)
                    }.addOnFailureListener {
                        showUserDetails(userInfo, viewmodel) {
                            // Fetch events from Google Calendar and save to database
                            setupUserInitial(it)
                            initializeAppEnvironment(it)
                        }
                    }
                }
                initializeAppEnvironment(userInfo)
            }
        }

    }

    private fun initializeAppEnvironment(userInfo: UserModel) {
        if (userInfo.userId.isNotEmpty()) {
            viewmodel.tasksLiveData.observe(this) { it ->
                Log.d(TAG, "initViews: $it")
                val dataList = filterEventsByExactDate(it, LocalDate())
                Log.d(TAG, "initViews: today task = $dataList")
                binding.spinnerMode.setSelection(if (dataList.isEmpty()) 2 else 0)

                binding.noDataTv.visibility = if (dataList.isEmpty()) View.VISIBLE else View.GONE
                adapter.setData(dataList)

                dataList.forEach {
                    if (it.assignedTo == getUserInfo().userId) {
                        Log.d(TAG, "initializeAppEnvironment: task matched ")
                        scheduleTask(it)
                    }
                }
            }

            viewmodel.groupMembers.observe(this) {
                Log.d(TAG, "initializeAppEnvironment: groupMembers list = $it")
                groupMembers = it
            }
            viewmodel.fetchTasks()
            viewmodel.fetchGroupMembers()

            binding.rv.adapter = adapter
            startUpdatingTime()
            binding.dateTv.text = LocalDate().toString("EEE, MMMM dd")

            handleIntent()
            handleSpinner()
            //                    startOverlayService()
        }
    }

    private fun handleSpinner() {
        val stateList = listOf(
            TypeState.SettingState, TypeState.UsageState, TypeState.SleepState
        )

        val stateNames = stateList.map { it.name }

        val adapter = ArrayAdapter(
            this, R.layout.spinner_text, stateNames
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)

        binding.spinnerMode.adapter = adapter
        binding.spinnerMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                handleState(stateList[position])

                (view as? TextView)?.apply {
                    typeface = ResourcesCompat.getFont(
                        this@MainActivity, R.font.poppins_medium
                    )
                    textSize = 12f
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun handleState(typeState: TypeState) {
        when (typeState) {
            TypeState.SettingState -> {
                binding.settingSegment.visibility = View.VISIBLE
                binding.sleepSegment.visibility = View.GONE
                adapter.changeState(typeState)
            }

            TypeState.SleepState -> {
                binding.settingSegment.visibility = View.GONE
                binding.sleepSegment.visibility = View.VISIBLE
                binding.sleepMessageTv.text = SessionManager.getString(Const.SLEEP_MODE_MESSAGE)
            }

            TypeState.UsageState -> {
                binding.settingSegment.visibility = View.VISIBLE
                binding.sleepSegment.visibility = View.GONE
                adapter.changeState(typeState)
            }
        }
    }

    private fun handleIntent() {
        if (intent.getBooleanExtra(Const.FROM_ALARM, false)) {
            val task = Gson().fromJson(
                intent.getStringExtra(Const.TASK_DATA), TaskItem::class.java
            )
            task?.let {
                showAddTask(task, groupMembers) {
                    viewmodel.updateTask(it)
                    scheduleTask(it)
                    viewmodel.fetchTasks()
                }
            }
        }
    }

    private fun setupUserInitial(it: UserModel) {

        if (it.userId == it.groupId) {
            val newTeam = GroupItem(
                id = it.groupId, createdBy = it.groupId, createdAt = System.currentTimeMillis()
            )
            viewmodel.createNewGroup(newTeam)
            uploadTasks(it)
        } else {
            viewmodel.fetchTasks()
        }

        viewmodel.createNewUser(it)
        viewmodel.addMember(
            GroupMember(it.userId, getAndroidId(), it.groupId, it.location)
        )

        /*val futureEvents = sortFutureEvents(eventsList)

        futureEvents.forEach { event ->
            scheduleEvent(event)
        }*/
    }

    private fun uploadTasks(it: UserModel) {
        val eventsList = CalendarHelper.fetchGoogleCalendarEvents(this@MainActivity, it)
        viewmodel.addTaskList(eventsList)
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
            val user = getUserInfo()
            if (user.role == Const.ROLE_PARENT) uploadTasks(getUserInfo())
            else viewmodel.fetchTasks()

            showToast("Task synced successfully")
        }

        binding.btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        binding.btnAddTask.setOnClickListener {
            showAddTask(members = groupMembers) {
                viewmodel.addTask(it)
                scheduleTask(it)
            }
        }

        adapter.setCallback {
            when (it) {
                is TaskCallback.OnDeleteClick -> {
                    deleteDialog {
                        viewmodel.deleteTask(it.task.taskId)
                        adapter.removeItem(it.position)
                        cancelScheduledAlarm(it.task.taskId)
                    }
                }

                is TaskCallback.OnEditClick -> {
                    Log.d(TAG, "initListeners: click task")
                    showAddTask(it.task, groupMembers) { newItem ->
                        adapter.editItem(it.position, newItem)
                        viewmodel.updateTask(newItem)
                        scheduleTask(it.task)
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