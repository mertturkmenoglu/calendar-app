package ce.yildiz.calendarapp.services;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "NotificationHelperId";
    public static final String channelName = NotificationHelper.class.getSimpleName();

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

    public NotificationCompat.Builder getChannelNotification(String title, String content, int icon) {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon);
    }

}
