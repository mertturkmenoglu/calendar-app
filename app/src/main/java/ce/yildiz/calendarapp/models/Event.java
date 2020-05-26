package ce.yildiz.calendarapp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class Event {
    private String detail;
    private String name;
    private Date endDate;
    private Date startDate;
    private GeoPoint location;
    private String reminderFreq;
    private String reminderType;
    private List<Date> reminders;
    private String type;

    public Event() {

    }

    public Event(String detail, String name, Date endDate, Date startDate, GeoPoint location,
                 String reminderFreq, String reminderType, List<Date> reminders, String type) {
        this.detail = detail;
        this.name = name;
        this.endDate = endDate;
        this.startDate = startDate;
        this.location = location;
        this.reminderFreq = reminderFreq;
        this.reminderType = reminderType;
        this.reminders = reminders;
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getReminderFreq() {
        return reminderFreq;
    }

    public void setReminderFreq(String reminderFreq) {
        this.reminderFreq = reminderFreq;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public List<Date> getReminders() {
        return reminders;
    }

    public void setReminders(List<Date> reminders) {
        this.reminders = reminders;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "detail='" + detail + '\'' +
                ", name='" + name + '\'' +
                ", endDate=" + endDate +
                ", startDate=" + startDate +
                ", location=" + location +
                ", reminderFreq='" + reminderFreq + '\'' +
                ", reminderType='" + reminderType + '\'' +
                ", reminders=" + reminders +
                ", type='" + type + '\'' +
                '}';
    }
}
