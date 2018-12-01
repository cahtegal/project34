package com.nextwin.piano.mini;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Menu extends AppCompatActivity {

    ImageView imgPlay, imgShare, imgRateUs, imgTemaDarbuka, imgPianoTheme;
    String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
    public static boolean iklanOpen = false, isUtama = false;
    AdView mAdView;
    public static int tema = 1, temaPiano = 0;
    MediaPlayer mediaPlayer = new MediaPlayer();
    boolean layakTampil = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        declaration();
        action();
        startAnimationPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        iklanOpen = false;
        isUtama = false;
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

    private void declaration() {
        imgPianoTheme = findViewById(R.id.imgPianoTheme);
        mediaPlayer = MediaPlayer.create(Menu.this, R.raw.tok);
        mAdView = findViewById(R.id.adview);
        imgPlay = findViewById(R.id.imgPlay);
        imgTemaDarbuka = findViewById(R.id.imgDarbukaTheme);
        imgRateUs = findViewById(R.id.imgRateUs);
        imgShare = findViewById(R.id.imgShare);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        cekJaringan(Menu.this);
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
        imgPianoTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                imgPianoTheme.startAnimation(AnimationUtils.loadAnimation(Menu.this, R.anim.zoom_out2));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Menu.this, TemaPiano.class));
                    }
                }, 300);

            }
        });
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                imgPlay.startAnimation(AnimationUtils.loadAnimation(Menu.this, R.anim.zoom_out2));
                cekKoneksi();
            }
        });
        imgTemaDarbuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                imgTemaDarbuka.startAnimation(AnimationUtils.loadAnimation(Menu.this, R.anim.zoom_out2));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Menu.this, TemaPony.class));
                    }
                }, 300);
            }
        });
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                imgShare.startAnimation(AnimationUtils.loadAnimation(Menu.this, R.anim.zoom_out2));
                final Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Play Piano Mini Sabyan with funny and happy, Get it on Play Store http://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(sendIntent);
                    }
                }, 300);
            }
        });
        imgRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                imgRateUs.startAnimation(AnimationUtils.loadAnimation(Menu.this, R.anim.zoom_out2));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                }, 300);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.start();
        dialogOut();
    }

    private void startAnimationPlay() {
        final AnimatorSet animatorSet1 = new AnimatorSet();
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgPlay, "scaleY", 0.6f);
        scaleY.setDuration(200);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(imgPlay, "scaleY", 1f);
        scaleYBack.setDuration(500);
        scaleYBack.setInterpolator(new BounceInterpolator());
        animatorSet1.setStartDelay(600);
        animatorSet1.playSequentially(scaleY, scaleYBack);
        animatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet1.setStartDelay(500);
                animatorSet1.start();
            }
        });
        imgPlay.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet1.start();
    }

    private void dialogOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Menu.this);
        alertDialogBuilder.setTitle("Quit");
        alertDialogBuilder
                .setMessage("You want to quit now ?")
                .setCancelable(false)
                .setPositiveButton("Yes, I want play again later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("No, I like this game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void iklan() {
        LayoutInflater inflater = LayoutInflater.from(Menu.this);
        @SuppressLint("InflateParams")
        View dialog_layout = inflater.inflate(R.layout.loading, null);

        AlertDialog.Builder dialLoad = new AlertDialog.Builder(Menu.this);
        dialLoad.setView(dialog_layout);
        final AlertDialog theDialog = dialLoad.create();
        theDialog.show();

        final InterstitialAd mInterstitialAd = new InterstitialAd(Menu.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5730449577374867/3898444925");
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    if (!iklanOpen) {
                        iklanOpen = true;
                    }
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (!isUtama) {
                    try {
                        theDialog.dismiss();
                    } catch (IllegalArgumentException exp) {
                        exp.printStackTrace();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                    startActivity(new Intent(Menu.this, GameMain.class));
                }
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (!iklanOpen) {
                    iklanOpen = true;
                    if (!isUtama) {
                        try {
                            theDialog.dismiss();
                        } catch (IllegalArgumentException exp) {
                            exp.printStackTrace();
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                        startActivity(new Intent(Menu.this, GameMain.class));
                    }
                }
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!iklanOpen) {
                    iklanOpen = true;
                    if (!isUtama) {
                        try {
                            theDialog.dismiss();
                        } catch (IllegalArgumentException exp) {
                            exp.printStackTrace();
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                        startActivity(new Intent(Menu.this, GameMain.class));
                    }
                }
            }
        }, 3000);
    }

    private void cekKoneksi() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr != null ? conMgr.getActiveNetworkInfo() : null;
        if (netInfo == null) {
            startActivity(new Intent(Menu.this, GameMain.class));
        } else {
            cekJaringan(Menu.this);
            if (layakTampil) {
                iklan();
            }
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
        if (info != null) {
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
