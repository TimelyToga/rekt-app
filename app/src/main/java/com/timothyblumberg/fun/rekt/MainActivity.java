package com.timothyblumberg.fun.rekt;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;


public class MainActivity extends Activity {

    Canvas canvas;
    Random rgen;
    RectangleDrawableView r;
    private RelativeLayout relativeLayout;
    private EditText editText;
    private TextView textView;
    boolean drawing;
    boolean firstTouchEvent;

    private int curXPivot;
    private int curYPivot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Rekt
        rgen = new Random();
        drawing = false;
        firstTouchEvent = true;

        // Screen size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        G.SCREEN_HEIGHT = displaymetrics.heightPixels;
        G.SCREEN_WIDTH = displaymetrics.widthPixels;

        newPivot();

        // Set up View
        relativeLayout = (RelativeLayout) findViewById(R.id.main_rel_layout);
        editText = (EditText) findViewById(R.id.number_text);
        textView = (TextView) findViewById(R.id.tap_label);
        editText.setText("");

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

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    G.SQUARE_SIZE = Integer.parseInt(editText.getText().toString());
                    editText.setVisibility(View.INVISIBLE);
                    textView.setText(getText(R.string.pick_a_center));
                    return true; // consume.
                }

                return false;
            }
        });
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
        return Color.argb(255, rgen.nextInt(255), rgen.nextInt(255), rgen.nextInt(255));
    }

    public void addRect(int x, int y){
        RectangleDrawableView newRect = new RectangleDrawableView(this, x, y);
        newRect.setPivots(curXPivot, curYPivot);
        relativeLayout.addView(newRect);
    }

    public void clearRectangles(){
        this.recreate();
    }

    private void newPivot(){
        curXPivot = rgen.nextInt(G.SCREEN_HEIGHT);
        curYPivot = rgen.nextInt(G.SCREEN_WIDTH);
    }

    private void setDrawingMode(){
        editText.setVisibility(View.INVISIBLE);

        relativeLayout.setBackgroundColor(Color.BLACK);
        firstTouchEvent = false;
        drawing = true;
    }

}
