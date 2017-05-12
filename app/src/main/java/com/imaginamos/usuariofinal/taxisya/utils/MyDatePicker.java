package com.imaginamos.usuariofinal.taxisya.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

public class MyDatePicker extends DatePickerDialog {

	static int maxYear = 2005;
	static int maxMonth = 11;
	static int maxDay = 31;

	int minYear  = 1955;
	int minMonth = 0;
	int minDay   = 1;

	public MyDatePicker(Context context, OnDateSetListener callBack,
			int minYear, int minMonth, int minDay, int maxYear, int maxMonth,
			int maxDay) {
		
		super(context, callBack, minYear, minMonth, minDay);
		
		this.minDay = minDay;
		this.minMonth = minMonth;
		this.minYear = minYear;
		
		MyDatePicker.maxDay   = maxDay;
		MyDatePicker.maxMonth = maxMonth;
		MyDatePicker.maxYear  = maxYear;
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		super.onDateChanged(view, year, monthOfYear, dayOfMonth);

		if (year > maxYear || monthOfYear > maxMonth && year == maxYear
				|| dayOfMonth > maxDay && year == maxYear
				&& monthOfYear == maxMonth) {
			view.updateDate(maxYear, maxMonth, maxDay);
		} else if (year < minYear || monthOfYear < minMonth && year == minYear
				|| dayOfMonth < minDay && year == minYear
				&& monthOfYear == minMonth) {
			view.updateDate(minYear, minMonth, minDay);
		}
	}
}
