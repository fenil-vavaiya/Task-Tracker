package com.example.googletaskproject.ui.components.dayview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

import com.example.googletaskproject.R;
import com.example.googletaskproject.domain.CalendarEvent;
import com.example.googletaskproject.domain.modelDate;
import com.example.googletaskproject.utils.CalendarHelper;
import com.example.googletaskproject.utils.Const;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WeekView extends View {


    private static final String TAG = Const.TAG;
    //Paint TimeText.....
    private Paint mTimeTextPaint;


    //Paint DayNamePaint
    private Paint mDayNameTextPaint;
    private Paint mTodayDayNameTextPaint;

    //paint date.....
    private Paint mDateTextPaint;
    private Paint mTodayDateTextPaint;
    private Paint mTodayBackgroundPaint;

    //mHeaderBgPaint...
    private Paint mHeaderBackgroundPaint;

    //mHourSeparatorPaint...
    private Paint mHourSeparatorPaint;

    //nowline...
    private Paint mNowLinePaint;

    //EventBackgroundPaint...
    private Paint mEventBackgroundPaint;


    //rippleEffect..
    private Paint ripplePaint;
    public RectF rippleRect;
    public boolean isRippleActiveEvent = false;
    private float rippleRadius = 0;
    private static final float MAX_RIPPLE_RADIUS = 100;
    private Handler rippleHandler = new Handler();

    List<Map<modelDate, Integer>> datelist = new ArrayList<>();

    private boolean mRefreshEvents = false;
    private List<EventRect> mEventRectS = new ArrayList<>();
    private int mFetchedPeriod = -1;
    private int mEventPadding = 8;
    int size;
    int leftDaysWithGaps;
    float startPixel;
    public static final int LENGTH_SHORT = 1, LENGTH_LONG = 2;
    private final Context mContext;
    private Paint mTaskBackgroundPaint;
    private Paint mHeaderColumnBackgroundPaint;
    private float mTimeTextWidth, mTimeTextHeight, mHeaderDayNameTextHeight, mHeaderDateTextHeight, mHeaderHeight, mWidthPerDay, mHeaderMarginBottom, mTimeColumnWidth;
    private View shadow;
    private GestureDetector mGestureDetector;
    private OverScroller mScroller;
    private final PointF mCurrentOrigin = new PointF(0f, 0f);
    private Direction mCurrentScrollDirection = Direction.NONE;
    private TextPaint mEventTextPaint, jheaderEventTextpaint;
    private Direction mCurrentFlingDirection = Direction.NONE;
    private ScaleGestureDetector mScaleDetector;
    private boolean mIsZooming;
    private Calendar mFirstVisibleDay;
    private boolean mShowFirstDayOfWeekFirst = false;
    private int mScaledTouchSlop = 0;
    private int mHourHeight = 50;
    private int mNewHourHeight = -1;
    private int mMinHourHeight = 0;
    private int mEffectiveMinHourHeight;
    private int mMaxHourHeight = 250;
    private int mFirstDayOfWeek = Calendar.MONDAY;
    private int mTextSize = 12;
    private int mTimeColumnPadding = 10;
    private int mHeaderColumnTextColor = Color.BLACK;
    private int mNumberOfVisibleDays = 3;
    private int mHeaderRowPadding = 10;
    private int mHeaderRowBackgroundColor = Color.WHITE;
    private int mDayBackgroundColor = Color.rgb(245, 245, 245);
    private int mPastBackgroundColor = Color.rgb(227, 227, 227);
    private int mFutureBackgroundColor = Color.rgb(245, 245, 245);
    private final int mPastWeekendBackgroundColor;
    private final int mFutureWeekendBackgroundColor;
    private int mNowLineColor;
    private int mNowLineThickness = 5;
    private int mHourSeparatorColor = Color.rgb(230, 230, 230);
    private int mTodayBackgroundColor = Color.rgb(239, 247, 254);
    private int mHourSeparatorHeight = 2;
    private int mTodayHeaderTextColor = Color.rgb(39, 137, 228);
    private int mEventTextSize = 12;
    private int mEventTextColor = Color.BLACK;
    int mHeaderColumnBackgroundColor = Color.WHITE;
    private boolean mIsFirstDraw = true;
    private boolean mAreDimensionsInvalid = true;
    private int mDayNameLength = LENGTH_LONG;
    private int mOverlappingEventGap = 0;
    private int mEventMarginVertical = 0;
    private float mXScrollingSpeed = 5f;
    private Calendar mScrollToDay = null;
    private double mScrollToHour = -1;
    private int mEventCornerRadius = 0;
    private boolean mShowDistinctWeekendColor = false;
    private boolean mShowNowLine = false;
    private boolean mShowDistinctPastFutureColor = false;
    private boolean mHorizontalFlingEnabled = true;
    private boolean mVerticalFlingEnabled = true;
    private int mAllDayEventHeight = 100;
    private int mScrollDuration = 150;
    private float weekX;
    private EventClickListener mEventClickListener;
    private EventLongPressListener mEventLongPressListener;
    private WeekViewLoader mWeekViewLoader;
    private EmptyViewClickListener mEmptyViewClickListener;
    private EmptyViewLongPressListener mEmptyViewLongPressListener;
    private final Map<RectF, Calendar> dayLabelRect1 = new HashMap<>();
    private List<? extends CalendarEvent> mPreviousPeriodEvents;
    private List<? extends CalendarEvent> mCurrentPeriodEvents;
    private List<? extends CalendarEvent> mNextPeriodEvents;

    boolean isInEvent = false;
    boolean isInTask = false;
    CalendarEvent selectedEvent;
    RectF eventRect, taskRect;
    int TaskColor;
    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            weekX = mCurrentOrigin.x;
            goToNearestOrigin();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            // Check if view is zoomed.

            if (mIsZooming) {
                mCurrentOrigin.x = weekX;
                return true;
            }
            boolean b = Math.abs(distanceX) > Math.abs(distanceY);
            switch (mCurrentScrollDirection) {
                case NONE: {
                    // Allow scrolling only in one direction.
                    if (b) {
                        if (distanceX > 0) {
                            mCurrentScrollDirection = Direction.LEFT;
                        } else {
                            mCurrentScrollDirection = Direction.RIGHT;
                        }
                    } else {
                        mCurrentScrollDirection = Direction.VERTICAL;
                    }
                    break;
                }
                case LEFT: {
                    // Change direction if there was enough change.
                    if (b && (distanceX < -mScaledTouchSlop)) {
                        mCurrentScrollDirection = Direction.RIGHT;
                    }
                    break;
                }
                case RIGHT: {
                    // Change direction if there was enough change.
                    if (b && (distanceX > mScaledTouchSlop)) {
                        mCurrentScrollDirection = Direction.LEFT;
                    }
                    break;
                }
            }

            // Calculate the new origin after scroll.
            switch (mCurrentScrollDirection) {
                case LEFT:
                case RIGHT:
                    mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
                    break;
                case VERTICAL:

                    mCurrentOrigin.y -= distanceY;
                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
                    break;
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {

            if (mIsZooming) return true;

            mScroller.forceFinished(true);
            mCurrentFlingDirection = mCurrentScrollDirection;


            float target;
            switch (mCurrentFlingDirection) {
                case LEFT:
                    target = weekX - (mWidthPerDay * getNumberOfVisibleDays());
                    mCurrentOrigin.x = target;
                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
                    break;
                case RIGHT:
                    target = weekX + (mWidthPerDay * getNumberOfVisibleDays());
                    mCurrentOrigin.x = target;
                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
                    break;
                case VERTICAL:
                    mScroller.fling((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, 0, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, (int) -(mHourHeight * 24 + mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom + mTimeTextHeight / 2 - getHeight()), 0);
                    ViewCompat.postInvalidateOnAnimation(WeekView.this);
                    break;
            }

            new Handler().postDelayed(() -> invalidate(), 100);
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {

            if (getNumberOfVisibleDays() != 1) {
                for (Map.Entry<RectF, Calendar> entry : dayLabelRect1.entrySet()) {
                    RectF rect = entry.getKey();
                    Calendar day = entry.getValue();
                    if (rect.contains(e.getX(), e.getY())) {
                        onDayLabelClick(day);
                        break;
                    }
                }
            }


            if (mEventRectS != null && mEventClickListener != null) {
                List<EventRect> reversedEventRectS = mEventRectS;
                for (EventRect event : reversedEventRectS) {
                    if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                        isInEvent = true;
                        selectedEvent = event.event;
                        eventRect = event.rectF;
                    }
                }
            }


            if (isInEvent && isInTask) {
                rippleRect = new RectF(taskRect.left, taskRect.top, taskRect.right, taskRect.bottom);
                rippleRadius = 0;
                rippleHandler.post(rippleRunnable);
                isInTask = false;
                isInEvent = false;
                eventRect = null;
                taskRect = null;
                selectedEvent = null;
                playSoundEffect(SoundEffectConstants.CLICK);
                return super.onSingleTapConfirmed(e);
            } else if (isInEvent) {
                rippleRect = new RectF(eventRect.left, eventRect.top, eventRect.right, eventRect.bottom);
                isRippleActiveEvent = true;
                rippleRadius = 0;
                rippleHandler.post(rippleRunnable);
                mEventClickListener.onEventClick(selectedEvent, eventRect);
                isInTask = false;
                isInEvent = false;
                eventRect = null;
                taskRect = null;
                selectedEvent = null;
                playSoundEffect(SoundEffectConstants.CLICK);
                return super.onSingleTapConfirmed(e);
            } else if (isInTask) {
                rippleRect = new RectF(taskRect.left, taskRect.top, taskRect.right, taskRect.bottom);
                rippleRadius = 0;
                rippleHandler.post(rippleRunnable);
                isInTask = false;
                isInEvent = false;
                eventRect = null;
                taskRect = null;
                selectedEvent = null;
                playSoundEffect(SoundEffectConstants.CLICK);
                return super.onSingleTapConfirmed(e);
            }


            // If the tap was on in an empty space, then trigger the callback.
            if (mEmptyViewClickListener != null && e.getX() > mTimeColumnWidth && e.getY() > (mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom)) {
                Calendar selectedTime = getTimeFromPoint(e.getX(), e.getY());
                if (selectedTime != null) {
                    playSoundEffect(SoundEffectConstants.CLICK);
                    mEmptyViewClickListener.onEmptyViewClicked(selectedTime);
                }
            }

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);


            // If the tap was on in an empty space, then trigger the callback.
            if (mEmptyViewLongPressListener != null && e.getX() > mTimeColumnWidth && e.getY() > (mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom)) {
                Calendar selectedTime = getTimeFromPoint(e.getX(), e.getY());
                if (selectedTime != null) {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    mEmptyViewLongPressListener.onEmptyViewLongPress(selectedTime);
                }
            }
        }
    };
    private DateTimeInterpreter mDateTimeInterpreter;
    private ScrollListener mScrollListener;

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Hold references.
        mContext = context;

        // Get the attribute values (if any).
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekView, 0, 0);
        try {
            mHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourHeight, mHourHeight);
            mMinHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_minHourHeight, mMinHourHeight);
            mEffectiveMinHourHeight = mMinHourHeight;
            mMaxHourHeight = a.getDimensionPixelSize(R.styleable.WeekView_maxHourHeight, mMaxHourHeight);
            mTextSize = a.getDimensionPixelSize(R.styleable.WeekView_textSize, mTextSize);
            mTimeColumnPadding = a.getDimensionPixelSize(R.styleable.WeekView_headerColumnPadding, mTimeColumnPadding);
            mHeaderColumnTextColor = a.getColor(R.styleable.WeekView_headerColumnTextColor, mHeaderColumnTextColor);
            mNumberOfVisibleDays = a.getInteger(R.styleable.WeekView_noOfVisibleDays, mNumberOfVisibleDays);
            mShowFirstDayOfWeekFirst = a.getBoolean(R.styleable.WeekView_showFirstDayOfWeekFirst, mShowFirstDayOfWeekFirst);
            mHeaderRowPadding = a.getDimensionPixelSize(R.styleable.WeekView_headerRowPadding, mHeaderRowPadding);
            mHeaderRowBackgroundColor = a.getColor(R.styleable.WeekView_headerRowBackgroundColor, mHeaderRowBackgroundColor);
            mDayBackgroundColor = a.getColor(R.styleable.WeekView_dayBackgroundColor, mDayBackgroundColor);
            mFutureBackgroundColor = a.getColor(R.styleable.WeekView_futureBackgroundColor, mFutureBackgroundColor);
            mPastBackgroundColor = a.getColor(R.styleable.WeekView_pastBackgroundColor, mPastBackgroundColor);
            mFutureWeekendBackgroundColor = a.getColor(R.styleable.WeekView_futureWeekendBackgroundColor, mFutureBackgroundColor); // If not set, use the same color as in the week
            mPastWeekendBackgroundColor = a.getColor(R.styleable.WeekView_pastWeekendBackgroundColor, mPastBackgroundColor);
            mNowLineColor = a.getColor(R.styleable.WeekView_nowLineColor, mNowLineColor);
            mNowLineThickness = a.getDimensionPixelSize(R.styleable.WeekView_nowLineThickness, mNowLineThickness);
            mHourSeparatorColor = a.getColor(R.styleable.WeekView_hourSeparatorColor, mHourSeparatorColor);
            mTodayBackgroundColor = a.getColor(R.styleable.WeekView_todayBackgroundColor, mTodayBackgroundColor);
            mHourSeparatorHeight = a.getDimensionPixelSize(R.styleable.WeekView_hourSeparatorHeight, mHourSeparatorHeight);
            mTodayHeaderTextColor = a.getColor(R.styleable.WeekView_todayHeaderTextColor, mTodayHeaderTextColor);
            mEventTextSize = a.getDimensionPixelSize(R.styleable.WeekView_eventTextSize, mEventTextSize);
            mEventTextColor = a.getColor(R.styleable.WeekView_eventTextColor, mEventTextColor);
            mEventPadding = a.getDimensionPixelSize(R.styleable.WeekView_eventPadding, mEventPadding);
            mHeaderColumnBackgroundColor = a.getColor(R.styleable.WeekView_headerColumnBackground, mHeaderColumnBackgroundColor);
            mDayNameLength = a.getInteger(R.styleable.WeekView_dayNameLength, mDayNameLength);
            mOverlappingEventGap = a.getDimensionPixelSize(R.styleable.WeekView_overlappingEventGap, mOverlappingEventGap);
            mEventMarginVertical = a.getDimensionPixelSize(R.styleable.WeekView_eventMarginVertical, mEventMarginVertical);
            mXScrollingSpeed = a.getFloat(R.styleable.WeekView_xScrollingSpeed, mXScrollingSpeed);
            mEventCornerRadius = a.getDimensionPixelSize(R.styleable.WeekView_eventCornerRadius, mEventCornerRadius);
            mShowDistinctPastFutureColor = a.getBoolean(R.styleable.WeekView_showDistinctPastFutureColor, mShowDistinctPastFutureColor);
            mShowDistinctWeekendColor = a.getBoolean(R.styleable.WeekView_showDistinctWeekendColor, mShowDistinctWeekendColor);
            mShowNowLine = a.getBoolean(R.styleable.WeekView_showNowLine, mShowNowLine);
            mHorizontalFlingEnabled = a.getBoolean(R.styleable.WeekView_horizontalFlingEnabled, mHorizontalFlingEnabled);
            mVerticalFlingEnabled = a.getBoolean(R.styleable.WeekView_verticalFlingEnabled, mVerticalFlingEnabled);
            mAllDayEventHeight = a.getDimensionPixelSize(R.styleable.WeekView_allDayEventHeight, mAllDayEventHeight);
            mScrollDuration = a.getInt(R.styleable.WeekView_scrollDuration, mScrollDuration);


            mFirstDayOfWeek = Calendar.SUNDAY;

           /* if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, getContext()).equals(getContext().getString(R.string.strSystemDefault))) {
                mFirstDayOfWeek = Calendar.SUNDAY;
            } else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, getContext()).equals(getContext().getString(R.string.strSunday))) {
                mFirstDayOfWeek = Calendar.SUNDAY;
            } else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, getContext()).equals(getContext().getString(R.string.strMonday))) {
                mFirstDayOfWeek = Calendar.MONDAY;
            } else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, getContext()).equals(getContext().getString(R.string.strSaturday))) {
                mFirstDayOfWeek = Calendar.SATURDAY;
            }*/

        } finally {
            a.recycle();
        }

        init();
    }

    private void onDayLabelClick(Calendar day) {
        // Handle the click event here

        setNumberOfVisibleDays(1);
        goToDate(day);

    }

    private void init() {


        ripplePaint = new Paint();
        ripplePaint.setColor(Color.GRAY);  // Rectangle color


        mGestureDetector = new GestureDetector(mContext, mGestureListener);
        mScroller = new OverScroller(mContext, new FastOutLinearInInterpolator());
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();


        Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setShadowLayer(12, 0, 0, mHourSeparatorColor);
        setLayerType(LAYER_TYPE_SOFTWARE, shadowPaint);


        // Measure settings for time column.
        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTimeTextPaint.setTextSize(mTextSize);
        mTimeTextPaint.setColor(mHeaderColumnTextColor);
        Rect rect = new Rect();
        mTimeTextPaint.getTextBounds("00 PM", 0, "00 PM".length(), rect);
        mTimeTextHeight = rect.height();
        mHeaderMarginBottom = mTimeTextHeight / 2;
        initTextTimeWidth();

        // Measure settings for header row.
        mDayNameTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayNameTextPaint.setColor(mHeaderColumnTextColor);
        mDayNameTextPaint.setTextAlign(Paint.Align.CENTER);
        mDayNameTextPaint.setTextSize(mTextSize - 1);
        mDayNameTextPaint.getTextBounds("0", 0, "0".length(), rect);
        mHeaderDayNameTextHeight = rect.height();

        mTodayDayNameTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTodayDayNameTextPaint.setTextAlign(Paint.Align.CENTER);
        mTodayDayNameTextPaint.setTextSize(mTextSize);
        mTodayDayNameTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTodayDayNameTextPaint.setColor(mTodayHeaderTextColor);

        mDateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDateTextPaint.setColor(mHeaderColumnTextColor);
        mDateTextPaint.setTextAlign(Paint.Align.CENTER);
        mDateTextPaint.setTextSize(mTextSize * 1.6f);
        mDateTextPaint.getTextBounds("00", 0, "00".length(), rect);
        mHeaderDateTextHeight = rect.height();

        mTodayDateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.ThemeIconColor, typedValue, true);
        int ThemeIconColor = typedValue.data;
        TaskColor = typedValue.data;


        mTodayDateTextPaint.setColor(ThemeIconColor);
        mTodayDateTextPaint.setTextAlign(Paint.Align.CENTER);
        mTodayDateTextPaint.setTextSize(mTextSize * 1.6f);

        mEventBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mHeaderBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderBackgroundPaint.setColor(mHeaderRowBackgroundColor);

        // Prepare day background color paint.
        Paint mDayBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayBackgroundPaint.setColor(mDayBackgroundColor);
        Paint mFutureBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFutureBackgroundPaint.setColor(mFutureBackgroundColor);
        Paint mPastBackgroundPaint = new Paint();
        mPastBackgroundPaint.setColor(mPastBackgroundColor);
        Paint mFutureWeekendBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFutureWeekendBackgroundPaint.setColor(mFutureWeekendBackgroundColor);
        Paint mPastWeekendBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPastWeekendBackgroundPaint.setColor(mPastWeekendBackgroundColor);

        // Prepare hour separator color paint.
        mHourSeparatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourSeparatorPaint.setStyle(Paint.Style.STROKE);
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight);
        int originalColor = mHourSeparatorColor;
        int alpha = (int) (255 * 0.40); // 20% opacity
        int newColor = (alpha << 24) | (originalColor & 0x00FFFFFF);
        mHourSeparatorPaint.setColor(newColor);

        // Prepare the "now" line color paint
        mNowLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNowLinePaint.setStrokeWidth(mNowLineThickness);
        mNowLinePaint.setColor(mNowLineColor);


        // Prepare today background color paint.
        mTodayBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTodayBackgroundPaint.setColor(mTodayBackgroundColor);


        // Prepare today header text color paint.
        mTaskBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        getContext().getTheme().resolveAttribute(R.attr.TaskColor, typedValue, true);
        TaskColor = typedValue.data;
        int newTaskColor = ColorUtils.setAlphaComponent(TaskColor, (int) (255 * 0.4f));
        mTaskBackgroundPaint.setColor(newTaskColor);


        // Prepare header column background color.
        mHeaderColumnBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderColumnBackgroundPaint.setColor(mHeaderColumnBackgroundColor);

        // Prepare event text size and color.
        mEventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mEventTextPaint.setStyle(Paint.Style.FILL);
        mEventTextPaint.setColor(mEventTextColor);
        mEventTextPaint.setTextSize((float) (mEventTextSize));

        jheaderEventTextpaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        jheaderEventTextpaint.setColor(Color.WHITE);
        jheaderEventTextpaint.setTextAlign(Paint.Align.LEFT);
        jheaderEventTextpaint.setTextSize(mTextSize);
        jheaderEventTextpaint.getTextBounds("a", 0, "a".length(), rect);

        // Set default event color.

        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                mIsZooming = false;

            }

            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                mIsZooming = true;
                goToNearestOrigin();
                return true;
            }

            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                mNewHourHeight = Math.round(mHourHeight * detector.getScaleFactor());
                invalidate();
                return true;
            }
        });
    }

    // fix rotation changes
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mAreDimensionsInvalid = true;
    }

    private void initTextTimeWidth() {
        mTimeTextWidth = 0;
        for (int i = 0; i < 24; i++) {
            // Measure time string and get max width.
            int hour = getDateTimeInterpreter().interpretTime(i);

            String time;

            if (hour < 11) {
                time = (hour + 1) + " AM";
            } else {

                if (hour == 11) {
                    time = 12 + " PM";
                } else {
                    time = ((hour + 1) - 12) + " PM";
                }
            }
            mTimeTextWidth = Math.max(mTimeTextWidth, mTimeTextPaint.measureText(time));
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);


        // Draw the header row.
        drawHeaderRowAndEvents(canvas);
        if (mNumberOfVisibleDays != 1) drawTimeColumnAndAxes(canvas);


    }


    public void calculateHeaderHeight() {
        //Make sure the header is the right size (depends on AllDay events)
        boolean containsAllDayEvent = false;
        int maxEventCount = 1;


        if (mEventRectS != null && mEventRectS.size() > 0) {

            for (int dayNumber = 0; dayNumber < mNumberOfVisibleDays; dayNumber++) {
                Calendar day = (Calendar) getFirstVisibleDay().clone();
                day.add(Calendar.DATE, dayNumber);
                int noOfEvent = 0;
                Set<Integer> drawnEventIds = new HashSet<>();
                Set<Integer> drawnTaskIds = new HashSet<>();

                for (int i = 0; i < mEventRectS.size(); i++) {

                    Calendar calendarStart = Calendar.getInstance();
                    calendarStart.setTimeInMillis(mEventRectS.get(i).event.getStartTime());

                    Calendar calendarEnd = Calendar.getInstance();
                    calendarEnd.setTimeInMillis(mEventRectS.get(i).event.getEndTime());

                    if (CalendarHelper.INSTANCE.isSameDay(calendarStart, day) && mEventRectS.get(i).event.getAllDay() == true || (CalendarHelper.INSTANCE.isSameDay(calendarStart, day) && !new LocalDate(calendarStart.getTimeInMillis()).equals(new LocalDate(calendarEnd.getTimeInMillis())))) {

                        int eventId = mEventRectS.get(i).event.getEventId(); // Assuming event has a unique ID
                        // Skip drawing if event has already been drawn
                        if (drawnEventIds.contains(eventId)) {
                            continue;
                        }

                        drawnEventIds.add(eventId);

                        if (mNumberOfVisibleDays != 1) {
                            modelDate modelDate = new modelDate(new LocalDate(day.getTimeInMillis()).getDayOfMonth(), new LocalDate(day.getTimeInMillis()).getMonthOfYear());
                            List<Map<modelDate, Integer>> mapList = getMapsContainingKey(modelDate);

                            for (int j = 0; j < mapList.size(); j++) {

                                Map<modelDate, Integer> map = mapList.get(j);
                                Integer count = map.entrySet().iterator().next().getValue();

                                if (count == noOfEvent) {
                                    noOfEvent++;

                                }
                            }

                            noOfEvent++;
                            containsAllDayEvent = true;
                        }
                    }

                }


                int totalCount = noOfEvent;

                // Update maxEventCount if totalCount is greater
                if (totalCount > maxEventCount) {
                    maxEventCount = totalCount;
                }
            }
        }


        if (mNumberOfVisibleDays == 1) {
            float without = mHeaderDayNameTextHeight + mHeaderDateTextHeight + mHeaderMarginBottom;
            if (containsAllDayEvent) {
                float with = (mAllDayEventHeight * maxEventCount) + ((maxEventCount - 1));
                mHeaderHeight = Math.max(without, with);

            } else {

                mHeaderHeight = without;
            }
            shadow.setY(mHeaderHeight + mHeaderRowPadding * 3);


        } else {
            if (containsAllDayEvent) {
                mHeaderHeight = mHeaderDayNameTextHeight + mHeaderDateTextHeight + ((mAllDayEventHeight * maxEventCount) + ((maxEventCount - 1)) + mHeaderMarginBottom);
                shadow.setY(mHeaderHeight + mHeaderRowPadding * 3);
            } else {
                mHeaderHeight = mHeaderDayNameTextHeight + mHeaderDateTextHeight + mHeaderMarginBottom;
                shadow.setY(mHeaderHeight + mHeaderRowPadding * 3);
            }
        }


    }

    private Runnable rippleRunnable = new Runnable() {
        @Override
        public void run() {
            if (rippleRadius < MAX_RIPPLE_RADIUS) {
                rippleRadius += 10;  // Increase radius
                invalidate();  // Redraw the view
                rippleHandler.postDelayed(this, 16);  // Schedule next frame
            } else {
                isRippleActiveEvent = false;
                rippleHandler.removeCallbacks(this); // Stop the handler
                invalidate();  // Redraw to clear ripple
            }
        }
    };

    private void canvasClipRect(Canvas mCanvas, float left, float top, float right, float bottom) {
        mCanvas.restore();
        mCanvas.save();
        mCanvas.clipRect(left, top, right, bottom);
    }

    private void drawTimeColumnAndAxes(Canvas canvas) {
        // Draw the background color for the header column.
        canvas.drawRect(0, mHeaderHeight + mHeaderRowPadding * 3, mTimeColumnWidth, getHeight(), mHeaderColumnBackgroundPaint);
        // Clip to paint in left column only.
        canvasClipRect(canvas, 0, mHeaderHeight + mHeaderRowPadding * 3, mTimeColumnWidth, getHeight());

        for (int i = 0; i < 24; i++) {
            float top = mHeaderHeight + mHeaderRowPadding * 3 + mCurrentOrigin.y + mHourHeight * i + mHeaderMarginBottom;

            int hour = getDateTimeInterpreter().interpretTime(i);

            String time;

            if (hour == 0) {
                time = "";
            } else if (hour < 12) {
                time = (hour) + " AM";
            } else {
                if (hour == 12) {
                    time = 12 + " PM";
                } else {
                    time = ((hour) - 12) + " PM";
                }
            }
            if (top < getHeight())
                canvas.drawText(time, (int) (mTimeTextWidth + mTimeColumnPadding / 2), top + (mTimeTextHeight / 2), mTimeTextPaint);
        }
    }

    private void drawTimeColumnAndAxes1day(Canvas canvas) {
        // Draw the background color for the header column.
        canvasClipRect(canvas, 0, mHeaderHeight + mHeaderRowPadding * 3, mTimeColumnWidth, getHeight());

        for (int i = 0; i < 24; i++) {
            float top = mHeaderHeight + mHeaderRowPadding * 3 + mCurrentOrigin.y + mHourHeight * i + mHeaderMarginBottom;

            // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
            int hour = getDateTimeInterpreter().interpretTime(i);

            String time;

            if (hour == 0) {
                time = "";
            } else if (hour < 12) {
                time = (hour) + " AM";
            } else {
                if (hour == 12) {
                    time = 12 + " PM";
                } else {
                    time = ((hour) - 12) + " PM";
                }
            }
            if (top < getHeight())
                canvas.drawText(time, (int) (mTimeTextWidth + mTimeColumnPadding / 2), top + (mTimeTextHeight / 2), mTimeTextPaint);
        }
        canvasClipRect(canvas, 0, 0, mTimeColumnWidth, getHeight());

    }

    private void drawHeaderRowAndEvents(Canvas canvas) {
        dayLabelRect1.clear();

        //timerColumnWidth...
        mTimeColumnWidth = mTimeTextWidth + mTimeColumnPadding;

        //mWidthPerDay
        mWidthPerDay = getWidth() - mTimeColumnWidth - (mNumberOfVisibleDays - 1);

        if (mNumberOfVisibleDays == 1) {
            mWidthPerDay = getWidth();
        } else {
            mWidthPerDay = mWidthPerDay / mNumberOfVisibleDays;
        }


        //mHeaderHeight
        calculateHeaderHeight();


        //todayDate...
        Calendar today = CalendarHelper.INSTANCE.today();


        //zoom scroll Invalidate...
        if (mAreDimensionsInvalid) {
            mEffectiveMinHourHeight = Math.max(mMinHourHeight, (int) ((getHeight() - mHeaderHeight - mHeaderRowPadding * 3 - mHeaderMarginBottom) / 24));

            mAreDimensionsInvalid = false;
            if (mScrollToDay != null) goToDate(mScrollToDay);

            mAreDimensionsInvalid = false;
            if (mScrollToHour >= 0) goToHour(mScrollToHour);

            mScrollToDay = null;
            mScrollToHour = -1;
            mAreDimensionsInvalid = false;
        }

        //firstTimeDrawWeek
        if (mIsFirstDraw) {
            mIsFirstDraw = false;
            // If the week view is being drawn for the first time, then consider the first day of the week.
            if (mNumberOfVisibleDays >= 7 && today.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek && mShowFirstDayOfWeekFirst) {
                int difference = (today.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek);

                mCurrentOrigin.x += (mWidthPerDay) * difference;
            }
        }

        // new height due to the zooming.
        if (mNewHourHeight > 0) {
            if (mNewHourHeight < mEffectiveMinHourHeight) mNewHourHeight = mEffectiveMinHourHeight;
            else if (mNewHourHeight > mMaxHourHeight) mNewHourHeight = mMaxHourHeight;

            mCurrentOrigin.y = (mCurrentOrigin.y / mHourHeight) * mNewHourHeight;
            mHourHeight = mNewHourHeight;
            mNewHourHeight = -1;
        }

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < getHeight() - mHourHeight * 24 - mHeaderHeight - mHeaderRowPadding * 3 - mHeaderMarginBottom - mTimeTextHeight / 2) {
            mCurrentOrigin.y = getHeight() - mHourHeight * 24 - mHeaderHeight - mHeaderRowPadding * 3 - mHeaderMarginBottom - mTimeTextHeight / 2;
        }

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0;
        }


        // Consider scroll offset.
        leftDaysWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / (mWidthPerDay)));
        float startFromPixel = mCurrentOrigin.x + (mWidthPerDay) * leftDaysWithGaps + mTimeColumnWidth;
        startPixel = startFromPixel;

        // Prepare to iterate for each day.
        Calendar day = (Calendar) today.clone();
        day.add(Calendar.HOUR, 6);

        // Prepare to iterate for each hour to draw the hour lines.
        int lineCount = (int) ((getHeight() - mHeaderHeight - mHeaderRowPadding * 3 - mHeaderMarginBottom) / mHourHeight) + 1;
        lineCount = (lineCount) * (mNumberOfVisibleDays + 1);
        float[] hourLines = new float[lineCount * 4];


        float starY = 0;
        Calendar oldFirstVisibleDay = mFirstVisibleDay;
        mFirstVisibleDay = (Calendar) today.clone();
        mFirstVisibleDay.add(Calendar.DATE, -(Math.round(mCurrentOrigin.x / (mWidthPerDay))));

        if (!mFirstVisibleDay.equals(oldFirstVisibleDay) && mScrollListener != null) {
            mScrollListener.onFirstVisibleDayChanged(mFirstVisibleDay, oldFirstVisibleDay);
        }
        for (int dayNumber = leftDaysWithGaps + 1; dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1; dayNumber++) {

            day = (Calendar) today.clone();
            Calendar mLastVisibleDay = (Calendar) day.clone();
            day.add(Calendar.DATE, dayNumber - 1);
            mLastVisibleDay.add(Calendar.DATE, dayNumber - 2);

            // Get more events if necessary. We want to store the events 3 months beforehand. Get
            // events only when it is the first iteration of the loop.
            if (mEventRectS == null || mRefreshEvents || (dayNumber == leftDaysWithGaps + 1 && mFetchedPeriod != (int) mWeekViewLoader.toWeekViewPeriodIndex(day) && Math.abs(mFetchedPeriod - mWeekViewLoader.toWeekViewPeriodIndex(day)) > 0.5)) {
                getMoreTaskAndEvents(day);
                mRefreshEvents = false;
                calculateHeaderHeight();
            }


            // Draw background color for each day.
            float start = (Math.max(startPixel, mTimeColumnWidth));
            canvasClipRect(canvas, 0, starY, getWidth(), getHeight());

            // Prepare the separator lines for hours.
            int i = 0;
            for (int hourNumber = 0; hourNumber < 24; hourNumber++) {
                float top = mHeaderHeight + mHeaderRowPadding * 3 + mCurrentOrigin.y + mHourHeight * (hourNumber) + mHeaderMarginBottom;

                if (top > mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom && top < getHeight() && startPixel + mWidthPerDay - start > 0) {
                    if (mNumberOfVisibleDays != 1) {
                        hourLines[i * 4] = start;
                        hourLines[i * 4 + 1] = top;
                        hourLines[i * 4 + 2] = startPixel + mWidthPerDay;
                    } else {
                        hourLines[i * 4] = startPixel;
                        hourLines[i * 4 + 1] = top;
                        hourLines[i * 4 + 2] = startPixel + mWidthPerDay - mTimeColumnWidth;
                    }
                    hourLines[i * 4 + 3] = top;
                    i++;
                }
            }

            // Draw the lines for hours.
            canvas.drawLines(hourLines, mHourSeparatorPaint);
            if (mNumberOfVisibleDays != 1)
                canvasClipRect(canvas, mTimeColumnWidth, mHeaderHeight, getWidth(), getHeight());

            Log.d(TAG, "drawHeaderRowAndEvents: startPixel = "+startPixel);
            drawEvents(day, startPixel, canvas);

            // In the next iteration, start from the next day.
            startPixel += mWidthPerDay;
        }


        if (mNumberOfVisibleDays != 1)
            canvasClipRect(canvas, mTimeColumnWidth, 0, getWidth(), getHeight());
        canvas.drawRect(0, 0, getWidth(), mHeaderHeight + mHeaderRowPadding * 3, mHeaderBackgroundPaint);
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight * 2);
        if (mNumberOfVisibleDays != 1)
            canvas.drawLine(mTimeColumnWidth, mHeaderHeight + mHeaderRowPadding * 3, mTimeColumnWidth, getHeight(), mHourSeparatorPaint);
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight);


        startPixel = startFromPixel;

        for (int dayNumber = leftDaysWithGaps + 1; dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1; dayNumber++) {
            // Check if the day is today.

            day = (Calendar) today.clone();
            day.add(Calendar.DATE, dayNumber - 1);
            boolean sameDay = CalendarHelper.INSTANCE.isSameDay(day, today);


            // Draw the day labels.
            String dateLabel = getDateTimeInterpreter().interpretDate(day);
            String dayLabel = getDateTimeInterpreter().interpretDay(day);
            if (dayLabel == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null date");


            float x = startPixel + mWidthPerDay / 2;
            float xx = startPixel - mTimeColumnWidth / 2.0f;
            float y = (mHeaderDayNameTextHeight + mHeaderRowPadding * 2.5f + mHeaderDateTextHeight) - mHeaderDateTextHeight / 2.0f;
            size = (int) getResources().getDimension(com.intuit.sdp.R.dimen._10sdp);

            if (mNumberOfVisibleDays != 1) {
                //for the raw
                canvas.drawLine(startPixel, mHeaderHeight + mHeaderRowPadding * 3, startPixel, getHeight(), mHourSeparatorPaint);
            } else {
                canvas.drawLine(startPixel, mHeaderRowPadding / 3.0f, startPixel, getHeight(), mHourSeparatorPaint);
            }
            if (mNumberOfVisibleDays == 1) {
                if (sameDay)
                    canvas.drawRoundRect(xx - size, y - size, xx + size, y + size, size, size, mTodayBackgroundPaint);
                canvas.drawText(dayLabel, startPixel - mTimeColumnWidth / 2.0f, mHeaderDayNameTextHeight + mHeaderRowPadding * 1.5f, sameDay ? mTodayDayNameTextPaint : mDayNameTextPaint);
                canvas.drawText(dateLabel, startPixel - mTimeColumnWidth / 2.0f, mHeaderDayNameTextHeight + mHeaderRowPadding * 2.5f + mHeaderDateTextHeight, sameDay ? mTodayDateTextPaint : mDateTextPaint);
                RectF dayLabelRect = new RectF(startPixel - mTimeColumnWidth / 2.0f - size, y - size, startPixel - mTimeColumnWidth / 2.0f + size, y + size);
                dayLabelRect1.put(dayLabelRect, (Calendar) day.clone());
                drawAllDayEvents(day, startPixel, canvas);

            } else {
                if (sameDay)
                    canvas.drawRoundRect(x - size, y - size, x + size, y + size, size, size, mTodayBackgroundPaint);
                canvas.drawText(dayLabel, startPixel + mWidthPerDay / 2, mHeaderDayNameTextHeight + mHeaderRowPadding * 1.5f, sameDay ? mTodayDayNameTextPaint : mDayNameTextPaint);
                canvas.drawText(dateLabel, startPixel + mWidthPerDay / 2, mHeaderDayNameTextHeight + mHeaderRowPadding * 2.5f + mHeaderDateTextHeight, sameDay ? mTodayDateTextPaint : mDateTextPaint);
                RectF dayLabelRect = new RectF(startPixel + mWidthPerDay / 2 - size, y - size, startPixel + mWidthPerDay / 2 + size, y + size);
                dayLabelRect1.put(dayLabelRect, (Calendar) day.clone());
                drawAllDayEvents(day, startPixel, canvas);

            }

            if (mShowNowLine && sameDay) {
                float startY = mHeaderHeight + mHeaderRowPadding * 3 + mTimeTextHeight / 2 + mCurrentOrigin.y;
                Calendar now = Calendar.getInstance();
                float beforeNow = ((now.get(Calendar.HOUR_OF_DAY)) + now.get(Calendar.MINUTE) / 60.0f) * mHourHeight;

                float startAt = Math.max(startPixel, mTimeColumnWidth);
                float wid = mWidthPerDay;
                float per = 20 * (1.0f - (startAt - startPixel) / wid);
                if (mNumberOfVisibleDays != 1) {
                    canvasClipRect(canvas, 0, mHeaderHeight + mHeaderRowPadding * 3, getWidth(), getHeight());
                    canvas.drawRoundRect(startAt, startY + beforeNow - (per / 2), startAt + per, startY + beforeNow + (per / 2), per, per, mNowLinePaint);
                    canvas.drawLine(startAt, startY + beforeNow, startPixel + wid, startY + beforeNow, mNowLinePaint);
                    canvasClipRect(canvas, mTimeColumnWidth, 0, getWidth(), getHeight());
                } else {
                    canvasClipRect(canvas, 0, mHeaderHeight + mHeaderRowPadding * 3, getWidth(), getHeight());
                    canvas.drawRoundRect(startPixel, startY + beforeNow - 12, startPixel + 24, startY + beforeNow + 12, 20, 20, mNowLinePaint);
                    canvas.drawLine(startPixel, startY + beforeNow, startPixel + wid - mTimeColumnWidth, startY + beforeNow, mNowLinePaint);
                    canvasClipRect(canvas, 0, 0, getWidth(), getHeight());
                }

            }
            if (mNumberOfVisibleDays == 1) drawTimeColumnAndAxes1day(canvas);

            startPixel += mWidthPerDay;

        }


    }

    private Calendar getTimeFromPoint(float x, float y) {
        int leftDaysWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / mWidthPerDay));
        float startPixel = mCurrentOrigin.x + mWidthPerDay * leftDaysWithGaps + mTimeColumnWidth;

        for (int dayNumber = leftDaysWithGaps + 1; dayNumber <= leftDaysWithGaps + mNumberOfVisibleDays + 1; dayNumber++) {
            float start = Math.max(startPixel, mTimeColumnWidth);
            if (mWidthPerDay + startPixel - start > 0 && x > start && x < startPixel + mWidthPerDay) {
                Calendar day = CalendarHelper.INSTANCE.today();
                day.add(Calendar.DATE, dayNumber - 1);

                // Calculate the Y position relative to the start of the time grid.
                float yPositionRelativeToGrid = y - (mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom);

                // Adjust the y position by the scroll offset.
                float pixelsFromZero = yPositionRelativeToGrid - mCurrentOrigin.y;

                // Convert pixels to hours.
                int hour = (int) (pixelsFromZero / mHourHeight);
//                int minute = (int) ((pixelsFromZero - hour * mHourHeight) / mHourHeight * 60);

                // Set the hour and minute to the calendar.
                day.set(Calendar.HOUR_OF_DAY, hour);
                day.set(Calendar.MINUTE, 0);

                return day;
            }
            startPixel += mWidthPerDay;
        }
        return null;
    }


    @Override
    public void invalidate() {
        super.invalidate();
        mAreDimensionsInvalid = true;
    }

    public void setOnEventClickListener(EventClickListener listener) {
        this.mEventClickListener = listener;
    }

    public void setShadow(View shadow) {
        this.shadow = shadow;
    }

    public void setFont(Typeface typeface, int type) {
        if (type == 0) {
            mTimeTextPaint.setTypeface(typeface);
            mDateTextPaint.setTypeface(null);


        } else {
            mDayNameTextPaint.setTypeface(typeface);
            jheaderEventTextpaint.setTypeface(typeface);

        }
        invalidate();

    }

    public void setMonthChangeListener(MonthLoader.MonthChangeListener monthChangeListener) {
        this.mWeekViewLoader = new MonthLoader(monthChangeListener);
    }


    public void setEventLongPressListener(EventLongPressListener eventLongPressListener) {
        this.mEventLongPressListener = eventLongPressListener;
    }

    public void setEmptyViewClickListener(EmptyViewClickListener emptyViewClickListener) {
        this.mEmptyViewClickListener = emptyViewClickListener;
    }


    public void setEmptyViewLongPressListener(EmptyViewLongPressListener emptyViewLongPressListener) {
        this.mEmptyViewLongPressListener = emptyViewLongPressListener;
    }


    public void setScrollListener(ScrollListener scrolledListener) {
        this.mScrollListener = scrolledListener;
    }

    public DateTimeInterpreter getDateTimeInterpreter() {
        if (mDateTimeInterpreter == null) {
            mDateTimeInterpreter = new DateTimeInterpreter() {
                @Override
                public String interpretDay(Calendar date) {
                    try {
                        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_NUMERIC_DATE;
                        String localizedDate = DateUtils.formatDateTime(getContext(), date.getTime().getTime(), flags);
                        SimpleDateFormat sdf = mDayNameLength == LENGTH_SHORT ? new SimpleDateFormat("EEEE", Locale.getDefault()) : new SimpleDateFormat("EEE", Locale.getDefault());
                        return String.format("%s %s", sdf.format(date.getTime()).toUpperCase(), localizedDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "";
                    }
                }

                @Override
                public String interpretDate(Calendar date) {
                    int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
                    return dayOfMonth + "";
                }

                @Override
                public int interpretTime(int hour) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, 0);
                    return hour;
                }

            };
        }
        return mDateTimeInterpreter;
    }

    public void setDateTimeInterpreter(DateTimeInterpreter dateTimeInterpreter) {
        this.mDateTimeInterpreter = dateTimeInterpreter;

        // Refresh time column width.
        initTextTimeWidth();
    }

    public int getNumberOfVisibleDays() {
        return mNumberOfVisibleDays;
    }

    public void setNumberOfVisibleDays(int numberOfVisibleDays) {
        this.mNumberOfVisibleDays = numberOfVisibleDays;
        mCurrentOrigin.x = 0;
        mCurrentOrigin.y = 0;
        invalidate();
    }


    public int getFirstDayOfWeek() {
        return mFirstDayOfWeek;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        mTodayDayNameTextPaint.setTextSize(mTextSize);
        mDayNameTextPaint.setTextSize(mTextSize);
        mTimeTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    @Deprecated
    public int getDayNameLength() {
        return mDayNameLength;
    }

    @Deprecated
    public void setDayNameLength(int length) {
        if (length != LENGTH_LONG && length != LENGTH_SHORT) {
            throw new IllegalArgumentException("length parameter must be either LENGTH_LONG or LENGTH_SHORT");
        }
        this.mDayNameLength = length;
    }

    public Calendar getFirstVisibleDay() {
        return mFirstVisibleDay;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent(event);
        boolean val = mGestureDetector.onTouchEvent(event);

        // Check after call of mGestureDetector, so mCurrentFlingDirection and mCurrentScrollDirection are set.
        if (event.getAction() == MotionEvent.ACTION_UP && !mIsZooming && mCurrentFlingDirection == Direction.NONE) {

            if (mCurrentScrollDirection == Direction.RIGHT || mCurrentScrollDirection == Direction.LEFT) {
                float k = 0;
                if (mCurrentScrollDirection == Direction.RIGHT && getNumberOfVisibleDays() == 1)
                    k = mTimeColumnWidth;
                int next;

                if (getNumberOfVisibleDays() == 7) {
                    if (mCurrentScrollDirection == Direction.LEFT) {
                        next = (int) (weekX - mWidthPerDay * 7);
                    } else {
                        next = (int) (weekX + mWidthPerDay * 7);
                    }
                } else {
                    if (mCurrentScrollDirection == Direction.LEFT) {
                        next = (int) (weekX - mWidthPerDay);
                    } else {
                        next = (int) (weekX + mWidthPerDay);
                    }
                }

                int previous = (int) weekX;
                if (Math.abs(Math.abs(mCurrentOrigin.x + k) - Math.abs(next)) < Math.abs(Math.abs(mCurrentOrigin.x + k) - Math.abs(previous))) {

                    mCurrentOrigin.x = next;

                } else {

                    mCurrentOrigin.x = previous;

                }
                ViewCompat.postInvalidateOnAnimation(WeekView.this);
                new Handler().postDelayed(this::invalidate, 100);
            }
            mCurrentScrollDirection = Direction.NONE;
        }

        return val;
    }

    private void goToNearestOrigin() {

        double leftDays = mCurrentOrigin.x / (mWidthPerDay);
        if (getNumberOfVisibleDays() == 1 && mCurrentScrollDirection == Direction.RIGHT) {
            leftDays = (mCurrentOrigin.x + mTimeColumnWidth) / (mWidthPerDay);
        }
        if (mCurrentFlingDirection != Direction.NONE) {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        } else if (mCurrentScrollDirection == Direction.LEFT) {
            // snap to last day
            leftDays = Math.floor(leftDays);
        } else if (mCurrentScrollDirection == Direction.RIGHT) {
            // snap to next day
            leftDays = Math.ceil(leftDays);
        } else {
            // snap to nearest day
            leftDays = Math.round(leftDays);
        }
        int nearestOrigin = (int) (mCurrentOrigin.x - leftDays * (mWidthPerDay));

        if (nearestOrigin != 0) {
            // Stop current animation.
            mScroller.forceFinished(true);
            // Snap to date.
            mScroller.startScroll((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, -nearestOrigin, 0, (int) (Math.abs(nearestOrigin) / mWidthPerDay * mScrollDuration));
            ViewCompat.postInvalidateOnAnimation(WeekView.this);
        }
        // Reset scrolling and fling direction.
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;
    }

    public void goToDate(Calendar date) {

        if (getNumberOfVisibleDays() == 7) {
            int diff = date.get(Calendar.DAY_OF_WEEK) - getFirstDayOfWeek();
            if (diff < 0) {
                date.add(Calendar.DAY_OF_MONTH, -(7 - Math.abs(diff)));
            } else {
                date.add(Calendar.DAY_OF_MONTH, -diff);
            }

        }

        mScroller.forceFinished(true);
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;

        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        if (mAreDimensionsInvalid) {
            mScrollToDay = date;
            return;
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        long day = 1000L * 60L * 60L * 24L;
        long dateInMillis = date.getTimeInMillis() + date.getTimeZone().getOffset(date.getTimeInMillis());
        long todayInMillis = today.getTimeInMillis() + today.getTimeZone().getOffset(today.getTimeInMillis());
        long dateDifference = (dateInMillis / day) - (todayInMillis / day);
        mCurrentOrigin.x = -dateDifference * (mWidthPerDay);
        invalidate();
    }

    public void goToHour(double hour) {
        if (mAreDimensionsInvalid) {
            mScrollToHour = hour;
            return;
        }

        int verticalOffset = 0;
        if (hour > 24) verticalOffset = mHourHeight * 24;
        else if (hour > 0) verticalOffset = (int) (mHourHeight * hour);

        if (verticalOffset > mHourHeight * 24 - getHeight() + mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom)
            verticalOffset = (int) (mHourHeight * 24 - getHeight() + mHeaderHeight + mHeaderRowPadding * 3 + mHeaderMarginBottom);

        mCurrentOrigin.y = -verticalOffset;
        invalidate();
    }


    private enum Direction {
        NONE, LEFT, RIGHT, VERTICAL
    }

    public interface EventClickListener {
        void onEventClick(CalendarEvent event, RectF eventRect);

    }

    public interface EventLongPressListener {
        void onEventLongPress(CalendarEvent event, RectF eventRect);
    }

    public interface EmptyViewClickListener {
        void onEmptyViewClicked(Calendar time);
    }

    public interface EmptyViewLongPressListener {
        void onEmptyViewLongPress(Calendar time);
    }

    public interface ScrollListener {
        void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay);
    }


    private void getMoreTaskAndEvents(Calendar day) {
        if (mEventRectS == null) mEventRectS = new ArrayList<>();
        if (mWeekViewLoader == null && !isInEditMode())
            throw new IllegalStateException("You must provide a MonthChangeListener");

        // If a refresh was requested then reset some variables.
        if (mRefreshEvents) {
            mEventRectS.clear();
            mPreviousPeriodEvents = null;
            mCurrentPeriodEvents = null;
            mNextPeriodEvents = null;
            mFetchedPeriod = -1;
        }

        if (mWeekViewLoader != null) {
            int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(day);

            if (!isInEditMode() && (mFetchedPeriod < 0 || mFetchedPeriod != periodToFetch || mRefreshEvents)) {
                List<? extends CalendarEvent> previousPeriodEvents = null;
                List<? extends CalendarEvent> currentPeriodEvents = null;
                List<? extends CalendarEvent> nextPeriodEvents = null;

                if (mPreviousPeriodEvents != null && mCurrentPeriodEvents != null && mNextPeriodEvents != null) {
                    if (periodToFetch == mFetchedPeriod - 1) {
                        currentPeriodEvents = mPreviousPeriodEvents;
                        nextPeriodEvents = mCurrentPeriodEvents;
                    } else if (periodToFetch == mFetchedPeriod) {
                        previousPeriodEvents = mPreviousPeriodEvents;
                        currentPeriodEvents = mCurrentPeriodEvents;
                        nextPeriodEvents = mNextPeriodEvents;
                    } else if (periodToFetch == mFetchedPeriod + 1) {
                        previousPeriodEvents = mCurrentPeriodEvents;
                        currentPeriodEvents = mNextPeriodEvents;
                    }
                }
                if (currentPeriodEvents == null)
                    currentPeriodEvents = mWeekViewLoader.onLoad(periodToFetch);
                if (previousPeriodEvents == null)
                    previousPeriodEvents = mWeekViewLoader.onLoad(periodToFetch - 1);
                if (nextPeriodEvents == null)
                    nextPeriodEvents = mWeekViewLoader.onLoad(periodToFetch + 1);


                // Clear events.
                mEventRectS.clear();
                sortAndCacheEvents(previousPeriodEvents);
                sortAndCacheEvents(currentPeriodEvents);
                sortAndCacheEvents(nextPeriodEvents);
                calculateHeaderHeight();

                mPreviousPeriodEvents = previousPeriodEvents;
                mCurrentPeriodEvents = currentPeriodEvents;
                mNextPeriodEvents = nextPeriodEvents;
                mFetchedPeriod = periodToFetch;
            }
        }


        if (mWeekViewLoader == null && !isInEditMode())
            throw new IllegalStateException("You must provide a MonthChangeListener");

        // If a refresh was requested then reset some variables.


        if (mWeekViewLoader != null) {
            int periodToFetch = (int) mWeekViewLoader.toWeekViewPeriodIndex(day);

            if (!isInEditMode() || mRefreshEvents) {
                calculateHeaderHeight();

            }

        }


        List<Object> objectsList = new ArrayList<>();
        List<EventRect> tempEvents = mEventRectS;
        mEventRectS = new ArrayList<>();
        objectsList.addAll(tempEvents);

        List<Object> TempObjectList = objectsList;
        objectsList = new ArrayList<>();

        while (TempObjectList.size() > 0) {

            ArrayList<Object> objectRects = new ArrayList<>(TempObjectList.size());

            Object objectRect1 = TempObjectList.remove(0);
            objectRects.add(objectRect1);

            int i = 0;
            while (i < TempObjectList.size()) {
                // Collect all other events for same day.
                Object objectRect2 = TempObjectList.get(i);

                Calendar calendarStart = Calendar.getInstance();
                if (objectRect1 instanceof EventRect) {
                    calendarStart.setTimeInMillis(((EventRect) objectRect1).event.getStartTime());
                } else {
                }


                Calendar calendarEnd = Calendar.getInstance();
                if (objectRect2 instanceof EventRect) {
                    calendarEnd.setTimeInMillis(((EventRect) objectRect2).event.getEndTime());
                } else {
                    calendarEnd.add(Calendar.HOUR_OF_DAY, 1);
                }


                if (CalendarHelper.INSTANCE.isSameDay(calendarStart, calendarEnd)) {
                    TempObjectList.remove(i);
                    objectRects.add(objectRect2);
                } else {
                    i++;
                }
            }
            computePositionOfTaskAndEvent(objectRects);
        }

    }

    private void computePositionOfTaskAndEvent(List<Object> objectlist) {
        // Make "collision groups" for all events that collide with others.
        List<List<Object>> collisionGroups = new ArrayList<>();
        for (Object objectRect : objectlist) {
            boolean isPlaced = false;

            outerLoop:
            for (List<Object> collisionGroup : collisionGroups) {
                for (Object groupTask : collisionGroup) {


                    Calendar calendarStart1 = Calendar.getInstance();
                    Calendar calendarEnd1 = Calendar.getInstance();
                    boolean isAllDay1 = false;
                    if (groupTask instanceof EventRect) {
                        calendarStart1.setTimeInMillis(((EventRect) groupTask).event.getStartTime());
                        calendarEnd1.setTimeInMillis(((EventRect) groupTask).event.getEndTime());
                        isAllDay1 = ((EventRect) groupTask).event.getAllDay();
                    }


                    Calendar calendarStart2 = Calendar.getInstance();
                    Calendar calendarEnd2 = Calendar.getInstance();
                    boolean isAllDay2 = false;

                    if (objectRect instanceof EventRect) {
                        calendarStart2.setTimeInMillis(((EventRect) objectRect).event.getStartTime());
                        calendarEnd2.setTimeInMillis(((EventRect) objectRect).event.getEndTime());
                        isAllDay2 = ((EventRect) objectRect).event.getAllDay();
                    }


                    if (isEventAndTaskCollide(calendarStart1.getTimeInMillis(), calendarEnd1.getTimeInMillis(), calendarStart2.getTimeInMillis(), calendarEnd2.getTimeInMillis()) && isAllDay1 == isAllDay2) {
                        collisionGroup.add(objectRect);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }

            if (!isPlaced) {
                List<Object> newGroup = new ArrayList<>();
                newGroup.add(objectRect);
                collisionGroups.add(newGroup);
            }
        }

        for (List<Object> collisionGroup : collisionGroups) {
            expandEventAndTaskToMaxWidth(collisionGroup);
        }
    }

    private void expandEventAndTaskToMaxWidth(List<Object> collisionGroup) {
        float margin = 0f; // Adjust this value to increase or decrease the spacing

        // Expand the events to maximum possible width.
        List<List<Object>> columns = new ArrayList<>();
        columns.add(new ArrayList<>());

        for (Object objectRect : collisionGroup) {
            boolean isPlaced = false;
            for (List<Object> column : columns) {
                if (column.isEmpty()) {
                    column.add(objectRect);
                    isPlaced = true;
                    break;
                }

                Object lastObjectRect = column.get(column.size() - 1);
                Calendar calendarStart2 = Calendar.getInstance();
                Calendar calendarEnd2 = Calendar.getInstance();

                if (lastObjectRect instanceof EventRect) {
                    calendarStart2.setTimeInMillis(((EventRect) lastObjectRect).event.getStartTime());
                    calendarEnd2.setTimeInMillis(((EventRect) lastObjectRect).event.getEndTime());
                }

                Calendar calendarStart1 = Calendar.getInstance();
                Calendar calendarEnd1 = Calendar.getInstance();

                if (objectRect instanceof EventRect) {
                    calendarStart1.setTimeInMillis(((EventRect) objectRect).event.getStartTime());
                    calendarEnd1.setTimeInMillis(((EventRect) objectRect).event.getEndTime());
                }
                if (!isEventAndTaskCollide(calendarStart1.getTimeInMillis(), calendarEnd1.getTimeInMillis(), calendarStart2.getTimeInMillis(), calendarEnd2.getTimeInMillis())) {
                    column.add(objectRect);
                    isPlaced = true;
                    break;
                }
            }

            if (!isPlaced) {
                List<Object> newColumn = new ArrayList<>();
                newColumn.add(objectRect);
                columns.add(newColumn);
            }
        }

        int maxRowCount = 0;
        for (List<Object> column : columns) {
            maxRowCount = Math.max(maxRowCount, column.size());
        }

        int columnCount = columns.size();
        float totalMargin = margin * (columnCount - 1);
        float availableWidth = 1f - totalMargin;
        float columnWidth = availableWidth / columnCount;

        for (int i = 0; i < maxRowCount; i++) {
            float leftPosition = 0;
            for (int j = 0; j < columnCount; j++) {
                List<Object> column = columns.get(j);
                if (column.size() > i) {
                    Object objectRect = column.get(i);
                    boolean isAllDay = false;

                    if (objectRect instanceof EventRect) {
                        ((EventRect) objectRect).width = columnWidth;
                        ((EventRect) objectRect).left = leftPosition;
                        isAllDay = ((EventRect) objectRect).event.getAllDay();
                    }

                    Calendar calendarStart = Calendar.getInstance();
                    Calendar calendarEnd = Calendar.getInstance();

                    if (objectRect instanceof EventRect) {
                        calendarStart.setTimeInMillis(((EventRect) objectRect).event.getStartTime());
                        calendarEnd.setTimeInMillis(((EventRect) objectRect).event.getEndTime());
                    }

                    LocalDate startDate = new LocalDate(calendarStart.getTimeInMillis());
                    LocalDate endDate = new LocalDate(calendarEnd.getTimeInMillis());

                    if (isAllDay == true && startDate.getDayOfMonth() == endDate.getDayOfMonth()) {

                        if (objectRect instanceof EventRect) {
                            ((EventRect) objectRect).top = calendarStart.get(Calendar.HOUR_OF_DAY) * 60 + calendarStart.get(Calendar.MINUTE);
                            ((EventRect) objectRect).bottom = calendarEnd.get(Calendar.HOUR_OF_DAY) * 60 + calendarEnd.get(Calendar.MINUTE);
                        }
                    } else {

                        if (objectRect instanceof EventRect) {
                            ((EventRect) objectRect).top = j * mAllDayEventHeight + j * 5;
                            ((EventRect) objectRect).bottom = mAllDayEventHeight;
                        }
                    }
                    mEventRectS.add((EventRect) objectRect);

                }
                leftPosition += columnWidth + margin;
            }
        }
    }

    private boolean isEventAndTaskCollide(long start1, long end1, long start2, long end2) {
        return !((start1 >= end2) || (end1 <= start2));
    }

    private void sortAndCacheEvents(List<? extends CalendarEvent> events) {
        for (CalendarEvent event : events) {
            cacheEvent(event);
        }
    }

    private void cacheEvent(CalendarEvent event) {

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(event.getStartTime());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(event.getEndTime());

        if (!CalendarHelper.INSTANCE.isSameDay(calendar1, calendar2)) {
            CalendarEvent event1 = new CalendarEvent(event.getEventId(), event.getTitle(), event.getDescription(), event.getStartTime(), event.getEndTime(), event.getAllDay(), event.getCalendarId(), event.getLocation(), event.getEventColor());
            CalendarEvent event2 = new CalendarEvent(event.getEventId(), event.getTitle(), event.getDescription(), event.getStartTime(), event.getEndTime(), event.getAllDay(), event.getCalendarId(), event.getLocation(), event.getEventColor()); // Null for minutes initially

            mEventRectS.add(new EventRect(event1, null));
            mEventRectS.add(new EventRect(event2, null));
        } else mEventRectS.add(new EventRect(event, null));
    }

    private void drawEventTitle(CalendarEvent event, RectF rect, Canvas canvas) {
        mEventTextPaint.setColor(event.getEventColor());
        float margin = 5f;
        float textSize = mEventTextPaint.getTextSize();
        float availableWidth = rect.width() - 1 * margin;
        String ellipsizedText = TextUtils.ellipsize(event.getTitle(), mEventTextPaint, availableWidth, TextUtils.TruncateAt.END).toString();
        canvas.drawText(ellipsizedText, rect.left + margin, rect.centerY() + textSize / 2 - 3, mEventTextPaint);
    }

    private void drawEvents(Calendar date, float startFromPixel, Canvas canvas) {
        if (mEventRectS != null && !mEventRectS.isEmpty()) {
            for (EventRect eventRect : mEventRectS) {
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTimeInMillis(eventRect.event.getStartTime());

                Calendar calendarEnd = Calendar.getInstance();

                calendarEnd.setTimeInMillis(eventRect.event.getEndTime());
                // Check if the event is on the same day as the provided date
                if (isSameDay(calendarStart, date) && !eventRect.event.getAllDay() && new LocalDate(calendarStart.getTimeInMillis()).equals(new LocalDate(calendarEnd.getTimeInMillis()))) {
                    float eventWidth = mWidthPerDay - (mNumberOfVisibleDays == 1 ? mTimeColumnWidth : 0);

                    // Calculate the left and right boundaries of the event rectangle
                    float left = startFromPixel + eventRect.left * eventWidth;
                    float right = left + eventWidth * eventRect.width;

                    float top;
                    float bottom;
                    Log.d(TAG, "drawEvents: getAllDay = "+eventRect.event.getAllDay());
                    if (!eventRect.event.getAllDay()) {

                        // Calculate top
                        top = mHourHeight * 24 * eventRect.top / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 3 + mEventMarginVertical + mHeaderMarginBottom;

                        // Calculate bottom
                        bottom = mHourHeight * 24 * eventRect.bottom / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 3 - mEventMarginVertical + mHeaderMarginBottom;

                        // Ensure the event rect is within visible bounds
                        float visibleTop = mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 3;
                        float visibleBottom = getHeight();

                        if (top < visibleTop) {
                            top = visibleTop;
                        }
                        if (bottom > visibleBottom) {
                            bottom = visibleBottom;
                        }

                        if (left < startFromPixel) {
                            left = startFromPixel;
                        }
                        if (right > startFromPixel + eventWidth) {
                            right = startFromPixel + eventWidth;
                        }

                        if (left < right && top < bottom) {
                            Log.d(TAG, "drawEvents: if ");
                            eventRect.rectF = new RectF(left + 3, top + 3, right - 3, bottom - 3);
                            int newColor = ColorUtils.setAlphaComponent(eventRect.event.getEventColor(), (int) (255 * 0.4f));
                            mEventBackgroundPaint.setColor(newColor);
                            canvas.drawRoundRect(eventRect.rectF, mEventCornerRadius, mEventCornerRadius, mEventBackgroundPaint);

                            if (isRippleActiveEvent && rippleRect.equals(eventRect.rectF)) {
                                canvas.drawRoundRect(rippleRect, mEventCornerRadius, mEventCornerRadius, ripplePaint);
                            }
                            drawEventTitle(eventRect.event, eventRect.rectF, canvas);
                        } else {
                            Log.d(TAG, "drawEvents: else ");
                            eventRect.rectF = null;
                        }
                    }
                }
            }
        }
    }

    private void drawAllDayEvents(Calendar date, float startFromPixel, Canvas canvas) {
        int eventsInRow = 0;


        if (mEventRectS != null && !mEventRectS.isEmpty()) {


            Set<Integer> drawnEventIds = new HashSet<>(); // To track drawn events


            for (int i = 0; i < mEventRectS.size(); i++) {

                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTimeInMillis(mEventRectS.get(i).event.getStartTime());
                calendarStart.set(Calendar.HOUR_OF_DAY, 0);
                calendarStart.set(Calendar.MINUTE, 0);
                calendarStart.set(Calendar.SECOND, 0);
                calendarStart.set(Calendar.MILLISECOND, 0);

                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTimeInMillis(mEventRectS.get(i).event.getEndTime());
                calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
                calendarEnd.set(Calendar.MINUTE, 0);
                calendarEnd.set(Calendar.SECOND, 0);
                calendarEnd.set(Calendar.MILLISECOND, 0);

                if ((CalendarHelper.INSTANCE.isSameDay(calendarStart, date) && mEventRectS.get(i).event.getAllDay()) || (CalendarHelper.INSTANCE.isSameDay(calendarStart, date) && !new LocalDate(calendarStart.getTimeInMillis()).equals(new LocalDate(calendarEnd.getTimeInMillis())))) {

                    if (mNumberOfVisibleDays != 1) {

                        modelDate modelDate = new modelDate(new LocalDate(calendarStart.getTimeInMillis()).getDayOfMonth(), new LocalDate(calendarStart.getTimeInMillis()).getMonthOfYear());
                        List<Map<modelDate, Integer>> mapList = getMapsContainingKey(modelDate);

                        for (int j = 0; j < mapList.size(); j++) {

                            Map<modelDate, Integer> map = mapList.get(j);
                            Integer count = map.entrySet().iterator().next().getValue();

                            if (count == eventsInRow) {
                                eventsInRow++;

                            }
                        }
                    }


                    int eventId = mEventRectS.get(i).event.getEventId(); // Assuming event has a unique ID
                    // Skip drawing if event has already been drawn
                    if (drawnEventIds.contains(eventId)) {
                        continue;
                    }
                    drawnEventIds.add(eventId);


                    // Calculate top and bottom
                    float top, bottom;
                    if (mNumberOfVisibleDays == 1) {
                        top = mHeaderRowPadding / 3.0f + mEventMarginVertical + mEventRectS.get(i).top + eventsInRow * mEventRectS.get(i).bottom + 5;
                    } else {
                        top = (3 * mHeaderRowPadding) + mHeaderDayNameTextHeight + mHeaderDateTextHeight + mEventMarginVertical + mEventRectS.get(i).top + eventsInRow * mEventRectS.get(i).bottom + 5;
                    }

                    bottom = top + mEventRectS.get(i).bottom - 4;

                    // Calculate left and right
                    float left = startFromPixel;
                    if (left < startFromPixel) left += mOverlappingEventGap;
                    float right = left + (mWidthPerDay - (mNumberOfVisibleDays == 1 ? mTimeColumnWidth : 0));
                    if (right < startFromPixel + (mWidthPerDay - (mNumberOfVisibleDays == 1 ? mTimeColumnWidth : 0))) {
                        right -= mOverlappingEventGap;
                    }


                    if (mNumberOfVisibleDays != 1) {
                        if (mEventRectS.get(i).event.getAllDay() && !new LocalDate(calendarStart.getTimeInMillis()).equals(new LocalDate(calendarEnd.getTimeInMillis()))) {

                            long daysSpan = TimeUnit.MILLISECONDS.toDays(calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis()) + 1;
                            right = left + (daysSpan * mWidthPerDay - (mNumberOfVisibleDays == 1 ? mTimeColumnWidth : 0));
                            List<Long> dates = getTimeMillisBetween(calendarStart.getTimeInMillis(), calendarEnd.getTimeInMillis());

                            for (int k = 0; k < dates.size(); k++) {
                                Map<modelDate, Integer> map = new HashMap<>();
                                modelDate modeldate = new modelDate(new LocalDate(dates.get(k)).getDayOfMonth(), new LocalDate(dates.get(k)).getMonthOfYear());
                                map.put(modeldate, (eventsInRow));

                                if (!datelist.contains(map)) {
                                    datelist.add(map);
                                }
                            }

                        }
                    }
                    // Draw the event if within bounds
                    if (left < right && left < getWidth() && top < getHeight() && right > mTimeColumnWidth && bottom > 0) {
                        mEventRectS.get(i).rectF = new RectF(left + 5, top, right - 3, bottom);
                        int newColor = ColorUtils.setAlphaComponent(mEventRectS.get(i).event.getEventColor(), (int) (255 * 0.4f));
                        mEventBackgroundPaint.setColor(newColor);
                        jheaderEventTextpaint.setColor(mEventRectS.get(i).event.getEventColor());
                        canvas.drawRoundRect(mEventRectS.get(i).rectF, mEventCornerRadius, mEventCornerRadius, mEventBackgroundPaint);
                        if (isRippleActiveEvent && rippleRect.equals(mEventRectS.get(i).rectF)) {
                            canvas.drawRoundRect(rippleRect, mEventCornerRadius, mEventCornerRadius, ripplePaint);
                        }
                        RectF rect = mEventRectS.get(i).rectF;
                        String text = mEventRectS.get(i).event.getTitle();
                        float textSize = jheaderEventTextpaint.getTextSize();
                        float margin = 5f;
                        float availableWidth = rect.width() - 1 * margin;
                        String ellipsizedText = TextUtils.ellipsize(text, jheaderEventTextpaint, availableWidth, TextUtils.TruncateAt.END).toString();
                        canvas.drawText(ellipsizedText, rect.left + margin, rect.centerY() + textSize / 2 - 3, jheaderEventTextpaint);
                    } else {
                        mEventRectS.get(i).rectF = null;
                    }

                    eventsInRow++;
                }
            }
        }


    }

    public List<Long> getTimeMillisBetween(long startDateMillis, long endDateMillis) {
        List<Long> timeMillisList = new ArrayList<>();

        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(startDateMillis);
        startCal.add(Calendar.DATE, 1); // Start from the day after the start date

        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(endDateMillis);

        while (!startCal.after(endCal)) {
            long timeMillis = startCal.getTimeInMillis();
            timeMillisList.add(timeMillis);
            startCal.add(Calendar.DATE, 1);
        }

        return timeMillisList;
    }

    public List<Map<modelDate, Integer>> getMapsContainingKey(modelDate Date) {
        List<Map<modelDate, Integer>> result = new ArrayList<>();

        // Use for loop for versions below Android N
        for (Map<modelDate, Integer> map : datelist) {

            modelDate mapDate = map.entrySet().iterator().next().getKey();

            if (mapDate.getDate() == Date.getDate() && mapDate.getMonth() == Date.getMonth()) {
                result.add(map);
            }
        }

        return result;
    }

    private static class EventRect {
        public CalendarEvent event;
        public RectF rectF;
        public float left;

        public float width;
        public float top;
        public float bottom;

        public EventRect(CalendarEvent event, RectF rectF) {
            this.event = event;
            this.rectF = rectF;
        }
    }


    public void notifyDatasetChanged() {
        mRefreshEvents = true;
        invalidate();
    }

    public static boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
        return new LocalDate(dayOne.getTimeInMillis()).isEqual(new LocalDate(dayTwo.getTimeInMillis()));
    }

    public static Calendar today() {
        return Calendar.getInstance();
    }
}