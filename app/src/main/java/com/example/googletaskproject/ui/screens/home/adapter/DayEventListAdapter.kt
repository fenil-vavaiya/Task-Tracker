package com.example.googletaskproject.ui.screens.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googletaskproject.R
import com.example.googletaskproject.databinding.ItemReminderListEventBinding
import com.example.googletaskproject.domain.CalendarEvent
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DayEventListAdapter(
    private val dataLIst: List<CalendarEvent>,
    private val callback: (CalendarEvent) -> Unit
) :
    RecyclerView.Adapter<DayEventListAdapter.DayEventListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayEventListViewHolder {
        return DayEventListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reminder_list_event, parent, false)
        )
    }

    override fun getItemCount() = dataLIst.size

    override fun onBindViewHolder(holder: DayEventListViewHolder, position: Int) {
        holder.bind(dataLIst[position])
    }


    inner class DayEventListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemReminderListEventBinding.bind(itemView)
        fun bind(item: CalendarEvent) {
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