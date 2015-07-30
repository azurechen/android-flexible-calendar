package com.azurechen.fcalendar.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.azurechen.fcalendar.R;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends BaseAdapter {

	private int mFirstDayOfWeek = 0;
	private Context mContext;
	private Calendar mCal;
	
	ArrayList<Day> dayList = new ArrayList<>();
	
	public CalendarAdapter(Context context, Calendar cal){
		this.mCal = cal;
		this.mContext = context;

		refresh();
	}

	@Override
	public int getCount() {
		return dayList.size() + 7;
	}

	@Override
	public Object getItem(int position) {
		return dayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		LayoutInflater vi =
				(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (position >= 0 && position < 7) {
			view = vi.inflate(R.layout.layout_day_of_week, null);
			TextView txtDayOfWeek = (TextView) view.findViewById(R.id.txt_day_of_week);

			int[] dayOfWeekIds = {
					R.string.sunday,
					R.string.monday,
					R.string.tuesday,
					R.string.wednesday,
					R.string.thursday,
					R.string.friday,
					R.string.saturday
			};
			txtDayOfWeek.setText(dayOfWeekIds[(position + mFirstDayOfWeek) % 7]);
		} else {
			view = vi.inflate(R.layout.layout_day, null);
			TextView txtDay = (TextView) view.findViewById(R.id.txt_day);

			Day day = dayList.get(position - 7);
			txtDay.setText(String.valueOf(day.getDay()));
		}

		return view;
	}
	
	public void refresh() {
    	// clear data
    	dayList.clear();

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
			dayList.add(new Day(numYear, numMonth, numDay));
		}
    }
	
}
