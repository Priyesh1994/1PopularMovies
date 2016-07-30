package com.firstexample.priyesh.popularmovies;

import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PRIYESH on 31-07-2016.
 */
public class Utility {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * //@param context Context to use for resource localization
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(String dateInMillis ) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Date date;
        String ans = null;
        try {
            date = dbDateFormat.parse(dateInMillis);
            ans = monthDayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //String monthDayString = monthDayFormat.format(dateInMillis);
        return ans;
    }

}
