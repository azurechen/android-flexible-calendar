package com.azurechen.fcalendar.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.azurechen.fcalendar.R;
import com.azurechen.fcalendar.View.LockScrollView;
import com.azurechen.fcalendar.data.CalendarAdapter;
import com.azurechen.fcalendar.data.Day;
import com.azurechen.fcalendar.data.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by azurechen on 7/29/15.
 */
public class FoldableCalendar extends LinearLayout {

    private static final int DEFAULT_FIRST_DAY_OF_WEEK = 0;

    private Context mContext;
    private LayoutInflater mInflater;

    private TextView mTxtTitle;
    private TableLayout mTableHead;
    private LockScrollView mScrollViewBody;
    private TableLayout mTableBody;
    private ImageButton mBtnPrevMonth;
    private ImageButton mBtnNextMonth;

    private CalendarAdapter mAdapter;

    private OnItemClickListener mListener;

    private enum State {COLLAPSED, EXPANDED, PROCESSING }
    private int mInitHeight = 0;
    private State mState = State.EXPANDED;

    private int mDefaultColor;
    private int mPrimaryColor;
    private Day mSelectedItem;

    public FoldableCalendar(Context context) {
        super(context);
        init(context);
    }

    public FoldableCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FoldableCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // load rootView from xml
        LayoutInflater vi =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = vi.inflate(R.layout.widget_foldable_calendar, this, true);

        // init UI
        mTxtTitle = (TextView) rootView.findViewById(R.id.txt_title);
        mTableHead = (TableLayout) rootView.findViewById(R.id.table_head);
        mScrollViewBody = (LockScrollView) rootView.findViewById(R.id.scroll_view_body);
        mTableBody = (TableLayout) rootView.findViewById(R.id.table_body);
        mBtnPrevMonth = (ImageButton) rootView.findViewById(R.id.btn_prev_month);
        mBtnNextMonth = (ImageButton) rootView.findViewById(R.id.btn_next_month);

        // init default attrs
        mPrimaryColor = context.getResources().getColor(R.color.primary_pink);
        mDefaultColor = Color.WHITE;

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mInitHeight = mTableBody.getMeasuredHeight();
    }

    private void prevMonth() {
        Calendar cal = mAdapter.getCalendar();
        if(cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR) - 1), cal.getActualMaximum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        }
        reload();
    }

    private void nextMonth() {
        Calendar cal = mAdapter.getCalendar();
        if(cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR)+1), cal.getActualMinimum(Calendar.MONTH),1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        }
        reload();
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

                if (mListener != null) {
                    mListener.onClick(view, new Day(
                            mSelectedItem.getYear(),
                            mSelectedItem.getMonth(),
                            mSelectedItem.getDay()));
                }
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
                    Day day = mAdapter.getItem(position);
                    mSelectedItem = new Day(day.getYear(), day.getMonth(), day.getDay());
                    highlight();
                }
            });
            rowCurrent.addView(view);
        }

        highlight();
    }

    // public methods
    public void setAdapter(CalendarAdapter adapter) {
        mAdapter = adapter;
        adapter.setFirstDayOfWeek(DEFAULT_FIRST_DAY_OF_WEEK);

        reload();
    }

    public void addEventTag(int numYear, int numMonth, int numDay) {
        mAdapter.addEvent(new Event(numYear, numMonth, numDay));

        reload();
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

            int index = getSuitableRowIndex();

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
                    }
                }
            };
            anim.setDuration(duration);
            startAnimation(anim);
        }
    }

    public void expand(int duration) {
        if (mState == State.COLLAPSED) {
            mState = State.PROCESSING;

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
                    }
                }
            };
            anim.setDuration(duration);
            startAnimation(anim);
        }
    }

    // callback
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public interface OnItemClickListener {
        void onClick(View v, Day d);
    }
}
