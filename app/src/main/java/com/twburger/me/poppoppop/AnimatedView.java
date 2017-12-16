/**
 * Created by me on 13/12/17.
 */

package com.twburger.me.poppoppop;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MotionEvent;

import static com.twburger.me.poppoppop.MainActivity.playSoundBounce;
//import static com.twburger.me.poppoppop.MainActivity.playSoundBoyp;
import static com.twburger.me.poppoppop.MainActivity.DisplayObjectList;
import static com.twburger.me.poppoppop.MainActivity.playSoundBoyp;
import static com.twburger.me.poppoppop.MainActivity.playSoundSwipe;
import static java.lang.Math.abs;

//public class AnimatedView extends ImageView{
public class AnimatedView extends AppCompatImageView{
    private Handler hndlr;
    private final int FRAME_RATE = 30;

    public AnimatedView(Context context, AttributeSet attrs)  {

        // Call to 'super()' must be first statement in constructor body
        super(context, attrs);

        //mContext = context;
        hndlr = new Handler();
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    protected void onDraw(Canvas c) {

        int wd = this.getWidth();
        int ht = this.getHeight();
        for( DisplayObject d : DisplayObjectList){
            d.DispObjDraw(wd, ht);
            c.drawBitmap( d.displayObj.getBitmap(), d.GetX(), d.GetY(), null);
        }

        hndlr.postDelayed(r, FRAME_RATE);
    }

    /*
    Determine what object was selected by determining if the touch was within the objects dimensions
    and the object is the one on top if two or more are in that position
    */
    private DisplayObject GetBallClicked(int pX, int pY) {

        DisplayObject dc = null;

        for( DisplayObject d : DisplayObjectList){
            int x = d.GetX();
            int y = d.GetY();
            int wd = d.displayObj.getBitmap().getWidth();
            int ht = d.displayObj.getBitmap().getHeight();
            if( pX > x && pX < x + wd && pY > y && pY < y + ht ) {
                if( null == dc ) {
                    dc = d;
                }
                else {
                    if (d.getInstance() > dc.getInstance()) {
                        dc = d;
                    }
                }
            }
        }

        return dc;
    }

    String DEBUG_TAG = "POPpopPOP";

    static boolean bMove = false;
    static DisplayObject displayObject = null;
    static int lastXpos = -1;
    static int lastYpos = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //int action = MotionEventCompat.getActionMasked(event);
        int action = event.getActionMasked();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                //Log.d(DEBUG_TAG, "Action was DOWN");

                //BitmapDrawable bmd = (BitmapDrawable) getResources().getDrawable(R.drawable.greenball128px);
                //int level = bmd.getLevel();
                lastXpos = (int) event.getX();
                lastYpos = (int) event.getY();

                displayObject = GetBallClicked(lastXpos,lastYpos);

                if(null != displayObject) {
                    displayObject.SetVelocity(0, 0);
                    playSoundBounce();
                }

                return true;
            case (MotionEvent.ACTION_MOVE):
                //Log.d(DEBUG_TAG, "Action was MOVE");
                // record the coords and time to calc a new vector
                bMove = true;
                return true;

            case (MotionEvent.ACTION_UP):
                //Log.d(DEBUG_TAG, "Action was UP");
                // recalculate the vector
                if( bMove ) {
                    if (null != displayObject) {
                        int Xpos = (int) event.getX();
                        int Ypos = (int) event.getY();
                        int xV = Xpos - lastXpos;
                        if(abs(xV) > 20) xV = (xV < 0) ? -20 : 20;

                        int yV = Ypos - lastYpos;
                        if(abs(yV) > 20) yV = (yV < 0) ? -20 : 20;

                        displayObject.SetVelocity(xV, yV);

                        playSoundSwipe();
                    }
                    bMove = false;
                    displayObject = null;
                }
                return true;
            case (MotionEvent.ACTION_CANCEL):
                //Log.d(DEBUG_TAG, "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                //Log.d(DEBUG_TAG, "Movement occurred outside bounds " + "of current screen element");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}