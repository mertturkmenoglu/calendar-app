package ce.yildiz.calendarapp.ui.splash;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.ui.login.LoginActivity;
import ce.yildiz.calendarapp.util.Constants;
import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(LoginActivity.class)
                .withSplashTimeOut(Constants.SPLASH_SCREEN_TIMEOUT_MILLIS)
                .withBackgroundResource(android.R.color.white)
                .withFooterText(getString(R.string.splash_footer_text))
                .withLogo(R.drawable.ic_event_accent_96dp)
                .withAfterLogoText(getString(R.string.app_name));

        Typeface tf = ResourcesCompat.getFont(this, R.font.montserrat);

        config.getFooterTextView().setTextAppearance(
                android.R.style.TextAppearance_Material_Body1
        );

        config.getAfterLogoTextView().setTextAppearance(
                android.R.style.TextAppearance_Material_Body1
        );

        config.getFooterTextView().setTypeface(tf);
        config.getAfterLogoTextView().setTypeface(tf);

        config.getFooterTextView().setTextColor(Color.BLACK);
        config.getAfterLogoTextView().setTextColor(Color.BLACK);

        config.getFooterTextView().setTextSize(Dimension.DP, 48);
        config.getAfterLogoTextView().setTextSize(Dimension.DP, 48);

        View splashScreen = config.create();
        setContentView(splashScreen);
    }
}
