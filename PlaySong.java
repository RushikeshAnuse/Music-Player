package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }


    TextView textView;
    SeekBar seekBar;
    ImageView play, previous, next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songslist");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        position = intent.getIntExtra("position", 0);
        textView.setSelected(true);

         Uri uri = Uri.parse(songs.get(position).toString());
             mediaPlayer = MediaPlayer.create(this, uri);
             mediaPlayer.start();
             seekBar.setMax(mediaPlayer.getDuration());






            //if user click on play button then following tasks to be performed
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        //if user click on next button then following tasks to be performed
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != songs.size() - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                textContent = songs.get(position).getName().replace(".mp3","").toString();
                textView.setText(textContent);
                seekBar.setMax(mediaPlayer.getDuration());
            }
        });

        //if user click on following previous button then following tasks to be performed
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0) {
                    position = position - 1;
                } else {
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                textContent = songs.get(position).getName().replace(".mp3","").toString();
                textView.setText(textContent);

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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


        //in this tasks you can change timing of songs
        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        };
        updateSeek.start();




        //here is the code to after completion of first song next song will play automatically
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                // Called when the current song is completed
               // for (int i = mediaPlayer.getCurrentPosition();i< songs.size(); i++) {
                    playNextSong();
            }
        });
    }

    public  void playNextSong () {
        mediaPlayer.stop();
        mediaPlayer.release();

            position = (position + 1) % songs.size(); // Move to the next song in the list (loop back to the first song if at the end)
            Uri uri = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(this,uri);
            play.setImageResource(R.drawable.pause);
            textContent = songs.get(position).getName().replace(".mp3","").toString();
            textView.setText(textContent);
           // seekBar.setMax(mediaPlayer.getDuration());

            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
        }
}

