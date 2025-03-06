package com.example.googletaskproject.ui.components

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.example.googletaskproject.R
import com.example.googletaskproject.data.model.GroupMember
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.databinding.DialogAddTaskBinding
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.extensions.context.createCustomDialog
import com.example.googletaskproject.utils.extensions.context.showDateTimePicker
import com.example.googletaskproject.utils.extensions.context.showToast
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import java.util.Calendar

fun FragmentActivity.showAddTask(
    taskItem: TaskItem = TaskItem(),
    members: LiveData<List<GroupMember>>,
    callback: (TaskItem) -> Unit
) {
    var calendar = Calendar.getInstance()

    createCustomDialog(
        DialogAddTaskBinding::inflate, isCancelable = false
    ) { dialog, binding ->
        var memberList = emptyList<GroupMember>()
        val adapter = ArrayAdapter(
            this, R.layout.spinner_text, ArrayList<Any>()
        )

        members.observe(this@showAddTask) { it ->
            memberList = it
            adapter.clear()
            adapter.addAll(it.map { it.location })
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
        }

        binding.spinnerLocation.adapter = adapter
        binding.spinnerLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    (view as? TextView ?: parent?.getChildAt(0) as? TextView)?.apply {
                        typeface = ResourcesCompat.getFont(
                            this@showAddTask, R.font.poppins_medium
                        )
                        textSize = 12f
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.etTaskName.setText(taskItem.title)
        binding.etStartTime.text = formatDate(taskItem.startTime)

        binding.etReminderBefore.setText(
            if (taskItem.reminderBefore == 0) "" else taskItem.reminderBefore.toString()
        )
        binding.etTaskDuration.setText(
            if (taskItem.taskDuration == 0) "" else taskItem.taskDuration.toString()
        )

        binding.etStartTime.setOnClickListener {
            showDateTimePicker(calendar) {
                calendar = it
                binding.etStartTime.text = formatDate(it.timeInMillis)
                taskItem.startTime = it.timeInMillis
            }
        }

        binding.btnNegative.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnPositive.setOnClickListener {
            if (binding.etTaskName.text.toString().trim().isEmpty()) {
                showToast("Please enter a task name")
                return@setOnClickListener
            }

            if (binding.etReminderBefore.text.toString().trim().isEmpty()) {
                showToast("Please enter reminder before task")
                return@setOnClickListener
            }
            if (binding.etTaskDuration.text.toString().trim().isEmpty()) {
                showToast("Please enter task duration")
                return@setOnClickListener
            }

            taskItem.title = binding.etTaskName.text.toString()

            if (memberList.isNotEmpty()) {
                val location = memberList.getOrNull(binding.spinnerLocation.selectedItemPosition)
                taskItem.location = location?.location ?: ""
                taskItem.assignedTo = location?.memberId ?: ""
            }

            taskItem.reminderBefore =
                binding.etReminderBefore.text.toString().trim().toIntOrNull() ?: 0
            taskItem.taskDuration = binding.etTaskDuration.text.toString().trim().toIntOrNull() ?: 0

            taskItem.isReminderShown = false

            if (taskItem.reminderBefore == 0) {
                taskItem.isReminderShown = true
            }

            Log.d(TAG, "showAddTask: isReminderShown = ${taskItem.isReminderShown}")

            callback(taskItem)
            dialog.dismiss()
        }
    }.show()
}


private fun formatDate(timeInMillis: Long): String {
    if (timeInMillis == 0.toLong()) {
        return ""
    }
    val dateTime = LocalDateTime(timeInMillis) // Convert to Joda-Time LocalDateTime
    val formatter = DateTimeFormat.forPattern("MMM dd, yyyy hh:mm a") // Define format
    val formattedDate = dateTime.toString(formatter) // Format to string
    return formattedDate
}


