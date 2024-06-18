package com.example.ijk.util;

import android.text.TextUtils;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtil {
    private static String mStrlog = "";
    private static TextView mTvPlayLog = null;

    public static void setTvPlayLog(TextView tvLog) {
        mTvPlayLog = tvLog;
    }

    public static void clear() {
        mStrlog = "";
        mTvPlayLog = null;
    }

    public static void logInfo(String log) {
        String[] lines = mStrlog.split("\\n");
        if (!TextUtils.isEmpty(mStrlog) && lines.length > 18) {//display 18 lines
            mStrlog = "";
            if (mTvPlayLog != null)
                mTvPlayLog.setText("");
        }
        mStrlog += logTime(System.currentTimeMillis()) + "\t " + log + "\n";
        if (mTvPlayLog != null)
            mTvPlayLog.setText(mStrlog);
    }

    private static String logTime(long time) {
        String str="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date date =new Date(time);
            str = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String showTime(long time) {
        int minute = (int) time / 60;
        int hour = (int) minute / 60;
        int second = (int) time % 60;
        minute %= 60;
        return String.format(Locale.US, "%02d:%02d:%02d", hour, minute, second);
    }
}
