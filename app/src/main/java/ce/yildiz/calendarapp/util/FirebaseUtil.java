package ce.yildiz.calendarapp.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {
    public static String getApplicationTheme() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            return Constants.AppThemes.LIGHT;
        }

        final String userId = auth.getCurrentUser().getUid();
        final String[] theme = new String[1];



        return theme[0];
    }
}
