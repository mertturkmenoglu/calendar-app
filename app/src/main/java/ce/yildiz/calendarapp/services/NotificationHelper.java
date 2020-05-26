package ce.yildiz.calendarapp.services;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

class NotificationHelper extends ContextWrapper {
    private static final String channelID = "NotificationHelperId";
    private static final String channelName = NotificationHelper.class.getSimpleName();

    private NotificationManager mNotificationManager;

    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
        );

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mNotificationManager;
    }

    public NotificationCompat.Builder getBuilder(String title, String content, int icon) {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
    }

}
