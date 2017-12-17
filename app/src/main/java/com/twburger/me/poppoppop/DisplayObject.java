package com.twburger.me.poppoppop;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;

import static com.twburger.me.poppoppop.MainActivity.playSoundWallBounce;

/**
 * Created by me on 16/12/17.
 */

class DisplayObject {
    private int objXpos = -1;
    private int objYpos = -1;
    private int xVelocity_Orig = 0;
    private int yVelocity_Orig = 0;
    private int xVelocity = 0;
    private int yVelocity = 0;
    private static int DisplayObjInstance = -1;
    final static int MAX_INSTANCES = 7;
    private static ArrayList<BitmapDrawable> draw_list = new ArrayList<BitmapDrawable>();
    private int ThisDispObjInstance = -1;

    BitmapDrawable displayObj = null;
    BitmapDrawable lastDisplayObj = null;
    BitmapDrawable alternativeDisplayObj = null;

    int getInstance() {
        return ThisDispObjInstance;
    }

    public DisplayObject(Context context )  {

        if (DisplayObjInstance == -1) {
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.greenball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.midblueball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.orangeball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.purpleball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.redball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.royalblueball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.yellowball128px));
            DisplayObjInstance = 0;
        }

        lastDisplayObj = displayObj = draw_list.get(DisplayObjInstance);
        alternativeDisplayObj = (BitmapDrawable) context.getResources().getDrawable(R.drawable.starpink128px);

        ThisDispObjInstance = DisplayObjInstance;
        DisplayObjInstance++;
        if (DisplayObjInstance >= MAX_INSTANCES)
            DisplayObjInstance = 0;
    }

    public void SetVelocity(int xV, int yV) {
        if ((0 == xV && 0 == yV) && (0 == xVelocity && 0 == yVelocity)) {
            xVelocity = xVelocity_Orig;
            yVelocity = yVelocity_Orig;
        } else {
            if (0 == xVelocity_Orig && 0 == yVelocity_Orig) {
                xVelocity_Orig = xV;
                yVelocity_Orig = yV;
            }
            xVelocity = xV;
            yVelocity = yV;
        }
    }

    public int GetX() {
        return objXpos;
    }

    public int GetY() {
        return objYpos;
    }

    public void DispObjDrawSetPos(int Width, int Height) {

        boolean b = false;

        if (objXpos < 0 && objYpos < 0) {
            objXpos = Width / 2;
            objYpos = Height / 2;
            //Random r = new Random();
            //objXpos = r.nextInt(Width/2)+1;
            //r = new Random();
            //objYpos = r.nextInt(Height/2+1);
        } else {
            if ((objXpos + xVelocity > Width - displayObj.getBitmap().getWidth()) || (objXpos < 0)) {

                if (objXpos < 0)
                    objXpos = 1;

                if (objXpos + xVelocity > Width - displayObj.getBitmap().getWidth())
                    objXpos = Width - displayObj.getBitmap().getWidth() - 1;

                xVelocity = xVelocity * -1;

                b = true;
            }
            if ((objYpos + yVelocity > Height - displayObj.getBitmap().getHeight()) || (objYpos < 0)) {

                if (objYpos < 0)
                    objYpos = 1;

                if (objYpos + yVelocity > Height - displayObj.getBitmap().getHeight())
                    objYpos = Height - displayObj.getBitmap().getHeight() - 1;

                yVelocity = yVelocity * -1;

                b = true;
            }

            objXpos += xVelocity;
            objYpos += yVelocity;

            if (b) {
                playSoundWallBounce();
            }
        }
    }
}
