package com.example.googletaskproject.ui.screens.setting

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.databinding.ActivityTimeZoneBinding
import com.example.googletaskproject.data.model.TimeZoneModel
import com.example.googletaskproject.ui.screens.setting.adapter.TimeZoneAdapter
import java.util.Locale
import java.util.TimeZone

class TimeZoneActivity : BaseActivity<ActivityTimeZoneBinding>() {
    private var timeZoneModels = ArrayList<TimeZoneModel>()
    private var filteredTimeZoneModels = ArrayList<TimeZoneModel>()
    private val adapter = TimeZoneAdapter()


    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityTimeZoneBinding.inflate(layoutInflater)


    override fun initViews(view: View) {
        timeZoneModels = getTimeZonesWithOffsets()
        addDeviceTimeZone()
        filteredTimeZoneModels.addAll(timeZoneModels)


        binding.rvTimeZone.adapter = adapter
        adapter.setData(filteredTimeZoneModels)
    }

    override fun initListeners(view: View) {
        binding.icBack.setOnClickListener { finish() }

        binding.edittextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {filter(p0.toString()) }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }
    private fun filter(text: String) {
        filteredTimeZoneModels.clear()
        if (text.isEmpty()) {
            filteredTimeZoneModels.addAll(timeZoneModels)
        } else {
            val query = text.lowercase(Locale.getDefault())
            for (model in timeZoneModels) {
                // Check if the query matches the time zone ID or display name
                if (model.timeZoneId.toLowerCase(Locale.getDefault()).contains(query) ||
                    model.displayName.toLowerCase(Locale.getDefault()).contains(query)
                ) {
                    filteredTimeZoneModels.add(model)
                }
            }
        }

        adapter.setData(filteredTimeZoneModels)
    }
    private fun addDeviceTimeZone() {
        val deviceTimeZone = TimeZone.getDefault()
        val deviceDisplayName = deviceTimeZone.getDisplayName(false, TimeZone.SHORT)
        val deviceTimeZoneModel =
            TimeZoneModel(
                "Auto (Device Time Zone)", deviceDisplayName
            )
        timeZoneModels.add(0, deviceTimeZoneModel)
    }


    private fun getTimeZonesWithOffsets(): ArrayList<TimeZoneModel> {
        val timeZoneIds = TimeZone.getAvailableIDs()
        val timeZonesWithOffsets: MutableList<TimeZoneModel> = java.util.ArrayList()

        for (id in timeZoneIds) {
            Log.e("msg", "" + id)
            val timeZone = TimeZone.getTimeZone(id)
            val displayName = timeZone.getDisplayName(false, TimeZone.SHORT)
            timeZonesWithOffsets.add(
                TimeZoneModel(
                    id,
                    displayName
                )
            )
        }

        return timeZonesWithOffsets as ArrayList<TimeZoneModel>
    }
}