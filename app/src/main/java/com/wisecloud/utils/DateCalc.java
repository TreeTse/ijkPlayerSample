package com.wisecloud.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateCalc {
    private String mTimeFormat = "";

    public void setTimeFormat(String format) {
        mTimeFormat = format;
    }

    public String getExpiredTime() {
        Calendar curTime = Calendar.getInstance();
        curTime.add(Calendar.HOUR_OF_DAY, 4);
        Date date = curTime.getTime();
        SimpleDateFormat format = new SimpleDateFormat(mTimeFormat, Locale.CHINA);
        String dateFormat = format.format(date);
        return dateFormat;
    }

    public String date2TimeStamp(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(mTimeFormat);
            return String.valueOf(format.parse(date).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
