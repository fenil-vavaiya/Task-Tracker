package com.example.googletaskproject.ui.screens.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googletaskproject.R
import com.example.googletaskproject.core.state.TypeState
import com.example.googletaskproject.data.model.TaskItem
import com.example.googletaskproject.databinding.ItemReminderListEventBinding
import com.example.googletaskproject.utils.Const.TAG
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TaskListAdapter : RecyclerView.Adapter<TaskListAdapter.DayEventListViewHolder>() {
    private var typeState: TypeState = TypeState.UsageState
    private var dataList = emptyList<TaskItem>()
    private lateinit var callback: (TaskCallback) -> Unit

    fun setData(list: List<TaskItem>) {
        this.dataList = list
        notifyDataSetChanged()
    }

    fun setCallback(callback: (TaskCallback) -> Unit) {
        this.callback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayEventListViewHolder {
        return DayEventListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reminder_list_event, parent, false)
        )
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: DayEventListViewHolder, position: Int) {
        holder.bind(dataList[position], position)
    }

    fun removeItem(position: Int) {
        if (position in dataList.indices) {
            dataList = dataList.toMutableList().apply { removeAt(position) } // ✅ Assign new list
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, dataList.size)
        }
    }

    fun editItem(position: Int, newItem: TaskItem) {
        if (position in dataList.indices) { // Ensure valid index
            dataList = dataList.toMutableList().apply { set(position, newItem) }
            notifyItemChanged(position) // Notify RecyclerView about the change
        }
    }

    fun changeState(typeState: TypeState) {
        this.typeState = typeState
        notifyDataSetChanged()
    }

    inner class DayEventListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemReminderListEventBinding.bind(itemView)
        fun bind(item: TaskItem, position: Int) {
            binding.reminderName.text = item.title
            binding.reminderTime.text = convertTimestampToTime(item.startTime)
            binding.checkBox.isChecked = item.isCompleted

            when (typeState) {
                TypeState.SettingState -> {
                    binding.btnEdit.visibility = View.VISIBLE; binding.btnDelete.visibility =
                        View.VISIBLE
                }

                TypeState.SleepState -> {}
                TypeState.UsageState -> {
                    binding.btnEdit.visibility = View.GONE; binding.btnDelete.visibility = View.GONE
                }
            }

            binding.root.setOnClickListener {
                callback(TaskCallback.OnTaskClick(item))
            }

            binding.btnEdit.setOnClickListener {
                callback(TaskCallback.OnEditClick(item, position))
            }

            binding.btnDelete.setOnClickListener {
                callback(
                    TaskCallback.OnDeleteClick(
                        item, position
                    )
                )
            }

        }

        private fun convertTimestampToTime(timestamp: Long): String {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())

            return formatter.format(Instant.ofEpochMilli(timestamp))
        }

    }


}


sealed class TaskCallback {
    data class OnTaskClick(val task: TaskItem) : TaskCallback()
    data class OnEditClick(val task: TaskItem, val position: Int) : TaskCallback()
    data class OnDeleteClick(val task: TaskItem, val position: Int) : TaskCallback()
}