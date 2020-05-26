package ce.yildiz.calendarapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class SoundAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        final Uri soundUri = (intent.hasExtra("default_sound"))
                ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                : Uri.parse(intent.getStringExtra("default_sound"));

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(context, soundUri);
            ringtone.play();
        } catch (Exception e) {
            Ringtone ringtone = RingtoneManager.getRingtone(
                    context,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            );

            ringtone.play();
        }
    }
}
