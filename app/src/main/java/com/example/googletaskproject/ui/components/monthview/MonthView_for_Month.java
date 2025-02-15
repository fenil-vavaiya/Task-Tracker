package com.example.googletaskproject.ui.components.monthview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.googletaskproject.R;
import com.example.googletaskproject.core.SessionManager;
import com.example.googletaskproject.domain.CalendarEvent;
import com.example.googletaskproject.domain.DayStickerModel;
import com.example.googletaskproject.domain.UserModel;
import com.example.googletaskproject.utils.CalendarHelper;
import com.example.googletaskproject.utils.Const;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class MonthView_for_Month extends View {
    private static final String TAG = Const.INSTANCE.TAG;
    private Context context;
    private Calendar calendar;
    private List<CalendarEvent> events = new ArrayList<>();
    private List<DayStickerModel> dayStickerList = new ArrayList<>();
    private TextPaint textPaint, DatePaint, TaskTxtPaint;
    private Paint eventPaint;
    private Paint gridPaint;
    private Paint todayDateBackgroundPaint;
    private Paint ripplePaint;
    private Paint todayDateTextPaint;
    int offset;
    private ContentObserver contentObserver;
    private GestureDetector gestureDetector;
    private int clickedCol = -1;
    private int clickedRow = -1;
    int count;
    private final Paint.Align DEFAULT_ALIGN = Paint.Align.CENTER;
    int TodayDateTextColor;
    int todayDateBackgroundColor;
    int todayDateBackgroundRadius;
    List<Map<Integer, Integer>> dateListEventInRaw = new ArrayList<>();
    List<Map<Integer, Integer>> dateListAdditionalRaw = new ArrayList<>();
    private boolean isContentObserverRegistered = false;
    int canvasBg;
    int taskColor;
    TimeZone timeZone;
    int StickerWidth, stickerMargin;


    public MonthView_for_Month(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public MonthView_for_Month(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        this.context = context;

       /* DataBase dataBase = DataBase.getDatabase(getContext());
        taskInstanceDao = dataBase.taskInstanceDao();
        stickerDao = dataBase.stickerDao();*/
        StickerWidth = (int) getResources().getDimension(com.intuit.sdp.R.dimen._15sdp);
        stickerMargin = (int) getResources().getDimension(com.intuit.sdp.R.dimen._1sdp);


        timeZone = CalendarHelper.INSTANCE.getCustomTimeZone();

        TypedValue typedValue = new TypedValue();


        Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins_regular);

        eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        eventPaint.setTextSize(30);

        getContext().getTheme().resolveAttribute(R.attr.TodayDateBgColor, typedValue, true);
        todayDateBackgroundColor = typedValue.data;
        todayDateBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        todayDateBackgroundPaint.setColor(todayDateBackgroundColor);
        todayDateBackgroundRadius = (int) getResources().getDimension(com.intuit.sdp.R.dimen._4sdp);


        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(30);
        textPaint.setTypeface(typeface);

        int DateTextSize = 26;
        int TodayDateTextSize = 26;
        getContext().getTheme().resolveAttribute(R.attr.themeColor, typedValue, true);
        TodayDateTextColor = typedValue.data;
        todayDateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        todayDateTextPaint.setColor(TodayDateTextColor);
        todayDateTextPaint.setTextSize(TodayDateTextSize);
        todayDateTextPaint.setTypeface(typeface);

        getContext().getTheme().resolveAttribute(R.attr.TxtColor, typedValue, true);
        int normalDateColor = typedValue.data;
        DatePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        DatePaint.setTextSize(DateTextSize);
        DatePaint.setColor(normalDateColor);
        DatePaint.setTypeface(typeface);

        TaskTxtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TaskTxtPaint.setTextSize(30);
        TaskTxtPaint.setTypeface(typeface);
        TaskTxtPaint.setColor(Color.WHITE);


        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(getResources().getColor(R.color.light_gray));
        gridPaint.setStrokeWidth(2);

        ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint.setColor(getResources().getColor(android.R.color.darker_gray));
        ripplePaint.setStyle(Paint.Style.FILL);


        getContext().getTheme().resolveAttribute(R.attr.bgColor, typedValue, true);
        canvasBg = typedValue.data;

        setupContentObserver();


        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                handleCellClick(e.getX(), e.getY());
                return true;
            }
        });

        setClickable(true);
    }

    private void setupContentObserver() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && !isContentObserverRegistered) {
            contentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    updateEvents();
                }
            };
            getContext().getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, contentObserver);
            isContentObserverRegistered = true;
        }
    }

    private void updateEvents() {
        if (calendar != null) {
            LocalDate minTime = new LocalDate(calendar.getTimeInMillis()).withDayOfMonth(1);
            LocalDate maxTime = new LocalDate(calendar.getTimeInMillis()).dayOfMonth().withMaximumValue();
            UserModel userModel = SessionManager.INSTANCE.getObject(Const.USER_INFO, UserModel.class);

            List<CalendarEvent> calendarEvents = CalendarHelper.INSTANCE.fetchGoogleCalendarEvents(context, userModel.getEmail());

            events = CalendarHelper.INSTANCE.filterEventsByLocalDate(calendarEvents, minTime, maxTime, DateTimeZone.getDefault());

            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isContentObserverRegistered) {
            getContext().getContentResolver().unregisterContentObserver(contentObserver);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setupContentObserver();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int cellWidth = getWidth() / 7;
        int cellHeight = getHeight() / 6;

        // Clear canvas
        canvas.drawColor(canvasBg);

        // Draw vertical grid lines
        for (int i = 0; i <= 6; i++) {
            float x = i * cellWidth;
            canvas.drawLine(x, 0, x, getHeight(), gridPaint);
        }

        // Draw horizontal grid lines
        for (int i = 0; i <= 5; i++) {
            float y = i * cellHeight;
            canvas.drawLine(0, y, getWidth(), y, gridPaint);
        }


        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Draw rectangle around clicked cell if valid
        if (clickedCol != -1 && clickedRow != -1) {
            canvas.drawRect(clickedCol * cellWidth, clickedRow * cellHeight, (clickedCol + 1) * cellWidth, (clickedRow + 1) * cellHeight, ripplePaint);
            postInvalidateOnAnimation();
        }

        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        offset = startDayOfWeek - Calendar.SUNDAY;
        /*if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, getContext()).equals(getContext().getString(R.string.strSystemDefault))) {
            offset = startDayOfWeek - Calendar.SUNDAY;
        } else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, getContext()).equals(getContext().getString(R.string.strSunday))) {
            offset = startDayOfWeek - Calendar.SUNDAY;
        } else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, getContext()).equals(getContext().getString(R.string.strMonday))) {
            offset = startDayOfWeek - Calendar.MONDAY;
        } else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, getContext()).equals(getContext().getString(R.string.strSaturday))) {
            offset = startDayOfWeek - Calendar.SATURDAY;
        }*/

        if (offset < 0) {
            offset += 7;
        }

        for (int day = 1; day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);

            int col = (offset + day - 1) % 7;
            int row = (offset + day - 1) / 7;


            int xPos = col * cellWidth + 10;
            float yPos = row * cellHeight + DatePaint.getTextSize() + 20;

            if (isToday(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day)) {
                drawCircleAroundText(canvas, day, todayDateTextPaint, todayDateBackgroundPaint, xPos, (int) (row * cellHeight + DatePaint.getTextSize() + 20), todayDateBackgroundRadius);
            } else {
                canvas.drawText(String.valueOf(day), xPos, yPos, DatePaint);
            }


            if (events != null) {
                drawEventAndTask(canvas, day, row, col, cellHeight, cellWidth);
            }

            if (dayStickerList != null && !dayStickerList.isEmpty()) {
                drawSticker(canvas, day, row, col, cellHeight, cellWidth);
            }


        }
    }


    public void drawSticker(Canvas canvas, int day, int row, int col, int cellHeight, int cellWidth) {

        for (DayStickerModel dayStickerModel : dayStickerList) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(dayStickerModel.getDayDate());
            LocalDate localDate = new LocalDate(calendar1.getTimeInMillis());


            if (day == localDate.getDayOfMonth()) {
                int xPosSticker = col * cellWidth + cellWidth / 2 + (StickerWidth / 3);
                int yPOsSticker = row * cellHeight + (StickerWidth / 2) + stickerMargin;
                Drawable drawable = ContextCompat.getDrawable(context, dayStickerModel.getImgResources());
                drawable.setBounds(xPosSticker, yPOsSticker, xPosSticker + StickerWidth, yPOsSticker + StickerWidth);
                drawable.draw(canvas);
            }
        }
    }


    public void drawEventAndTask(Canvas canvas, int day, int row, int col, int cellHeight, int cellWidth) {
        int eventsInRow = 0;
        int additionalEvents = 0;

        List<Map<Integer, Integer>> mapList1 = getMapsContainingKeyAdditional(day);

        for (Map<Integer, Integer> map : mapList1) {
            Integer count = map.entrySet().iterator().next().getValue();
            if (count > 0) {
                dateListAdditionalRaw.remove(map);
                additionalEvents = count;
            }
        }

        // Separate multi-day and single-day events
        List<CalendarEvent> multiDayEvents = new ArrayList<>();
        List<CalendarEvent> singleDayEvents = new ArrayList<>();

        for (CalendarEvent event : events) {
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTimeInMillis(event.getStartTime());
            calendarStart.setTimeZone(timeZone);
            resetCalendarTime(calendarStart);


            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTimeInMillis(event.getEndTime());
            calendarEnd.setTimeZone(timeZone);

            resetCalendarTime(calendarEnd);

            if (isMultiDayEvent(event, calendarStart, calendarEnd)) {
                multiDayEvents.add(event);
            } else {
                singleDayEvents.add(event);
            }
        }

// Process multi-day events first
        for (CalendarEvent event : multiDayEvents) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(event.getStartTime());
            if (calendar1.get(Calendar.DAY_OF_MONTH) == day) {
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTimeInMillis(event.getStartTime());
                resetCalendarTime(calendarStart);

                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTimeInMillis(event.getEndTime());
                resetCalendarTime(calendarEnd);


                List<Map<Integer, Integer>> mapList = getMapsContainingKey(day);

                for (Map<Integer, Integer> map : mapList) {
                    Integer count = map.entrySet().iterator().next().getValue();

                    if (count == eventsInRow) {
                        dateListEventInRaw.remove(map);
                        eventsInRow++;
                    }
                }

                if (eventsInRow < 2) {
                    float eventIndicatorY = calculateEventIndicatorY(row, eventsInRow, cellHeight);

                    eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    int originalColor = event.getEventColor();
                    if (originalColor == 0) {
                        originalColor = Color.BLUE; // Default color if something is wrong
                    }

                    int newColor = ColorUtils.setAlphaComponent(originalColor, (int) (255 * 0.2f));
                    eventPaint.setColor(newColor);

                    if (isMultiDayEvent(event, calendarStart, calendarEnd)) {

                        canvas.drawRect((col * cellWidth), eventIndicatorY, (col * cellWidth + cellWidth), eventIndicatorY + 29, eventPaint);
                        drawMultiDayEvent(canvas, calendarStart, calendarEnd, cellWidth, cellHeight, eventsInRow);
                    } else {

                        drawEventBackground(canvas, col, cellWidth, eventIndicatorY);
                    }

                    drawEventName(canvas, event, col, cellWidth, eventIndicatorY);


                    eventsInRow++;
                } else {
                    // Handle additional events beyond the first two
                    additionalEvents++;
                    List<Integer> dates = getDayNumbersBetween(calendarStart.getTimeInMillis(), calendarEnd.getTimeInMillis());
                    updateDateListWithEventsAdditional(dates, additionalEvents);
                }
            }
        }

