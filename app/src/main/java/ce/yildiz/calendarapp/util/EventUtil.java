package ce.yildiz.calendarapp.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.GeoPoint;

import java.text.DateFormat;
import java.util.Locale;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.models.Event;

public class EventUtil {
    @NonNull
    public static String getShareableEventMessage(@NonNull Context context, @NonNull Locale locale,
                                                  @NonNull Event event) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

        String name = "";
        if (event.getName() != null) {
            name = event.getName();
        }

        String detail = "";
        if (event.getDetail() != null) {
            detail = event.getDetail();
        }

        String startDate = "";
        if (event.getStartDate() != null) {
            startDate = df.format(event.getStartDate());
        }

        String endDate = "";
        if (event.getEndDate() != null) {
            endDate = df.format(event.getEndDate());
        }

        String location = "";
        if (event.getLocation() != null) {
            location = getLocationShareText(event.getLocation());
        }

        @SuppressWarnings("StringBufferReplaceableByString")
        StringBuilder sb = new StringBuilder();

        sb.append(context.getString(R.string.event_share_event_name)).append(": ").append(name);
        sb.append(System.lineSeparator());

        sb.append(context.getString(R.string.event_share_event_detail)).append(": ").append(detail);
        sb.append(System.lineSeparator());

        sb.append(context.getString(R.string.event_share_event_start_date)).append(": ");
        sb.append(startDate);
        sb.append(System.lineSeparator());

        sb.append(context.getString(R.string.event_share_event_end_date)).append(": ");
        sb.append(endDate);
        sb.append(System.lineSeparator());

        sb.append(context.getString(R.string.event_share_location)).append(": ").append(location);
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    @NonNull
    private static String getLocationShareText(@NonNull GeoPoint location) {
        return "[" + location.getLatitude() + ", " + location.getLongitude() + "]";
    }

    @NonNull
    public static String getLocationText(@NonNull GeoPoint location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    @Nullable
    public static GeoPoint getLocationFromText(@NonNull String text) {
        String[] coordinates = text.trim().split(",");

        GeoPoint location;

        try {
            double latitude = Double.parseDouble(coordinates[0]);
            double longitude = Double.parseDouble(coordinates[1]);

            location = new GeoPoint(latitude, longitude);
        } catch (Exception e) {
            return null;
        }

        return location;
    }
}
