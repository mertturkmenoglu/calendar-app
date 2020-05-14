package ce.yildiz.calendarapp.util;

public final class StringUtil {
    public static String getUserImageURL(String gUsername) {
        return Constants.IMAGE_BASE_URL + gUsername + "." + Constants.IMAGE_EXTENSION;
    }
}
