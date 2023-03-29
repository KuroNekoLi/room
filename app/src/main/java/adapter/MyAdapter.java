package adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import data.MediaEntity;

import android.view.ViewGroup;

import java.util.List;

public class MyAdapter extends ListAdapter<MediaEntity, MyViewHolder> {
    private OnImageClickListener mOnImageClickListener;

    public MyAdapter(@NonNull DiffUtil.ItemCallback<MediaEntity> diffCallback, OnImageClickListener onImageClickListener) {
        super(diffCallback);
        mOnImageClickListener = onImageClickListener;
    }

    public interface OnImageClickListener {
        void onImageClick(long id);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return MyViewHolder.create(parent, mOnImageClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MediaEntity current = getItem(position);
        holder.bind(current, mOnImageClickListener);
    }

    public static class MediaEntityDiff extends DiffUtil.ItemCallback<MediaEntity> {
        @Override
        public boolean areItemsTheSame(@NonNull MediaEntity oldItem, @NonNull MediaEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MediaEntity oldItem, @NonNull MediaEntity newItem) {
            return oldItem.getImageUri().equals(newItem.getImageUri()) &&
                    oldItem.getAudioUri().equals(newItem.getAudioUri());
        }
    }
}