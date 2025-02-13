package com.example.googletaskproject.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googletaskproject.R
import com.example.googletaskproject.databinding.ItemReminderListEventBinding
import com.example.googletaskproject.domain.CalendarEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventsListAdapter(private val dataList: List<CalendarEvent>) :
    RecyclerView.Adapter<EventsListAdapter.EventsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        return EventsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reminder_list_event, parent, false)
        )
    }


    override fun getItemCount() = dataList.size


    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.bind(dataList[position])
    }


    class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemReminderListEventBinding.bind(itemView)
        fun bind(item: CalendarEvent) {
            binding.reminderName.text = item.title
            binding.reminderTime.text = formatTime(item.startTime)
        }

        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = Date(timestamp)
            return sdf.format(date)
        }
    }
}