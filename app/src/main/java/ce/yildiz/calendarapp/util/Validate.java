package ce.yildiz.calendarapp.util;

public final class Validate {
    public static final class Date {
        public static boolean validateFields(int year, int monthOfYear, int dayOfMonth) {
            return year != Constants.DATE_DEFAULT_YEAR
                    && monthOfYear != Constants.DATE_DEFAULT_MONTH
                    && dayOfMonth != Constants.DATE_DEFAULT_DAY;
        }
    }
}
