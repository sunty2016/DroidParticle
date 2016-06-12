package com.sunty.droidparticle;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by sunty on 16-5-7.
 */
public class Particle {

    public static class Inter {
        public float get(float v0, float v1, float t) {
            return v0 + (v1 - v0) * t;
        }
    }
    public static class Var {
        public Var() {
            inter = new Inter();
        }
        public void set(float v0, float v1) {
            this.v0 = v0;
            this.v1 = v1;
            this.vt = v0;
        }
        public void set(Var other) {
            this.v0 = other.v0;
            this.v1 = other.v1;
            this.vt = other.vt;
        }
        public void update(float t) {
            this.vt = inter.get(v0, v1, t);
        }
        public float v0;
        public float v1;
        public float vt;
        public Inter inter;
    }

    public Particle() {
        velo__ = new Var();
        bias__ = new Var();
        spin__ = new Var();
        scale__ = new Var();
        alpha__ = new Var();
        red__ = new Var();
        green__ = new Var();
        blue__ = new Var();
        matrix = new Matrix();
        colorMatrix = new ColorMatrix();
        colorFilter = new ColorMatrixColorFilter(colorMatrix);
    }

    public long durInMillis;
    public long timeInMillis;
    public long startTimeInMillis;
    public float x, y;  // pixel
    public float theta; // degree
    public float rot;   // degeee
    public float scale; // 1.0
    public float alpha; // 1.0
    public float red;   // 1.0
    public float green; // 1.0
    public float blue;  // 1.0

    public Bitmap bitmap;
    public Matrix matrix;
    public ColorMatrix colorMatrix;
    public ColorMatrixColorFilter colorFilter;
    public boolean deleteMark;

    public Var velo__;  // pixel per second
    public Var bias__;  // degree per second
    public Var spin__;  // degree per second
    public Var scale__; // 1.0
    public Var alpha__; // 1.0
    public Var red__;   // 1.0
    public Var green__; // 1.0
    public Var blue__;  // 1.0

    static private final double PI_D_180 = Math.PI / 180.0;


    public void config(Bitmap image, int x, int y, ParticleSystemConfig config, Random random) {
        this.bitmap = image;
        this.x = x;
        this.y = y;
        config.setParticle(random, this);
    }

    public void reset(long timeInMillis) {
        this.startTimeInMillis = timeInMillis;
        this.timeInMillis = timeInMillis;
        this.deleteMark = false;
    }

    public boolean update(long timeInMillis) {

        matrix.reset();
        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
        matrix.postScale(scale, scale);
        matrix.postRotate(rot);
        matrix.postTranslate(x, y);
        colorMatrix.reset();
        colorMatrix.setScale(red, green, blue, alpha);
        ColorMatrixFilterHelper.setFilterMatrix(
                colorFilter,
                colorMatrix);


        if (this.timeInMillis - this.startTimeInMillis >= this.durInMillis) {
            return true;
        }

        double dtInSeconds = (double)(timeInMillis - this.timeInMillis) * 0.001;

        double rad = theta * PI_D_180;
        double dd = velo__.vt * dtInSeconds;

        //Log.i("STY", String.format("velo__.vt %f, dt %f, dd %f", velo__.vt, dt, dd));

        x += dd * Math.cos(rad);
        y += dd * Math.sin(rad);
        theta += bias__.vt * dtInSeconds;
        rot += spin__.vt * dtInSeconds;
        scale = scale__.vt;
        alpha = alpha__.vt;
        red = red__.vt;
        green = green__.vt;
        blue = blue__.vt;

        this.timeInMillis = timeInMillis;
        float t = (float)(this.timeInMillis - this.startTimeInMillis) / (float)this.durInMillis;
        velo__.update(t);
        bias__.update(t);
        spin__.update(t);
        scale__.update(t);
        alpha__.update(t);
        red__.update(t);
        green__.update(t);
        blue__.update(t);


        return false;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(bitmap, matrix, paint);
    }


}
