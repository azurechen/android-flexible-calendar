package com.azurechen.fcalendar.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.azurechen.fcalendar.R;
import com.azurechen.fcalendar.data.CalendarAdapter;
import com.azurechen.fcalendar.data.Day;
import com.azurechen.fcalendar.data.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by azurechen on 7/29/15.
 */
public class FoldableCalendar extends RelativeLayout {

    private Context mContext;
    private LayoutInflater mInflater;

    private TextView mTxtTitle;
    private TableLayout mTableBody;
    private ImageButton mBtnPrevMonth;
    private ImageButton mBtnNextMonth;

    private Calendar mCal;
    private CalendarAdapter mAdapter;

    private OnItemClickListener mListener;

    private int mDefaultColor;
    private int mPrimaryColor;
    private int mFirstDayOfWeek = 0;

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

    private void prevMonth() {
        if(mCal.get(Calendar.MONTH) == mCal.getActualMinimum(Calendar.MONTH)) {
            mCal.set((mCal.get(Calendar.YEAR) - 1), mCal.getActualMaximum(Calendar.MONTH), 1);
        } else {
            mCal.set(Calendar.MONTH, mCal.get(Calendar.MONTH) - 1);
        }
        reload();
    }

    private void nextMonth() {
        if(mCal.get(Calendar.MONTH) == mCal.getActualMaximum(Calendar.MONTH)) {
            mCal.set((mCal.get(Calendar.YEAR)+1), mCal.getActualMinimum(Calendar.MONTH),1);
        } else {
            mCal.set(Calendar.MONTH, mCal.get(Calendar.MONTH) + 1);
        }
        reload();
    }

    private void initHighlight() {
        // reset other items
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Day day = mAdapter.getItem(i);
            View view = day.getView();
            TextView txtDay = (TextView) view.findViewById(R.id.txt_day);
            txtDay.setBackgroundResource(Color.TRANSPARENT);
            txtDay.setTextColor(mDefaultColor);

            // is today?
            Calendar todayCal = Calendar.getInstance();
            if (day.getYear() == todayCal.get(Calendar.YEAR)
                    && day.getMonth() == todayCal.get(Calendar.MONTH)
                    && day.getDay() == todayCal.get(Calendar.DAY_OF_MONTH)) {

                txtDay.setBackgroundResource(R.drawable.circle_white_stroke_background);
                txtDay.setTextColor(mDefaultColor);
            }
        }
    }

    private void highlight(View v, int position) {
        initHighlight();


        // set the color of item
        TextView txtDay = (TextView) v.findViewById(R.id.txt_day);
        txtDay.setBackgroundResource(R.drawable.circle_white_solid_background);
        txtDay.setTextColor(mPrimaryColor);

        if (mListener != null) {
            mListener.onClick(v, mAdapter.getItem(position));
        }
    }

    private void reload() {
        mAdapter.refresh();
        mAdapter.notifyDataSetChanged();

        // reset UI
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        dateFormat.setTimeZone(mCal.getTimeZone());
        mTxtTitle.setText(dateFormat.format(mCal.getTime()));
        mTableBody.removeAllViews();

        initHighlight();

        TableRow rowCurrent = null;

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
            txtDayOfWeek.setText(dayOfWeekIds[(i + mFirstDayOfWeek) % 7]);
            view.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1));
            rowCurrent.addView(view);
        }
        mTableBody.addView(rowCurrent);

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
            final View item = mAdapter.getView(position, null, rowCurrent);
            item.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1));
            if (position >= 7) {
                item.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        highlight(v, position);
                    }
                });
            }
            rowCurrent.addView(item);
        }
    }

    // public methods
    public void setAdapter(CalendarAdapter adapter) {
        mAdapter = adapter;
        mCal = adapter.getCalendar();

        reload();
    }

    public void addEventTag(int numYear, int numMonth, int numDay) {
        mAdapter.addEvent(new Event(numYear, numMonth, numDay));
        reload();
    }

    // callback
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public interface OnItemClickListener {
        void onClick(View v, Day d);
    }
}
