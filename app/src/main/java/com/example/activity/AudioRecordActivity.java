package com.example.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.homework2rewrite.R;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import data.AppDatabase;
import data.MediaEntity;

public class AudioRecordActivity extends AppCompatActivity
{

    private static final String LOG_TAG = "AudioRecordTest";
    private static String fileName = null;
    private Button recordButton = null;
    private MediaRecorder recorder = null;
    private Button playButton = null;
    private MediaPlayer player = null;
    private String imagePath;
    private Button saveButton = null;
    private ImageView mImageView;
    private MediaEntity mediaEntity;
    private AppDatabase mDb;

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate( Bundle icicle )
    {
        super.onCreate( icicle );
        setContentView( R.layout.activity_audio_record );

        mDb = AppDatabase.getDatabase(this);
        fileName = getExternalCacheDir().getAbsolutePath() + "/audiorecordtest_" + System.currentTimeMillis() + ".3gp";
        //fileName = getExternalCacheDir().getAbsolutePath(); //每次新錄音會覆蓋上一次的
        //fileName += "/audiorecordtest.3gp";
        // Initialize recordButton
        recordButton = findViewById( R.id.record_button );
        recordButton.setOnClickListener( new View.OnClickListener()
        {
            boolean mStartRecording = true;

            @Override
            public void onClick( View v )
            {
                onRecord( mStartRecording );
                if ( mStartRecording )
                {
                    recordButton.setText( "Stop recording" );
                }
                else
                {
                    recordButton.setText( "Start recording" );
                }
                mStartRecording = !mStartRecording;
            }
        } );
        // Initialize playButton
        playButton = findViewById( R.id.play_button );
        playButton.setOnClickListener( new View.OnClickListener()
        {
            boolean mStartPlaying = true;

            @Override
            public void onClick( View v )
            {
                onPlay( mStartPlaying );
                if ( mStartPlaying )
                {
                    playButton.setText( "Stop playing" );
                }
                else
                {
                    playButton.setText( "Start playing" );
                }
                mStartPlaying = !mStartPlaying;
            }
        } );

        // Initialize saveButton
        saveButton = findViewById( R.id.save_button );
        saveButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                saveRecording();
            }
        } );

        mImageView = findViewById( R.id.imageView );

        long id = MediaUtil.getImageId( this );
        mediaEntity = mDb.Dao().getMediaEntityById( id );
        imagePath = mediaEntity.getImageUri();
        MediaUtil.loadImage(this, imagePath, mImageView);
    }

    private void onRecord( boolean start )
    {
        if ( start )
        {
            startRecording();
        }
        else
        {
            stopRecording();
        }
    }

    private void onPlay( boolean start )
    {
        if ( start )
        {
            startPlaying();
        }
        else
        {
            stopPlaying();
        }
    }

    private void startPlaying()
    {
        player = new MediaPlayer();
        try
        {
            player.setDataSource( fileName );
            player.prepare();
            player.start();
        }
        catch ( IOException e )
        {
            Log.e( LOG_TAG, "prepare() failed" );
        }
    }

    private void stopPlaying()
    {
        player.release();
        player = null;
    }

    private void startRecording()
    {
        recorder = new MediaRecorder();
        recorder.setAudioSource( MediaRecorder.AudioSource.MIC );
        recorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );
        recorder.setOutputFile( fileName );
        recorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB );

        try
        {
            recorder.prepare();
        }
        catch ( IOException e )
        {
            Log.e( LOG_TAG, "prepare() failed" );
        }

        recorder.start();
    }

    private void stopRecording()
    {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    // Save file to the AudioEntity
    private void saveRecording()
    {
        mediaEntity.setAudioUri( fileName );
        mDb.Dao().update( mediaEntity );
        setResult( Activity.RESULT_OK );
        finish();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if ( recorder != null )
        {
            recorder.release();
            recorder = null;
        }

        if ( player != null )
        {
            player.release();
            player = null;
        }
    }
}