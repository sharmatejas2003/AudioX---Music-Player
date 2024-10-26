package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    MediaPlayer player;
    AudioManager audioManager;
    Timer timer;
    private int currentSongIndex = 0;
    private int[] songs = {R.raw.on, R.raw.blessings, R.raw.sevenyears};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize MediaPlayer with the first song
        player = MediaPlayer.create(this, songs[currentSongIndex]);

        // Initialize AudioManager for volume control
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        SeekBar volumeSeekBar = findViewById(R.id.volumeseekbar);
        volumeSeekBar.setMax(maxVol);
        volumeSeekBar.setProgress(curVol);

        // Volume SeekBar listener
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Duration SeekBar setup
        final SeekBar durationSeekBar = findViewById(R.id.durationseek);
        durationSeekBar.setMax(player.getDuration());

        // Timer for updating the SeekBar
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (player.isPlaying()) {
                    durationSeekBar.setProgress(player.getCurrentPosition());
                }
            }
        }, 0, 1000);

        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Play the current song
    public void play(View view) {
        if (player != null) {
            player.start();
        }
    }

    // Pause the current song
    public void pause(View view) {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    // Stop the current song
    public void stop(View view) {
        if (timer != null) {
            timer.cancel(); // Stop the timer when the music stops
        }
        if (player != null) {
            player.stop();
            player.release();
            player = null; // Release the player
        }
    }

    // Play the next song
    public void nex(View view) {
        currentSongIndex = (currentSongIndex + 1) % songs.length; // Loop to the first song
        playCurrentSong();
    }

    // Play the previous song
    public void prev(View view) {
        currentSongIndex = (currentSongIndex - 1 + songs.length) % songs.length; // Loop to the last song if needed
        playCurrentSong();
    }

    // Play the current song based on the index
    private void playCurrentSong() {
        if (player != null) {
            player.stop();
            player.release();
        }
        player = MediaPlayer.create(this, songs[currentSongIndex]);
        player.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
