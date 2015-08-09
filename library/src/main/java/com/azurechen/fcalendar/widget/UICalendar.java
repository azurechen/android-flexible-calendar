package com.azurechen.fcalendar.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.azurechen.fcalendar.R;
import com.azurechen.fcalendar.data.Day;
import com.azurechen.fcalendar.view.LockScrollView;

/**
 * Created by AzureChen on 15/8/9.
 */
public abstract class UICalendar extends LinearLayout {

    protected Context mContext;
    protected LayoutInflater mInflater;

    // UI
    protected TextView mTxtTitle;
    protected TableLayout mTableHead;
    protected LockScrollView mScrollViewBody;
    protected TableLayout mTableBody;
    protected RelativeLayout mLayoutBtnGroupMonth;
    protected RelativeLayout mLayoutBtnGroupWeek;
    protected ImageButton mBtnPrevMonth;
    protected ImageButton mBtnNextMonth;
    protected ImageButton mBtnPrevWeek;
    protected ImageButton mBtnNextWeek;

    // Attributes
    protected String mStyle;
    protected boolean mShowWeek = true;
    protected int mFirstDayOfWeek = 0;
    protected State mState = State.EXPANDED;

    protected int mTextColor = Color.BLACK;
    protected int mPrimaryColor = Color.WHITE;

    protected int mTodayItemTextColor = Color.BLACK;
    protected int mTodayItemBackground = R.drawable.circle_black_stroke_background;
    protected int mSelectedItemTextColor = Color.WHITE;
    protected int mSelectedItemBackground = R.drawable.circle_black_solid_background;

    protected int mButtonLeftSrc = R.drawable.ic_navigate_before_black;
    protected int mButtonRightSrc = R.drawable.ic_navigate_next_black;

    protected Day mSelectedItem = null;

    // enums
    public enum State { COLLAPSED, EXPANDED, PROCESSING }



    public UICalendar(Context context) {
        super(context);
        init(context);
    }

    public UICalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UICalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
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

    }

}
