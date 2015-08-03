package com.azurechen.fcalendarsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.azurechen.fcalendar.data.CalendarAdapter;
import com.azurechen.fcalendar.data.Day;
import com.azurechen.fcalendar.widget.FlexibleCalendar;

import java.util.Calendar;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FlexibleCalendar viewCalendar = (FlexibleCalendar) findViewById(R.id.calendar);
        final Button btnCollapse = (Button) findViewById(R.id.btn_collapse);
        final Button btnExpand = (Button) findViewById(R.id.btn_expand);

        // init calendar
        Calendar cal = Calendar.getInstance();
        CalendarAdapter adapter = new CalendarAdapter(this, cal);
        viewCalendar.setAdapter(adapter);
        viewCalendar.setOnItemClickListener(new FlexibleCalendar.OnItemClickListener() {
            @Override
            public void onClick(View v, Day d) {
                Log.i(getClass().getName(), "Selected Day: "
                        + d.getYear() + "/" + (d.getMonth() + 1) + "/" + d.getDay());
            }
        });
        viewCalendar.addEventTag(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 10);
        viewCalendar.addEventTag(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 22);
        viewCalendar.addEventTag(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 27);
        viewCalendar.addEventTag(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1);

        btnCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCalendar.collapse(500);
            }
        });

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCalendar.expand(500);
            }
        });
    }
}
