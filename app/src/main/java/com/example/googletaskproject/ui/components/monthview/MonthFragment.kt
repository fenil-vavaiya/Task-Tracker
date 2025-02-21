package com.example.googletaskproject.ui.components.monthview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.googletaskproject.R
import com.example.googletaskproject.utils.Const
import java.util.Calendar

class MonthFragment : Fragment() {

    private lateinit var monthAdapter: MonthAdapter
    private lateinit var monthListView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var textViewArray: Array<TextView>
    private lateinit var dayNames: Array<String>

    private var currentPos: Int = 0
    private var mYear: Int = 0
    private var mMonth: Int = 0

    companion object {
        fun newInstance(year: Int, month: Int) = MonthFragment().apply {
            arguments = Bundle().apply {
                putInt("Year", year)
                putInt("Month", month)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mYear = it.getInt("Year")
            mMonth = it.getInt("Month")
        }
        monthAdapter = MonthAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_month, container, false)

        monthListView = view.findViewById(R.id.monthListView)
        textViewArray = arrayOf(
            view.findViewById(R.id.day1), view.findViewById(R.id.day2),
            view.findViewById(R.id.day3), view.findViewById(R.id.day4),
            view.findViewById(R.id.day5), view.findViewById(R.id.day6),
            view.findViewById(R.id.day7)
        )

        layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        monthListView.layoutManager = layoutManager

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Calendar.getInstance().apply {
            set(Calendar.YEAR, mYear)
            set(Calendar.MONTH, mMonth)
        }
        initView()
    }

    private fun initView() {
        monthListView.adapter = monthAdapter
        selectFirstDisplayedYear()

        dayNames = arrayOf("S", "M", "T", "W", "T", "F", "S")

        /*if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON,R.string.strSystemDefault,requireContext()).equals(getString(R.string.strSystemDefault)))
      {
          DAY_NAMES = new String[]{"S", "M", "T", "W", "T", "F", "S"};
      }
      else  if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON,R.string.strSystemDefault,requireContext()).equals(getString(R.string.strSunday)))
      {
          DAY_NAMES = new String[]{"S", "M", "T", "W", "T", "F", "S"};
      }
      else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, requireContext()).equals(getString(R.string.strMonday)))
      {
          DAY_NAMES = new String[]{"M", "T", "W", "T", "F", "S","S"};
      }
      else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, requireContext()).equals(getString(R.string.strSaturday)))
      {
          DAY_NAMES = new String[]{"S","S","M", "T", "W", "T", "F"};
      }*/
        textViewArray.forEachIndexed { index, textView ->
            textView.text = dayNames.getOrNull(index) ?: ""
        }

        val snapHelper = object : LinearSnapHelper() {
            override fun findTargetSnapPosition(
                layoutManager: RecyclerView.LayoutManager,
                velocityX: Int,
                velocityY: Int
            ): Int {
                if (layoutManager !is LinearLayoutManager) return RecyclerView.NO_POSITION
                val position = layoutManager.findFirstVisibleItemPosition()
                if (position == RecyclerView.NO_POSITION) return RecyclerView.NO_POSITION

                val targetPosition = when {
                    layoutManager.canScrollHorizontally() ->
                        if (velocityX < 0) position else position + 1

                    layoutManager.canScrollVertically() ->
                        if (velocityY < 0) position - 1 else position + 1

                    else -> position
                }
                return targetPosition.coerceIn(0, layoutManager.itemCount - 1)
            }
        }
        snapHelper.attachToRecyclerView(monthListView)

        monthListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val centerView = snapHelper.findSnapView(layoutManager) ?: return
                currentPos = layoutManager.getPosition(centerView)
                val currentMonth = monthAdapter.getTittle(currentPos)
                val intent = Intent(Const.TITLE_EVENT).apply {
                    putExtra(Const.TITLE, currentMonth)
                }
                LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyMonthView() {
        monthAdapter.notifyDataSetChanged()
    }

    private fun selectFirstDisplayedYear() {
        monthAdapter.getMonthPosition(mYear, mMonth).takeIf { it > -1 }?.let { position ->
            try {
                monthListView.scrollToPosition(position - 1)
                monthListView.scrollToPosition(position)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
