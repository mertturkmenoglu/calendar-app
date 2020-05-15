package ce.yildiz.calendarapp.util;

public final class Constants {
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String IMAGE_BASE_URL = "https://github.com/";
    public static final String IMAGE_EXTENSION = "png";
    public static final String DEFAULT_SOUND = "default_sound";
    public static final String DEFAULT_REMINDER_FREQUENCY = ReminderFrequencies.DAILY;

    public static final class Collections {
        public static final String USERS = "Users";
        public static final String USER_EVENTS = "Events";
    }

    public static final class UserFields {
        public static final String EMAIL = "email";
        public static final String GITHUB_USERNAME = "gUsername";
        public static final String EVENTS = "events";
        public static final String DEFAULT_SOUND = "defaultSound";
        public static final String DEFAULT_REMINDER_FREQUENCY = "defaultReminderFreq";
        public static final String APP_THEME = "appTheme";
    }

    public static final class EventFields {
        public static final String DETAIL = "detail";
        public static final String END_DATE = "endDate";
        public static final String LOCATION = "location";
        public static final String NAME = "name";
        public static final String REMINDER_FREQ = "reminderFreq";
        public static final String REMINDER_TYPE = "reminderType";
        public static final String REMINDERS = "reminders";
        public static final String START_DATE = "startDate";
        public static final String TYPE = "type";
    }

    public static final long ONE_MONTH_IN_MILLIS = 1000L * 60 * 60 * 24 * 30;
    public static final long ONE_WEEK_IN_MILLIS = 1000L * 60 * 60 * 24 * 7;
    public static final long ONE_DAY_IN_MILLIS = 1000L * 60 * 60 * 24;

    public static final class ReminderFrequencies {
        public static final String DAILY = "Daily";
        public static final String WEEKLY = "Weekly";
        public static final String MONTHLY = "Monthly";
    }

    public static final class ReminderTypes {
        public static final String VIBRATION = "Vibration";
        public static final String SOUND = "Sound";
    }

    public static final class AppThemes {
        public static final String DARK = "Dark";
        public static final String LIGHT = "Light";
    }
}
