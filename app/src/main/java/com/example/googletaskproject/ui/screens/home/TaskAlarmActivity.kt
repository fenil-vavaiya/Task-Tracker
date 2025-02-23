package com.example.googletaskproject.ui.screens.home

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.googletaskproject.R
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.databinding.ActivityTaskAlarmBinding
import com.example.googletaskproject.presentation.TaskViewmodel
import com.example.googletaskproject.utils.Const
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskAlarmBinding
    private val viewmodel: TaskViewmodel by viewModels()

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


    }

    private fun initView() {
        intent.getStringExtra(Const.EVENT_DATA)?.let {
            val task = Gson().fromJson(it, TaskItem::class.java)

            binding.taskTitleTv.text = task.title
            binding.descriptionTv.text = task.description
        }

    }

    private fun initListener() {
        binding.btnStart.setOnClickListener {

        }

        binding.btnReschedule.setOnClickListener {

        }

    }
}