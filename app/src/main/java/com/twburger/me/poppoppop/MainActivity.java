package com.twburger.me.poppoppop;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import java.util.ArrayList;
import static com.twburger.me.poppoppop.DisplayObject.MAX_INSTANCES;

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
    private static float volume;

    public static ArrayList<DisplayObject> DisplayObjectList = new ArrayList<DisplayObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

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

        // --------------------------------------- Objects to display
        Context mContext = getApplicationContext();

        // if the list is already built do not redo
        if( !(DisplayObjectList.size() >= MAX_INSTANCES ) ) {
            for (int i = 0; i < MAX_INSTANCES; i++) {
                DisplayObjectList.add(i, new DisplayObject(mContext, getResources()));
            }

            DisplayObjectList.get(0).SetVelocity(7, 14);
            DisplayObjectList.get(1).SetVelocity(8, 4);
            DisplayObjectList.get(2).SetVelocity(5, 3);
            DisplayObjectList.get(3).SetVelocity(13, 15);
            DisplayObjectList.get(4).SetVelocity(2, 6);
            DisplayObjectList.get(5).SetVelocity(7, 3);
            DisplayObjectList.get(6).SetVelocity(18, 12);
        }

        // Pin the app to the screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 21
            startLockTask();
        }

    } // OnCreate

    // play boyp sound
    public static void playSoundWallBounce()  {
        if(bsoundLoaded)  {
            float leftVolumn = volume/2;
            float rightVolumn = volume/2;
            // Play sound of gunfire. Returns the ID of the new stream.
            int streamId = soundPool.play(soundIdBoyp,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }

    // play bounce sound
    public static void playSoundSelect()  {
        if(bsoundLoaded)  {
            float leftVolumn = volume;
            float rightVolumn = volume;

            // Play sound objects destroyed. Returns the ID of the new stream.
            int streamId = soundPool.play(soundIdBounce,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }

    // play bounce sound
    public static void playSoundMove()  {
        if(bsoundLoaded)  {
            float leftVolumn = volume;
            float rightVolumn = volume;

            // Play sound objects destroyed. Returns the ID of the new stream.
            int streamId = soundPool.play(soundIdSwipe,leftVolumn, rightVolumn, 1, 0, 1f);
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