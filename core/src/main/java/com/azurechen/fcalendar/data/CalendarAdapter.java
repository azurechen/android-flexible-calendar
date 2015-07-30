package com.azurechen.fcalendar.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.azurechen.fcalendar.R;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends BaseAdapter {

	private int mFirstDayOfWeek = 0;
	private Context mContext;
	private Calendar mCal;
	private LayoutInflater mInflater;
	
	ArrayList<Day> mDayList = new ArrayList<>();
	ArrayList<Event> mEventList = new ArrayList<>();
	
	public CalendarAdapter(Context context, Calendar cal){
		this.mCal = cal;
		this.mContext = context;

		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		refresh();
	}

	@Override
	public int getCount() {
		return mDayList.size();
	}

	@Override
	public Day getItem(int position) {
		return mDayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;


		view = mDayList.get(position).getView();

		return view;
	}

	public Calendar getCalendar() {
		return mCal;
	}

	public void addEvent(Event event) {
		mEventList.add(event);
	}
	
	public void refresh() {
    	// clear data
    	mDayList.clear();

		// set calendar
		int year = mCal.get(Calendar.YEAR);
		int month = mCal.get(Calendar.MONTH);

		Calendar firstDayCal = Calendar.getInstance();
		firstDayCal.set(year, month, 1);

		int lastDayOfMonth = firstDayCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = firstDayCal.get(Calendar.DAY_OF_WEEK) - 1;

		// generate day list
		int offset = 0 - (firstDayOfWeek - mFirstDayOfWeek) + 1;
		int length = (int) Math.ceil((float) (lastDayOfMonth - offset) / 7) * 7;
		for (int i = offset; i < length + offset; i++) {
			int numYear;
			int numMonth;
			int numDay;

			Calendar tempCal = Calendar.getInstance();
			if (i <= 0) { // prev month
				if (month == 0) {
					numYear = year - 1;
					numMonth = 11;
				} else {
					numYear = year;
					numMonth = month - 1;
				}
				tempCal.set(numYear, numMonth, 1);
				numDay = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH) + i;
			} else if (i > lastDayOfMonth) { // next month
				if (month == 11) {
					numYear = year + 1;
					numMonth = 0;
				} else {
					numYear = year;
					numMonth = month + 1;
				}
				tempCal.set(numYear, numMonth, 1);
				numDay = i - lastDayOfMonth;
			} else {
				numYear = year;
				numMonth = month;
				numDay = i;
			}

			Day day = new Day(numYear, numMonth, numDay);

			View view = mInflater.inflate(R.layout.layout_day, null);
			TextView txtDay = (TextView) view.findViewById(R.id.txt_day);
			ImageView imgEventTag = (ImageView) view.findViewById(R.id.img_event_tag);

			txtDay.setText(String.valueOf(day.getDay()));
			if (day.getMonth() != mCal.get(Calendar.MONTH)) {
				txtDay.setAlpha(0.3f);
			}

			for (int j = 0; j < mEventList.size(); j++) {
				Event event = mEventList.get(j);
				if (day.getYear() == event.getYear()
						&& day.getMonth() == event.getMonth()
						&& day.getDay() == event.getDay()) {
					imgEventTag.setVisibility(View.VISIBLE);
				}
			}

			day.setView(view);
			mDayList.add(day);
		}
    }
	
}
