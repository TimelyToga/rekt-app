package com.timothyblumberg.fun.rekt;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.fun.rekt.ShakeDector.ShakeDetector;
import com.timothyblumberg.fun.rekt.ShakeDector.ShakeDetectorListener;

import java.util.Random;


public class MainActivity extends Activity {

    private final String TAG = MainActivity.class.getSimpleName();

    // Views
    private RelativeLayout relativeLayout;
    private TextView textView;
    private SeekBar seekBar;
    private View exampleSquare;

    // Weird booleans
    boolean drawing;
    boolean firstTouchEvent;

    // Weird numbers
    private int curXPivot;
    private int curYPivot;
    private final int progressFactor = 5;

    // Shake Detector
    ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Screen size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        G.SCREEN_HEIGHT = displaymetrics.heightPixels;
        G.SCREEN_WIDTH = displaymetrics.widthPixels;

        // Rekt Initialization
        drawing = false;
        firstTouchEvent = true;
        G.rgen = new Random();
        newPivot();

        // Get view references
        relativeLayout = (RelativeLayout) findViewById(R.id.main_rel_layout);
        textView = (TextView) findViewById(R.id.tap_label);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        exampleSquare = findViewById(R.id.example_square);

        // Set up the views
        setupRelativeLayout();
        setupSeekbar();

        ViewGroup.LayoutParams exampleSquareLayoutParams = exampleSquare.getLayoutParams();
        exampleSquareLayoutParams.height = G.SQUARE_SIZE;
        exampleSquareLayoutParams.width = G.SQUARE_SIZE;
        exampleSquare.setLayoutParams(exampleSquareLayoutParams);
        exampleSquare.setBackgroundColor(generateRandColor());

        // Shake Detector
        shakeDetector = new ShakeDetector(this);
        shakeDetector.addListener(new ShakeDetectorListener() {
            @Override
            public void shakeDetected() {
                clearRectangles();
            }
        });
    }


    /*
     * These methods are needed for the ShakeDetector to work properly
     */
    @Override
    protected void onResume(){
        super.onResume();
        shakeDetector.onResume();
    }

    @Override
    protected void onPause(){
        shakeDetector.onPause();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_clear) {
            clearRectangles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public  int generateRandColor(){
        return Color.argb(255, G.rgen.nextInt(255), G.rgen.nextInt(255), G.rgen.nextInt(255));
    }

    public void addRect(int x, int y){
        RectangleDrawableView newRect = new RectangleDrawableView(this, x, y);
        newRect.setPivots(curXPivot, curYPivot);
        relativeLayout.addView(newRect);
    }

    public void clearRectangles(){
        this.recreate();
    }

    private void setupRelativeLayout(){
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(firstTouchEvent && event.getAction() == MotionEvent.ACTION_UP){
                    curXPivot = (int) event.getX();
                    curYPivot = (int) event.getY();
                    setDrawingMode();
                } else if(drawing){
                    if(event.getAction() == MotionEvent.ACTION_MOVE){
                        int xCood = (int) event.getX();
                        int yCood = (int) event.getY();
                        addRect(xCood, yCood);
                    }
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    int xCood = (int) event.getX();
                    int yCood = (int) event.getY();
                    addRect(xCood, yCood);
                    newPivot();

                } else if(event.getAction() == MotionEvent.ACTION_DOWN){
//                    int xCood = (int) event.getX();
//                    int yCood = (int) event.getY();
//                    addRect(xCood, yCood);
//                    newPivot();
                }

                return false;
            }
        });

        relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RelativeLayout r = (RelativeLayout) v;
                r.removeAllViews();
                return false;
            }
        });
    }

    private void setupSeekbar(){
        seekBar.setProgress(G.SQUARE_SIZE / 5);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ViewGroup.LayoutParams layoutParams = exampleSquare.getLayoutParams();
                if(progress == 0){
                    layoutParams.height = 5;
                    layoutParams.width = 5;
                } else {
                    layoutParams.height = progress * progressFactor;
                    layoutParams.width = progress * progressFactor;
                }

                exampleSquare.setLayoutParams(layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                exampleSquare.setVisibility(View.INVISIBLE);
                G.SQUARE_SIZE = seekBar.getProgress() * progressFactor;
                if(G.SQUARE_SIZE == 0) G.SQUARE_SIZE = progressFactor;
                Log.i(TAG, "Progress: " + G.SQUARE_SIZE);
                seekBar.setVisibility(View.INVISIBLE);
                textView.setText(getText(R.string.pick_a_center));

            }
        });
    }

    private void newPivot(){
        curXPivot = G.rgen.nextInt(G.SCREEN_HEIGHT);
        curYPivot = G.rgen.nextInt(G.SCREEN_WIDTH);
    }

    private void setDrawingMode(){
//        editText.setVisibility(View.INVISIBLE);

        if(G.firstTimeDrawing){
            textView.setTextColor(Color.WHITE);
            textView.setText("Draw some cool lines...");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    textView.setText("");
                    textView.setTextColor(Color.BLACK);
                }
            };
            textView.postDelayed(r, 1000);
            Toast.makeText(this, "Shake to reset", Toast.LENGTH_SHORT).show();
            G.firstTimeDrawing = false;
        }

        firstTouchEvent = false;
        relativeLayout.setBackgroundColor(Color.BLACK);
        drawing = true;

    }

}
