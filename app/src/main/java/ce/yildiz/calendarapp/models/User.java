package ce.yildiz.calendarapp.models;

import androidx.annotation.NonNull;

import java.util.List;

@SuppressWarnings("unused")
public class User {
    private String email;
    private String gUsername;
    private List<Event> events;
    private String defaultSound;
    private String defaultReminderFreq;
    private String appTheme;

    public User() {

    }

    public User(String email, String gUsername, List<Event> events, String defaultSound,
                String defaultReminderFreq, String appTheme) {
        this.email = email;
        this.gUsername = gUsername;
        this.events = events;
        this.defaultSound = defaultSound;
        this.defaultReminderFreq = defaultReminderFreq;
        this.appTheme = appTheme;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public String getgUsername() {
        return gUsername;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void setgUsername(String gUsername) {
        this.gUsername = gUsername;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String getDefaultSound() {
        return defaultSound;
    }

    public void setDefaultSound(String defaultSound) {
        this.defaultSound = defaultSound;
    }

    public String getDefaultReminderFreq() {
        return defaultReminderFreq;
    }

    public void setDefaultReminderFreq(String defaultReminderFreq) {
        this.defaultReminderFreq = defaultReminderFreq;
    }

    public String getAppTheme() {
        return appTheme;
    }

    public void setAppTheme(String appTheme) {
        this.appTheme = appTheme;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Event e : events) {
            sb.append(e.getName());
            sb.append("\n");
        }

        return "User{" +
                "email='" + email + '\'' +
                ", gUsername='" + gUsername + '\'' +
                ", events=" + sb.toString() +
                ", defaultSound='" + defaultSound + '\'' +
                ", defaultReminderFreq='" + defaultReminderFreq + '\'' +
                ", appTheme='" + appTheme + '\'' +
                '}';
    }
}
