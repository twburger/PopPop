package com.twburger.me.poppoppop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
    final static int MAX_COLORS = 7;
    final static int MAX_SHAPES = 4;

    //private static ArrayList<BitmapDrawable> draw_list = new ArrayList<BitmapDrawable>();
    private static ArrayList<BitmapDrawable> shape_list = new ArrayList<BitmapDrawable>();
    private int ThisDispObjInstance = -1;

    boolean bIsSelected = false;
    boolean bIsStopped = false;

    BitmapDrawable displayBMP = null;
    BitmapDrawable standardDisplayBMP = null;
    BitmapDrawable alternativeDisplayBMP = null;

    int ObjColors[] = new int[]
            {
                    Color.BLUE,
                    Color.CYAN,
                    Color.GREEN,
                    Color.LTGRAY,
                    Color.MAGENTA,
                    Color.RED,
                    //Color.WHITE,
                    Color.YELLOW
            };

    int CurrentColor = -1;
    int CurrentShape = -1;

    int getInstance() {
        return ThisDispObjInstance;
    }

    public DisplayObject( Context context, Resources r )  {

        if (DisplayObjInstance == -1) {
            /*
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.greenball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.midblueball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.orangeball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.purpleball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.redball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.royalblueball128px));
            draw_list.add((BitmapDrawable) context.getResources().getDrawable(R.drawable.yellowball128px));
            */

            shape_list.add(0, (BitmapDrawable) context.getResources().getDrawable(R.drawable.trianglewhite128px));
            shape_list.add(1, (BitmapDrawable) context.getResources().getDrawable(R.drawable.starblue128px));
            shape_list.add(2, (BitmapDrawable) context.getResources().getDrawable(R.drawable.yellowball128px));
            shape_list.add(3, (BitmapDrawable) context.getResources().getDrawable(R.drawable.squarewhite128px));

            DisplayObjInstance = 0;
            bIsSelected = false;
        }

        //standardDisplayBMP = displayBMP = draw_list.get(DisplayObjInstance);
        //alternativeDisplayBMP = (BitmapDrawable) context.getResources().getDrawable(R.drawable.starpink128px);
        alternativeDisplayBMP = standardDisplayBMP = displayBMP = shape_list.get(0);

        ThisDispObjInstance = DisplayObjInstance;
        CurrentShape = 0;

        DisplayObjInstance++;
        if (DisplayObjInstance >= MAX_INSTANCES)
            DisplayObjInstance = 0;

        // set a color
        CurrentColor = ThisDispObjInstance;
        changeColor( ObjColors[CurrentColor], r );
    }

    public void SetVelocity(int xV, int yV) {
        if (0 == xVelocity_Orig && 0 == yVelocity_Orig) {
            xVelocity_Orig = xV;
            yVelocity_Orig = yV;
        }
        xVelocity = xV;
        yVelocity = yV;
    }

    public void ResetVelocity() {
            xVelocity = xVelocity_Orig;
            yVelocity = yVelocity_Orig;
    }


    public int GetX() {
        return objXpos;
    }
    public int GetY() {
        return objYpos;
    }

    public void SetX(int x) {
        objXpos = x;
    }
    public void SetY(int y) {
        objYpos = y;
    }


    public void DispObjDrawSetPos(int Width, int Height, boolean bAddAcceleration ) {

        boolean bHitWall = false;

        if ( (objXpos < -(displayBMP.getBitmap().getWidth()/2) && objYpos < -(displayBMP.getBitmap().getHeight()/2) ) ||
                (objXpos > Width + (displayBMP.getBitmap().getWidth()/2) && objYpos < Height + (displayBMP.getBitmap().getHeight()/2))
                ) {
            objXpos = (Width / 2) + (displayBMP.getBitmap().getWidth()/2);
            objYpos = (Height / 2) + (displayBMP.getBitmap().getHeight()/2);
            //Random r = new Random();
            //objXpos = r.nextInt(Width/2)+1;
            //r = new Random();
            //objYpos = r.nextInt(Height/2+1);
        } else {
            if ((objXpos > Width - (displayBMP.getBitmap().getWidth()/2) || (objXpos < - (displayBMP.getBitmap().getWidth()/2)))) {

                if (objXpos < 0 - (displayBMP.getBitmap().getWidth()/2))
                    objXpos =  -(displayBMP.getBitmap().getWidth()/2);

                if (objXpos + xVelocity > Width - (displayBMP.getBitmap().getWidth()/2))
                    objXpos = Width - (displayBMP.getBitmap().getWidth()/2) - 1;

                xVelocity = xVelocity * -1;

                bHitWall = true;
            }
            if ((objYpos > Height - (displayBMP.getBitmap().getHeight()/2))
                    || (objYpos < -(displayBMP.getBitmap().getHeight()/2))) {

                if (objYpos < 0 - (displayBMP.getBitmap().getHeight()/2))
                    objYpos = -(displayBMP.getBitmap().getHeight()/2);

                if (objYpos + yVelocity > Height - (displayBMP.getBitmap().getHeight()/2))
                    objYpos = Height - (displayBMP.getBitmap().getHeight()/2) - 1;

                yVelocity = yVelocity * -1;

                bHitWall = true;
            }

            if( bAddAcceleration ) {
                objXpos += xVelocity;
                objYpos += yVelocity;
            }

            if (bHitWall) {
                playSoundWallBounce();
            }
        }
    }

    public void rotateShape(){

        CurrentShape++;
        if( CurrentShape >= MAX_SHAPES )
            CurrentShape = 0;
        displayBMP = shape_list.get(CurrentShape);
    }

    public void changeColor(int iColor, Resources r) {

        Bitmap result = Bitmap.createBitmap(displayBMP.getIntrinsicWidth(),
                displayBMP.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        displayBMP.setBounds(0, 0, displayBMP.getIntrinsicWidth(), displayBMP.getIntrinsicHeight());
        displayBMP.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);
        displayBMP.draw(canvas);
        displayBMP.setColorFilter(null);
        displayBMP.setCallback(null);

        displayBMP = new BitmapDrawable(r, result);

        return;
    }

    public void rotateColor(Resources r) {

        CurrentColor++;
        if( CurrentColor >= MAX_COLORS ) {
            rotateShape();
            CurrentColor = 0;
        }
        changeColor(ObjColors[CurrentColor], r);

        return;
    }
}
