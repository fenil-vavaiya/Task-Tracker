package com.example.googletaskproject.ui.components.monthview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.googletaskproject.R;
import com.example.googletaskproject.data.CalendarEventItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
    private static final int MIN_YEAR = 1945;  // Minimum year for the range
    private static final int MAX_YEAR = 2045;  // Maximum year for the range
    private final List<Calendar> monthList;
    int year;
    String monthName;
    String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public MonthAdapter( ) {
        this.monthList = buildListOfMonths();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Calendar calendar = monthList.get(position);
        int month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        monthName = monthNames[month];
        holder.monthViewForMonth.setCalendar(calendar);
        holder.monthViewForMonth.invalidate(); // Ensure the view is redrawn
    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lout_main;
        MonthView_for_Month monthViewForMonth;

        public ViewHolder(View itemView) {
            super(itemView);
            lout_main = itemView.findViewById(R.id.lout_main);
            monthViewForMonth = itemView.findViewById(R.id.monthView);
        }
    }

    private List<Calendar> buildListOfMonths() {
        List<Calendar> monthsList = new ArrayList<>();
        // Iterate through each year from MIN_YEAR to MAX_YEAR
        for (int year = MIN_YEAR; year <= MAX_YEAR; year++) {
            // Iterate through each month (Calendar.MONTH is zero-based)
            for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, 1); // Ensure the start of the month
                monthsList.add(calendar);
            }
        }
        return monthsList;
    }

    public int getMonthPosition(int year, int month) {
        for (int i = 0; i < monthList.size(); i++) {
            Calendar calendar = monthList.get(i);
            if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month) {
                return i;
            }
        }
        return -1;
    }

    public String getTittle(int position) {
        Calendar calendar = monthList.get(position);
        int month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        monthName = monthNames[month];
        return (monthName + " " + year);
    }
}
