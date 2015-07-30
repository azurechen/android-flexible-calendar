package com.azurechen.fcalendarsample;

import android.app.Activity;
import android.os.Bundle;

import com.azurechen.fcalendar.data.CalendarAdapter;
import com.azurechen.fcalendar.widget.FoldableCalendar;

import java.util.Calendar;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init calendar
        FoldableCalendar viewCalendar = (FoldableCalendar) findViewById(R.id.calendar);
        Calendar cal = Calendar.getInstance();
        CalendarAdapter adapter = new CalendarAdapter(this, cal);
        viewCalendar.setAdapter(adapter);
    }
}
