package com.example.googletaskproject.ui.components.dayview;

import com.example.googletaskproject.domain.CalendarEvent;

import java.util.Calendar;
import java.util.List;

public interface WeekViewLoader {

    double toWeekViewPeriodIndex(Calendar instance);
    List<? extends CalendarEvent> onLoad(int periodIndex);
}
