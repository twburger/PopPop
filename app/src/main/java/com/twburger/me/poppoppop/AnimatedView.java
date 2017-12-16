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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //int action = MotionEventCompat.getActionMasked(event);
        int action = event.getActionMasked();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d(DEBUG_TAG, "Action was DOWN");

                //BitmapDrawable bmd = (BitmapDrawable) getResources().getDrawable(R.drawable.greenball128px);
                //int level = bmd.getLevel();
                int x = (int) event.getX();
                int y = (int) event.getY();

                DisplayObject displayObject = GetBallClicked(x,y);

                if(null != displayObject) {
                    displayObject.SetVelocity(0, 0);
                    //playSoundBoyp();
                    playSoundBounce();
                }

                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(DEBUG_TAG, "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Log.d(DEBUG_TAG, "Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG, "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}