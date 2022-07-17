package com.rlearsi.apps.zipcode.whatsthezipcode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class MyDate {

    public static String datetimeToInt(String date, String date_or_time) {

        SimpleDateFormat sdf = null;

        switch (date_or_time) {
            case "date_int":

                sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

                break;
            case "time_int":

                sdf = new SimpleDateFormat("HHmm", Locale.ENGLISH);

                break;
            case "datetime_int":

                sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.ENGLISH);

                break;
        }

        String dateS = String.valueOf(date);
        Calendar c = Calendar.getInstance();

        try {
            assert sdf != null;
            c.setTime(Objects.requireNonNull(sdf.parse(dateS)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = sdf.format(c.getTime());

        return date;

    }

}