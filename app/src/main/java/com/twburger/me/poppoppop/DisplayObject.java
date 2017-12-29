package com.twburger.me.poppoppop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;
import java.util.Random;

import static com.twburger.me.poppoppop.MainActivity.playSoundWallBounce;

/**
 * Created by me on 16/12/17.
 */

class DisplayObject {

    boolean bIsSelected = false;
    boolean bIsStopped = false;

    private int objXpos = -1;
    private int objYpos = -1;
    private int xVelocity_Orig = 0;
    private int yVelocity_Orig = 0;
    private int xVelocity = 0;
    private int yVelocity = 0;
    private int iCurrentColor = 0;
    private int iCurrentShape = 0;
    private int ThisObjInstance = 0;
    BitmapDrawable displayBMP = null;
    //BitmapDrawable standardDisplayBMP = null;
    //BitmapDrawable alternativeDisplayBMP = null;

    int rev = 0;

    final static int MAX_INSTANCES = 8;
    static int MAX_COLORS = 0;
    static int MAX_SHAPES = 0;
    static int ObjInstances = 0;

    //private static ArrayList<BitmapDrawable> draw_list = new ArrayList<BitmapDrawable>();
    private static ArrayList<BitmapDrawable> shape_list = new ArrayList<BitmapDrawable>();

    private static int ObjColors[] = new int[]
            {
                    Color.BLUE,
                    Color.CYAN,
                    //Color.LTGRAY,
                    //Color.parseColor("purple"), // does not work on older systems
                    Color.rgb(128, 64,255),
                    Color.GREEN,
                    Color.YELLOW,
                    Color.MAGENTA,
                    Color.RED,
                    Color.rgb(32, 200,64)
                    //Color.WHITE
            };

    private static int colorAssignmentIncrementer = 0;
    private static int shapeAssignmentIncrementer = 0;

    private static boolean bStaticsAreInitialized = false;

    public DisplayObject( Context context, Resources rscs)  {

        if (!bStaticsAreInitialized) {
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
            shape_list.add(2, (BitmapDrawable) context.getResources().getDrawable(R.drawable.invertedtrianglewhite128px));
            //shape_list.add(3, (BitmapDrawable) context.getResources().getDrawable(R.drawable.yellowball128px));
            shape_list.add(3, (BitmapDrawable) context.getResources().getDrawable(R.drawable.offsetsmallmidblueball128px));
            shape_list.add(4, (BitmapDrawable) context.getResources().getDrawable(R.drawable.squarewhite128px));

            colorAssignmentIncrementer = 0;
            shapeAssignmentIncrementer = 0;

            MAX_COLORS = ObjColors.length;
            MAX_SHAPES = shape_list.size();

            bStaticsAreInitialized = true;
        }

        //standardDisplayBMP = displayBMP = draw_list.get(DisplayObjColor);
        //alternativeDisplayBMP = (BitmapDrawable) context.getResources().getDrawable(R.drawable.starpink128px);
        //alternativeDisplayBMP = standardDisplayBMP = displayBMP = shape_list.get(shapeAssignmentIncrementer);

        displayBMP = shape_list.get(shapeAssignmentIncrementer);
        iCurrentShape = shapeAssignmentIncrementer;
        shapeAssignmentIncrementer++;
        if( shapeAssignmentIncrementer >= MAX_SHAPES)
            shapeAssignmentIncrementer = 0;

        changeColor( ObjColors[colorAssignmentIncrementer], rscs);
        iCurrentColor = colorAssignmentIncrementer;
        colorAssignmentIncrementer++;
        if (colorAssignmentIncrementer >= MAX_INSTANCES)
            colorAssignmentIncrementer = 0;

        ThisObjInstance = ObjInstances;
        ObjInstances++;

        //set the rotation revolution counter to a random number between 0 - 100
        Random r = new Random();
        rev = r.nextInt(AnimatedView.FRAME_ROTATE_COUNT);

    }

