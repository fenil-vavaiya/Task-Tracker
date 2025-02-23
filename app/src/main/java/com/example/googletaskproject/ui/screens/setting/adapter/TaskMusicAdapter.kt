package com.example.googletaskproject.ui.screens.setting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googletaskproject.R
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.databinding.ItemRingtoneBinding
import com.example.googletaskproject.data.model.TaskRingtoneModel
import com.example.googletaskproject.utils.Const

class TaskMusicAdapter : RecyclerView.Adapter<TaskMusicAdapter.ViewHolder>() {

    private var dataList = emptyList<TaskRingtoneModel>()
    private lateinit var callback: (TaskRingtoneModel) -> Unit


    fun setData(list: List<TaskRingtoneModel>) {
        this.dataList = list
        notifyDataSetChanged()
    }

    fun setCallback(callback: (TaskRingtoneModel) -> Unit) {
        this.callback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ringtone, parent, false)
        )
    }

    override fun getItemCount() = dataList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRingtoneBinding.bind(itemView)

        fun bind(item: TaskRingtoneModel) {
            binding.tv.text = item.name

            binding.checked.visibility = if (SessionManager.getObject(
                    Const.RINGTONE_MUSIC, TaskRingtoneModel::class.java
                )?.name == item.name
            ) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                SessionManager.putObject(Const.RINGTONE_MUSIC, item)
                callback(item)
                notifyDataSetChanged()
            }
        }

    }
}