package ce.yildiz.calendarapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import ce.yildiz.calendarapp.R;

public class NotificationAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = "CalendarEvent";
        String content = "There is an event soon";
        int icon = R.drawable.web_hi_res_512;

        if (intent != null) {
            String titleTemp = intent.getStringExtra("title");
            String contentTemp = intent.getStringExtra("content");
            int iconTemp = intent.getIntExtra("icon", -1);

            if (titleTemp != null) {
                title = titleTemp;
            }

            if (contentTemp != null) {
                content = contentTemp;
            }

            if (iconTemp != -1) {
                icon = iconTemp;
            }
        }

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, content, icon);
        notificationHelper.getManager().notify(1, nb.build());
    }
}
