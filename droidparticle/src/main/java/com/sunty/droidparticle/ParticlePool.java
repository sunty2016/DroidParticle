package com.sunty.droidparticle;

import java.util.ArrayDeque;

/**
 * Created by sunty on 16-6-4.
 */
public class ParticlePool {
    static final private int INITIAL_SIZE = 256;
    static final private int INCREASE_SIZE = 128;
    static private ParticlePool sInstance = null;

    static public ParticlePool get() {
        if (sInstance == null) {
            synchronized (ParticlePool.class) {
                if (sInstance == null) {
                    sInstance = new ParticlePool();
                }
            }
        }
        return sInstance;
    }

    private ParticlePool() {
        for (int i = 0; i < INITIAL_SIZE; ++i) {
            mAll.add(new Particle());
        }
        mAvails.addAll(mAll);
    }

    final private ArrayDeque<Particle> mAll = new ArrayDeque<>();
    final private ArrayDeque<Particle> mAvails = new ArrayDeque<>();
    final private ArrayDeque<Particle> mWaits = new ArrayDeque<>();

    public Particle obtain() {
        if (mAvails.size() == 0) {
            if (mWaits.size() == 0) {
                for (int i = 0; i < INCREASE_SIZE; ++i) {
                    mWaits.add(new Particle());
                }
                mAll.addAll(mWaits);
            }
            mAvails.addAll(mWaits);
            mWaits.clear();
        }
        Particle ptc = mAvails.removeFirst();
        return ptc;
    }

    public void recycle(Particle ptc) {
        mWaits.add(ptc);
    }
}
