package com.example.googletaskproject.ui.components

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.databinding.DialogAddTaskBinding
import com.example.googletaskproject.utils.Const.TAG
import com.example.googletaskproject.utils.extensions.context.createCustomDialog
import com.example.googletaskproject.utils.extensions.context.showDateTimePicker
import com.example.googletaskproject.utils.extensions.context.showToast
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import java.util.Calendar

fun FragmentActivity.showAddTask(taskItem: TaskItem = TaskItem(), callback: (TaskItem) -> Unit) {
    var calendar = Calendar.getInstance()
    createCustomDialog(
        DialogAddTaskBinding::inflate, isCancelable = false
    ) { dialog, binding ->

        binding.etTaskName.setText(taskItem.title)
        binding.etStartTime.text = formatDate(taskItem.startTime)
        binding.etLocation.setText(taskItem.location)
        binding.etReminderBefore.setText(if (taskItem.reminderBefore == 0) "" else taskItem.reminderBefore.toString())
        binding.etTaskDuration.setText(if (taskItem.taskDuration == 0) "" else taskItem.taskDuration.toString())

        binding.etStartTime.setOnClickListener {
            showDateTimePicker(calendar) {
                calendar = it

                binding.etStartTime.text = formatDate(it.timeInMillis)

                Log.d(
                    TAG,
                    "showAddTask: ${LocalDate(it.timeInMillis).toString("MMM dd, yyyy hh:mm a")}"
                )
                Log.d(TAG, "showAddTask: timeInMillis = ${it.timeInMillis}")

                taskItem.startTime = it.timeInMillis
            }
        }

        binding.btnNegative.setOnClickListener {0
            dialog.dismiss()
        }

        binding.btnPositive.setOnClickListener {
            if (binding.etTaskName.text.toString().trim().isEmpty()) {
                showToast("Please enter a task name")
                return@setOnClickListener
            }
            if (binding.etLocation.text.toString().trim().isEmpty()) {
                showToast("Please enter location")
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
            taskItem.location = binding.etLocation.text.toString()
            taskItem.reminderBefore = binding.etReminderBefore.text.toString().trim().toInt()
            taskItem.taskDuration = binding.etTaskDuration.text.toString().trim().toInt()

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


