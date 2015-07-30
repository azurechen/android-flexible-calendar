package com.azurechen.fcalendarsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.azurechen.fcalendar.data.CalendarAdapter;
import com.azurechen.fcalendar.data.Day;
import com.azurechen.fcalendar.widget.FoldableCalendar;

import java.util.Calendar;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init calendar
        FoldableCalendar viewCalendar = (FoldableCalendar) findViewById(R.id.calendar);
        CalendarAdapter adapter = new CalendarAdapter(this, Calendar.getInstance());
        viewCalendar.setAdapter(adapter);
        viewCalendar.setOnItemClickListener(new FoldableCalendar.OnItemClickListener() {
            @Override
            public void onClick(View v, Day d) {
                Log.i(getClass().getName(), d.getYear() + "/" + d.getMonth() + "/" + d.getDay());
            }
        });
    }
}
