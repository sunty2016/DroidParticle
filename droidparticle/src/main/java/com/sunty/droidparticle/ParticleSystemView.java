package com.sunty.droidparticle;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by sunty on 16-5-9.
 */
public class ParticleSystemView extends View {

    final private HandlerThread mHandlerThread;
    final private Handler mHandler;
    final private ArrayList<ParticleSystem> mParticleSystems = new ArrayList<>();


    public ParticleSystemView(Context context) {
        super(context);
        mHandlerThread = new HandlerThread(this.toString());
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public ParticleSystemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandlerThread = new HandlerThread(this.toString());
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public ParticleSystem createParticleSystem() {
        final ParticleSystem sys = new ParticleSystem(this);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mParticleSystems) {
                    mParticleSystems.add(sys);
                }
            }
        });
        return sys;
    }

    public void releaseParticleSystem(final ParticleSystem sys) {
        sys.stop();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mParticleSystems) {
                    mParticleSystems.remove(sys);
                }
            }
        });
    }

    Handler getParticleSystemHandler() {
        return mHandler;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (mParticleSystems) {
            for (ParticleSystem sys : mParticleSystems) {
                sys.draw(canvas);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
