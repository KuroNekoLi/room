package com.example.activity;

import adapter.MyAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import data.MediaEntity;
import etc.MyViewModel;
import etc.OptionDialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.homework2rewrite.R;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnImageClickListener
{
    private MyViewModel mViewModel;
    private MyAdapter.OnImageClickListener onImageClickListener;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        RecyclerView recyclerView = findViewById( R.id.recyclerView );
        MyAdapter.MediaEntityDiff mediaEntityDiff = new MyAdapter.MediaEntityDiff();
        MyAdapter adapter = new MyAdapter(mediaEntityDiff, onImageClickListener);
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager( this, 2 );
        layoutManager.setSpanSizeLookup( new GridLayoutManager.SpanSizeLookup()
        {
            @Override
            public int getSpanSize( int position )
            {
                return 1;
            }
        } );
        recyclerView.setLayoutManager( layoutManager );

        // Get a new or existing ViewModel from the ViewModelProvider.
        mViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mViewModel.getAllMediaEntities().observe(this, mediaEntities -> {
            // Update the cached copy of the words in the adapter.
            adapter.submitList(mediaEntities);
        });
    }

    @Override
    public void onImageClick( long id )
    {
        MediaEntity mediaEntity = findMediaEntityById( id );
        if ( mediaEntity != null )
        {
            showOptionDialog( mediaEntity );
        }
        else
        {
            Log.e( "MainActivity", "MediaEntity not found for imageUri: " + id );
        }
    }
    private void showOptionDialog( MediaEntity mediaEntity )
    {
        OptionDialogFragment dialogFragment = OptionDialogFragment.newInstance( mediaEntity );
        dialogFragment.show( getSupportFragmentManager(), "option_dialog" );
    }
    public void intentIdToAnotherActivity( Class<?> cls, String key, long id )
    {
        Intent intent = new Intent( MainActivity.this, cls );
        intent.putExtra( key, id );
        startActivity( intent );
    }
    public MediaEntity findMediaEntityById( long id )
    {
        for ( MediaEntity mediaEntity : mMediaList )
        {
            if ( mediaEntity.getId() == id )
            {
                return mediaEntity;
            }
        }
        return null;
    }
}