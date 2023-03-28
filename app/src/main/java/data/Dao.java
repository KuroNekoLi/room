package data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@androidx.room.Dao
public interface Dao {

    @Insert
    long insert(MediaEntity mediaEntity);

    @Update
    void update(MediaEntity mediaEntity);

    @Delete
    void delete(MediaEntity mediaEntity);

    @Query("SELECT * FROM media_table")
    LiveData<List<MediaEntity>> getAllMedia();

    @Query("SELECT * FROM media_table WHERE id = :id")
    MediaEntity getMediaEntityById(long id);

}
