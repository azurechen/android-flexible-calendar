package com.azurechen.fcalendar.data;

import android.view.View;

public class Day {
	
	private int year;
	private int month;
	private int day;

	private View mView;
	
	public Day(int year, int month, int day){
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public int getDay(){
		return day;
	}

	public View getView() {
		return mView;
	}

	public void setView(View view) {
		mView = view;
	}

}
