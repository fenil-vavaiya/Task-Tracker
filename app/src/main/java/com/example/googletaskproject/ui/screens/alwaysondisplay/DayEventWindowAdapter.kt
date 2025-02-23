package com.example.googletaskproject.ui.screens.alwaysondisplay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googletaskproject.R
import com.example.googletaskproject.databinding.ItemEventWindowBinding
import com.example.googletaskproject.data.model.TaskItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DayEventWindowAdapter(
    private val dataLIst: List<TaskItem>,
    private val callback: (TaskItem) -> Unit
) :
    RecyclerView.Adapter<DayEventWindowAdapter.DayEventListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayEventListViewHolder {
        return DayEventListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event_window, parent, false)
        )
    }

    override fun getItemCount() = dataLIst.size

    override fun onBindViewHolder(holder: DayEventListViewHolder, position: Int) {
        holder.bind(dataLIst[position])
    }


    inner class DayEventListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemEventWindowBinding.bind(itemView)
        fun bind(item: TaskItem) {
            binding.reminderName.text = item.title
            binding.reminderTime.text = convertTimestampToTime(item.startTime)

            binding.root.setOnClickListener {
                callback(item)
            }
        }

        private fun convertTimestampToTime(timestamp: Long): String {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
                .withZone(ZoneId.systemDefault())

            return formatter.format(Instant.ofEpochMilli(timestamp))
        }

    }
}