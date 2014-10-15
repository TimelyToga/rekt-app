package com.timothyblumberg.fun.rekt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import java.util.Random;

/**
 * Created by Tim on 10/14/14.
 */
public class RectangleDrawableView extends View {
    private ShapeDrawable sDrawable;
    private Random rgen = new Random();
    private RotateAnimation animation;
    private int xPivot = 0;
    private int yPivot = 0;
    public final int REPEAT_COUNT = 4;



    public RectangleDrawableView(Context context, int xCenter, int yCenter){
        super(context);

        int xStart = xCenter - (G.SQUARE_SIZE / 2);
        int yStart = yCenter - (G.SQUARE_SIZE / 2);
        int xEnd = xStart + G.SQUARE_SIZE;
        int yEnd = yStart + G.SQUARE_SIZE;
        sDrawable = new ShapeDrawable(new RectShape());
        sDrawable.getPaint().setColor(generateRandColor());
        sDrawable.setBounds(xStart, yStart, xEnd, yEnd);

    }

    @Override
    protected void onDraw(Canvas canvas){
        this.setClipBounds(sDrawable.getBounds());
        animation = createAnimation(true);
        this.setAnimation(animation);
//        sDrawable.getPaint().setColor(generateRandColor());
        sDrawable.draw(canvas);
    }

    public  int generateRandColor(){
        return Color.argb(255, rgen.nextInt(255), rgen.nextInt(255), rgen.nextInt(255));
    }

    public void changeColor(){
//        yPivot = rgen.nextInt(SCREEN_HEIGHT + 1);
//        xPivot = rgen.nextInt(SCREEN_WIDTH + 1);

        invalidate();
    }

    private RotateAnimation createAnimation(boolean infinite){
        // Create an animation instance
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, xPivot, yPivot);

        // Set the animation's parameters
        anim.setDuration(1000);               // duration in ms
        if(infinite){
            anim.setRepeatCount(-1);                // -1 = infinite repeated
        } else {
            anim.setRepeatCount(REPEAT_COUNT);
        }
        anim.setFillAfter(false);               // keep rotation after animation

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                changeColor();
            }
        });

        return anim;
    }

    public void setPivots(int x, int y){
        this.xPivot = x;
        this.yPivot = y;
    }


}
