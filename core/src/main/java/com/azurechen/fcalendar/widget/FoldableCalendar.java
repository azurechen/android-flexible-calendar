package com.azurechen.fcalendar.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.azurechen.fcalendar.R;
import com.azurechen.fcalendar.data.CalendarAdapter;

import java.util.Calendar;

/**
 * Created by azurechen on 7/29/15.
 */
public class FoldableCalendar extends RelativeLayout {

    private GridView gridviewBody;
    private ImageButton btnPrevMonth;
    private ImageButton btnNextMonth;

    private Calendar cal;
    private CalendarAdapter adapter;

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
        // load rootView from xml
        LayoutInflater vi =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = vi.inflate(R.layout.widget_foldable_calendar, this, true);

        // init UI
        gridviewBody = (GridView) rootView.findViewById(R.id.gridview_body);
        btnPrevMonth = (ImageButton) rootView.findViewById(R.id.btn_prev_month);
        btnNextMonth = (ImageButton) rootView.findViewById(R.id.btn_next_month);

        // init calendar
        cal = Calendar.getInstance();

        // set calendar view
        gridviewBody.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        gridviewBody.setDrawSelectorOnTop(true);
        gridviewBody.setSelector(android.R.color.transparent);
        adapter = new CalendarAdapter(context, cal);
        gridviewBody.setAdapter(adapter);
        gridviewBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        // bind events
        btnPrevMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prevMonth();
            }
        });

        btnNextMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });
    }

    private void prevMonth(){
        if(cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR) - 1), cal.getActualMaximum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        }
        refresh();
    }

    private void nextMonth(){
        if(cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR)+1),cal.getActualMinimum(Calendar.MONTH),1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        }
        refresh();
    }

    private void refresh(){
        adapter.refresh();
        adapter.notifyDataSetChanged();
    }
}
