package com.example.googletaskproject.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import com.example.googletaskproject.core.BaseActivity
import com.example.googletaskproject.databinding.ActivityDayEventBinding
import com.example.googletaskproject.presentation.TaskViewmodel
import com.example.googletaskproject.utils.Const
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.LocalDate

@AndroidEntryPoint
class DayEventActivity : BaseActivity<ActivityDayEventBinding>() {

    private val viewmodel: TaskViewmodel by viewModels()

    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityDayEventBinding.inflate(layoutInflater)


    override fun initViews(view: View) {

        intent.getLongExtra(Const.DATA, 0).let { it ->
            val today = LocalDate(it)
            binding.title.text = today.toString("EEEE, MMMM dd, yyyy")

            /*viewmodel.todayEvents.observe(this) {
                binding.noDataToday.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                binding.rv.adapter = DayEventListAdapter(it) {

                }

            }*/
        }
    }

    override fun initListeners(view: View) {
        binding.btnBack.setOnClickListener { finish() }
    }
}