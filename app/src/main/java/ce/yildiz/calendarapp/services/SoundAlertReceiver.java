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

        String defaultSound = intent.getStringExtra("default_sound");
        Uri soundUri;

        if (defaultSound == null) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            soundUri = Uri.parse(defaultSound);
        }

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(context, soundUri);
            ringtone.play();
        } catch (Exception e) {
            Ringtone ringtone = RingtoneManager.getRingtone(context,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            ringtone.play();
        }
    }
}
