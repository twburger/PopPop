/**
 * Created by me on 13/12/17.
 */

package com.twburger.me.poppoppop;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;
import android.view.MotionEvent;
import static com.twburger.me.poppoppop.MainActivity.playSoundSelect;
import static com.twburger.me.poppoppop.MainActivity.DisplayObjectList;
import static com.twburger.me.poppoppop.MainActivity.playSoundMove;
import static java.lang.Math.abs;

//public class AnimatedView extends ImageView{
public class AnimatedView extends AppCompatImageView{
    private Handler hndlr;
    private final int FRAME_RATE = 30;
    private final int MAX_V = 15;

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
        for( DisplayObject d : DisplayObjectList) {

            d.DispObjDrawSetPos(wd, ht, !d.bIsSelected );
            c.drawBitmap( d.displayBMP.getBitmap(), d.GetX(), d.GetY(), null);
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
            int wd = d.displayBMP.getBitmap().getWidth();
            int ht = d.displayBMP.getBitmap().getHeight();
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

    static boolean bSwipe2Restart = false;
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

                lastXpos = (int) event.getX();
                lastYpos = (int) event.getY();
                displayObject = GetBallClicked(lastXpos, lastYpos);
                if (null != displayObject) {
                    playSoundSelect();
                    displayObject.bIsSelected = true;
                }

                return true;

            case (MotionEvent.ACTION_MOVE):
                //Log.d(DEBUG_TAG, "Action was MOVE");

                // move the object with the finger
                if (null != displayObject) {
                    if( displayObject.bIsSelected ) {
                        float Xpos = event.getX();
                        float Ypos = event.getY();
                        //displayObject.DispObjDrawSetPos((int) Xpos, (int) Ypos, false);
                        displayObject.SetX((int) Xpos);
                        displayObject.SetY((int) Ypos);

                        // can not move a stopped object
                        if (!displayObject.bIsStopped)
                            bSwipe2Restart = true;
                    }
                }

                return true;

            case (MotionEvent.ACTION_UP):
                //Log.d(DEBUG_TAG, "Action was UP");

                // if a selection was made
                if (null != displayObject) {
                    if (displayObject.bIsSelected) {
                        if (bSwipe2Restart) {
                            //int Xpos = (int) event.getX();
                            //int Ypos = (int) event.getY();
                            int Xpos = (int) displayObject.GetX();
                            int Ypos = (int) displayObject.GetY();

                            // limit the speed to 20 pixels per frame
                            int xV = Xpos - lastXpos;
                            if (abs(xV) > MAX_V) xV = (xV < 0) ? -MAX_V : MAX_V;

                            int yV = Ypos - lastYpos;
                            if (abs(yV) > MAX_V) yV = (yV < 0) ? -MAX_V : MAX_V;

                            // modulate the selection so that a press that moves the object slightly will
                            // still be considered just pressing it
                            if (abs(xV) < 3 && abs(yV) < 3) {
                                //xV = 0;
                                //yV = 0;
                                //displayObject.displayBMP = displayObject.alternativeDisplayBMP;
                                //displayObject.SetVelocity(0, 0); // stop
                                //displayObject.bIsStopped = true;

                                displayObject.rotateColor(getResources());

                            } else {
                                displayObject.SetVelocity(xV, yV);
                                playSoundMove();
                            }
                        } else { // tapped but not moved
                            // if it was stopped restart it with default vector
                            if (displayObject.bIsStopped) {
                                //displayObject.displayBMP = displayObject.standardDisplayBMP;
                                //displayObject.ResetVelocity();  // set to default
                                //displayObject.bIsStopped = false;
                            } else {

                                // when clicked make noise and change color
                                displayObject.rotateColor(getResources());

                                //displayObject.SetVelocity(0, 0); // reset to default vector
                                //displayObject.bIsStopped = true;
                            }
                        }
                    }
                    displayObject.bIsSelected = false;
                }
                bSwipe2Restart = false;
                displayObject = null;

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