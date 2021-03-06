package com.mobileframe.widegt.datapicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.mobileframe.R;
import com.mobileframe.widegt.datapicker.core.AbstractWheelPicker;
import com.mobileframe.widegt.datapicker.core.OnWheelPickedListener;
import com.mobileframe.widegt.datapicker.view.TextWheelPicker;
import com.mobileframe.widegt.datapicker.view.TextWheelPickerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateWheelPicker extends LinearLayout implements OnWheelPickedListener {
    public final static int TYPE_YEAR = 1 << 1;
    public final static int TYPE_MONTH = 1 << 2;
    public final static int TYPE_DAY = 1 << 3;
    public final static int TYPE_HOUR = 1 << 4;
    public final static int TYPE_MINUTE = 1 << 5;
    public final static int TYPE_SECOND = 1 << 6;
    //所有
    public final static int TYPE_ALL = TYPE_YEAR | TYPE_MONTH | TYPE_DAY |
            TYPE_HOUR | TYPE_MINUTE | TYPE_SECOND;
    //年月日
    public final static int TYPE_YY_MM_DD = TYPE_YEAR | TYPE_MONTH | TYPE_DAY;
    //时分秒
    public final static int TYPE_HH_MM_SS = TYPE_HOUR | TYPE_SECOND | TYPE_MINUTE;

    private String mYearStr;
    private String mMonthStr;
    private String mDayStr;
    private String mHourStr;
    private String mMinuteStr;
    private String mSecondStr;

    /**
     * 未来模式，时间下限为当前时间
     */
    public final static int MODE_PENDING = 0;
    /**
     * 生日模式，时间上限为当前时间
     */
    public final static int MODE_BIRTHDAY = 1;
    /**
     * 正常模式
     */
    public final static int MODE_RANGE = 2;

    public final static int BIRTHDAY_RANGE = 100;

    private TextWheelPicker mYearWheelPicker;
    private TextWheelPicker mMonthWheelPicker;
    private TextWheelPicker mDayWheelPicker;
    private TextWheelPicker mHourWheelPicker;
    private TextWheelPicker mMinuteWheelPicker;
    private TextWheelPicker mSecondWheelPicker;

    private int mCurrYear;
    private int mCurrMonth;
    private int mCurrDay;
    private int mCurrHour;
    private int mCurrMinute;
    private int mCurrSecond;
    private int mMode = MODE_BIRTHDAY;

    private int mSelectedYear;
    private int mSelectedMonth;
    private int mSelectedDay;
    private int mSelectedHour;
    private int mSelectedMinute;
    private int mSelectedSecond;

    private List<String> mYears;
    private List<String> mMonths;
    private List<String> mDays;
    private List<String> mHours;
    private List<String> mMinutes;
    private List<String> mSeconds;

    private TextWheelPickerAdapter mYearPickerAdapter;
    private TextWheelPickerAdapter mMonthPickerAdapter;
    private TextWheelPickerAdapter mDayPickerAdapter;

    private TextWheelPickerAdapter mHourPickerAdapter;
    private TextWheelPickerAdapter mMinutePickerAdapter;
    private TextWheelPickerAdapter mSecondPickerAdapter;

    private OnDatePickListener mOnDatePickListener;
    //开始月份
    private List<String> mMonthsStart;
    //开始日期
    private List<String> mDaysStart;
    //开始年份
    private int YEAR_STAR;
    //开始月份
    private int MONTH_START;

    public DateWheelPicker(Context context) {
        super(context);
        init();
    }

    public DateWheelPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateWheelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DateWheelPicker(Context context, int year_start, int month_start, int day_start) {
        super(context);
        YEAR_STAR = year_start;
        MONTH_START = month_start;
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        if (month_start > 0) {
            mMonthsStart = new ArrayList<>();
            for (int i = month_start; i < currentMonth + 2; i++) {
                mMonthsStart.add(i + context.getString(R.string._month));
            }
        }
        if (day_start > 0) {
            mDaysStart = new ArrayList<>();
            int max;
            if (month_start == currentMonth + 1) {
                max = calendar.get(Calendar.DAY_OF_MONTH) + 1;
            } else {
                max = getCorrectMaxDays(year_start, month_start) + 1;
            }

            for (int i = day_start; i < max; i++) {
                mDaysStart.add(i + context.getString(R.string._day));
            }
        }
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);

        mYearStr = getResources().getString(R.string._year);
        mMonthStr = getResources().getString(R.string._month);
        mDayStr = getResources().getString(R.string._day);
        mHourStr = getResources().getString(R.string._hour);
        mMinuteStr = getResources().getString(R.string._minute);
        mSecondStr = getResources().getString(R.string._second);

        LayoutParams llParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        llParams.weight = 1;

        mYearWheelPicker = new TextWheelPicker(getContext(), TYPE_YEAR);
        mMonthWheelPicker = new TextWheelPicker(getContext(), TYPE_MONTH);
        mDayWheelPicker = new TextWheelPicker(getContext(), TYPE_DAY);
        mHourWheelPicker = new TextWheelPicker(getContext(), TYPE_HOUR);
        mMinuteWheelPicker = new TextWheelPicker(getContext(), TYPE_MINUTE);
        mSecondWheelPicker = new TextWheelPicker(getContext(), TYPE_SECOND);

        mYearWheelPicker.setOnWheelPickedListener(this);
        mMonthWheelPicker.setOnWheelPickedListener(this);
        mDayWheelPicker.setOnWheelPickedListener(this);
        mHourWheelPicker.setOnWheelPickedListener(this);
        mMinuteWheelPicker.setOnWheelPickedListener(this);
        mSecondWheelPicker.setOnWheelPickedListener(this);

        addView(mYearWheelPicker, llParams);
        addView(mMonthWheelPicker, llParams);
        addView(mDayWheelPicker, llParams);
        addView(mHourWheelPicker, llParams);
        addView(mMinuteWheelPicker, llParams);
        addView(mSecondWheelPicker, llParams);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        mCurrHour = calendar.get(Calendar.HOUR_OF_DAY);
        mCurrMinute = calendar.get(Calendar.MINUTE);
        mCurrSecond = calendar.get(Calendar.SECOND);

        initData();
    }

    private void initData() {
        mYearPickerAdapter = new TextWheelPickerAdapter();
        mMonthPickerAdapter = new TextWheelPickerAdapter();
        mDayPickerAdapter = new TextWheelPickerAdapter();
        mHourPickerAdapter = new TextWheelPickerAdapter();
        mMinutePickerAdapter = new TextWheelPickerAdapter();
        mSecondPickerAdapter = new TextWheelPickerAdapter();

        mYears = new ArrayList<String>();
        mMonths = new ArrayList<String>();
        mDays = new ArrayList<String>();
        mHours = new ArrayList<String>();
        mMinutes = new ArrayList<String>();
        mSeconds = new ArrayList<String>();

        updateYears(mCurrYear - BIRTHDAY_RANGE + 1, mCurrYear);
        updateMaxMonths(11);
        updateMaxDays(31);
        updateMaxHour(24);
        updateMaxMinute(60);
        updateMaxSecond(60);

        mYearPickerAdapter.setData(mYears);
        mMonthPickerAdapter.setData(mMonths);
        mDayPickerAdapter.setData(mDays);
        mHourPickerAdapter.setData(mHours);
        mMinutePickerAdapter.setData(mMinutes);
        mSecondPickerAdapter.setData(mSeconds);

        mYearWheelPicker.setAdapter(mYearPickerAdapter);
        mMonthWheelPicker.setAdapter(mMonthPickerAdapter);
        mDayWheelPicker.setAdapter(mDayPickerAdapter);
        mHourWheelPicker.setAdapter(mHourPickerAdapter);
        mMinuteWheelPicker.setAdapter(mMinutePickerAdapter);
        mSecondWheelPicker.setAdapter(mSecondPickerAdapter);

    }

    public void setWheelPickerVisibility(int wheelType, int visibility) {
        int antiVisibility = VISIBLE;
        if (visibility == VISIBLE) {
            antiVisibility = GONE;
        } else if (visibility == GONE) {
            antiVisibility = VISIBLE;
        }

        if ((wheelType & TYPE_YEAR) != 0) {
            mYearWheelPicker.setVisibility(visibility);
        } else {
            mYearWheelPicker.setVisibility(antiVisibility);
        }

        if ((wheelType & TYPE_MONTH) != 0) {
            mMonthWheelPicker.setVisibility(visibility);
        } else {
            mMonthWheelPicker.setVisibility(antiVisibility);
        }

        if ((wheelType & TYPE_DAY) != 0) {
            mDayWheelPicker.setVisibility(visibility);
        } else {
            mDayWheelPicker.setVisibility(antiVisibility);
        }

        if ((wheelType & TYPE_HOUR) != 0) {
            mHourWheelPicker.setVisibility(visibility);
        } else {
            mHourWheelPicker.setVisibility(antiVisibility);
        }

        if ((wheelType & TYPE_MINUTE) != 0) {
            mMinuteWheelPicker.setVisibility(visibility);
        } else {
            mMinuteWheelPicker.setVisibility(antiVisibility);
        }

        if ((wheelType & TYPE_SECOND) != 0) {
            mSecondWheelPicker.setVisibility(visibility);
        } else {
            mSecondWheelPicker.setVisibility(antiVisibility);
        }
    }

    public void setOnDatePickListener(OnDatePickListener listener) {
        mOnDatePickListener = listener;
    }

    public void setDateRange(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("the from year less than to year!");
        }

        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("the passed year must be > 0");
        }

        mMode = to == mCurrYear ? MODE_BIRTHDAY : (from == mCurrYear ? MODE_PENDING : MODE_RANGE);

        updateYears(from, to);
        mYearPickerAdapter.setData(mYears);
    }

    public void setCurrentDate(int year, int month, int day) {
        if (mYears.isEmpty() || mMonths.isEmpty() || mDays.isEmpty()) {
            return;
        }

        mSelectedYear = year;
        mSelectedMonth = month;
        mSelectedDay = day;

        //更新月份和天数
        if (mCurrYear == mSelectedYear) {
            if (mMode == MODE_BIRTHDAY) {
                updateMaxMonths(mCurrMonth);
                correctMaxDays();
            } else if (mMode == MODE_PENDING) {
                updateMinMonths(mCurrMonth);
                correctMinDays(mCurrDay);
            } else {
                updateMaxMonths(11);
                correctMaxDays();
            }
        } else {
            updateMaxMonths(11);
        }

        int yearIndex = Math.max(0, mYears.indexOf(year + mYearStr));
        int monthIndex = Math.max(0, mMonths.indexOf((month + 1) + mMonthStr));
        int dayIndex;
        if (MONTH_START > 0 && year == YEAR_STAR && (month + 1) == MONTH_START) {
            dayIndex = Math.max(0, mDaysStart.indexOf(day + mDayStr));
        } else {

            dayIndex = Math.max(0, mDays.indexOf(day + mDayStr));
        }
        setDateItemIndexWithoutReLayout(yearIndex, monthIndex, dayIndex);
    }

    public void setCurrentTime(int hour, int minute, int second) {
        if (mHours.isEmpty() || mMinutes.isEmpty() || mSeconds.isEmpty()) {
            return;
        }

        mSelectedHour = hour;
        mSelectedMinute = minute;
        mSelectedSecond = second;

        if (mMode == MODE_PENDING) {
            updateMinHour(mCurrHour);
            updateMinMinute(mCurrMinute);
            //TODO
            //updateMinSecond(mCurrSecond);
            updateMaxSecond(mCurrSecond);
        } else {
            updateMaxHour(mCurrHour);
            updateMaxMinute(mCurrMinute);
            updateMaxSecond(mCurrSecond);
        }
        int hourIndex = Math.max(0, mHours.indexOf(hour + mHourStr));
        int minuteIndex = Math.max(0, mMinutes.indexOf(minute + mMinuteStr));
        int secondIndex = Math.max(0, mSeconds.indexOf(second + mSecondStr));
        setTimeItemIndexWithoutReLayout(hourIndex, minuteIndex, secondIndex);
    }

    public void notifyDataSetChanged() {
        //年月日市时分秒是联动的，所以只需要通知年的数据变化
        mYearPickerAdapter.notifyDataSetChanged();
    }

    private void setDateItemIndex(int yearIndex, int monthIndex, int dayIndex) {
        mYearWheelPicker.setCurrentItem(yearIndex);
        mMonthWheelPicker.setCurrentItem(monthIndex);
        mDayWheelPicker.setCurrentItem(dayIndex);
    }

    private void setTimeItemIndex(int hourIndex, int minuteIndex, int secondIndex) {
        mHourWheelPicker.setCurrentItem(hourIndex);
        mMinuteWheelPicker.setCurrentItem(minuteIndex);
        mSecondWheelPicker.setCurrentItem(secondIndex);
    }

    private void setDateItemIndexWithoutReLayout(int yearIndex, int monthIndex, int dayIndex) {
        mYearWheelPicker.setCurrentItem(yearIndex);
        mMonthWheelPicker.setCurrentItem(monthIndex);
        mDayWheelPicker.setCurrentItem(dayIndex);
    }

    private void setTimeItemIndexWithoutReLayout(int hourIndex, int minuteIndex, int secondIndex) {
        mHourWheelPicker.setCurrentItemWithoutReLayout(hourIndex);
        mMinuteWheelPicker.setCurrentItemWithoutReLayout(minuteIndex);
        mSecondWheelPicker.setCurrentItemWithoutReLayout(secondIndex);
    }

    public void setTextSize(int textSize) {
        if (textSize < 0) {
            return;
        }

        mYearWheelPicker.setTextSize(textSize);
        mMonthWheelPicker.setTextSize(textSize);
        mDayWheelPicker.setTextSize(textSize);
        mHourWheelPicker.setTextSize(textSize);
        mMinuteWheelPicker.setTextSize(textSize);
        mSecondWheelPicker.setTextSize(textSize);
    }

    public void setTextColor(int textColor) {
        mYearWheelPicker.setTextColor(textColor);
        mMonthWheelPicker.setTextColor(textColor);
        mDayWheelPicker.setTextColor(textColor);
        mHourWheelPicker.setTextColor(textColor);
        mMinuteWheelPicker.setTextColor(textColor);
        mSecondWheelPicker.setTextColor(textColor);
    }

    public void setLineColor(int lineColor) {
        mYearWheelPicker.setLineColor(lineColor);
        mMonthWheelPicker.setLineColor(lineColor);
        mDayWheelPicker.setLineColor(lineColor);
        mHourWheelPicker.setLineColor(lineColor);
        mMinuteWheelPicker.setLineColor(lineColor);
        mSecondWheelPicker.setLineColor(lineColor);
    }

    public void setLineWidth(int width) {
        mYearWheelPicker.setLineStorkeWidth(width);
        mMonthWheelPicker.setLineStorkeWidth(width);
        mDayWheelPicker.setLineStorkeWidth(width);
        mHourWheelPicker.setLineColor(width);
        mMinuteWheelPicker.setLineColor(width);
        mSecondWheelPicker.setLineColor(width);
    }

    public void setItemSpace(int space) {
        mYearWheelPicker.setItemSpace(space);
        mMonthWheelPicker.setItemSpace(space);
        mDayWheelPicker.setItemSpace(space);
        mHourWheelPicker.setItemSpace(space);
        mMinuteWheelPicker.setItemSpace(space);
        mSecondWheelPicker.setItemSpace(space);
    }

    public void setVisibleItemCount(int itemCount) {
        mYearWheelPicker.setVisibleItemCount(itemCount);
        mMonthWheelPicker.setVisibleItemCount(itemCount);
        mDayWheelPicker.setVisibleItemCount(itemCount);
        mHourWheelPicker.setVisibleItemCount(itemCount);
        mMinuteWheelPicker.setVisibleItemCount(itemCount);
        mSecondWheelPicker.setVisibleItemCount(itemCount);
    }

    public void setItemSize(int itemWidth, int itemHeight) {
        mYearWheelPicker.setItemSize(itemWidth, itemHeight);
        mMonthWheelPicker.setItemSize(itemWidth, itemHeight);
        mDayWheelPicker.setItemSize(itemWidth, itemHeight);
        mHourWheelPicker.setItemSize(itemWidth, itemHeight);
        mMinuteWheelPicker.setItemSize(itemWidth, itemHeight);
        mSecondWheelPicker.setItemSize(itemWidth, itemHeight);
    }

    public int getDateMode() {
        return mMode;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onWheelSelected(AbstractWheelPicker wheelPicker, int index, Object data) {
        switch (wheelPicker.getId()) {
            case TYPE_YEAR:
                int year = getCurrentDate(data, mYearStr);
                if (year > 0) {
                    mSelectedYear = year;
                }

                boolean changed = false;
                if (mMode == MODE_PENDING) {
                    if (mSelectedYear == mCurrYear) {
                        //current year
                        updateMinMonths(mCurrMonth);
                    } else {
                        changed = mMonths.size() != 12;
                        if (changed) {
                            updateMaxMonths(11);
                        }
                    }
                    //update month index
                    int monthIndex = Math.max(0, mMonths.indexOf((mSelectedMonth + 1) + mMonthStr));
                    mMonthWheelPicker.setCurrentItemWithoutReLayout(monthIndex);
                } else {
                    if (mSelectedYear == mCurrYear && mMode == MODE_BIRTHDAY) {
                        //current year
                        updateMaxMonths(mCurrMonth);
                    } else {
                        changed = mMonths.size() != 12;
                        if (changed) {
                            updateMaxMonths(11);
                        }
                    }
                }
                //update month
                if (mSelectedYear == YEAR_STAR) {
                    mMonthPickerAdapter.setData(mMonthsStart);
                } else {
                    mMonthPickerAdapter.setData(mMonths);
                }
                break;
            case TYPE_MONTH:
                int month = getCurrentMonth(data, mMonthStr);
                if (month >= 0) {
                    mSelectedMonth = month;
                }
                if (mMode == MODE_PENDING) {
                    if (mSelectedYear == mCurrYear && mSelectedMonth == mCurrMonth) {
                        //current month
                        correctMinDays(mCurrDay);
                    } else {
                        correctMaxDays();
                    }
                    //update day index
                    int dayIndex = Math.max(0, mDays.indexOf(mSelectedDay + mDayStr));
                    mDayWheelPicker.setCurrentItemWithoutReLayout(dayIndex);
                } else {
                    if (mSelectedYear == mCurrYear && mSelectedMonth == mCurrMonth && mMode == MODE_BIRTHDAY) {
                        //current month
                        updateMaxDays(mCurrDay);
                    } else {
                        correctMaxDays();
                    }
                }
                if (mSelectedYear == YEAR_STAR && (mSelectedMonth + 1) == MONTH_START) {
                    mDayPickerAdapter.setData(mDaysStart);
                } else {
                    mDayPickerAdapter.setData(mDays);
                }
                break;
            case TYPE_DAY:
                mSelectedDay = getCurrentDate(data, mDayStr);
                if (mMode == MODE_PENDING) {
                    if (mSelectedYear == mCurrYear && mSelectedMonth == mCurrMonth && mSelectedDay == mCurrDay) {
                        //current day
                        updateMinHour(mCurrHour);
                    } else {
                        updateMaxHour(24);
                    }

                    int hourIndex = Math.max(0, mHours.indexOf(mSelectedHour + mHourStr));
                    mHourWheelPicker.setCurrentItemWithoutReLayout(hourIndex);
                } else {
                    if (mSelectedYear == mCurrYear && mSelectedMonth == mCurrMonth && mSelectedDay == mCurrDay
                            && mMode == MODE_BIRTHDAY) {
                        //current month
                        updateMaxHour(mCurrHour);
                    } else {
                        updateMaxHour(24);
                    }
                }
                mHourPickerAdapter.setData(mHours);
                break;
            case TYPE_HOUR:
                mSelectedHour = getCurrentDate(data, mHourStr);
                if (mMode == MODE_PENDING) {
                    if (mSelectedYear == mCurrYear && mSelectedMonth == mCurrMonth && mSelectedDay == mCurrDay
                            && mSelectedHour == mCurrHour) {
                        //current hour
                        updateMinMinute(mCurrMinute);
                    } else {
                        updateMaxMinute(60);
                    }

                    int minuteIndex = Math.max(0, mMinutes.indexOf(mSelectedMinute + mMinuteStr));
                    mMinuteWheelPicker.setCurrentItemWithoutReLayout(minuteIndex);
                } else {
                    if (mSelectedYear == mCurrYear && mSelectedMonth == mCurrMonth && mSelectedDay == mCurrDay
                            && mSelectedHour == mCurrHour && mMode == MODE_BIRTHDAY) {
                        //current month
                        updateMaxMinute(mCurrMinute);
                    } else {
                        updateMaxMinute(60);
                    }
                }
                mMinutePickerAdapter.setData(mMinutes);
                break;
            case TYPE_MINUTE:
                mSelectedMinute = getCurrentDate(data, mMinuteStr);
                if (mMode == MODE_PENDING) {
                    if (mSelectedYear == mCurrYear && mSelectedMonth == mCurrMonth && mSelectedDay == mCurrDay
                            && mSelectedHour == mCurrHour && mSelectedMinute == mCurrMinute) {
                        //current minute
                        updateMinSecond(mCurrSecond);
                    } else {
                        updateMaxSecond(60);
                    }

                    int secondIndex = Math.max(0, mSeconds.indexOf(mSelectedSecond + mSecondStr));
                    mSecondWheelPicker.setCurrentItemWithoutReLayout(secondIndex);
                } else {
                    if (mSelectedYear == mCurrYear && mSelectedMonth == mCurrMonth && mSelectedDay == mCurrDay
                            && mSelectedHour == mCurrHour && mSelectedMinute == mCurrMinute && mMode == MODE_BIRTHDAY) {
                        //current month
                        updateMaxSecond(mCurrSecond);
                    } else {
                        updateMaxSecond(60);
                    }
                }
                mSecondPickerAdapter.setData(mSeconds);
                break;
            case TYPE_SECOND:
                mSelectedSecond = getCurrentDate(data, mSecondStr);
                break;
            default:
                break;
        }

        if (mOnDatePickListener != null) {
            mOnDatePickListener.onDatePicked(mSelectedYear, mSelectedMonth, mSelectedDay,
                    mSelectedHour, mSelectedMinute, mSelectedSecond);
        }
    }


    private void correctMaxDays() {
        int month = mSelectedMonth + 1;
        switch (month) {
            case 2:
                if (isLeapYear(mSelectedYear)) {
                    updateMaxDays(29);
                } else {
                    updateMaxDays(28);
                }
                break;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                updateMaxDays(31);
                break;
            default:
                updateMaxDays(30);
                break;
        }
    }

    private void correctMinDays(int minDay) {
        int month = mSelectedMonth + 1;
        switch (month) {
            case 2:
                if (isLeapYear(mSelectedYear)) {
                    updateMinDays(minDay, 29);
                } else {
                    updateMinDays(minDay, 28);
                }
                break;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                updateMinDays(minDay, 31);
                break;
            default:
                updateMinDays(minDay, 30);
                break;
        }
    }


    private void updateYears(int from, int to) {
        mYears.clear();

        int size = to - from;
        for (int i = from; i <= from + size; i++) {
            mYears.add(i + mYearStr);
        }
    }

    private void updateMinMonths(int minMonth) {
        mMonths.clear();

        for (int i = minMonth; i <= 11; i++) {
            mMonths.add((i + 1) + mMonthStr);
        }
    }

    private void updateMinDays(int minDay, int maxDay) {
        mDays.clear();

        for (int i = minDay; i <= maxDay; i++) {
            mDays.add(i + mDayStr);
        }
    }

    private void updateMaxMonths(int maxMonth) {
        mMonths.clear();

        for (int i = 0; i <= maxMonth; i++) {
            mMonths.add((i + 1) + mMonthStr);
        }
    }

    private void updateMaxDays(int maxDay) {
        mDays.clear();

        for (int i = 1; i <= maxDay; i++) {
            mDays.add(i + mDayStr);
        }
    }

    private void updateMinHour(int minHour) {
        mHours.clear();
        for (int i = minHour; i < 24; i++) {
            mHours.add(i + mHourStr);
        }
    }

    private void updateMinMinute(int minMinute) {
        mMinutes.clear();
        for (int i = minMinute; i < 60; i++) {
            mMinutes.add(i + mMinuteStr);
        }
    }

    private void updateMinSecond(int minSecond) {
        mSeconds.clear();
        for (int i = minSecond; i < 60; i++) {
            mSeconds.add(i + mSecondStr);
        }
    }

    private void updateMaxHour(int maxHour) {
        mHours.clear();
        maxHour = Math.max(24, maxHour);
        for (int i = 0; i < maxHour; i++) {
            mHours.add(i + mHourStr);
        }
    }

    private void updateMaxMinute(int maxMinute) {
        mMinutes.clear();
        maxMinute = Math.max(60, maxMinute);
        for (int i = 0; i < maxMinute; i++) {
            mMinutes.add(i + mMinuteStr);
        }
    }

    private void updateMaxSecond(int maxSecond) {
        mSeconds.clear();
        maxSecond = Math.max(60, maxSecond);
        for (int i = 0; i < maxSecond; i++) {
            mSeconds.add(i + mSecondStr);
        }
    }

    private int getCurrentDate(Object data, String suffix) {
        if (data instanceof String) {
            int suffixLeg = suffix == null ? 0 : suffix.length();
            String temp = (String) data;
            return Integer.parseInt(temp.substring(0, temp.length() - suffixLeg));
        }

        return -1;
    }

    private int getCurrentMonth(Object data, String suffix) {
        if (data instanceof String) {
            int suffixLeg = suffix == null ? 0 : suffix.length();
            String temp = (String) data;
            return Integer.parseInt(temp.substring(0, temp.length() - suffixLeg)) - 1;
        }

        return -1;
    }

    private boolean isLeapYear(int year) {
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            return true;
        }

        return false;
    }

    public interface OnDatePickListener {
        public void onDatePicked(int year, int month, int day, int hour, int minute, int second);
    }

    public int getSelectedYear() {
        return mSelectedYear;
    }

    public int getSelectedMonth() {
        return mSelectedMonth;
    }

    public int getSelectedDay() {
        return mSelectedDay;
    }

    public int getSelectedHour() {
        return mSelectedHour;
    }

    public int getSelectedMinute() {
        return mSelectedMinute;
    }

    public int getSelectedSecond() {
        return mSelectedSecond;
    }

    private int getCorrectMaxDays(int aimsYear, int aimsMonth) {
        switch (aimsMonth) {
            case 2:
                if (isLeapYear(aimsYear)) {
                    return 29;
                } else {
                    return 28;
                }
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            default:
                return 30;
        }
    }
}
