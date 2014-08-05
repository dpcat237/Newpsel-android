package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.service.PlayerService;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.SongRepository;

import java.util.concurrent.TimeUnit;

public class PlayerActivity extends Activity{
    public TextView songName,startTimeField,endTimeField;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private ImageButton playButton,pauseButton;
    public static int oneTimeOnly = 0;
    private static final String TAG = "NPS:PlayerActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        songName = (TextView)findViewById(R.id.textView4);
        startTimeField =(TextView)findViewById(R.id.textView1);
        endTimeField =(TextView)findViewById(R.id.textView2);
        seekbar = (SeekBar)findViewById(R.id.seekBar1);
        playButton = (ImageButton)findViewById(R.id.imageButton1);
        pauseButton = (ImageButton)findViewById(R.id.imageButton2);
        songName.setText("song.mp3");

        /*File voicesFolder = fileService.getVoicesFolder();
        String fileName = voicesFolder.getAbsolutePath()+"/test2.wav";
        Uri uri  = Uri.parse("file://" + fileName);*/

        //mediaPlayer = MediaPlayer.create(this, uri);
        seekbar.setClickable(false);
        pauseButton.setEnabled(false);



        //AlarmReceiver alarm = new AlarmReceiver();
        //alarm.setAlarm(this);

        /*Intent service = new Intent(this, DownloadSongsService.class);
        startService(service);*/
    }

    public void play(View view){
        /*Toast.makeText(getApplicationContext(), "Playing sound",
                Toast.LENGTH_SHORT).show();
        mediaPlayer.start();
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        if(oneTimeOnly == 0){
            seekbar.setMax((int) finalTime);
            oneTimeOnly = 1;
        }

        endTimeField.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) finalTime)))
        );
        startTimeField.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
        );
        seekbar.setProgress((int)startTime);
        myHandler.postDelayed(UpdateSongTime,100);
        pauseButton.setEnabled(true);
        playButton.setEnabled(false);*/
        playTwo();
    }

    public void playTwo() {
        SongRepository songRepo = new SongRepository(this);
        songRepo.open();

        /*Song songOne = new Song();
        songOne.setListId(1);
        songOne.setItemId(1);
        songOne.setListTitle("List 1");
        songOne.setTitle("song 1");
        songOne.setType(Song.TYPE_ITEM_TITLE);
        songOne.setFilename("test.wav");
        songRepo.addSong(songOne);

        Song songTwo = new Song();
        songTwo.setListId(1);
        songTwo.setItemId(2);
        songTwo.setListTitle("List 1");
        songTwo.setTitle("song 2");
        songTwo.setType(Song.TYPE_ITEM_TITLE);
        songTwo.setFilename("test2.wav");
        songRepo.addSong(songTwo);

        Log.d(TAG, "tut: songs added");*/

        //ArrayList<Song> songs = songRepo.getSongs(SongConstants.GRABBER_TYPE_TITLE, 6);
        //Integer quant = songs.size();

        //Log.d(TAG, "tut: count to play "+quant.toString());

        Log.d(TAG, "tut: PlayerService.play");
        PlayerService.play(this, SongConstants.GRABBER_TYPE_TITLE, 6);

        /*File voicesFolder = fileService.getVoicesFolder();
        String fileName = voicesFolder.getAbsolutePath()+"/test.wav";
        Log.d(TAG, "tut: "+fileName);*/

        //PlayerService.play(this, songOne);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            startTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };
    public void pause(View view){
        Toast.makeText(getApplicationContext(), "Pausing sound",
                Toast.LENGTH_SHORT).show();

        mediaPlayer.pause();
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
    }
    public void forward(View view){
        int temp = (int)startTime;
        if((temp+forwardTime)<=finalTime){
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Cannot jump forward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }
    public void rewind(View view){
        int temp = (int)startTime;
        if((temp-backwardTime)>0){
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Cannot jump backward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}