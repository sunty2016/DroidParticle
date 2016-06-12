package com.sunty.droidparticle.demo1;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sunty.droidparticle.ParticleSystem;
import com.sunty.droidparticle.ParticleSystemConfig;
import com.sunty.droidparticle.ParticleSystemView;

public class MainActivity extends Activity implements
        ParticleSystem.OnPerformanceUpdateListener,
        ParticleSystem.OnStateChangeListener,
        SettingsFragment.OnConfigUpdateListener {

    static class OtherConfig {
        public int image = 0;
        public int blend = 0;
        public int fps = 40;
        public int pps = 100;
    }

    ParticleSystemView mPtcSysView;
    ParticleSystem mPtcSys;
    TextView mTvInfo;
    ImageView mIvEdit;
    ImageView mIvPlay;
    int mState = ParticleSystem.STATE_READY;
    SettingsFragment mSettingsFragment = null;
    ParticleSystemConfig mConfig = createConfig();
    private OtherConfig mOtherConfig = new OtherConfig();
    private Bitmap[] mImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initImages();

        mPtcSysView = (ParticleSystemView)findViewById(R.id.canvas);
        mTvInfo = (TextView) findViewById(R.id.tv_info);
        mIvEdit = (ImageView) findViewById(R.id.iv_edit);

        mPtcSys = mPtcSysView.createParticleSystem();
        mPtcSys.setOnPerformanceUpdateListener(this);
        mPtcSys.setOnStateChangeListener(this);
        mPtcSysView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                int x = v.getWidth() / 2;
                int y = v.getHeight() / 2;
                mPtcSys.setPtcPosition(x, y);
            }
        });

        mIvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettingsFragment == null || !mSettingsFragment.isAdded()) {
                    mSettingsFragment = new SettingsFragment();
                    FragmentManager fm = getFragmentManager();
                    String jsonConfig = new Gson().toJson(mConfig);
                    Bundle args = new Bundle();
                    args.putString("config", jsonConfig);
                    args.putInt("image", mOtherConfig.image);
                    args.putInt("blend", mOtherConfig.blend);
                    args.putInt("fps", mOtherConfig.fps);
                    args.putInt("pps", mOtherConfig.pps);
                    mSettingsFragment.setArguments(args);
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(R.id.container, mSettingsFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    FragmentManager fm = getFragmentManager();
                    fm.popBackStack();
                }
            }
        });

        mIvPlay = (ImageView) findViewById(R.id.iv_play);
        mIvPlay.setAlpha(0.6f);
        mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState == ParticleSystem.STATE_READY) {
                    mPtcSys.start();
                } else if (mState == ParticleSystem.STATE_BUSY) {
                    mPtcSys.stop();
                }
            }
        });


        loadConfig();
        mPtcSys.setPtcImage(mImages[mOtherConfig.image]);
        mPtcSys.setPtcBlend(mOtherConfig.blend);
        mPtcSys.setConfig(mConfig);
        mPtcSys.setPps(mOtherConfig.fps);
        mPtcSys.setPps(mOtherConfig.pps);
    }
    @Override
    protected  void onDestroy() {
        super.onDestroy();
        saveConfig();
        mPtcSys.stop();
        mPtcSysView.releaseParticleSystem(mPtcSys);
    }

    private void initImages() {
        int[] ids = new int[] {
                R.drawable.ptc01,
                R.drawable.ptc02,
                R.drawable.ptc03,
                R.drawable.ptc04,
                R.drawable.ptc05,
                R.drawable.ptc06,
                R.drawable.ptc07,
                R.drawable.ptc08,
                R.drawable.ptc09,
                R.drawable.ptc10,
                R.drawable.ptc11,
                R.drawable.ptc12,
                R.drawable.ptc13,
                R.drawable.ptc14,
                R.drawable.ptc15,
                R.drawable.ptc16,
        };
        mImages = new Bitmap[ids.length];
        for (int i = 0; i < mImages.length; ++i) {
            BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(ids[i]);
            mImages[i] = bd.getBitmap();
        }
    }

    static private ParticleSystemConfig createConfig() {
        ParticleSystemConfig config = new ParticleSystemConfig();
        config.duration.set(1000, 0);
        config.startVelocity.set(800, 0);
        config.endVelocity.set(800, 0);
        config.startAngularRate.set(360, 0);
        config.endAngularRate.set(360, 0);
        config.startSpinRate.set(360, 0);
        config.endSpinRate.set(360, 0);
        config.startScale.set(1, 0);
        config.endScale.set(1, 0);
        config.startAlpha.set(0.8f, 0);
        config.endAlpha.set(0.8f, 0);
        config.startRed.set(0, 0);
        config.endRed.set(0, 0);
        config.startGreen.set(1, 0);
        config.endGreen.set(0, 0);
        config.startBlue.set(0, 0);
        config.endBlue.set(1, 0);
        return config;
    }

    private void saveConfig() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ptc", new Gson().toJson(mConfig));
        editor.putString("other", new Gson().toJson(mOtherConfig));
        editor.commit();
    }

    private void loadConfig() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String jsonPtcConfig = pref.getString("ptc", null);
        if (jsonPtcConfig != null) {
            mConfig = new Gson().fromJson(jsonPtcConfig, ParticleSystemConfig.class);
        }
        String jsonOtherConfig = pref.getString("other", null);
        if (jsonOtherConfig != null) {
            mOtherConfig = new Gson().fromJson(jsonOtherConfig, OtherConfig.class);
        }
    }

    @Override
    public void onPerformanceUpdate(final long fps, final long pps) {
        mTvInfo.post(new Runnable() {
            @Override
            public void run() {
                String info = String.format("FPS: %d  PPS: %d", fps, pps);
                mTvInfo.setText(info);
            }
        });
    }

    @Override
    public void onConfigUpdate(String jsonConfig) {
        mConfig = new Gson().fromJson(jsonConfig, ParticleSystemConfig.class);
        mPtcSys.setConfig(mConfig);
    }

    @Override
    public void onOtherUpdate(String key, int value) {
        if (key.equals("image")) {
            mOtherConfig.image = value % mImages.length;
            mPtcSys.setPtcImage(mImages[mOtherConfig.image]);
        } else if (key.equals("blend")) {
            mOtherConfig.blend = value;
            mPtcSys.setPtcBlend(mOtherConfig.blend);
        } else if (key.equals("fps")) {
            mOtherConfig.fps = value;
            mPtcSys.setFps(mOtherConfig.fps);
        } else if (key.equals("pps")) {
            mOtherConfig.pps = value;
            mPtcSys.setPps(mOtherConfig.pps);
        }
    }

    @Override
    public void onStateChanged(final int state, final int oldState) {
        mIvPlay.post(new Runnable() {
            @Override
            public void run() {
                mState = state;
                if (mState == ParticleSystem.STATE_READY) {
                    mIvPlay.setAlpha(0.6f);
                    Drawable drawable = getResources().getDrawable(android.R.drawable.ic_media_play);
                    mIvPlay.setImageDrawable(drawable);
                } else if (mState == ParticleSystem.STATE_BUSY) {
                    mIvPlay.setAlpha(0.6f);
                    Drawable drawable = getResources().getDrawable(android.R.drawable.ic_media_pause);
                    mIvPlay.setImageDrawable(drawable);
                } else if (mState == ParticleSystem.STATE_STOPPING) {
                    mIvPlay.setAlpha(0.25f);
                }
            }
        });
    }
}
