package com.example.MPlay;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class PlaySong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView songname;
    ImageView play, prev, next, shuffle, repeat;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekbar;
    Thread updateSeek;
    boolean repeatFlag;
    boolean shuffleFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout_playsong);
        View view =getSupportActionBar().getCustomView();


       ImageView imageButton = (ImageButton) view.findViewById(R.id.action_bar_back);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PlaySong.this, MainActivity.class);
                startActivity(intent);
            }
        });

        songname = findViewById(R.id.songname);
        play = findViewById(R.id.play);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);
        seekbar = findViewById(R.id.seekbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        songname.setText(textContent);
        songname.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekbar.setMax(mediaPlayer.getDuration());

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while(currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                    mediaPlayer.start();
                }

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if(shuffleFlag&&!repeatFlag){
                    position = getRandom(songs.size()-1);
                }
                else if(!shuffleFlag&&!repeatFlag){
                    if(position!=0){
                        position = position - 1;
                    }
                    else{
                        position = songs.size() - 1;
                    }
                }

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                seekbar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                songname.setText(textContent);
            }
            private int getRandom(int i) {

                Random random = new Random();
                return random.nextInt(i+1);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if(shuffleFlag&&!repeatFlag){
                    position = getRandom(songs.size()-1);
                }
                else if(!shuffleFlag&&!repeatFlag){
                    if(position!=songs.size()-1){
                        position = position + 1;
                    }
                    else{
                        position = 0;
                    }
                }

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                seekbar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                songname.setText(textContent);

            }

            private int getRandom(int i) {

                Random random = new Random();
                return random.nextInt(i+1);
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleFlag){
                    shuffleFlag = false;
                    shuffle.setImageResource(R.drawable.ic_baseline_shuffle_24);
                }
                else{
                    shuffleFlag = true;
                    repeatFlag = false;
                    shuffle.setImageResource(R.drawable.ic_baseline_shuffleon_24);
                    repeat.setImageResource(R.drawable.ic_baseline_repeat_24);
                }

            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatFlag){
                    repeatFlag = false;
                    repeat.setImageResource(R.drawable.ic_baseline_repeat_24);
                }
                else{
                    repeatFlag = true;
                    shuffleFlag = false;
                    shuffle.setImageResource(R.drawable.ic_baseline_shuffle_24);
                    repeat.setImageResource(R.drawable.ic_baseline_repeaton_24);
                }
            }
        });

    }
}