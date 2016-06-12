package com.sunty.droidparticle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.view.View;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Created by sunty on 16-6-12.
 */
public class ParticleSystem {

    static final public int STATE_READY = 0;
    static final public int STATE_BUSY = 1;
    static final public int STATE_STOPPING = 2;

    static private class SimpleTimer {
        private long mTime;
        public long mark() {
            long time = System.currentTimeMillis();
            long res = time - mTime;
            mTime = time;
            return res;
        }
    }

    public interface OnPerformanceUpdateListener {
        void onPerformanceUpdate(long fps, long pps);
    }

    public interface OnStateChangeListener {
        void onStateChanged(int state, int oldState);
    }

    final private ArrayDeque<Particle> mParticles = new ArrayDeque<>();
    final private Random mRandom = new Random();
    final private ParticleSystemConfig mConfig = new ParticleSystemConfig();

    final private Runnable mUpdatePost = new Runnable() {
        @Override
        public void run() {

            mSimpleTimer.mark();

            if (mState != STATE_BUSY && mState != STATE_STOPPING) {
                return;
            }

            long time = System.currentTimeMillis();

            ++mFrameCount;

            synchronized (mParticles) {
                updatePtc(time);
                if (mState == STATE_STOPPING && mParticles.size() == 0) {
                    mState = STATE_READY;
                    if (mOnStateChangeListener != null) {
                        mOnStateChangeListener.onStateChanged(STATE_READY, STATE_STOPPING);
                    }
                }
            }
            mParticleSystemView.postInvalidate();

            long cost = mSimpleTimer.mark();
            mHandler.postDelayed(this, mDpf - cost);
        }
    };

    final private Runnable mSpawnPost = new Runnable() {
        @Override
        public void run() {

            mSimpleTimer.mark();

            if (mState != STATE_BUSY) {
                return;
            }

            long time = System.currentTimeMillis();

            if (mPtcImage != null) {
                ++mPtcCount;
                Particle ptc = ParticlePool.get().obtain();
                ptc.config(mPtcImage, mX, mY, mConfig, mRandom);
                ptc.reset(time);
                ptc.update(time);
                synchronized (mParticles) {
                    mParticles.push(ptc);
                }
            }
            long cost = mSimpleTimer.mark();
            mHandler.postDelayed(this, mDpp - cost);
        }
    };

    final private Runnable mPerformanceUpdatePost = new Runnable() {
        @Override
        public void run() {

            mActualFps = mFrameCount - mLastFrameCount;
            mActualPps = mPtcCount - mLastPtcCount;

            mLastFrameCount = mFrameCount;
            mLastPtcCount = mPtcCount;

            if (mOnPerformanceUpdateListener != null) {
                mOnPerformanceUpdateListener.onPerformanceUpdate(mActualFps, mActualPps);
            }

            if (mState != STATE_BUSY && mState != STATE_STOPPING) {
                return;
            }

            mHandler.postDelayed(this, 1000);
        }
    };

    final private ParticleSystemView mParticleSystemView;
    final private Handler mHandler;
    final private SimpleTimer mSimpleTimer = new SimpleTimer();

    private Bitmap mPtcImage;
    private Paint mPaint;
    private int mBlend = 0;
    private int mNewBlend = 0;
    private OnPerformanceUpdateListener mOnPerformanceUpdateListener;
    private OnStateChangeListener mOnStateChangeListener;
    private int mState = 0;

    private float mFps; //frame per second
    private long mDpf; // delay per frame
    private float mPps; //ptc per second
    private long mDpp; //delay per ptc

    private int mX;
    private int mY;

    private long mFrameCount;
    private long mPtcCount;
    private long mLastFrameCount;
    private long mLastPtcCount;
    private long mActualFps;
    private long mActualPps;

    ParticleSystem(ParticleSystemView attachedView) {

        mParticleSystemView = attachedView;
        mHandler = mParticleSystemView.getParticleSystemHandler();

        mPaint = new Paint();

        this.setFps(40);
        this.setPps(10);
    }

    public void setFps(final float fps) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mFps = fps;
                mDpf = Math.round(1000.0 / mFps);
            }
        });
    }

    public void setPps(final float pps) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPps = pps;
                mDpp = Math.round(1000.0 / mPps);
            }
        });
    }

    public void setConfig(final ParticleSystemConfig config) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mConfig.set(config);
            }
        });
    }

    public void setPtcImage(final Bitmap image) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPtcImage = image;
            }
        });
    }

    public void setPtcPosition(final int x, final int y) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mX = x;
                mY = y;
            }
        });
    }

    public void setPtcBlend(final int mode) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mNewBlend = mode;
            }
        });
    }

    public void setOnPerformanceUpdateListener(final OnPerformanceUpdateListener listener) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mOnPerformanceUpdateListener = listener;
            }
        });
    }

    public void setOnStateChangeListener(final OnStateChangeListener listener) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mOnStateChangeListener = listener;
            }
        });
    }

    public void start() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mState != STATE_READY) {
                    return;
                }
                mState = STATE_BUSY;
                mFrameCount = 0;
                mPtcCount = 0;
                mLastFrameCount = 0;
                mLastPtcCount = 0;
                mActualFps = 0;
                mActualPps = 0;
                if (mOnStateChangeListener != null) {
                    mOnStateChangeListener.onStateChanged(STATE_BUSY, STATE_READY);
                }
                mHandler.post(mUpdatePost);
                mHandler.post(mSpawnPost);
                mHandler.post(mPerformanceUpdatePost);
            }
        });
    }

    public void stop() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mState == STATE_BUSY) {
                    mState = STATE_STOPPING;
                    if (mOnStateChangeListener != null) {
                        mOnStateChangeListener.onStateChanged(STATE_STOPPING, STATE_BUSY);
                    }
                }
            }
        });
    }

    public long getActualFps() {
        return mActualFps;
    }

    public long getActualPps() {
        return mActualPps;
    }

    private void updatePtc(long time) {
        for (int i = 0; i < mParticles.size(); ++i) {
            Particle ptc = mParticles.removeFirst();
            if (ptc.deleteMark) {
                ParticlePool.get().recycle(ptc);
            } else {
                ptc.deleteMark = ptc.update(time);
                mParticles.addLast(ptc);
            }
        }
        if (mNewBlend != mBlend) {
            mBlend = mNewBlend;
            if (mBlend == 0) {
                mPaint.setXfermode(null);
            } else if (mBlend == 1) {
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
            }
        }
    }

    void draw(Canvas canvas) {
        synchronized (mParticles) {
            for (int i = 0; i < mParticles.size(); ++i) {
                Particle ptc = mParticles.removeFirst();
                ptc.draw(canvas, mPaint);
                mParticles.addLast(ptc);
            }
        }
    }

}
