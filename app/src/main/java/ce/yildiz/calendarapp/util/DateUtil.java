package ce.yildiz.calendarapp.util;

import java.util.Date;

public class DateUtil {
    public static boolean isSameDate(Date fst, Date snd) {
        return fst.getYear() == snd.getYear()
                && fst.getMonth() == snd.getMonth()
                && fst.getDate() == snd.getDate();
    }
}
