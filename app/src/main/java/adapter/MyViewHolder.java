package adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import data.MediaEntity;

import com.bumptech.glide.Glide;
import com.example.homework2rewrite.R;

import java.io.IOException;

public class MyViewHolder extends RecyclerView.ViewHolder
{
    private final ImageView imageView;
    private final ImageButton playAudioButton;
    private static MediaPlayer mediaPlayer;

    private MyViewHolder( View itemView, MyAdapter.OnImageClickListener onImageClickListener )
    {
        super( itemView );
        imageView = itemView.findViewById( R.id.imageView );
        playAudioButton = itemView.findViewById( R.id.playAudioButton );
        if ( mediaPlayer == null )
        {
            mediaPlayer = new MediaPlayer();
        }
    }

    public void bind( MediaEntity mediaEntity, MyAdapter.OnImageClickListener onImageClickListener )
    {
        String imageUri = mediaEntity.getImageUri();
        String audioUri = mediaEntity.getAudioUri();
        long id = mediaEntity.getId();
        Context context = imageView.getContext();
        try
        {
            Glide.with( context )
                    .load( imageUri )
                    .centerCrop()
                    .placeholder( R.drawable.image_placeholder )
                    .into( imageView );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        int thumbnailSize = 480;
        imageView.setLayoutParams( new RelativeLayout.LayoutParams( thumbnailSize, thumbnailSize ) );

        imageView.setOnClickListener( v ->
        {
            if ( onImageClickListener != null )
            {
                onImageClickListener.onImageClick( id );
            }
        } );

        playAudioButton.setOnClickListener( v ->
        {
            if ( audioUri == null )
            {
                Toast.makeText( context, "No audio available", Toast.LENGTH_SHORT ).show();
                return;
            }

            if ( !mediaPlayer.isPlaying() )
            {
                try
                {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource( audioUri ); // 使用錄音的URI
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
            else
            {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try
                {
                    mediaPlayer.setDataSource( audioUri ); // 使用錄音的URI
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        } );
    }

    static MyViewHolder create( ViewGroup parent, MyAdapter.OnImageClickListener onImageClickListener )
    {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_image, parent, false );
        return new MyViewHolder( view, onImageClickListener );
    }
}
