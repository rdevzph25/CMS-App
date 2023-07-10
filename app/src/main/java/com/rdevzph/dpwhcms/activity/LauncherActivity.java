package com.rdevzph.dpwhcms.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rdevzph.dpwhcms.R;
import com.rdevzph.dpwhcms.activity.WebViewActivity;
import android.widget.ImageView;
import java.util.Locale;
import android.os.CountDownTimer;
import android.content.res.AssetManager;
import java.io.InputStream;
import java.io.IOException;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import com.rdevzph.dpwhcms.view.SVGImageView;
import android.content.Context;
import android.content.pm.PackageManager;

public class LauncherActivity extends AppCompatActivity {

	private CountDownTimer mCountDown;
	private long mTimeLeft;
	private TextView skip;
	private SVGImageView svg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

		skip = (TextView) findViewById(R.id.skip);
		
		svg = (SVGImageView) findViewById(R.id.svg);
        svg.setImageAsset("svg/powered_by_android.svg");
        
        PackageInfo pinfo = getAppInfo(LauncherActivity.this);
        if (pinfo != null) {
            String version_nome = pinfo.versionName;
            int version_code = pinfo.versionCode;
            String version_text = String.format("v%s (Build %d)", version_nome, version_code);
            
            TextView version = (TextView) findViewById(R.id.version);
            
            version.setText(version_text);
        }
		
		startTimer();

    }

	private void startTimer() {

        mCountDown = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft = millisUntilFinished;
                updateText();
            }
            @Override
            public void onFinish() {
				skip.setText("Please wait...");

                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
            }

        }.start();

    }

    private void updateText() {
        int seconds = (int) (mTimeLeft / 1000) % 60;
        String timeLeftFormatted;
        if (seconds > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                                              "%02d", seconds);

            skip.setText("Skip in " + timeLeftFormatted + "s");

        }
    }
    
    public static PackageInfo getAppInfo(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}

