package com.healthhearts.app.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class DateTimeUtil {
    private DateTimeUtil() {
    }

    public static void pickDateTime(Context ctx, long initialTs, Callback cb) {
        Calendar cal = Calendar.getInstance();
        if (initialTs > 0) cal.setTimeInMillis(initialTs);

        DatePickerDialog dp = new DatePickerDialog(ctx, (view, year, month, dayOfMonth) -> {
            Calendar c2 = Calendar.getInstance();
            c2.set(Calendar.YEAR, year);
            c2.set(Calendar.MONTH, month);
            c2.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog tp = new TimePickerDialog(ctx, (v2, hourOfDay, minute) -> {
                c2.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c2.set(Calendar.MINUTE, minute);
                c2.set(Calendar.SECOND, 0);
                cb.onPicked(c2.getTimeInMillis());
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);

            tp.show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        dp.show();
    }

    public static String fmtDate(long ts) {
        return new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(ts);
    }

    public static String fmtTime(long ts) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(ts);
    }

    public static String fmtDateTime(long ts) {
        return new SimpleDateFormat("MM/dd/yyyy • hh:mm a", Locale.getDefault()).format(ts);
    }

    public interface Callback {
        void onPicked(long timestampMillis);
    }
}
