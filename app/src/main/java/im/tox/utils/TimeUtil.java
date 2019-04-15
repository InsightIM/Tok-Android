package im.tox.utils;

import com.client.tok.R;
import com.client.tok.TokApplication;
import com.client.tok.utils.StringUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    private static String HM = "HH:mm";
    private static String MD_HM = "MM/dd HH:mm";
    private static String YMD_HM = "yyyy/MM/dd HH:mm";
    private static String dayNames[] =
        TokApplication.getInstance().getResources().getStringArray(R.array.week);

    public static String getTimePoint(Long timeStamp) {
        if (timeStamp < 100) {
            return "";
        }
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timeStamp);

        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = getHM(timeStamp);
                        break;
                    case 1:
                        result = getYesterdayHM(timeStamp);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH);
                        if (dayOfMonth == todayOfMonth) {
                            result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1] + getHM(
                                timeStamp);
                        } else {
                            result = getMD_HM(timeStamp);
                        }
                        break;
                    default:
                        result = getMD_HM(timeStamp);
                        break;
                }
            } else {
                result = getMD_HM(timeStamp);
            }
        } else {
            result = getYMD_HM(timeStamp);
        }
        return result;
    }

    private static String getHM(long time) {
        return timeFormat(time, HM);
    }

    private static String getYesterdayHM(long time) {
        return StringUtils.getTextFromResId(R.string.yesterday) + " " + getHM(time);
    }

    private static String getMD_HM(long time) {
        return timeFormat(time, MD_HM);
    }

    private static String getYMD_HM(long time) {
        return timeFormat(time, YMD_HM);
    }

    private static String timeFormat(long time, String format) {
        DateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(time));
    }
}
