package com.example.googletaskproject.ui.components.dayview;

import com.example.googletaskproject.domain.CalendarEvent;

import java.util.Calendar;
import java.util.List;

public class MonthLoader implements WeekViewLoader {

    private final MonthChangeListener mOnMonthChangeListener;

    public MonthLoader(MonthChangeListener listener) {
        this.mOnMonthChangeListener = listener;
    }

    @Override
    public double toWeekViewPeriodIndex(Calendar instance) {
        return instance.get(Calendar.YEAR) * 12 + instance.get(Calendar.MONTH) + (instance.get(Calendar.DAY_OF_MONTH) - 1) / 30.0;
    }

    @Override
    public List<? extends CalendarEvent> onLoad(int periodIndex) {
        return mOnMonthChangeListener.onMonthChange(periodIndex / 12, periodIndex % 12 + 1);
    }


    public interface MonthChangeListener {
        List<? extends CalendarEvent> onMonthChange(int newYear, int newMonth);
    }


}
