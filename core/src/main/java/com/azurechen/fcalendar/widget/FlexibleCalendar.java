package com.azurechen.fcalendar.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.azurechen.fcalendar.R;
import com.azurechen.fcalendar.view.LockScrollView;
import com.azurechen.fcalendar.data.CalendarAdapter;
import com.azurechen.fcalendar.data.Day;
import com.azurechen.fcalendar.data.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by azurechen on 7/29/15.
 */
public class FlexibleCalendar extends LinearLayout {

    private static final int DEFAULT_FIRST_DAY_OF_WEEK = 0;

    private Context mContext;
    private LayoutInflater mInflater;

    private TextView mTxtTitle;
    private TableLayout mTableHead;
    private LockScrollView mScrollViewBody;
    private TableLayout mTableBody;
    private RelativeLayout mLayoutBtnGroupMonth;
    private RelativeLayout mLayoutBtnGroupWeek;
    private ImageButton mBtnPrevMonth;
    private ImageButton mBtnNextMonth;
    private ImageButton mBtnPrevWeek;
    private ImageButton mBtnNextWeek;

    private CalendarAdapter mAdapter;

    private CalendarListener mListener;

    private enum State { COLLAPSED, EXPANDED, PROCESSING }
    private int mInitHeight = 0;
    private State mState = State.EXPANDED;

    private Handler mHandler = new Handler();
    private boolean mIsWaitingForUpdate = false;

    private int mCurrentWeekIndex;

    // attributes
    private int mDefaultColor;
    private int mPrimaryColor;
    private Day mSelectedItem = null;

    public FlexibleCalendar(Context context) {
        super(context);
        init(context);
    }

    public FlexibleCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlexibleCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // load rootView from xml
        LayoutInflater vi =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = vi.inflate(R.layout.widget_flexible_calendar, this, true);

        // init UI
        mTxtTitle = (TextView) rootView.findViewById(R.id.txt_title);
        mTableHead = (TableLayout) rootView.findViewById(R.id.table_head);
        mScrollViewBody = (LockScrollView) rootView.findViewById(R.id.scroll_view_body);
        mTableBody = (TableLayout) rootView.findViewById(R.id.table_body);
        mLayoutBtnGroupMonth = (RelativeLayout) rootView.findViewById(R.id.layout_btn_group_month);
        mLayoutBtnGroupWeek = (RelativeLayout) rootView.findViewById(R.id.layout_btn_group_week);
        mBtnPrevMonth = (ImageButton) rootView.findViewById(R.id.btn_prev_month);
        mBtnNextMonth = (ImageButton) rootView.findViewById(R.id.btn_next_month);
        mBtnPrevWeek = (ImageButton) rootView.findViewById(R.id.btn_prev_week);
        mBtnNextWeek = (ImageButton) rootView.findViewById(R.id.btn_next_week);

        // init default attrs
        mPrimaryColor = context.getResources().getColor(R.color.primary_pink);
        mDefaultColor = Color.WHITE;
        mLayoutBtnGroupWeek.setVisibility(GONE);

