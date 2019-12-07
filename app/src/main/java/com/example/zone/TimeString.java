package com.example.zone;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeString {
    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;

    public static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;

        if (diffTime < TimeString.SEC) {
            msg = "방금";
        } else if ((diffTime /= TimeString.SEC) < TimeString.MIN) {
            msg = diffTime + "분전";
        } else if ((diffTime /= TimeString.MIN) < TimeString.HOUR) {
            msg = (diffTime) + "시간전";
        } else if ((diffTime /= TimeString.HOUR) < 4) { //3일전이후는 시간
            msg = (diffTime) + "일전";
        }
        /*else if ((diffTime /= TimeString.DAY) < TimeString.MONTH) {
            msg = (diffTime) + "달전";
        } else {
            msg = (diffTime) + "년전";
        }*/
        else{
            Date date = new Date(regTime);
            Format format = new SimpleDateFormat("yyyy MM dd");
            msg = format.format(date);
        }
        return msg;
    }
}