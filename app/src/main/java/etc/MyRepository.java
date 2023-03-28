package etc;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.LiveData;
import data.AppDatabase;
import data.Dao;
import data.MediaEntity;

public class MyRepository
{
    private Dao mDao;
    private LiveData<List<MediaEntity>> mAllMediaEntities;

    MyRepository( Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDao = db.Dao();
        mAllMediaEntities = mDao.getAllMedia();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<MediaEntity>> getAllMediaEntities(){return mAllMediaEntities;}

    long insert(MediaEntity mediaEntity){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDao.insert(mediaEntity);
        });
        return mDao.insert(mediaEntity);
    }

    MediaEntity getMediaEntityById(long id){
        AppDatabase.databaseWriteExecutor.execute( ()->{
            mDao.getMediaEntityById(id);
        } );
        return mDao.getMediaEntityById(id);
    }
}
