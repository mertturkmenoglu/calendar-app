package ce.yildiz.calendarapp.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public final class Validate {
    public static final class Date {
        public static boolean validateFields(int year, int monthOfYear, int dayOfMonth) {
            return year != Constants.DATE_DEFAULT_YEAR
                    && monthOfYear != Constants.DATE_DEFAULT_MONTH
                    && dayOfMonth != Constants.DATE_DEFAULT_DAY;
        }
    }

    public static boolean validateEmail(@NonNull String email) {
        return !TextUtils.isEmpty(email);
    }

    public static boolean validatePassword(@NonNull String password) {
        return !TextUtils.isEmpty(password) && !(password.length() < Constants.MIN_PASSWORD_LENGTH);
    }
}
