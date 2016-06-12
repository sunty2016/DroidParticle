package com.sunty.droidparticle.demo2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sunty.droidparticle.ParticleSystem;
import com.sunty.droidparticle.ParticleSystemConfig;
import com.sunty.droidparticle.ParticleSystemView;

public class MainActivity extends Activity {

    ParticleSystemView mPtcSysView;
    ParticleSystem mPtcSys1;
    ParticleSystem mPtcSys2;
    ParticleSystem mPtcSys3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPtcSysView = (ParticleSystemView) findViewById(R.id.canvas);
        mPtcSys1 = mPtcSysView.createParticleSystem();
        mPtcSys2 = mPtcSysView.createParticleSystem();
        mPtcSys3 = mPtcSysView.createParticleSystem();

        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ptc16);
        Bitmap img = drawable.getBitmap();

        mPtcSys1.setPtcBlend(1);
        mPtcSys1.setFps(40);
        mPtcSys1.setPps(30);
        mPtcSys1.setPtcImage(img);
        mPtcSys1.setConfig(createConfig(1));


        mPtcSys2.setPtcBlend(1);
        mPtcSys2.setFps(40);
        mPtcSys2.setPps(30);
        mPtcSys2.setPtcImage(img);
        mPtcSys2.setConfig(createConfig(2));


        mPtcSys3.setPtcBlend(1);
        mPtcSys3.setFps(40);
        mPtcSys3.setPps(30);
        mPtcSys3.setPtcImage(img);
        mPtcSys3.setConfig(createConfig(3));

        mPtcSysView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                mPtcSys1.setPtcPosition(v.getWidth() / 6, v.getHeight() / 2);
                mPtcSys2.setPtcPosition(v.getWidth() / 2, v.getHeight() / 2);
                mPtcSys3.setPtcPosition(v.getWidth() * 5 / 6, v.getHeight() / 2);
                mPtcSys1.start();
                mPtcSys2.start();
                mPtcSys3.start();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPtcSysView.releaseParticleSystem(mPtcSys1);
        mPtcSysView.releaseParticleSystem(mPtcSys2);
        mPtcSysView.releaseParticleSystem(mPtcSys3);
    }
    static private ParticleSystemConfig createConfig(int id) {
        ParticleSystemConfig config = new ParticleSystemConfig();
        config.duration.set(1000, 0);
        config.theta.set(270, 15);
        config.startVelocity.set(400, 0);
        config.endVelocity.set(400, 0);
        config.startAngularRate.set(0, 0);
        config.endAngularRate.set(0, 0);
        config.startSpinRate.set(360, 0);
        config.endSpinRate.set(360, 0);
        config.startScale.set(1, 0);
        config.endScale.set(1.5f, 0);
        config.startAlpha.set(1, 0);
        config.endAlpha.set(0.75f, 0);
        if (id == 1) {
            config.startRed.set(1, 0);
            config.endRed.set(1, 0);
            config.startGreen.set(0, 0);
            config.endGreen.set(1, 0);
            config.startBlue.set(0, 0);
            config.endBlue.set(0, 0);
        } else if (id == 2) {
            config.startRed.set(0, 0);
            config.endRed.set(0, 0);
            config.startGreen.set(1, 0);
            config.endGreen.set(1, 0);
            config.startBlue.set(0, 0);
            config.endBlue.set(1, 0);
        } else if (id == 3) {
            config.startRed.set(0, 0);
            config.endRed.set(1, 0);
            config.startGreen.set(0, 0);
            config.endGreen.set(0, 0);
            config.startBlue.set(1, 0);
            config.endBlue.set(1, 0);
        }
        return config;
    }
}