// Process single-day events next
        for (CalendarEvent event : singleDayEvents) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(event.getStartTime());
            if (calendar1.get(Calendar.DAY_OF_MONTH) == day) {
                List<Map<Integer, Integer>> mapList = getMapsContainingKey(day);

                for (Map<Integer, Integer> map : mapList) {
                    Integer count = map.entrySet().iterator().next().getValue();

                    if (count == eventsInRow) {
                        dateListEventInRaw.remove(map);
                        eventsInRow++;
                    }
                }

                if (eventsInRow < 2) {

                    float eventIndicatorY = calculateEventIndicatorY(row, eventsInRow, cellHeight);
                    eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    int originalColor = event.getEventColor();
                    if (originalColor == 0) {
                        originalColor = Color.BLUE; // Default color if something is wrong
                    }
                    int newColor = ColorUtils.setAlphaComponent(originalColor, (int) (255 * 0.2f));
                    eventPaint.setColor(newColor);
                    // Draw event background
                    drawEventBackground(canvas, col, cellWidth, eventIndicatorY);

                    drawEventName(canvas, event, col, cellWidth, eventIndicatorY);

                    eventsInRow++;
                } else {
                    // Handle additional events beyond the first two
                    additionalEvents++;
                }
            } else {
                List<Map<Integer, Integer>> mapList2 = getMapsContainingKey(day);
                for (Map<Integer, Integer> map : mapList2) {
                    Integer count = map.entrySet().iterator().next().getValue();

                    if (count == eventsInRow) {
                        dateListEventInRaw.remove(map);
                        eventsInRow++;
                    }
                }
            }
        }


        if (additionalEvents > 0) {
            String additionalText = "+" + additionalEvents;
            DatePaint.setTextSize(22);
            float additionalTextWidth = DatePaint.measureText(additionalText);
            float additionalTextX = (col * cellWidth + (cellWidth - additionalTextWidth) / 2f) - 5;
            float additionalTextY = row * cellHeight + cellHeight - 10;

            canvas.drawText(additionalText, additionalTextX, additionalTextY, DatePaint);
            DatePaint.setTextSize(26);
        }

    }

    private void handleCellClick(float x, float y) {
        int cellWidth = getWidth() / 7;
        int cellHeight = getHeight() / 6;

        int col = (int) (x / cellWidth);
        int row = (int) (y / cellHeight);

        int day = row * 7 + col - offset + 1;

        if (day > 0 && day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            clickedCol = col;
            clickedRow = row;
            count = 0;
            invalidate();
            performClick();


            calendar.set(Calendar.DAY_OF_MONTH, day);


            Log.d(TAG, "handleCellClick: month-day clicked ");
            Intent intent = new Intent(Const.CLICK_EVENT);
            intent.putExtra(Const.DO_CLICK, calendar.getTimeInMillis());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        }
    }

    public List<Integer> getDayNumbersBetween(long startDateMillis, long endDateMillis) {
        List<Integer> dayList = new ArrayList<>();

        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(startDateMillis);
        startCal.add(Calendar.DATE, 1);

        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(endDateMillis);

        while (!startCal.after(endCal)) {
            int dayOfMonth = startCal.get(Calendar.DAY_OF_MONTH);
            dayList.add(dayOfMonth);
            startCal.add(Calendar.DATE, 1);
        }

        return dayList;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void postInvalidateOnAnimation() {
        super.postInvalidateOnAnimation();

        if (clickedCol != -1 && clickedRow != -1) {
            count += 1;
            if (count > 20) {
                clickedCol = -1; // Stop the animation
                clickedRow = -1; // Stop the animation
                invalidate();
            } else {
                postInvalidateOnAnimation();
            }

        }

    }

    public List<Map<Integer, Integer>> getMapsContainingKey(Integer date) {
        List<Map<Integer, Integer>> result = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Use Stream API for Android N and above
            return dateListEventInRaw.stream().filter(map -> map.containsKey(date)).collect(Collectors.toList());
        } else {
            // Use for loop for versions below Android N
            for (Map<Integer, Integer> map : dateListEventInRaw) {
                if (map.containsKey(date)) {
                    result.add(map);
                }
            }
        }

        return result;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.calendar.setTimeZone(timeZone);

        upDateTask();
        updateEvents();
        updateSticker();

    }


    public void upDateTask() {
        LocalDate minTime = new LocalDate(this.calendar.getTimeInMillis()).withDayOfMonth(1);
        LocalDate maxTime = new LocalDate(this.calendar.getTimeInMillis()).dayOfMonth().withMaximumValue();

        long startMillis = minTime.toDateTimeAtStartOfDay().getMillis();
        long endMillis = maxTime.plusDays(1).toDateTimeAtStartOfDay().minusMillis(1).getMillis();


    }


    public void updateSticker() {
        LocalDate minTime = new LocalDate(this.calendar.getTimeInMillis()).withDayOfMonth(1);
        LocalDate maxTime = new LocalDate(this.calendar.getTimeInMillis()).dayOfMonth().withMaximumValue();

        long startMillis = minTime.toDateTimeAtStartOfDay().getMillis();
        long endMillis = maxTime.plusDays(1).toDateTimeAtStartOfDay().minusMillis(1).getMillis();

       /* stickerDao.getStickerByDate(startMillis, endMillis).observeForever(new Observer<List<DayStickerModel>>() {
            @Override
            public void onChanged(List<DayStickerModel> dayStickerModel) {
                dayStickerList = dayStickerModel;
                invalidate();
            }
        });*/
    }

    private boolean isToday(int Year, int month, int dayOfMonth) {
        DateTime dateTime = new DateTime().withYear(Year).withMonthOfYear(month + 1).withDayOfMonth(dayOfMonth);
        return dateTime.toLocalDate().equals(new LocalDate());
    }

    private void drawCircleAroundText(Canvas canvas, int dayOfMonth, Paint textPaint, Paint backgroundPaint, int xValue, int yValue, int margin) {
        // Convert dayOfMonth to string for measurement
        String dayText = String.valueOf(dayOfMonth);

        // Calculate the bounds of the text
        Rect bounds = new Rect();
        textPaint.getTextBounds(dayText, 0, dayText.length(), bounds);

        // Calculate text width and height
        int textWidth = bounds.width();
        int textHeight = bounds.height();

        // Calculate center X and Y for the circle
        int centerX = (xValue + textWidth / 2) + 5;
        int centerY = yValue - textHeight / 2;

        // Calculate radius as half of text's diagonal plus margin for padding
        int radius = (int) (Math.sqrt(textWidth * textWidth + textHeight * textHeight) / 2 + margin);

        // Draw the background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // Draw the text centered within the circle
        canvas.drawText(dayText, xValue + 3, yValue, textPaint);
    }


    private void resetCalendarTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private float calculateEventIndicatorY(int row, int eventsInRow, int cellHeight) {
        return row * cellHeight + textPaint.getTextSize() + 50f + eventsInRow * (textPaint.getTextSize() + 5);
    }

    private void drawEventBackground(Canvas canvas, int col, int cellWidth, float eventIndicatorY) {
        canvas.drawRect((col * cellWidth) + 1, // Start from the left edge of the cell
                eventIndicatorY, (col * cellWidth + cellWidth) - 1, // Full cell width
                eventIndicatorY + 24 + 5, // Adjusted height for smaller text
                eventPaint);
    }

    private void drawEventName(Canvas canvas, CalendarEvent event, int col, int cellWidth, float eventIndicatorY) {


        String eventTitle = event.getTitle();
        float originalTextSize = textPaint.getTextSize();
        textPaint.setTextSize(24);
        textPaint.setColor(event.getEventColor());

        float availableWidth = cellWidth - 20;
        float textWidth = textPaint.measureText(eventTitle);

        if (textWidth > availableWidth) {
            String truncatedText = truncateText(eventTitle, textPaint, availableWidth);
            canvas.drawText(truncatedText, col * cellWidth + 10, eventIndicatorY + 24, textPaint);
        } else {
            canvas.drawText(eventTitle, col * cellWidth + 10, eventIndicatorY + 24, textPaint);
        }

        textPaint.setTextSize(originalTextSize); // Reset text size to default
    }

    /**
     * Truncates the text to fit within the available width, adding "..." if necessary.
     */
    private String truncateText(String text, Paint paint, float maxWidth) {
        if (paint.measureText(text) <= maxWidth) {
            return text; // No need to truncate
        }

        String ellipsis = "...";
        float ellipsisWidth = paint.measureText(ellipsis);
        int endIndex = text.length();

        while (endIndex > 0 && paint.measureText(text.substring(0, endIndex)) + ellipsisWidth > maxWidth) {
            endIndex--;
        }

        return text.substring(0, endIndex) + ellipsis;
    }


    private boolean isMultiDayEvent(CalendarEvent event, Calendar calendarStart, Calendar calendarEnd) {
        return !new LocalDate(calendarStart.getTimeInMillis()).equals(new LocalDate(calendarEnd.getTimeInMillis()));

    }

    private void drawMultiDayEvent(Canvas canvas, Calendar calendarStart, Calendar calendarEnd, int cellWidth, int cellHeight, int eventsInRow) {
        List<Integer> dates = getDayNumbersBetween(calendarStart.getTimeInMillis(), calendarEnd.getTimeInMillis());

        for (int i = 0; i < dates.size(); i++) {
            int date = dates.get(i);

            int col1 = (offset + date - 1) % 7;
            int row1 = (offset + date - 1) / 7;
            float eventIndicatorY = calculateEventIndicatorY(row1, eventsInRow, cellHeight);
            canvas.drawRect((col1 * cellWidth), // Start from the left edge of the cell
                    eventIndicatorY, (col1 * cellWidth + cellWidth), // Full cell width
                    eventIndicatorY + 24 + 5, // Adjusted height for smaller text
                    eventPaint);
        }

        updateDateListWithEvents(dates, eventsInRow);
    }

    private void updateDateListWithEvents(List<Integer> dates, int eventsInRow) {
        // Assuming dates and eventsInRow are of the same size
        for (int i = 0; i < dates.size(); i++) {
            int date = dates.get(i);

            Map<Integer, Integer> map = new HashMap<>();
            map.put(date, eventsInRow);

            if (!dateListEventInRaw.contains(map)) {
                dateListEventInRaw.add(map);
            }
        }
    }

    public void updateDateListWithEventsAdditional(List<Integer> dates, int additional) {
        for (int date : dates) {
            boolean dateExists = false;

            // Iterate over existing maps to find if the date already exists
            for (Map<Integer, Integer> existingMap : dateListAdditionalRaw) {
                if (existingMap.containsKey(date)) {
                    // Date exists, update the entry if the new additional count is greater
                    int currentCount = existingMap.get(date);
                    if (additional > currentCount) {
                        int count = currentCount + 1;
                        existingMap.put(date, count);
                    }
                    dateExists = true;
                    break;
                }
            }

            // If the date does not exist, add a new map
            if (!dateExists) {
                Map<Integer, Integer> map = new HashMap<>();
                map.put(date, 1);
                dateListAdditionalRaw.add(map);
            }
        }
    }

    public List<Map<Integer, Integer>> getMapsContainingKeyAdditional(Integer date) {
        List<Map<Integer, Integer>> result = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Use Stream API for Android N and above
            return dateListAdditionalRaw.stream().filter(map -> map.containsKey(date)).collect(Collectors.toList());
        } else {
            // Use for loop for versions below Android N
            for (Map<Integer, Integer> map : dateListAdditionalRaw) {
                if (map.containsKey(date)) {
                    result.add(map);
                }
            }
        }
        return result;
    }
}