    int getInstance() {
        return ThisObjInstance;
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

    public int getVelocity( boolean bX ) {
        if(bX)
            return xVelocity;
        else
            return yVelocity;
    }

    public boolean objIsStopped(){
        return bIsStopped;
        //return( 0 == xVelocity && 0 == yVelocity);
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

    public void DispObjDrawSetPos(int canvasWidth, int canvasHeight, boolean bAddAcceleration ) {

        boolean bHitWall = false;

        // Get the center point of the bitmap image
        int bitmapWidthCtr = displayBMP.getBitmap().getWidth() / 2;
        int bitmapHeightCtr = displayBMP.getBitmap().getHeight() / 2;

        // if the center of the bitmap is past the canvas boundaries reset it to a place in the canvas
        if ((objXpos < -(bitmapWidthCtr) && objYpos < -(bitmapHeightCtr)) ||
                (objXpos > canvasWidth + (bitmapHeightCtr) && objYpos < canvasHeight + (bitmapHeightCtr))) {

            objXpos = 0; //(canvasWidth / 2) + (bitmapWidthCtr);
            objYpos = 0; //(canvasHeight / 2) + (bitmapHeightCtr);
        } else {
            if (bAddAcceleration) {
                objXpos += xVelocity;
                objYpos += yVelocity;

                // if it hit a wall reverse the vector
                if ((objXpos > canvasWidth - (bitmapWidthCtr) || (objXpos < -(bitmapWidthCtr)))) {

                    if (objXpos < -bitmapWidthCtr)
                        objXpos = -(bitmapWidthCtr);

                    if (objXpos + xVelocity > canvasWidth - bitmapWidthCtr)
                        objXpos = canvasWidth - bitmapWidthCtr - 1;

                    xVelocity = xVelocity * -1;

                    bHitWall = true;
                }

                if ((objYpos > canvasHeight - bitmapHeightCtr) || (objYpos < -bitmapHeightCtr)) {

                    if (objYpos < -bitmapHeightCtr)
                        objYpos = -bitmapHeightCtr;

                    if (objYpos + yVelocity > canvasHeight - bitmapHeightCtr)
                        objYpos = canvasHeight - bitmapHeightCtr - 1;

                    yVelocity = yVelocity * -1;

                    bHitWall = true;
                }

                if (bHitWall) {
                    playSoundWallBounce();
                }
            }
        }
    }

    public void nextShape(){

        iCurrentShape++;
        if( iCurrentShape >= MAX_SHAPES )
            iCurrentShape = 0;
        displayBMP = shape_list.get(iCurrentShape);
    }

    public void changeColor(int iColor, Resources r) {

        int w = displayBMP.getIntrinsicWidth();
        int h = displayBMP.getIntrinsicHeight();

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        displayBMP.setBounds(0, 0, w, h);

        displayBMP.setColorFilter(iColor, PorterDuff.Mode.SRC_IN);

        displayBMP.draw(canvas);

        displayBMP.setColorFilter(null);
        displayBMP.setCallback(null);

        displayBMP = new BitmapDrawable(r, result);

        //result.recycle();

        return;
    }

    public void nextColor(Resources r) {

        iCurrentColor++;
        if( iCurrentColor >= MAX_COLORS ) {
            nextShape();
            iCurrentColor = 0;
        }
        changeColor(ObjColors[iCurrentColor], r);

        return;
    }

    private Bitmap rotate(Bitmap paramBitmap, int rotateAngle)
    {
        if (rotateAngle % 360 == 0) {
            return paramBitmap;
        }
        /*
        Matrix localMatrix = new Matrix();
        float w = paramBitmap.getWidth();
        float h = paramBitmap.getHeight();
        localMatrix.postTranslate(-w / 2, -h / 2);
        localMatrix.postRotate(rotateAngle);
        localMatrix.postTranslate(w/2, h/2);
        paramBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, (int)w, (int)h, localMatrix, true);
        //paramBitmap = Bitmap.createScaledBitmap(paramBitmap, (int) w, (int) h, false);

        new Canvas(paramBitmap).drawBitmap(paramBitmap, 0.0F, 0.0F, null);
        return paramBitmap;
        */
        //Bitmap arrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.generic2rb);

        // Create blank bitmap of equal size
        Bitmap canvasBitmap = paramBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasBitmap.eraseColor(0x00000000);

        // Create canvas
        Canvas canvas = new Canvas(canvasBitmap);

        // Create rotation matrix
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(rotateAngle, canvas.getWidth()/2, canvas.getHeight()/2);

        //Draw bitmap onto canvas using matrix
        canvas.drawBitmap(paramBitmap, rotateMatrix, null);

        //return new BitmapDrawable(canvasBitmap);
        return canvasBitmap;
    }

    public void rotateObject(float degrees, Resources r) {

        displayBMP = new BitmapDrawable(r, rotate(displayBMP.getBitmap(), (int) degrees));
        return;


/*
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);

        Bitmap original = displayBMP.getBitmap();

        int width = original.getWidth();
        int height = original.getHeight();

        Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);

        Canvas canvas = new Canvas(rotatedBitmap);

        //canvas.drawBitmap(rotatedBitmap, 0.0f, 0.0f, null);

        //displayBMP.setBounds(0, 0, width, height);
        //displayBMP.draw(canvas);

        //displayBMP.invalidateSelf();

        displayBMP = new BitmapDrawable(r, rotatedBitmap);

        return;
*/
    }

}
