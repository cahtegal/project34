package com.nextwin.piano.mini;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Splash extends AppCompatActivity {

    ImageView logoJadis;
    RelativeLayout rlCahTegal;
    AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mAdView = findViewById(R.id.adview);
        logoJadis = findViewById(R.id.logo_pianolittleponi);
        rlCahTegal = findViewById(R.id.rlCahtegal);
        logoJadis.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(Splash.this, R.anim.blink);
        rlCahTegal.setAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rlCahTegal.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLogo();
                    }
                },200);
            }

        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }
    @Override
    public void onPause() {
        // This method should be called in the parent Activity's onPause() method.
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // This method should be called in the parent Activity's onDestroy() method.
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void showLogo() {
        Animation animation = AnimationUtils.loadAnimation(Splash.this, R.anim.blink);
        logoJadis.setAnimation(animation);
        logoJadis.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(Splash.this,Menu.class));
            }
        },3000);
    }
}
