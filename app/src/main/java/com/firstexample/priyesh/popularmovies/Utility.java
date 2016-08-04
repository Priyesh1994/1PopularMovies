package com.firstexample.priyesh.popularmovies;

import android.text.format.Time;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

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

    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            // when adapter is null
            return;
        }
        int height = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(), MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }

}
