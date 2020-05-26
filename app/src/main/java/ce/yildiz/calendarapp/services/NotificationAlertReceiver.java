package ce.yildiz.calendarapp.services;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ce.yildiz.calendarapp.R;

public class NotificationAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = context.getString(R.string.default_notification_title);
        String content = context.getString(R.string.default_notification_content);
        int icon = R.drawable.web_hi_res_512;

        if (intent != null) {
            if (intent.hasExtra("title")) {
                title = intent.getStringExtra("title");
            }

            if (intent.hasExtra("content")) {
                content = intent.getStringExtra("content");
            }

            if (intent.hasExtra("icon")) {
                icon = intent.getIntExtra("icon", -1);
            }
        }

        NotificationHelper notificationHelper = new NotificationHelper(context);
        Notification notification = notificationHelper
                .getBuilder(title, content, icon)
                .build();

        notificationHelper.getManager().notify(1, notification);
    }
}
