package ce.yildiz.calendarapp.util;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

public final class StringUtil {
    public static String getUserImageURL(String gUsername) {
        return Constants.IMAGE_BASE_URL + gUsername + "." + Constants.IMAGE_EXTENSION;
    }

    public static String getLocationShareUriString(@NonNull GeoPoint location) {
        return Constants.MAP_BASE_URL + location.getLatitude() + "," + location.getLongitude();
    }
}
