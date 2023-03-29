package etc;

import android.app.Application;

import java.util.List;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import data.MediaEntity;

public class MyViewModel extends AndroidViewModel
{
    private MyRepository mRepository;
    private final LiveData<List<MediaEntity>> mAllMediaEntities;

    public MyViewModel( Application application) {
        super(application);
        mRepository = new MyRepository( application );
        mAllMediaEntities = mRepository.getAllMediaEntities();
    }

    public LiveData<List<MediaEntity>> getAllMediaEntities(){
        return mAllMediaEntities;
    }

    long insert(MediaEntity mediaEntity){
        mRepository.insert( mediaEntity );
        return mRepository.insert( mediaEntity );
    }
}
