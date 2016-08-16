package ministudio.fundsflow.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ministudio.fundsflow.R;

/**
 * Created by min on 16/1/30.
 */
public final class DateTimeHelper {

    private static final String FMT_DEFAULT_DATE    = "yyyy/MM/dd";
    private static final String FMT_TIME24          = "HH:mm";
    private static final String FMT_TIME12          = "a hh:mm";

    private DateTimeHelper() { }

    private static String dateFormat;
    private static String timeFormat;

    private synchronized static void init(Context context) {
        ArgumentValidator.checkNull(context, "context");

        ContentResolver resolver = context.getContentResolver();
        if (dateFormat == null) {
            try {
                dateFormat = Settings.System.getString(resolver, Settings.System.DATE_FORMAT);
            } catch (Exception ex) {
                dateFormat = FMT_DEFAULT_DATE;
            }
            if (dateFormat == null) {
                dateFormat = FMT_DEFAULT_DATE;
            }
        }
        if (timeFormat == null) {
            try {
                String fmt = Settings.System.getString(resolver, Settings.System.TIME_12_24);
                if ("24".equals(fmt)) {
                    timeFormat = FMT_TIME24;
                } else {
                    timeFormat = FMT_TIME12;
                }
            } catch (Exception ex) {
                timeFormat = FMT_TIME24;
            }
        }
    }

    public static String getDate(Context context, Date date) {
        ArgumentValidator.checkNull(date, "date");
        init(context);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }

    public static String getTime(Context context, Date date) {
        ArgumentValidator.checkNull(date, "date");
        init(context);
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        return sdf.format(date);
    }

    public static String getSweetDate(Context context, Date date) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(date);
        int originalDay = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(new Date());
        int currentDay = aCalendar.get(Calendar.DAY_OF_YEAR);

        String prefix = null;
        if (originalDay - currentDay == 0) {
            prefix = context.getResources().getString(R.string.today);
        } else if (originalDay - currentDay == -1) {
            prefix = context.getResources().getString(R.string.yesterday);
        }
        return prefix + " " + getDate(context, date);
    }

    public static boolean isSameDate(long time1, long time2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(time1);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(time2);
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
            return false;
        }
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)) {
            return false;
        }
        if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }
        return true;
    }
}
