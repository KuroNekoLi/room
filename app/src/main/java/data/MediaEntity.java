package data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_table")
public class MediaEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "image_uri")
    private String imageUri;

    @ColumnInfo(name = "audio_uri")
    private String audioUri;

    public MediaEntity( String imageUri, String audioUri )
    {
        this.id = 0;
        this.imageUri = imageUri;
        this.audioUri = audioUri;
    }

    public long getId()
    {
        return id;
    }
    public void setId( long id )
    {
        this.id = id;
    }
    public String getImageUri()
    {
        return imageUri;
    }
    public void setImageUri( String imageUri )
    {
        this.imageUri = imageUri;
    }
    public String getAudioUri()
    {
        return audioUri;
    }
    public void setAudioUri( String audioUri )
    {
        this.audioUri = audioUri;
    }
}
