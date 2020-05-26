package ce.yildiz.calendarapp.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.services.NotificationAlertReceiver;
import ce.yildiz.calendarapp.services.SoundAlertReceiver;
import ce.yildiz.calendarapp.services.VibrationAlertReceiver;

@SuppressWarnings("unused")
public class NotificationUtil {
    public static void startRepeatingNotification(Context ctx, Date d, int requestCode,
                                                  String title, String content,
                                                  String reminderFreq) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, NotificationAlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("icon", R.drawable.web_hi_res_512);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                0
        );

        if (d.getTime() <= Calendar.getInstance().getTimeInMillis() || alarmManager == null) {
            return;
        }

        long interval;

        switch (reminderFreq) {
            case Constants.ReminderFrequencies.DAILY:
                interval = AlarmManager.INTERVAL_DAY;
                break;
            case Constants.ReminderFrequencies.WEEKLY:
                interval = AlarmManager.INTERVAL_DAY * 7;
                break;
            case Constants.ReminderFrequencies.MONTHLY:
                interval = AlarmManager.INTERVAL_DAY * 30;
                break;
            default:
                interval = AlarmManager.INTERVAL_HOUR;
                break;
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, d.getTime(), interval, pendingIntent);
    }

    public static void cancelRepeatingNotification(Context ctx, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, NotificationAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                0
        );

        if (alarmManager == null) return;

        alarmManager.cancel(pendingIntent);
    }

    public static void startNotification(Context ctx, Date d, int requestCode, String title,
                                         String content) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, NotificationAlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("icon", R.drawable.web_hi_res_512);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                0
        );

        if (d.getTime() <= Calendar.getInstance().getTimeInMillis() || alarmManager == null) {
            return;
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, d.getTime(), pendingIntent);
    }

    public static void cancelNotification(Context ctx, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, NotificationAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                0
        );

        if (alarmManager == null) return;

        alarmManager.cancel(pendingIntent);
    }

    public static void startSound(Context ctx, Date d, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, SoundAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                0
        );

        if (d.getTime() <= Calendar.getInstance().getTimeInMillis() || alarmManager == null) {
            return;
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, d.getTime(), pendingIntent);
    }

    public static void cancelSound(Context ctx, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, SoundAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                0
        );

        if (alarmManager == null) return;

        alarmManager.cancel(pendingIntent);
    }

    public static void startVibration(Context ctx, Date d, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, VibrationAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                0
        );

        if (d.getTime() <= Calendar.getInstance().getTimeInMillis() || alarmManager == null) {
            return;
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, d.getTime(), pendingIntent);
    }

    public static void cancelVibration(Context ctx, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, VibrationAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                0
        );

        if (alarmManager == null) return;

        alarmManager.cancel(pendingIntent);
    }
}
