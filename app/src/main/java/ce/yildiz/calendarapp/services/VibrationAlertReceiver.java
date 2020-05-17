package ce.yildiz.calendarapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

public class VibrationAlertReceiver extends BroadcastReceiver {
    private static final String TAG = VibrationAlertReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(
                1000,
                VibrationEffect.DEFAULT_AMPLITUDE
        );

        if (vibrator == null) {
            Log.e(TAG, "Vibrator service is null");
            return;
        }

        vibrator.vibrate(vibrationEffect);
    }
}
