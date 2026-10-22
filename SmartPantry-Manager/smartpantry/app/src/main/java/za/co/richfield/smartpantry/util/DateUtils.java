package za.co.richfield.smartpantry.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public final class DateUtils {

    public static final String ISO_PATTERN = "yyyy-MM-dd";

    private DateUtils() {
        // Utility class: no instances.
    }

    private static SimpleDateFormat isoFormat() {
        return new SimpleDateFormat(ISO_PATTERN, Locale.US);
    }

    /** @return today's date as yyyy-MM-dd. */
    public static String todayIso() {
        return isoFormat().format(new Date());
    }

    public static String toIso(Calendar calendar) {
        return isoFormat().format(calendar.getTime());
    }

    public static String isoDaysFromToday(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return toIso(calendar);
    }

    public static Calendar parseIso(String isoDate) {
        if (isoDate == null || isoDate.trim().isEmpty()) {
            return null;
        }
        try {
            Date date = isoFormat().parse(isoDate);
            if (date == null) {
                return null;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    public static int daysUntil(String isoDate) {
        Calendar target = parseIso(isoDate);
        if (target == null) {
            return Integer.MIN_VALUE;
        }
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long difference = target.getTimeInMillis() - today.getTimeInMillis();
        return (int) Math.round(difference / (double) (24L * 60L * 60L * 1000L));
    }

    public static boolean isExpired(String isoDate) {
        int days = daysUntil(isoDate);
        return days != Integer.MIN_VALUE && days < 0;
    }

    public static boolean isExpiringSoon(String isoDate, int withinDays) {
        int days = daysUntil(isoDate);
        return days != Integer.MIN_VALUE && days >= 0 && days <= withinDays;
    }

    public static String toDisplayDate(String isoDate) {
        Calendar calendar = parseIso(isoDate);
        if (calendar == null) {
            return "";
        }
        return new SimpleDateFormat("dd MMM yyyy", Locale.US).format(calendar.getTime());
    }

    public static String describeExpiry(String isoDate) {
        int days = daysUntil(isoDate);
        if (days == Integer.MIN_VALUE) {
            return "";
        }
        if (days < -1) {
            return "Expired " + (-days) + " days ago";
        }
        if (days == -1) {
            return "Expired yesterday";
        }
        if (days == 0) {
            return "Expires today";
        }
        if (days == 1) {
            return "Expires tomorrow";
        }
        return "Expires in " + days + " days";
    }

    public static boolean isValidFutureOrToday(String isoDate) {
        Calendar calendar = parseIso(isoDate);
        if (calendar == null) {
            return false;
        }
        return daysUntil(isoDate) >= 0;
    }
}
