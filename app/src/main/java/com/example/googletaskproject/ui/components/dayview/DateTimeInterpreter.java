package com.example.googletaskproject.ui.components.dayview;

import java.util.Calendar;

public interface DateTimeInterpreter {
    String interpretDay(Calendar date);

    String interpretDate(Calendar date);

    int interpretTime(int hour);
}
