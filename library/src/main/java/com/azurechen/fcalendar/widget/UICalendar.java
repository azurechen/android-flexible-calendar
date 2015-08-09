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
    private String mStyle;
    private boolean mShowWeek = true;
    private int mFirstDayOfWeek = 0;
    private State mState = State.EXPANDED;

    private int mTextColor = Color.BLACK;
    private int mPrimaryColor = Color.WHITE;

    private int mTodayItemTextColor = Color.BLACK;
    private int mTodayItemBackground = R.drawable.circle_black_stroke_background;
    private int mSelectedItemTextColor = Color.WHITE;
    private int mSelectedItemBackground = R.drawable.circle_black_solid_background;

    private int mButtonLeftSrc = R.drawable.ic_navigate_before_black;
    private int mButtonRightSrc = R.drawable.ic_navigate_next_black;

    private Day mSelectedItem = null;

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

    public String getStyle() {
        return mStyle;
    }

    public void setStyle(String mStyle) {
        this.mStyle = mStyle;
    }

    public boolean isShowWeek() {
        return mShowWeek;
    }

    public void setShowWeek(boolean mShowWeek) {
        this.mShowWeek = mShowWeek;
    }

    public int getFirstDayOfWeek() {
        return mFirstDayOfWeek;
    }

    public void setFirstDayOfWeek(int mFirstDayOfWeek) {
        this.mFirstDayOfWeek = mFirstDayOfWeek;
    }

    public State getState() {
        return mState;
    }

    public void setState(State mState) {
        this.mState = mState;

        if (mState == State.EXPANDED) {
            mLayoutBtnGroupMonth.setVisibility(VISIBLE);
            mLayoutBtnGroupWeek.setVisibility(GONE);
        }
        if (mState == State.COLLAPSED) {
            mLayoutBtnGroupMonth.setVisibility(GONE);
            mLayoutBtnGroupWeek.setVisibility(VISIBLE);
        }
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public int getPrimaryColor() {
        return mPrimaryColor;
    }

    public void setPrimaryColor(int mPrimaryColor) {
        this.mPrimaryColor = mPrimaryColor;
    }

    public int getTodayItemTextColor() {
        return mTodayItemTextColor;
    }

    public void setTodayItemTextColor(int mTodayItemTextColor) {
        this.mTodayItemTextColor = mTodayItemTextColor;
    }

    public int getTodayItemBackground() {
        return mTodayItemBackground;
    }

    public void setTodayItemBackground(int mTodayItemBackground) {
        this.mTodayItemBackground = mTodayItemBackground;
    }

    public int getSelectedItemTextColor() {
        return mSelectedItemTextColor;
    }

    public void setSelectedItemTextColor(int mSelectedItemTextColor) {
        this.mSelectedItemTextColor = mSelectedItemTextColor;
    }

    public int getSelectedItemBackground() {
        return mSelectedItemBackground;
    }

    public void setSelectedItemBackground(int mSelectedItemBackground) {
        this.mSelectedItemBackground = mSelectedItemBackground;
    }

    public int getButtonLeftSrc() {
        return mButtonLeftSrc;
    }

    public void setButtonLeftSrc(int mButtonLeftSrc) {
        this.mButtonLeftSrc = mButtonLeftSrc;
    }

    public int getButtonRightSrc() {
        return mButtonRightSrc;
    }

    public void setButtonRightSrc(int mButtonRightSrc) {
        this.mButtonRightSrc = mButtonRightSrc;
    }

    public Day getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(Day mSelectedItem) {
        this.mSelectedItem = mSelectedItem;
    }

}
