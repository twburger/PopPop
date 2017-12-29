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

import static com.twburger.me.poppoppop.BitmapObjectCollision.isCollisionDetected;
import static com.twburger.me.poppoppop.MainActivity.playSoundCollide;
import static com.twburger.me.poppoppop.MainActivity.playSoundJump;
import static com.twburger.me.poppoppop.MainActivity.playSoundSelect;
import static com.twburger.me.poppoppop.MainActivity.DisplayObjectList;
import static com.twburger.me.poppoppop.MainActivity.playSoundMove;
import static java.lang.Math.abs;

//public class AnimatedView extends ImageView{
public class AnimatedView extends AppCompatImageView{
    private Handler hndlr;
    private static final int FRAME_RATE = 30;
    static final int MAX_V = 25;
    static final int MIN_V = 8;

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

    static final int FRAME_ROTATE_COUNT = 100;
    private static final int REDRAW_ROTATION_DEGREES = 18;
    private static DisplayObject lastCollideObj = null;

    protected void onDraw(Canvas c) {

        int wd = this.getWidth();
        int ht = this.getHeight();

        for( DisplayObject d : DisplayObjectList) {

            // See if this object is hitting another in the 2-D world and if so change the travel vector
            if( d.objIsStopped() ) {  //only the stopped object will interact
                for (DisplayObject o : DisplayObjectList) {
                    if (o != d && lastCollideObj != o && !o.objIsStopped()) {
                        // if the objects occupy the same space do not detect a collision
                        if (isCollisionDetected(d.displayBMP.getBitmap(), d.GetX(), d.GetY(),
                                o.displayBMP.getBitmap(), o.GetX(), o.GetY())) {
                            // switch the velocity vectors
                            //int vX = d.getVelocity(true);
                            //int vY = d.getVelocity(false);
                            //d.SetVelocity(o.getVelocity(true), o.getVelocity(false));
                            //o.SetVelocity(vX, vY);
                            // only detect one collision per pass
                            o.SetVelocity(-o.getVelocity(true), -o.getVelocity(false));
                            playSoundCollide();
                            lastCollideObj = o;
                            break;
                        }
                    }
                }
            }

            // rotate the object if it should be in this frame
            d.rev++;
            if(0==(d.rev % FRAME_ROTATE_COUNT)) {
                d.rotateObject(REDRAW_ROTATION_DEGREES, getResources());
            }

            // Set the point to draw the object, adding a velocity if not a selected obect
            d.DispObjDrawSetPos(wd, ht, !d.objIsStopped());

            c.drawBitmap( d.displayBMP.getBitmap(), d.GetX(), d.GetY(), null);

            // reset the frame count for detemining rotation
            if(0==(d.rev % FRAME_ROTATE_COUNT)) {
                d.rev = 0;
            }
        }

        hndlr.postDelayed(r, FRAME_RATE);
    }

    /*
    Get object selected by determining if the touch was within the objects dimensions
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
                    // Need to determine which level is being clicked on by using order created
                    if (d.getInstance() > dc.getInstance()) {
                        dc = d;
                    }
                }
            }
        }

        return dc;
    }

    String DEBUG_TAG = "POPpopPOP";

    static boolean bWasMoved = false;
    static DisplayObject displayObject = null;
    static DisplayObject lastDisplayObject = null;
    static int lastXpos = -1;
    static int lastYpos = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //int action = MotionEventCompat.getActionMasked(event);
        int action = event.getActionMasked();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                //Log.d(DEBUG_TAG, "Action was DOWN");

                int x = (int) event.getX();
                int y = (int) event.getY();
                displayObject = GetBallClicked(x, y);
                if (null != displayObject) { // Object selected
                    playSoundSelect();
                    displayObject.bIsSelected = true;
                    lastDisplayObject = displayObject;
                    lastXpos = displayObject.GetX() + displayObject.displayBMP.getBitmap().getWidth()/2;
                    lastYpos = displayObject.GetY() + displayObject.displayBMP.getBitmap().getHeight()/2;
                } else if ( null != lastDisplayObject ){ // move the last selection to this point
                    lastDisplayObject.SetX(x - lastDisplayObject.displayBMP.getBitmap().getWidth()/2);
                    lastDisplayObject.SetY(y - lastDisplayObject.displayBMP.getBitmap().getHeight()/2);
                    playSoundJump();
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
                        displayObject.SetX((int) Xpos - displayObject.displayBMP.getBitmap().getWidth()/2);
                        displayObject.SetY((int) Ypos - displayObject.displayBMP.getBitmap().getHeight()/2);

                        // can not move a stopped object
                        if (!displayObject.bIsStopped)
                            bWasMoved = true;
                    }
                }

                return true;

            case (MotionEvent.ACTION_UP):
                //Log.d(DEBUG_TAG, "Action was UP");

                // if a selection was made
                if (null != displayObject) {
                    if (displayObject.bIsSelected) {
                        if (bWasMoved) {
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
                            if (abs(xV) < MIN_V && abs(yV) < MIN_V) {
                                displayObject.bIsStopped = true;
                            } else {
                                displayObject.SetVelocity(xV, yV);
                                playSoundMove();
                            }
                        } else { // tapped but not moved
                            // if it was stopped restart it with default vector
                            if (displayObject.bIsStopped) {
                                displayObject.ResetVelocity();  // set to default
                                displayObject.bIsStopped = false;
                            } else {
                                displayObject.bIsStopped = true;
                            }
                        }
                        if (displayObject.bIsStopped) {
                            //displayObject.displayBMP = displayObject.alternativeDisplayBMP;
                            //displayObject.displayBMP = displayObject.standardDisplayBMP;
                            displayObject.SetVelocity(0, 0); // stop
                            displayObject.nextColor(getResources());
                            displayObject.rotateObject(36, getResources());
                        }
                    }

                    displayObject.bIsSelected = false;
                }
                bWasMoved = false;
                displayObject = null;

                return true;
            /*
            case (MotionEvent.ACTION_CANCEL):
                //Log.d(DEBUG_TAG, "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                //Log.d(DEBUG_TAG, "Movement occurred outside bounds " + "of current screen element");
                return true;
            */

            default:
                return super.onTouchEvent(event);
        }
    }
}