package com.azurechen.fcalendar.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by azurechen on 7/31/15.
 */
public class LockScrollView extends ScrollView {
    public LockScrollView(Context context) {
        super(context);
    }

    public LockScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