        // bind events
        mBtnPrevMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prevMonth();
            }
        });

        mBtnNextMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });

        mBtnPrevWeek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prevWeek();
            }
        });

        mBtnNextWeek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWeek();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mInitHeight = mTableBody.getMeasuredHeight();

        if (mIsWaitingForUpdate) {
            highlight();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    collapseTo(mCurrentWeekIndex);
                }
            });
            mIsWaitingForUpdate = false;
            mListener.onDataUpdate();
        }
    }

    private void highlight() {
        // reset other items
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Day day = mAdapter.getItem(i);
            View view = mAdapter.getView(i);
            TextView txtDay = (TextView) view.findViewById(R.id.txt_day);
            txtDay.setBackgroundResource(Color.TRANSPARENT);
            txtDay.setTextColor(mDefaultColor);

            // set today's item
            if (isToady(day)) {
                txtDay.setBackgroundResource(R.drawable.circle_white_stroke_background);
                txtDay.setTextColor(mDefaultColor);
            }

            // set the selected item
            if (isSelectedDay(day)) {
                txtDay.setBackgroundResource(R.drawable.circle_white_solid_background);
                txtDay.setTextColor(mPrimaryColor);
            }
        }
    }

    private int getSuitableRowIndex() {
        if (getSelectedItemPosition() != -1) {
            View view = mAdapter.getView(getSelectedItemPosition());
            TableRow row = (TableRow) view.getParent();

            return mTableBody.indexOfChild(row);
        } else if (getTodayItemPosition() != -1) {
            View view = mAdapter.getView(getTodayItemPosition());
            TableRow row = (TableRow) view.getParent();

            return mTableBody.indexOfChild(row);
        } else {
            return 0;
        }
    }

    private void reload() {
        mAdapter.refresh();

        // reset UI
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        dateFormat.setTimeZone(mAdapter.getCalendar().getTimeZone());
        mTxtTitle.setText(dateFormat.format(mAdapter.getCalendar().getTime()));
        mTableHead.removeAllViews();
        mTableBody.removeAllViews();

        TableRow rowCurrent;

        // set day of week
        int[] dayOfWeekIds = {
                R.string.sunday,
                R.string.monday,
                R.string.tuesday,
                R.string.wednesday,
                R.string.thursday,
                R.string.friday,
                R.string.saturday
        };
        rowCurrent = new TableRow(mContext);
        rowCurrent.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < 7; i++) {
            View view = mInflater.inflate(R.layout.layout_day_of_week, null);
            TextView txtDayOfWeek = (TextView) view.findViewById(R.id.txt_day_of_week);
            txtDayOfWeek.setText(dayOfWeekIds[(i + mAdapter.getFirstDayOfWeek()) % 7]);
            view.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1));
            rowCurrent.addView(view);
        }
        mTableHead.addView(rowCurrent);

        // set day view
        for (int i = 0; i < mAdapter.getCount(); i ++) {
            final int position = i;

            if (position % 7 == 0) {
                rowCurrent = new TableRow(mContext);
                rowCurrent.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                mTableBody.addView(rowCurrent);
            }
            final View view = mAdapter.getView(position);
            view.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1));
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(mAdapter.getItem(position));

                    if (mListener != null) {
                        mListener.onItemClick(view);
                    }
                }
            });
            rowCurrent.addView(view);
        }

        highlight();
        mIsWaitingForUpdate = true;
    }

    // public methods
    public void setAdapter(CalendarAdapter adapter) {
        mAdapter = adapter;
        adapter.setFirstDayOfWeek(DEFAULT_FIRST_DAY_OF_WEEK);

        reload();

        // init week
        mCurrentWeekIndex = getSuitableRowIndex();
    }

    public void addEventTag(int numYear, int numMonth, int numDay) {
        mAdapter.addEvent(new Event(numYear, numMonth, numDay));

        reload();
    }

    public void prevMonth() {
        Calendar cal = mAdapter.getCalendar();
        if(cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR) - 1), cal.getActualMaximum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        }
        reload();
        mListener.onMonthChange();
    }

    public void nextMonth() {
        Calendar cal = mAdapter.getCalendar();
        if(cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR)+1), cal.getActualMinimum(Calendar.MONTH),1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        }
        reload();
        mListener.onMonthChange();
    }

    public void prevWeek() {
        if (mCurrentWeekIndex - 1 < 0) {
            mCurrentWeekIndex = -1;
            prevMonth();
        } else {
            mCurrentWeekIndex --;
            collapseTo(mCurrentWeekIndex);
        }
    }

    public void nextWeek() {
        if (mCurrentWeekIndex + 1 >= mTableBody.getChildCount()) {
            mCurrentWeekIndex = 0;
            nextMonth();
        } else {
            mCurrentWeekIndex ++;
            collapseTo(mCurrentWeekIndex);
        }
    }

    public int getYear() {
        return mAdapter.getCalendar().get(Calendar.YEAR);
    }

    public int getMonth() {
        return mAdapter.getCalendar().get(Calendar.MONTH);
    }

    public Day getSelectedDay() {
        return new Day(
                mSelectedItem.getYear(),
                mSelectedItem.getMonth(),
                mSelectedItem.getDay());
    }

    public boolean isSelectedDay(Day day) {
        return day != null
                && mSelectedItem != null
                && day.getYear() == mSelectedItem.getYear()
                && day.getMonth() == mSelectedItem.getMonth()
                && day.getDay() == mSelectedItem.getDay();
    }

    public boolean isToady(Day day) {
        Calendar todayCal = Calendar.getInstance();
        return day != null
                && day.getYear() == todayCal.get(Calendar.YEAR)
                && day.getMonth() == todayCal.get(Calendar.MONTH)
                && day.getDay() == todayCal.get(Calendar.DAY_OF_MONTH);
    }

    public int getSelectedItemPosition() {
        int position = -1;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Day day = mAdapter.getItem(i);

            if (isSelectedDay(day)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public int getTodayItemPosition() {
        int position = -1;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Day day = mAdapter.getItem(i);

            if (isToady(day)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void collapse(int duration) {
        if (mState == State.EXPANDED) {
            mState = State.PROCESSING;

            mLayoutBtnGroupMonth.setVisibility(GONE);
            mLayoutBtnGroupWeek.setVisibility(VISIBLE);
            mBtnPrevWeek.setClickable(false);
            mBtnNextWeek.setClickable(false);

            int index = getSuitableRowIndex();
            mCurrentWeekIndex = index;

            final int currentHeight = mInitHeight;
            final int targetHeight = mTableBody.getChildAt(index).getMeasuredHeight();
            int tempHeight = 0;
            for (int i = 0; i < index; i++) {
                tempHeight += mTableBody.getChildAt(i).getMeasuredHeight();
            }
            final int topHeight = tempHeight;

            Animation anim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {

                    mScrollViewBody.getLayoutParams().height = (interpolatedTime == 1)
                            ? targetHeight
                            : currentHeight - (int) ((currentHeight - targetHeight) * interpolatedTime);
                    mScrollViewBody.requestLayout();

                    if (mScrollViewBody.getMeasuredHeight() < topHeight + targetHeight) {
                        int position = topHeight + targetHeight - mScrollViewBody.getMeasuredHeight();
                        mScrollViewBody.smoothScrollTo(0, position);
                    }

                    if (interpolatedTime == 1) {
                        mState = State.COLLAPSED;

                        mBtnPrevWeek.setClickable(true);
                        mBtnNextWeek.setClickable(true);
                    }
                }
            };
            anim.setDuration(duration);
            startAnimation(anim);
        }
    }

    private void collapseTo(int index) {
        if (mState == State.COLLAPSED) {
            if (index == -1) {
                index = mTableBody.getChildCount() - 1;
            }
            mCurrentWeekIndex = index;

            final int targetHeight = mTableBody.getChildAt(index).getMeasuredHeight();
            int tempHeight = 0;
            for (int i = 0; i < index; i++) {
                tempHeight += mTableBody.getChildAt(i).getMeasuredHeight();
            }
            final int topHeight = tempHeight;

            mScrollViewBody.getLayoutParams().height = targetHeight;

            mScrollViewBody.smoothScrollTo(0, topHeight);
            mScrollViewBody.requestLayout();

            mListener.onWeekChange(mCurrentWeekIndex);
        }
    }

    public void expand(int duration) {
        if (mState == State.COLLAPSED) {
            mState = State.PROCESSING;

            mLayoutBtnGroupMonth.setVisibility(VISIBLE);
            mLayoutBtnGroupWeek.setVisibility(GONE);
            mBtnPrevMonth.setClickable(false);
            mBtnNextMonth.setClickable(false);

            final int currentHeight = mScrollViewBody.getMeasuredHeight();
            final int targetHeight = mInitHeight;

            Animation anim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {

                    mScrollViewBody.getLayoutParams().height = (interpolatedTime == 1)
                            ? LinearLayout.LayoutParams.WRAP_CONTENT
                            : currentHeight - (int) ((currentHeight - targetHeight) * interpolatedTime);
                    mScrollViewBody.requestLayout();

                    if (interpolatedTime == 1) {
                        mState = State.EXPANDED;

                        mBtnPrevMonth.setClickable(true);
                        mBtnNextMonth.setClickable(true);
                    }
                }
            };
            anim.setDuration(duration);
            startAnimation(anim);
        }
    }

    public void select(Day day) {
        mSelectedItem = new Day(day.getYear(), day.getMonth(), day.getDay());

        Calendar cal = mAdapter.getCalendar();
        if (day.getMonth() != cal.get(Calendar.MONTH)) {
            cal.set(day.getYear(), day.getMonth(), 1);
            reload();
            mListener.onMonthChange();
        }
        highlight();
        mListener.onDaySelect();
    }

    // callback
    public void setCalendarListener(CalendarListener listener) {
        mListener = listener;
    }
    public interface CalendarListener {

        // trigger when the day is selected programmatically or clicked by user.
        void onDaySelect();

        // trigger only when the views of day on calendar are clicked by user.
        void onItemClick(View v);

        // trigger when the data of calendar are updated by changing month or adding events.
        void onDataUpdate();

        // trigger when the month are changed.
        void onMonthChange();

        // trigger when the week position are changed.
        void onWeekChange(int position);
    }
}
