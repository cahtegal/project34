package com.nextwin.piano.mini;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TemaPony extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    Button btnOk;
    private int[] layouts;
    ImageView imgBack;
    MediaPlayer mpSound1 = new MediaPlayer();
    AdView mAdView;
    boolean layakTampil = false;
    int tema = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tema_pony);
        deklarasi();
        action();
    }

    @Override
    public void onBackPressed() {
        mpSound1.start();
        finish();
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
    public void onResume() {
        super.onResume();
        // This method should be called in the parent Activity's onResume() method.
        if (mAdView != null) {
            mAdView.resume();
        }
        Menu.isUtama = true;

    }

    @Override
    public void onDestroy() {
        // This method should be called in the parent Activity's onDestroy() method.
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void deklarasi() {
        mAdView = findViewById(R.id.adView);
        mpSound1 = MediaPlayer.create(TemaPony.this,R.raw.tok);
        imgBack = findViewById(R.id.imgBack);
        btnOk = findViewById(R.id.btnOk);
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        cekJaringan(TemaPony.this);
        if (layakTampil) {
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }
            });
        }
    }

    private void action() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpSound1.start();
                Menu.tema = tema;
                finish();
            }
        });
        layouts = new int[]{
                R.layout.poni_1,
                R.layout.poni_2,
                R.layout.poni_3,
                R.layout.poni_4,
                R.layout.poni_5};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {
            tema = position;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {
        }

        @SuppressWarnings("ConstantConditions")
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = null;
            if (layoutInflater != null) {
                view = layoutInflater.inflate(layouts[position], container, false);
            }
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getActiveNetworkInfo();
        } else {
            return null;
        }
    }

    private void cekJaringan(Activity activity) {
        NetworkInfo info = getNetworkInfo(activity);
        if (info !=null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                layakTampil = true;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        // ~ 50-100 kbps
                        layakTampil = false;
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        // ~ 14-64 kbps
                        layakTampil = false;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        // ~ 50-100 kbps
                        layakTampil = false;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        // ~ 400-1000 kbps
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        // ~ 600-1400 kbps
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        // ~ 100 kbps
                        layakTampil = false;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        // ~ 2-14 Mbps
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        // ~ 700-1700 kbps
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        // ~ 1-23 Mbps
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        // ~ 400-7000 kbps
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                        // ~ 1-2 Mbps
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                        // ~ 5 Mbps
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                        // ~ 10-20 Mbps
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                        // ~ 25 kbps
                        layakTampil = false;
                    case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                        // ~ 10+ Mbps
                        // Unknown
                        layakTampil = true;
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        layakTampil = true;
                }
            }
        }
    }
}
