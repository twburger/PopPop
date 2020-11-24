package com.twburger.me.poppoppop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;
//import java.util.concurrent.TimeUnit;

import static com.twburger.me.poppoppop.DisplayObject.MAX_INSTANCES;
import static com.twburger.me.poppoppop.AnimatedView.MAX_V;
import static com.twburger.me.poppoppop.AnimatedView.MIN_V;

public class MainActivity extends Activity {

    private static SoundPool soundPool;
    private static AudioManager audioManager;
    // Maximum sound stream.
    private static final int MAX_STREAMS = 5;
    // Stream type.
    private static final int streamType = AudioManager.STREAM_MUSIC;
    private static boolean bsoundLoaded;
    private static int soundIdBoyp;
    private static int soundIdBounce;
    private static int soundIdSwipe;
    private static int soundIdJump;
    private static int soundIdCollide;
    private static float volume;

    public static ArrayList<DisplayObject> DisplayObjectList = new ArrayList<DisplayObject>();

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private ImageView splashImageView;
    //boolean splashloading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // remove title and make a full screen app so no buttons are available
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Display the splash screen
        splashImageView = new ImageView(this);
        //splashImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        splashImageView.setImageResource(R.drawable.pop);
        splashImageView.setBackgroundColor(Color.BLACK);
        setContentView(splashImageView);
        //splashloading = true;

        //setContentView(R.layout.activity_main);

        // -------------------------------- SOUND
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Current volumn Index of particular stream type.
        float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);

        // Get the maximum volume index for a particular stream type.
        float maxVolumeIndex = (float) audioManager.getStreamMaxVolume(streamType);

        // Volumn (0 --> 1)
        this.volume = currentVolumeIndex / maxVolumeIndex;

        // Suggests an audio stream whose volume should be changed by
        // the hardware volume controls.
        this.setVolumeControlStream(streamType);

        // For Android SDK >= 21
        if (Build.VERSION.SDK_INT >= 21) { // LOLLIPOP

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        // for Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }


        // When Sound Pool load complete.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                bsoundLoaded = true;
            }
        });

        // Load sound files into SoundPool.
        this.soundIdBoyp = this.soundPool.load(this, R.raw.boyp2, 1);
        this.soundIdBounce = this.soundPool.load(this, R.raw.bounce, 1);
        this.soundIdSwipe = this.soundPool.load(this, R.raw.swipe, 1);
        this.soundIdJump = this.soundPool.load(this, R.raw.jump, 1);
        soundIdCollide = soundPool.load(this, R.raw.pop, 1);

        // --------------------------------------- Objects to display
        Context mContext = getApplicationContext();

        // if the list is already built do not redo
        Random r = new Random();
        int xV = 0;
        int yV = 0;
        if( !(DisplayObjectList.size() >= MAX_INSTANCES ) ) {
            for (int i = 0; i < MAX_INSTANCES; i++) {
                DisplayObjectList.add(i, new DisplayObject(mContext, getResources()));
                xV = r.nextInt(MAX_V-MIN_V)+MIN_V+1;
                yV = r.nextInt(MAX_V-MIN_V)+MIN_V+1;
                //xV = 0; yV = 0;
                DisplayObjectList.get(i).SetVelocity(xV, yV);
            }

            /*
            DisplayObjectList.get(0).SetVelocity(7, 14);
            DisplayObjectList.get(1).SetVelocity(8, 4);
            DisplayObjectList.get(2).SetVelocity(5, 3);
            DisplayObjectList.get(3).SetVelocity(13, 15);
            DisplayObjectList.get(4).SetVelocity(2, 6);
            DisplayObjectList.get(5).SetVelocity(7, 3);
            DisplayObjectList.get(6).SetVelocity(18, 12);
            */
        }

        // Pin the app to the screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 21
            startLockTask();
        }

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                //splashloading = false;
                setContentView(R.layout.activity_main); // run the active layout after the splash
            }

        }, SPLASH_DISPLAY_LENGTH);

    } // OnCreate

    // play boyp sound
    public static void playSoundWallBounce()  {
        if(bsoundLoaded)  {
            //float leftVolumn = volume/2;
            //float rightVolumn = volume/2;
            float v = volume/8;
            soundPool.play(soundIdBoyp,v, v, 1, 0, 1f);
        }
    }

    // play bounce sound
    public static void playSoundSelect()  {
        if(bsoundLoaded)  {
            soundPool.play(soundIdBounce,volume, volume, 1, 0, 1f);
        }
    }

    // play bounce sound
    public static void playSoundMove()  {
        if(bsoundLoaded)  {
            soundPool.play(soundIdSwipe, volume, volume, 1, 0, 1f);
        }
    }

    // play bounce sound
    public static void playSoundJump()  {
        if(bsoundLoaded)  {
            soundPool.play(soundIdJump, volume, volume, 1, 0, 1f);
        }
    }
    // play bounce sound
    public static void playSoundCollide()  {
        if(bsoundLoaded)  {
            soundPool.play(soundIdCollide, volume, volume, 1, 0, 1f);
        }
    }


} // class MainActivity

/*

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
*/