package com.nextwin.piano.mini;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressWarnings("deprecation")
public class GameMain extends AppCompatActivity {

    ImageView imgDo, imgRe, imgMi, imgFa, imgSol, imgLa, imgSi, imgDoo, imgNote1, imgNote2, imgNote3, imgBack;
    LinearLayout imgPianika, imgPiano, layNote;
    RelativeLayout layUtama, rlPiano;
    TextView teksNote;
    Button btnClose;
    private int ss1;
    private int ss2;
    private int ss3;
    private int ss4;
    private int ss5;
    private int ss6;
    private int ss7;
    private int ss8;
    MediaPlayer mpSound1 = new MediaPlayer();
    MediaPlayer mpSound2 = new MediaPlayer();
    MediaPlayer mpSound3 = new MediaPlayer();
    MediaPlayer mpSound4 = new MediaPlayer();
    MediaPlayer mpSound5 = new MediaPlayer();
    MediaPlayer mpSound6 = new MediaPlayer();
    MediaPlayer mpSound7 = new MediaPlayer();
    MediaPlayer mpSound8 = new MediaPlayer();
    MediaPlayer mediaPlayer = new MediaPlayer();
    SoundPool sp;
    int music = 1;
    boolean layakTampil = false, loadDB = false;
    boolean isFirst1 = false, isFirst2 = false, isFirst3 = false, isFirstDo = true, isFirstRe = true, isFirstMi = true, isFirstFa = true,
            isFirstSol = true, isFirstLa = true, isFirstSi = true, isFirstDoo = true;
    AdView mAdViewBottom, mAdViewTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);
        deklarasi();
        action();
    }

    @Override
    public void onPause() {
        // This method should be called in the parent Activity's onPause() method.
        if (mAdViewBottom != null) {
            mAdViewBottom.pause();
        }
        if (mAdViewTop != null) {
            mAdViewTop.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // This method should be called in the parent Activity's onResume() method.
        if (mAdViewBottom != null) {
            mAdViewBottom.resume();
        }
        if (mAdViewTop != null) {
            mAdViewTop.resume();
        }
        Menu.isUtama = true;
    }

    @Override
    public void onDestroy() {
        // This method should be called in the parent Activity's onDestroy() method.
        if (mAdViewBottom != null) {
            mAdViewBottom.destroy();
        }
        if (mAdViewTop != null) {
            mAdViewTop.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        cekKoneksi();
    }

    private void deklarasi() {
        mediaPlayer = MediaPlayer.create(GameMain.this, R.raw.tok);
        mpSound1 = MediaPlayer.create(GameMain.this, R.raw.hohner_soprano_melodica_01_c3);
        mpSound2 = MediaPlayer.create(GameMain.this, R.raw.hohner_soprano_melodica_02_d3);
        mpSound3 = MediaPlayer.create(GameMain.this, R.raw.hohner_soprano_melodica_03_e3);
        mpSound4 = MediaPlayer.create(GameMain.this, R.raw.hohner_soprano_melodica_04_f3);
        mpSound5 = MediaPlayer.create(GameMain.this, R.raw.hohner_soprano_melodica_05_g3);
        mpSound6 = MediaPlayer.create(GameMain.this, R.raw.hohner_soprano_melodica_06_a3);
        mpSound7 = MediaPlayer.create(GameMain.this, R.raw.hohner_soprano_melodica_07_b3);
        mpSound8 = MediaPlayer.create(GameMain.this, R.raw.hohner_soprano_melodica_08_c4);

        mAdViewBottom = findViewById(R.id.adview);
        mAdViewTop = findViewById(R.id.adviewTop);
        layNote = findViewById(R.id.linearTeks);
        layNote.setVisibility(View.GONE);
        teksNote = findViewById(R.id.teksNote);
        btnClose = findViewById(R.id.btnClose);
        layUtama = findViewById(R.id.layUtama);
        imgBack = findViewById(R.id.imgBack);
        imgDo = findViewById(R.id.imgDo);
        imgRe = findViewById(R.id.imgRe);
        imgMi = findViewById(R.id.imgMi);
        imgFa = findViewById(R.id.imgFa);
        imgSol = findViewById(R.id.imgSol);
        imgLa = findViewById(R.id.imgLa);
        imgSi = findViewById(R.id.imgSi);
        imgDoo = findViewById(R.id.imgDoo);
        imgNote1 = findViewById(R.id.imgNote1);
        imgNote2 = findViewById(R.id.imgNote2);
        imgNote3 = findViewById(R.id.imgNote3);
        imgPianika = findViewById(R.id.imgMusicPianika);
        imgPiano = findViewById(R.id.imgMusicPiano);
        imgNote1.setVisibility(View.GONE);
        imgNote2.setVisibility(View.GONE);
        imgNote3.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sp = new SoundPool.Builder().setMaxStreams(5).build();
        } else {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        setMusic();
        rlPiano = findViewById(R.id.rlPiano);
    }

    private void action() {
        mAdViewBottom.setVisibility(View.GONE);
        mAdViewTop.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("isBottomSabyan");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value != null) {
                    loadDB = true;
                    if (Integer.parseInt(value) == 1) {
                        mAdViewBottom.setVisibility(View.VISIBLE);
                        mAdViewTop.setVisibility(View.GONE);
                        cekJaringan(GameMain.this);
                        if (layakTampil) {
                            loadBawah();
                        }
                    } else {
                        mAdViewBottom.setVisibility(View.GONE);
                        mAdViewTop.setVisibility(View.VISIBLE);
                        cekJaringan(GameMain.this);
                        if (layakTampil) {
                            loadAtas();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                loadDB = true;
                mAdViewBottom.setVisibility(View.GONE);
                mAdViewTop.setVisibility(View.VISIBLE);
                cekJaringan(GameMain.this);
                if (layakTampil) {
                    loadAtas();
                }
            }
        });
        if (loadDB) {
            cekJaringan(GameMain.this);
            if (layakTampil) {
                loadAtas();
            }
        }

        teksNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                layNote.setVisibility(View.VISIBLE);
                teksNote.setVisibility(View.GONE);
                btnClose.setVisibility(View.VISIBLE);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                btnClose.setVisibility(View.GONE);
                layNote.setVisibility(View.GONE);
                teksNote.setVisibility(View.VISIBLE);
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                onBackPressed();
            }
        });
        if (Menu.tema == 0) {
            layUtama.setBackground(getResources().getDrawable(R.drawable.bg1));
        } else if (Menu.tema == 1) {
            layUtama.setBackground(getResources().getDrawable(R.drawable.bg2));
        } else if (Menu.tema == 2) {
            layUtama.setBackground(getResources().getDrawable(R.drawable.bg3));
        } else if (Menu.tema == 3) {
            layUtama.setBackground(getResources().getDrawable(R.drawable.bg4));
        } else if (Menu.tema == 4) {
            layUtama.setBackground(getResources().getDrawable(R.drawable.bg5));
        }

        if (Menu.temaPiano == 0) {
            rlPiano.setBackground(getResources().getDrawable(R.drawable.pianikamini));
        } else if (Menu.temaPiano == 1) {
            rlPiano.setBackground(getResources().getDrawable(R.drawable.pianikamini2));
        } else if (Menu.temaPiano == 2) {
            rlPiano.setBackground(getResources().getDrawable(R.drawable.pianikamini3));
        } else if (Menu.temaPiano == 3) {
            rlPiano.setBackground(getResources().getDrawable(R.drawable.pianikamini4));
        } else if (Menu.temaPiano == 4) {
            rlPiano.setBackground(getResources().getDrawable(R.drawable.pianikamini5));
        }

        imgPiano.setAlpha(1);
        imgPianika.setAlpha(0.5f);
        imgPiano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music = 1;
                imgPiano.setAlpha(1);
                imgPianika.setAlpha(0.5f);
                setMusic();
            }
        });

        imgPianika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music = 0;
                imgPiano.setAlpha(0.5f);
                imgPianika.setAlpha(1);
                setMusic();
            }
        });

        imgDo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() <= imgDo.getWidth()) { // TRUE SOUND
                    playSound1(ss1, event);
                } else if (event.getX() > imgDo.getWidth() && event.getX() <= imgDo.getWidth()*2) {
                    playSound2(ss2, event);
                } else if (event.getX() > imgDo.getWidth()*2 && event.getX() <= imgDo.getWidth()*3) {
                    playSound3(ss3, event);
                } else if (event.getX() > imgDo.getWidth()*3 && event.getX() <= imgDo.getWidth()*4) {
                    playSound4(ss4, event);
                } else if (event.getX() > imgDo.getWidth()*4 && event.getX() <= imgDo.getWidth()*5) {
                    playSound5(ss5, event);
                } else if (event.getX() > imgDo.getWidth()*5 && event.getX() <= imgDo.getWidth()*6) {
                    playSound6(ss6, event);
                } else if (event.getX() > imgDo.getWidth()*6 && event.getX() <= imgDo.getWidth()*7) {
                    playSound7(ss7, event);
                } else if (event.getX() > imgDo.getWidth()*7 && event.getX() <= imgDo.getWidth()*8) {
                    playSound8(ss8, event);
                }
                return true;
            }
        });

        imgRe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < 0) {
                    playSound1(ss1, event);
                } else if (event.getX() >=0 && event.getX() <= imgRe.getWidth()) { // TRUE SOUND
                    playSound2(ss2, event);
                } else if (event.getX() > imgRe.getWidth() && event.getX() <= imgRe.getWidth()*2) {
                    playSound3(ss3, event);
                } else if (event.getX() > imgRe.getWidth()*2 && event.getX() <= imgRe.getWidth()*3) {
                    playSound4(ss4, event);
                } else if (event.getX() > imgRe.getWidth()*3 && event.getX() <= imgRe.getWidth()*4) {
                    playSound5(ss5, event);
                } else if (event.getX() > imgRe.getWidth()*4 && event.getX() <= imgRe.getWidth()*5) {
                    playSound6(ss6, event);
                } else if (event.getX() > imgRe.getWidth()*5 && event.getX() <= imgRe.getWidth()*6) {
                    playSound7(ss7, event);
                } else if (event.getX() > imgRe.getWidth()*6 && event.getX() <= imgRe.getWidth()*7) {
                    playSound8(ss8, event);
                }
                return true;
            }
        });

        imgMi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < -imgDo.getWidth()) {
                    playSound1(ss1, event);
                } else if (event.getX() >= -imgDo.getWidth() && event.getX() < 0) {
                    playSound2(ss2, event);
                } else if (event.getX() >=0 && event.getX() <= imgDo.getWidth()) { // TRUE SOUND
                    playSound3(ss3, event);
                } else if (event.getX() > imgDo.getWidth() && event.getX() <= imgDo.getWidth()*2) {
                    playSound4(ss4, event);
                } else if (event.getX() > imgDo.getWidth()*2 && event.getX() <= imgDo.getWidth()*3) {
                    playSound5(ss5, event);
                } else if (event.getX() > imgDo.getWidth()*3 && event.getX() <= imgDo.getWidth()*4) {
                    playSound6(ss6, event);
                } else if (event.getX() > imgDo.getWidth()*4 && event.getX() <= imgDo.getWidth()*5) {
                    playSound7(ss7, event);
                } else if (event.getX() > imgDo.getWidth()*5 && event.getX() <= imgDo.getWidth()*6) {
                    playSound8(ss8, event);
                }
                return true;
            }
        });

        imgFa.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < -imgDo.getWidth()*2) {
                    playSound1(ss1, event);
                } else if (event.getX() >= -imgDo.getWidth()*2 && event.getX() < -imgDo.getWidth()) {
                    playSound2(ss2, event);
                } else if (event.getX() >= -imgDo.getWidth() && event.getX() < 0) {
                    playSound3(ss3, event);
                } else if (event.getX() >=0 && event.getX() <= imgDo.getWidth()) { // TRUE SOUND
                    playSound4(ss4, event);
                } else if (event.getX() > imgDo.getWidth() && event.getX() <= imgDo.getWidth()*2) {
                    playSound5(ss5, event);
                } else if (event.getX() > imgDo.getWidth()*2 && event.getX() <= imgDo.getWidth()*3) {
                    playSound6(ss6, event);
                } else if (event.getX() > imgDo.getWidth()*3 && event.getX() <= imgDo.getWidth()*4) {
                    playSound7(ss7, event);
                } else if (event.getX() > imgDo.getWidth()*4 && event.getX() <= imgDo.getWidth()*5) {
                    playSound8(ss8, event);
                }
                return true;
            }
        });

        imgSol.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < -imgDo.getWidth()*3) {
                    playSound1(ss1, event);
                } else if (event.getX() >= -imgDo.getWidth()*3 && event.getX() < -imgDo.getWidth()*2) {
                    playSound2(ss2, event);
                } else if (event.getX() >= -imgDo.getWidth()*2 && event.getX() < -imgDo.getWidth()) {
                    playSound3(ss3, event);
                } else if (event.getX() >= -imgDo.getWidth() && event.getX() < 0) {
                    playSound4(ss4, event);
                } else if (event.getX() >=0 && event.getX() <= imgDo.getWidth()) { // TRUE SOUND
                    playSound5(ss5, event);
                } else if (event.getX() > imgDo.getWidth() && event.getX() <= imgDo.getWidth()*2) {
                    playSound6(ss6, event);
                } else if (event.getX() > imgDo.getWidth()*2 && event.getX() <= imgDo.getWidth()*3) {
                    playSound7(ss7, event);
                } else if (event.getX() > imgDo.getWidth()*3 && event.getX() <= imgDo.getWidth()*4) {
                    playSound8(ss8, event);
                }
                return true;
            }
        });

        imgLa.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < -imgDo.getWidth()*4) {
                    playSound1(ss1, event);
                } else if (event.getX() >= -imgDo.getWidth()*4 && event.getX() < -imgDo.getWidth()*3) {
                    playSound2(ss2, event);
                } else if (event.getX() >= -imgDo.getWidth()*3 && event.getX() < -imgDo.getWidth()*2) {
                    playSound3(ss3, event);
                } else if (event.getX() >= -imgDo.getWidth()*2 && event.getX() < -imgDo.getWidth()) {
                    playSound4(ss4, event);
                } else if (event.getX() >= -imgDo.getWidth() && event.getX() < 0) {
                    playSound5(ss5, event);
                } else if (event.getX() >=0 && event.getX() <= imgDo.getWidth()) { // TRUE SOUND
                    playSound6(ss6, event);
                } else if (event.getX() > imgDo.getWidth() && event.getX() <= imgDo.getWidth()*2) {
                    playSound7(ss7, event);
                } else if (event.getX() > imgDo.getWidth()*2 && event.getX() <= imgDo.getWidth()*3) {
                    playSound8(ss8, event);
                }
                return true;
            }
        });

        imgSi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < -imgDo.getWidth()*5) {
                    playSound1(ss1, event);
                } else if (event.getX() >= -imgDo.getWidth()*5 && event.getX() < -imgDo.getWidth()*4) {
                    playSound2(ss2, event);
                } else if (event.getX() >= -imgDo.getWidth()*4 && event.getX() < -imgDo.getWidth()*3) {
                    playSound3(ss3, event);
                } else if (event.getX() >= -imgDo.getWidth()*3 && event.getX() < -imgDo.getWidth()*2) {
                    playSound4(ss4, event);
                } else if (event.getX() >= -imgDo.getWidth()*2 && event.getX() < -imgDo.getWidth()) {
                    playSound5(ss5, event);
                } else if (event.getX() >= -imgDo.getWidth() && event.getX() < 0) {
                    playSound6(ss6, event);
                } else if (event.getX() >=0 && event.getX() <= imgDo.getWidth()) { // TRUE SOUND
                    playSound7(ss7, event);
                } else if (event.getX() > imgDo.getWidth() && event.getX() <= imgDo.getWidth()*2) {
                    playSound8(ss8, event);
                }
                return true;
            }
        });

        imgDoo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < -imgDo.getWidth()*6) {
                    playSound1(ss1, event);
                } else if (event.getX() >= -imgDo.getWidth()*6 && event.getX() < -imgDo.getWidth()*5) {
                    playSound2(ss2, event);
                } else if (event.getX() >= -imgDo.getWidth()*5 && event.getX() < -imgDo.getWidth()*4) {
                    playSound3(ss3, event);
                } else if (event.getX() >= -imgDo.getWidth()*4 && event.getX() < -imgDo.getWidth()*3) {
                    playSound4(ss4, event);
                } else if (event.getX() >= -imgDo.getWidth()*3 && event.getX() < -imgDo.getWidth()*2) {
                    playSound5(ss5, event);
                } else if (event.getX() >= -imgDo.getWidth()*2 && event.getX() < -imgDo.getWidth()) {
                    playSound6(ss6, event);
                } else if (event.getX() >= -imgDo.getWidth() && event.getX() < 0) {
                    playSound7(ss7, event);
                } else if (event.getX() >=0 && event.getX() <= imgDo.getWidth()) { // TRUE SOUND
                    playSound8(ss8, event);
                }
                return true;
            }
        });
    }

    private void setMusic() {
        if (music == 0) {
            ss1 = sp.load(GameMain.this, R.raw.hohner_soprano_melodica_01_c3, 1);
            ss2 = sp.load(GameMain.this, R.raw.hohner_soprano_melodica_02_d3, 1);
            ss3 = sp.load(GameMain.this, R.raw.hohner_soprano_melodica_03_e3, 1);
            ss4 = sp.load(GameMain.this, R.raw.hohner_soprano_melodica_04_f3, 1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ss5 = sp.load(GameMain.this, R.raw.hohner_soprano_melodica_05_g3, 1);
                    ss6 = sp.load(GameMain.this, R.raw.hohner_soprano_melodica_06_a3, 1);
                    ss7 = sp.load(GameMain.this, R.raw.hohner_soprano_melodica_07_b3, 1);
                    ss8 = sp.load(GameMain.this, R.raw.hohner_soprano_melodica_08_c4, 1);
                }
            }, 100);
        } else {
            ss1 = sp.load(GameMain.this, R.raw.w40, 1);
            ss2 = sp.load(GameMain.this, R.raw.w41, 1);
            ss3 = sp.load(GameMain.this, R.raw.w42, 1);
            ss4 = sp.load(GameMain.this, R.raw.w43, 1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ss5 = sp.load(GameMain.this, R.raw.w44, 1);
                    ss6 = sp.load(GameMain.this, R.raw.w45, 1);
                    ss7 = sp.load(GameMain.this, R.raw.w46, 1);
                    ss8 = sp.load(GameMain.this, R.raw.w50, 1);
                }
            }, 100);
        }
    }

    private void playSound1(int id, MotionEvent motionEvent) {

        if (motionEvent == null || motionEvent.getAction() == 0 || motionEvent.getAction() == 2 && isFirstDo) {
            isFirstMi = true;
            isFirstRe = true;
            isFirstDo = false;
            imgDo.setBackground(getResources().getDrawable(R.drawable.do2));
            imgRe.setBackground(getResources().getDrawable(R.drawable.re1));
            if (music == 0) {
                mpSound1.start();
            } else {
                sp.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
            }
            imgNote1.setVisibility(View.VISIBLE);
            if (isFirst1) {
                isFirst1 = false;
                imgNote1.setImageDrawable(getResources().getDrawable(R.drawable.musical_note3));
            } else {
                isFirst1 = true;
                imgNote1.setImageDrawable(getResources().getDrawable(R.drawable.musical_note));
            }
            Animation animation = AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin);
            imgNote1.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgNote1.setVisibility(View.GONE);
                    imgNote1.startAnimation(AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin2));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3 ) {
            isFirstMi = true;
            isFirstRe = true;
            isFirstDo = true;
            if (music != 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sp.autoPause();
                    }
                }, 2000);
            }
            imgDo.setBackground(getResources().getDrawable(R.drawable.do1));
        }
    }

    private void playSound2(int id, MotionEvent motionEvent) {

        if (motionEvent == null || motionEvent.getAction() == 0 || motionEvent.getAction() == 2 && isFirstRe) {
            isFirstMi = true;
            isFirstRe = false;
            isFirstDo = true;
            imgMi.setBackground(getResources().getDrawable(R.drawable.mi1));
            imgRe.setBackground(getResources().getDrawable(R.drawable.re2));
            imgDo.setBackground(getResources().getDrawable(R.drawable.do1));
            if (music == 0) {
                mpSound2.start();
            } else {
                sp.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
            }
            imgNote1.setVisibility(View.VISIBLE);
            if (isFirst1) {
                isFirst1 = false;
                imgNote1.setImageDrawable(getResources().getDrawable(R.drawable.musical_note3));
            } else {
                isFirst1 = true;
                imgNote1.setImageDrawable(getResources().getDrawable(R.drawable.musical_note));
            }
            Animation animation = AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin);
            imgNote1.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgNote1.setVisibility(View.GONE);
                    imgNote1.startAnimation(AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin2));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            isFirstMi = true;
            isFirstRe = true;
            isFirstDo = true;
            if (music != 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sp.autoPause();
                    }
                }, 2000);
            }
            imgRe.setBackground(getResources().getDrawable(R.drawable.re1));
        }
    }

    private void playSound3(int id, MotionEvent motionEvent) {
        if (motionEvent == null || motionEvent.getAction() == 0  || motionEvent.getAction() == 2 && isFirstMi) {
            isFirstFa = true;
            isFirstMi = false;
            isFirstRe = true;
            imgFa.setBackground(getResources().getDrawable(R.drawable.fa1));
            imgMi.setBackground(getResources().getDrawable(R.drawable.mi2));
            imgRe.setBackground(getResources().getDrawable(R.drawable.re1));
            if (music == 0) {
                mpSound3.start();
            } else {
                sp.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
            }
            imgNote1.setVisibility(View.VISIBLE);
            if (isFirst1) {
                isFirst1 = false;
                imgNote1.setImageDrawable(getResources().getDrawable(R.drawable.musical_note3));
            } else {
                isFirst1 = true;
                imgNote1.setImageDrawable(getResources().getDrawable(R.drawable.musical_note));
            }
            Animation animation = AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin);
            imgNote1.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgNote1.setVisibility(View.GONE);
                    imgNote1.startAnimation(AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin2));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            isFirstMi = true;
            isFirstRe = true;
            isFirstDo = true;
            imgMi.setBackground(getResources().getDrawable(R.drawable.mi1));
        }
    }

    private void playSound4(int id, MotionEvent motionEvent) {
        if (motionEvent == null || motionEvent.getAction() == 0  || motionEvent.getAction() == 2 && isFirstFa) {
            isFirstSol = true;
            isFirstFa = false;
            isFirstMi = true;
            imgSol.setBackground(getResources().getDrawable(R.drawable.sol1));
            imgFa.setBackground(getResources().getDrawable(R.drawable.fa2));
            imgMi.setBackground(getResources().getDrawable(R.drawable.mi1));
            imgNote2.setVisibility(View.VISIBLE);
            if (isFirst2) {
                isFirst2 = false;
                imgNote2.setImageDrawable(getResources().getDrawable(R.drawable.musical_note4));
            } else {
                isFirst2 = true;
                imgNote2.setImageDrawable(getResources().getDrawable(R.drawable.musical_note1));
            }
            if (music == 0) {
                mpSound4.start();
            } else {
                sp.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
            }
            Animation animation = AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin);
            imgNote2.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgNote2.setVisibility(View.GONE);
                    imgNote2.startAnimation(AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin2));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            imgFa.setBackground(getResources().getDrawable(R.drawable.fa1));
        }
    }

    private void playSound5(int id, MotionEvent motionEvent) {
        if (motionEvent == null || motionEvent.getAction() == 0  || motionEvent.getAction() == 2 && isFirstSol) {
            isFirstLa = true;
            isFirstSol = false;
            isFirstFa = true;
            imgLa.setBackground(getResources().getDrawable(R.drawable.la1));
            imgSol.setBackground(getResources().getDrawable(R.drawable.sol2));
            imgFa.setBackground(getResources().getDrawable(R.drawable.fa1));
            imgNote2.setVisibility(View.VISIBLE);
            if (isFirst2) {
                isFirst2 = false;
                imgNote2.setImageDrawable(getResources().getDrawable(R.drawable.musical_note4));
            } else {
                isFirst2 = true;
                imgNote2.setImageDrawable(getResources().getDrawable(R.drawable.musical_note1));
            }
            if (music == 0) {
                mpSound5.start();
            } else {
                sp.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
            }
            Animation animation = AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin);
            imgNote2.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgNote2.setVisibility(View.GONE);
                    imgNote2.startAnimation(AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin2));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {

            imgSol.setBackground(getResources().getDrawable(R.drawable.sol1));
        }
    }

    private void playSound6(int id, MotionEvent motionEvent) {

        if (motionEvent == null || motionEvent.getAction() == 0  || motionEvent.getAction() == 2 && isFirstLa) {
            isFirstSi = true;
            isFirstLa = false;
            isFirstSol = true;
            imgSi.setBackground(getResources().getDrawable(R.drawable.si));
            imgLa.setBackground(getResources().getDrawable(R.drawable.la2));
            imgSol.setBackground(getResources().getDrawable(R.drawable.sol1));
            imgNote2.setVisibility(View.VISIBLE);
            if (isFirst2) {
                isFirst2 = false;
                imgNote2.setImageDrawable(getResources().getDrawable(R.drawable.musical_note4));
            } else {
                isFirst2 = true;
                imgNote2.setImageDrawable(getResources().getDrawable(R.drawable.musical_note1));
            }
            if (music == 0) {
                mpSound6.start();
            } else {
                sp.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
            }
            Animation animation = AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin);
            imgNote2.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgNote2.setVisibility(View.GONE);
                    imgNote2.startAnimation(AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin2));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {

            imgLa.setBackground(getResources().getDrawable(R.drawable.la1));
        }
    }

    private void playSound7(int id, MotionEvent motionEvent) {

        if (motionEvent == null || motionEvent.getAction() == 0  || motionEvent.getAction() == 2 && isFirstSi) {
            isFirstDoo = true;
            isFirstSi = false;
            isFirstLa = true;
            imgDoo.setBackground(getResources().getDrawable(R.drawable.doo1));
            imgSi.setBackground(getResources().getDrawable(R.drawable.si2));
            imgLa.setBackground(getResources().getDrawable(R.drawable.la1));
            imgNote3.setVisibility(View.VISIBLE);
            if (isFirst3) {
                isFirst3 = false;
                imgNote3.setImageDrawable(getResources().getDrawable(R.drawable.musical_note5));
            } else {
                isFirst3 = true;
                imgNote3.setImageDrawable(getResources().getDrawable(R.drawable.musical_note2));
            }
            Animation animation = AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin);
            imgNote3.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgNote3.setVisibility(View.GONE);
                    imgNote3.startAnimation(AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin2));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (music == 0) {
                mpSound7.start();
            } else {
                sp.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            imgSi.setBackground(getResources().getDrawable(R.drawable.si));
        }
    }

    private void playSound8(int id, MotionEvent motionEvent) {

        if (motionEvent == null || motionEvent.getAction() == 0  || motionEvent.getAction() == 2 && isFirstDoo) {
            isFirstDoo = false;
            isFirstSi = true;
            imgDoo.setBackground(getResources().getDrawable(R.drawable.doo2));
            imgSi.setBackground(getResources().getDrawable(R.drawable.si));
            imgNote3.setVisibility(View.VISIBLE);
            if (isFirst3) {
                isFirst3 = false;
                imgNote3.setImageDrawable(getResources().getDrawable(R.drawable.musical_note5));
            } else {
                isFirst3 = true;
                imgNote3.setImageDrawable(getResources().getDrawable(R.drawable.musical_note2));
            }
            if (music == 0) {
                mpSound8.start();
            } else {
                sp.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
            }
            Animation animation = AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin);
            imgNote3.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgNote3.setVisibility(View.GONE);
                    imgNote3.startAnimation(AnimationUtils.loadAnimation(GameMain.this, R.anim.zoomin2));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            imgDoo.setBackground(getResources().getDrawable(R.drawable.doo1));
        }
    }

    void loadAtas() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdViewTop.loadAd(adRequest);
        mAdViewTop.setAdListener(new AdListener() {

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

    void loadBawah() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdViewBottom.loadAd(adRequest);
        mAdViewBottom.setAdListener(new AdListener() {

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

    private void iklan() {
        final InterstitialAd mInterstitialAd = new InterstitialAd(GameMain.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5730449577374867/3898444925");
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                finish();
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void cekKoneksi() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr != null ? conMgr.getActiveNetworkInfo() : null;
        if (netInfo == null) {
            finish();
        } else {
            finish();
            cekJaringan(GameMain.this);
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
