package ce.yildiz.calendarapp.util;

public final class Constants {
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String IMAGE_BASE_URL = "https://github.com/";
    public static final String IMAGE_EXTENSION = "png";

    public static final class Collections {
        public static final String USERS = "Users";
        public static final String USER_EVENTS = "Events";
    }

    public static final class UserFields {
        public static final String EMAIL = "email";
        public static final String GITHUB_USERNAME = "gUsername";
        public static final String EVENTS = "events";
    }

    public static final long ONE_MONTH_IN_MILLIS = 1000L * 60 * 60 * 24 * 30;
    public static final long ONE_WEEK_IN_MILLIS = 1000L * 60 * 60 * 24 * 7;
    public static final long ONE_DAY_IN_MILLIS = 1000L * 60 * 60 * 24;
}
