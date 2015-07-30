package com.azurechen.fcalendar.data;

public class Day {
	
	private int year;
	private int month;
	private int day;
	
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

}
