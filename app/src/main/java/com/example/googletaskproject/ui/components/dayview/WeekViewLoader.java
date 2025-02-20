package com.example.googletaskproject.ui.components.dayview;

import com.example.googletaskproject.data.CalendarEventItem;

import java.util.Calendar;
import java.util.List;

public interface WeekViewLoader {

    double toWeekViewPeriodIndex(Calendar instance);
    List<? extends CalendarEventItem> onLoad(int periodIndex);
}
