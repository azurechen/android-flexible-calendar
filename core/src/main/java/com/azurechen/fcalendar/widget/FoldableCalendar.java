package com.azurechen.fcalendar.widget;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by azurechen on 7/29/15.
 */
public class FoldableCalendar extends RelativeLayout {

    private Context mContext;

    private TextView mTxtTitle;
    private TableLayout mTableBody;
    private ImageButton mBtnPrevMonth;
    private ImageButton mBtnNextMonth;

    private Calendar mCal;
    private CalendarAdapter mAdapter;

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

        // load rootView from xml
        LayoutInflater vi =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = vi.inflate(R.layout.widget_foldable_calendar, this, true);

        // init UI
        mTxtTitle = (TextView) rootView.findViewById(R.id.txt_title);
        mTableBody = (TableLayout) rootView.findViewById(R.id.table_body);
        mBtnPrevMonth = (ImageButton) rootView.findViewById(R.id.btn_prev_month);
        mBtnNextMonth = (ImageButton) rootView.findViewById(R.id.btn_next_month);

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
        refresh();
    }

    private void nextMonth() {
        if(mCal.get(Calendar.MONTH) == mCal.getActualMaximum(Calendar.MONTH)) {
            mCal.set((mCal.get(Calendar.YEAR)+1), mCal.getActualMinimum(Calendar.MONTH),1);
        } else {
            mCal.set(Calendar.MONTH, mCal.get(Calendar.MONTH) + 1);
        }
        refresh();
    }

    private void refresh() {
        mAdapter.refresh();
        mAdapter.notifyDataSetChanged();

        // reset UI
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        dateFormat.setTimeZone(mCal.getTimeZone());
        mTxtTitle.setText(dateFormat.format(mCal.getTime()));
        mTableBody.removeAllViews();

        TableRow rowCurrent = null;
        for (int i = 0; i < mAdapter.getCount(); i ++) {

            if (i % 7 == 0) {
                rowCurrent = new TableRow(mContext);
                rowCurrent.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                mTableBody.addView(rowCurrent);
            }
            if (rowCurrent != null) {
                View item = mAdapter.getView(i, null, rowCurrent);
                item.setLayoutParams(new TableRow.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1));
                rowCurrent.addView(item);
            }
        }
    }

    // public methods
    public void setAdapter(CalendarAdapter adapter) {
        mAdapter = adapter;
        mCal = adapter.getCalendar();

        refresh();
    }
}
