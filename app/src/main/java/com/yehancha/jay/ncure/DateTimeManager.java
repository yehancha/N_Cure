package com.yehancha.jay.ncure;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeManager {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    private static final String EXTRA_YEAR = "EXTRA_YEAR";
    private static final String EXTRA_MONTH = "EXTRA_MONTH";
    private static final String EXTRA_DAY_OF_MONTH = "EXTRA_DAY_OF_MONTH";
    private static final String EXTRA_HOUR_OF_DAY = "EXTRA_HOUR_OF_DAY";
    private static final String EXTRA_MINUTE = "EXTRA_MINUTE";
    private static final String SAVED_STATE_DATE_TIME = "SAVED_STATE_DATE_TIME";

    public static void showDatePicker(Calendar calendar, FragmentManager fragmentManager) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(getDateTimeBundle(calendar));
        newFragment.show(fragmentManager, "datePicker");
    }

    public static void showTimePicker(Calendar calendar, FragmentManager fragmentManager) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(getDateTimeBundle(calendar));
        newFragment.show(fragmentManager, "timePicker");
    }

    private static Bundle getDateTimeBundle(Calendar calendar) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_YEAR, calendar.get(Calendar.YEAR));
        bundle.putInt(EXTRA_MONTH, calendar.get(Calendar.MONTH));
        bundle.putInt(EXTRA_DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        bundle.putInt(EXTRA_HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        bundle.putInt(EXTRA_MINUTE, calendar.get(Calendar.MINUTE));
        return bundle;
    }

    public static void onSavedInstanceState(Bundle outState, Calendar calendar) {
        outState.putBundle(SAVED_STATE_DATE_TIME, getDateTimeBundle(calendar));
    }

    public static void onRestoreInstanceState(Bundle savedInstanceState, Calendar calendar) {
        Bundle dateTimeBundle = savedInstanceState.getBundle(SAVED_STATE_DATE_TIME);
        restoreDateTime(dateTimeBundle, calendar);
    }

    private static void restoreDateTime(Bundle dateTimeBundle, Calendar calendar) {
        calendar.set(
                dateTimeBundle.getInt(EXTRA_YEAR),
                dateTimeBundle.getInt(EXTRA_MONTH),
                dateTimeBundle.getInt(EXTRA_DAY_OF_MONTH),
                dateTimeBundle.getInt(EXTRA_HOUR_OF_DAY),
                dateTimeBundle.getInt(EXTRA_MINUTE)
        );
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();

            int year = arguments.getInt(EXTRA_YEAR);
            int month = arguments.getInt(EXTRA_MONTH);
            int day = arguments.getInt(EXTRA_DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();

            int hour = arguments.getInt(EXTRA_HOUR_OF_DAY);
            int minute = arguments.getInt(EXTRA_MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getContext(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, false /* is not 24 hour format*/);
        }
    }
}
