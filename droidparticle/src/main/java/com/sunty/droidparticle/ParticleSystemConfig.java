package com.sunty.droidparticle;

import java.util.Random;

/**
 * Created by sunty on 16-6-5.
 */
public class ParticleSystemConfig {


    public Range duration = new Range(1000, 0);
    public Range theta = new Range(0, 180);
    public Range rotation = new Range(0, 180);
    public Range startVelocity = new Range(200, 0);
    public Range endVelocity = new Range(200, 0);
    public Range startAngularRate = new Range(0, 0);
    public Range endAngularRate = new Range(0, 0);
    public Range startSpinRate = new Range(360, 0);
    public Range endSpinRate = new Range(360, 0);
    public Range startScale = new Range(1, 0);
    public Range endScale = new Range(1, 0);
    public Range startAlpha = new Range(1, 0);
    public Range endAlpha = new Range(1, 0);
    public Range startRed = new Range(1, 0);
    public Range endRed = new Range(1, 0);
    public Range startGreen = new Range(1, 0);
    public Range endGreen = new Range(1, 0);
    public Range startBlue = new Range(1, 0);
    public Range endBlue = new Range(1, 0);

    public void set(ParticleSystemConfig other) {
        duration.set(other.duration);
        theta.set(other.theta);
        rotation.set(other.rotation);
        startVelocity.set(other.startVelocity);
        endVelocity.set(other.endVelocity);
        startAngularRate.set(other.startAngularRate);
        endAngularRate.set(other.endAngularRate);
        startSpinRate.set(other.startSpinRate);
        endSpinRate.set(other.endSpinRate);
        startScale.set(other.startScale);
        endScale.set(other.endScale);
        startAlpha.set(other.startAlpha);
        endAlpha.set(other.endAlpha);
        startRed.set(other.startRed);
        endRed.set(other.endRed);
        startGreen.set(other.startGreen);
        endGreen.set(other.endGreen);
        startBlue.set(other.startBlue);
        endBlue.set(other.endBlue);
    }

    public void setParticle(Random random, Particle ptc) {

        ptc.velo__.set(startVelocity.get(random), endVelocity.get(random));
        ptc.bias__.set(startAngularRate.get(random), endAngularRate.get(random));
        ptc.spin__.set(startSpinRate.get(random), endSpinRate.get(random));
        ptc.scale__.set(startScale.get(random), endScale.get(random));
        ptc.alpha__.set(startAlpha.get(random), endAlpha.get(random));
        ptc.red__.set(startRed.get(random), endRed.get(random));
        ptc.green__.set(startGreen.get(random), endGreen.get(random));
        ptc.blue__.set(startBlue.get(random), endBlue.get(random));


        ptc.durInMillis = Math.round(duration.get(random));
        ptc.theta = theta.get(random);
        ptc.rot = rotation.get(random);

        ptc.scale = ptc.scale__.vt;
        ptc.alpha = ptc.alpha__.vt;
        ptc.red = ptc.red__.vt;
        ptc.green = ptc.green__.vt;
        ptc.blue = ptc.blue__.vt;
    }

    static public class Range {
        public Range(float base, float bias) {
            this.base = base;
            this.bias = bias;
        }
        public float base;
        public float bias;
        public float get(Random random) {
            return base - bias + random.nextFloat() * bias * 2;
        }
        public void set(float base, float bias) {
            this.base = base;
            this.bias = bias;
        }
        public void set(Range other) {
            this.base = other.base;
            this.bias = other.bias;
        }
    }
}