package ce.yildiz.calendarapp.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public final class Validate {
    public static boolean validateEmail(@NonNull String email) {
        return !TextUtils.isEmpty(email);
    }

    public static boolean validatePassword(@NonNull String password) {
        return !TextUtils.isEmpty(password) && !(password.length() < Constants.MIN_PASSWORD_LENGTH);
    }

    public static boolean validateGithubUsername(@NonNull String username) {
        return !TextUtils.isEmpty(username);
    }
}
