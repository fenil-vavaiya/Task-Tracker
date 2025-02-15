package com.example.googletaskproject.ui.screens.setting.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googletaskproject.R
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.databinding.TimeZoneLayoutBinding
import com.example.googletaskproject.domain.TimeZoneModel
import com.example.googletaskproject.utils.Const
import java.util.TimeZone

class TimeZoneAdapter() : RecyclerView.Adapter<TimeZoneAdapter.TimeZoneViewHolder>() {
    private var dataList = ArrayList<TimeZoneModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeZoneViewHolder {
        return TimeZoneViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.time_zone_layout, parent, false)
        )
    }

    fun setData(dataList: ArrayList<TimeZoneModel>) {
        this.dataList = ArrayList(dataList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataList.size


    override fun onBindViewHolder(holder: TimeZoneViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    inner class TimeZoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TimeZoneLayoutBinding.bind(itemView)
        fun bind(item: TimeZoneModel) {

            binding.txtTittle.text = item.timeZoneId
            binding.txtOffset.text = item.displayName

            binding.checked.visibility =
                if (SessionManager.getString(Const.SELECTED_TIME_ZONE) == item.timeZoneId) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                SessionManager.putString(Const.SELECTED_TIME_ZONE, item.timeZoneId)
                notifyDataSetChanged()
            }
        }

    }


}