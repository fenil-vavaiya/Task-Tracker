package com.example.googletaskproject.ui.components.dayview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.example.googletaskproject.R;
import com.example.googletaskproject.core.SessionManager;
import com.example.googletaskproject.domain.CalendarEvent;
import com.example.googletaskproject.domain.UserModel;
import com.example.googletaskproject.utils.helper.CalendarHelper;
import com.example.googletaskproject.utils.Const;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekViewFragment extends Fragment implements MonthLoader.MonthChangeListener, WeekView.ScrollListener, WeekView.EmptyViewLongPressListener, WeekView.EventClickListener, WeekView.EmptyViewClickListener {


    private static final String TAG = Const.TAG;
    private ContentObserver contentObserver;
    WeekView mWeekView;
    private View myShadow;
    public static LocalDate CurrentDate;
    int count;
    private boolean isContentObserverRegistered = false;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    public WeekViewFragment() {
        // Required empty public constructor
    }

    public static WeekViewFragment newInstance(int count, LocalDate date) {
        WeekViewFragment fragment = new WeekViewFragment();
        Bundle args = new Bundle();
        args.putInt("count", count);
        args.putSerializable("calender", date);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (getArguments() != null) {
            count = getArguments().getInt("count");
            CurrentDate = (LocalDate) getArguments().getSerializable("calender");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_week, container, false);
        mWeekView = view.findViewById(R.id.weekView);
        myShadow = view.findViewById(R.id.myShadow);


        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                assert key != null;
                /*if (key.equals(ConstantField.TaskS)) {
                    mWeekView.notifyDatasetChanged();
                }*/
            }
        };


        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);



        setWeekView(count);

        setupContentObservers();




        return view;
    }


    public void setWeekView(int count) {
        mWeekView.setNumberOfVisibleDays(count);

        mWeekView.setTextSize((int) getResources().getDimension(com.intuit.sdp.R.dimen._10sdp));
        Calendar todayDate = Calendar.getInstance();
        todayDate.set(Calendar.DAY_OF_MONTH, CurrentDate.getDayOfMonth());
        todayDate.set(Calendar.MONTH, CurrentDate.getMonthOfYear() - 1);
        todayDate.set(Calendar.YEAR, CurrentDate.getYear());
        mWeekView.goToDate(todayDate);

        mWeekView.setShadow(myShadow);

        mWeekView.setFont(ResourcesCompat.getFont(requireActivity(), R.font.poppins_regular), 0);
        mWeekView.setFont(ResourcesCompat.getFont(requireActivity(), R.font.poppins_regular), 1);

        mWeekView.setMonthChangeListener(this);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setEmptyViewClickListener(this);
        mWeekView.setEmptyViewLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setScrollListener(this);
        setupDateTimeInterpreter();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        int visibleYear = newFirstVisibleDay.get(Calendar.YEAR);
        int month = newFirstVisibleDay.get(Calendar.MONTH);
        String monthName = new DateFormatSymbols().getMonths()[month];

        Intent intent = new Intent(Const.TITLE_EVENT);
        if (visibleYear == currentYear) {
            intent.putExtra(Const.TITLE, monthName);
        } else {
            intent.putExtra(Const.TITLE, monthName + " " + visibleYear);
        }
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);

    }


    private void setupDateTimeInterpreter() {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDay(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());

                if (mWeekView.getNumberOfVisibleDays() == 7)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase();
            }

            @Override
            public String interpretDate(Calendar date) {
                int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
                return dayOfMonth + "";
            }

            @Override
            public int interpretTime(int hour) {
                return hour;
            }
        });
    }

    @Override
    public List<? extends CalendarEvent> onMonthChange(int newYear, int newMonth) {
        LocalDate StartDate = new LocalDate(newYear, newMonth, 1);
        int length = StartDate.dayOfMonth().getMaximumValue();
        LocalDate EndDate = new LocalDate(newYear, newMonth, length);

        UserModel userModel = SessionManager.INSTANCE.getObject(Const.USER_INFO, UserModel.class);

        List<CalendarEvent> calendarEvents = CalendarHelper.INSTANCE.fetchGoogleCalendarEvents(requireActivity(), userModel.getEmail());

        return CalendarHelper.INSTANCE.filterEventsByLocalDate(calendarEvents, StartDate, EndDate, DateTimeZone.getDefault());
    }




    @Override
    public void onEventClick(CalendarEvent event, RectF eventRect) {
        Log.d(TAG, "onEventClick: event clicked on day view");

    }



    @Override
    public void onEmptyViewClicked(Calendar time) {

    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
//        Intent intent = new Intent(requireActivity(), Event_Add_Activity.class);
//        intent.putExtra("Cal", time);
//        requireContext().startActivity(intent);
        Log.d(TAG, "onEmptyViewLongPress: Add new event click by user long press");
    }





    private void setupContentObservers() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && !isContentObserverRegistered) {


            contentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    mWeekView.notifyDatasetChanged();

                }
            };


            // Register content observer for calendar events
            requireActivity().getContentResolver().registerContentObserver(
                    CalendarContract.Events.CONTENT_URI,
                    true,
                    contentObserver
            );
            isContentObserverRegistered = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isContentObserverRegistered) {
            requireContext().getContentResolver().unregisterContentObserver(contentObserver);
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }


    public void notifyWeekView()
    {
        mWeekView.notifyDatasetChanged();
    }
}