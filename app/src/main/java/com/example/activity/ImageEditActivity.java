package com.example.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import com.example.homework2rewrite.R;


import androidx.appcompat.app.AppCompatActivity;
import data.AppDatabase;
import data.MediaEntity;

public class ImageEditActivity extends AppCompatActivity
{
    private ImageView mImageView;
    private SeekBar mSeekBar;
    private MediaEntity mediaEntity;
    private Button m_saveButton;
    private String imagePath;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_image_edit );

        mImageView = findViewById( R.id.imageView );
        mSeekBar = findViewById( R.id.seekBar );
        m_saveButton = findViewById( R.id.saveButton );

        AppDatabase mDb = AppDatabase.getDatabase(this);
        long id = MediaUtil.getImageId( this );
        mediaEntity = mDb.Dao().getMediaEntityById( id );
        imagePath = mediaEntity.getImageUri();
        MediaUtil.loadImage(this, imagePath, mImageView);

        mSeekBar.setProgress( 0 );
        m_saveButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                onBackPressed();
            }
        } );
    }
}