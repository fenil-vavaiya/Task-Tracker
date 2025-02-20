package com.example.googletaskproject.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.databinding.ActivityDayEventBinding
import com.example.googletaskproject.domain.UserModel
import com.example.googletaskproject.ui.screens.home.adapter.DayEventListAdapter
import com.example.googletaskproject.utils.helper.CalendarHelper
import com.example.googletaskproject.utils.helper.CalendarHelper.filterEventsByExactDate
import com.example.googletaskproject.utils.Const
import org.joda.time.LocalDate

class DayEventActivity : BaseActivity<ActivityDayEventBinding>() {
    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityDayEventBinding.inflate(layoutInflater)


    override fun initViews(view: View) {

        intent.getLongExtra(Const.DATA, 0).let {
            val today = LocalDate(it)
            binding.title.text = today.toString("EEEE, MMMM dd, yyyy")

            val userInfo = SessionManager.getObject(Const.USER_INFO, UserModel::class.java)

            userInfo?.let {
                val eventsList = CalendarHelper.fetchGoogleCalendarEvents(this, userInfo.email)

                val dayEvent = filterEventsByExactDate(eventsList, today)

                binding.noDataToday.visibility = if (dayEvent.isEmpty()) View.VISIBLE else View.GONE
                binding.rv.adapter = DayEventListAdapter(dayEvent) {

                }
            }
        }
    }

    override fun initListeners(view: View) {
        binding.btnBack.setOnClickListener { finish() }
    }
}