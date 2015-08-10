package com.azurechen.fcalendar.widget;

import android.content.Context;
import android.content.res.TypedArray;
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

    // Style
    public static final int STYLE_LIGHT  = 0;
    public static final int STYLE_PINK   = 1;
    public static final int STYLE_ORANGE = 2;
    public static final int STYLE_BLUE   = 3;
    public static final int STYLE_GREEN  = 4;
    // Day of Week
    public static final int SUNDAY    = 0;
    public static final int MONDAY    = 1;
    public static final int TUESDAY   = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY  = 4;
    public static final int FRIDAY    = 5;
    public static final int SATURDAY  = 6;
    // State
    public static final int STATE_EXPANDED   = 0;
    public static final int STATE_COLLAPSED  = 1;
    public static final int STATE_PROCESSING = 2;

    protected Context mContext;
    protected LayoutInflater mInflater;

    // UI
    protected LinearLayout mLayoutRoot;
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
    private int mStyle = STYLE_LIGHT;
    private boolean mShowWeek = true;
    private int mFirstDayOfWeek = SUNDAY;
    private int mState = STATE_EXPANDED;

    private int mTextColor = Color.BLACK;
    private int mPrimaryColor = Color.WHITE;

    private int mTodayItemTextColor = Color.BLACK;
    private int mTodayItemBackground = R.drawable.circle_black_stroke_background;
    private int mSelectedItemTextColor = Color.WHITE;
    private int mSelectedItemBackground = R.drawable.circle_black_solid_background;

    private int mButtonLeftSrc = R.drawable.ic_navigate_before_black;
    private int mButtonRightSrc = R.drawable.ic_navigate_next_black;

    private Day mSelectedItem = null;

    public UICalendar(Context context) {
        this(context, null);
    }

    public UICalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UICalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.UICalendar, defStyleAttr, 0);
        setAttributes(attributes);
        attributes.recycle();
    }

    protected abstract void redraw();
    protected abstract void reload();

    protected void init(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        // load rootView from xml
        View rootView = mInflater.inflate(R.layout.widget_flexible_calendar, this, true);

        // init UI
        mLayoutRoot          = (LinearLayout)   rootView.findViewById(R.id.layout_root);
        mTxtTitle            = (TextView)       rootView.findViewById(R.id.txt_title);
        mTableHead           = (TableLayout)    rootView.findViewById(R.id.table_head);
        mScrollViewBody      = (LockScrollView) rootView.findViewById(R.id.scroll_view_body);
        mTableBody           = (TableLayout)    rootView.findViewById(R.id.table_body);
        mLayoutBtnGroupMonth = (RelativeLayout) rootView.findViewById(R.id.layout_btn_group_month);
        mLayoutBtnGroupWeek  = (RelativeLayout) rootView.findViewById(R.id.layout_btn_group_week);
        mBtnPrevMonth        = (ImageButton)    rootView.findViewById(R.id.btn_prev_month);
        mBtnNextMonth        = (ImageButton)    rootView.findViewById(R.id.btn_next_month);
        mBtnPrevWeek         = (ImageButton)    rootView.findViewById(R.id.btn_prev_week);
        mBtnNextWeek         = (ImageButton)    rootView.findViewById(R.id.btn_next_week);
    }

    protected void setAttributes(TypedArray attrs) {
        setStyle(attrs.getInt(R.styleable.UICalendar_style, mStyle));
        setShowWeek(attrs.getBoolean(R.styleable.UICalendar_showWeek, mShowWeek));
        setFirstDayOfWeek(attrs.getInt(R.styleable.UICalendar_firstDayOfWeek, mFirstDayOfWeek));
        setState(attrs.getInt(R.styleable.UICalendar_state, mState));
    }

    // getters and setters
    public int getStyle() {
        return mStyle;
    }

    public void setStyle(int style) {
        this.mStyle = style;

        if (style == STYLE_LIGHT) {
            setTextColor(Color.BLACK);
            setPrimaryColor(Color.WHITE);
            setTodayItemTextColor(Color.BLACK);
            setTodayItemBackground(R.drawable.circle_black_stroke_background);
            setSelectedItemTextColor(Color.WHITE);
            setSelectedItemBackground(R.drawable.circle_black_solid_background);
            setButtonLeftSrc(R.drawable.ic_navigate_before_black);
            setButtonRightSrc(R.drawable.ic_navigate_next_black);
        } else {
            setTextColor(Color.WHITE);
            setTodayItemTextColor(Color.WHITE);
            setTodayItemBackground(R.drawable.circle_white_stroke_background);
            setSelectedItemBackground(R.drawable.circle_white_solid_background);
            setButtonLeftSrc(R.drawable.ic_navigate_before_white);
            setButtonRightSrc(R.drawable.ic_navigate_next_white);

            int color = 0;
            if (style == STYLE_PINK) {
                color = mContext.getResources().getColor(R.color.primary_pink);
            }
            if (style == STYLE_ORANGE) {
                color = mContext.getResources().getColor(R.color.primary_orange);
            }
            if (style == STYLE_BLUE) {
                color = mContext.getResources().getColor(R.color.primary_blue);
            }
            if (style == STYLE_GREEN) {
                color = mContext.getResources().getColor(R.color.primary_green);
            }
            setPrimaryColor(color);
            setSelectedItemTextColor(color);
        }
    }

    public boolean isShowWeek() {
        return mShowWeek;
    }

    public void setShowWeek(boolean showWeek) {
        this.mShowWeek = showWeek;

        if (showWeek) {
            mTableHead.setVisibility(VISIBLE);
        } else {
            mTableHead.setVisibility(GONE);
        }
    }

    public int getFirstDayOfWeek() {
        return mFirstDayOfWeek;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.mFirstDayOfWeek = firstDayOfWeek;
        reload();
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        this.mState = state;

        if (mState == STATE_EXPANDED) {
            mLayoutBtnGroupMonth.setVisibility(VISIBLE);
            mLayoutBtnGroupWeek.setVisibility(GONE);
        }
        if (mState == STATE_COLLAPSED) {
            mLayoutBtnGroupMonth.setVisibility(GONE);
            mLayoutBtnGroupWeek.setVisibility(VISIBLE);
        }
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        redraw();

        mTxtTitle.setTextColor(mTextColor);
    }

    public int getPrimaryColor() {
        return mPrimaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.mPrimaryColor = primaryColor;
        redraw();

        mLayoutRoot.setBackgroundColor(mPrimaryColor);
    }

    public int getTodayItemTextColor() {
        return mTodayItemTextColor;
    }

    public void setTodayItemTextColor(int todayItemTextColor) {
        this.mTodayItemTextColor = todayItemTextColor;
        redraw();
    }

    public int getTodayItemBackground() {
        return mTodayItemBackground;
    }

    public void setTodayItemBackground(int todayItemBackground) {
        this.mTodayItemBackground = todayItemBackground;
        redraw();
    }

    public int getSelectedItemTextColor() {
        return mSelectedItemTextColor;
    }

    public void setSelectedItemTextColor(int selectedItemTextColor) {
        this.mSelectedItemTextColor = selectedItemTextColor;
        redraw();
    }

    public int getSelectedItemBackground() {
        return mSelectedItemBackground;
    }

    public void setSelectedItemBackground(int selectedItemBackground) {
        this.mSelectedItemBackground = selectedItemBackground;
        redraw();
    }

    public int getButtonLeftSrc() {
        return mButtonLeftSrc;
    }

    public void setButtonLeftSrc(int buttonLeftSrc) {
        this.mButtonLeftSrc = buttonLeftSrc;
        mBtnPrevMonth.setImageResource(buttonLeftSrc);
        mBtnPrevWeek.setImageResource(buttonLeftSrc);
    }

    public int getButtonRightSrc() {
        return mButtonRightSrc;
    }

    public void setButtonRightSrc(int buttonRightSrc) {
        this.mButtonRightSrc = buttonRightSrc;
        mBtnNextMonth.setImageResource(buttonRightSrc);
        mBtnNextWeek.setImageResource(buttonRightSrc);
    }

    public Day getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(Day selectedItem) {
        this.mSelectedItem = selectedItem;
    }

}
