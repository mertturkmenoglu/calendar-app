package ce.yildiz.calendarapp.modal;

import java.util.List;

public class User {
    private String email;
    private String gUsername;
    private List<Event> events;

    public User() {
    }

    public User(String email, String gUsername, List<Event> events) {
        this.email = email;
        this.gUsername = gUsername;
        this.events = events;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getgUsername() {
        return gUsername;
    }

    public void setgUsername(String gUsername) {
        this.gUsername = gUsername;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

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
                '}';
    }
}
