package com.example.homework2rewrite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import data.MediaEntity;

public class OptionDialogFragment extends DialogFragment
{
    private static final String IMAGE_URI_KEY = "image_uri";
    private static final String MEDIA_ENTITY_ID_KEY = "media_entity_id";

    public OptionDialogFragment() {
    }

    public static OptionDialogFragment newInstance( MediaEntity mediaEntity) {
        OptionDialogFragment fragment = new OptionDialogFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URI_KEY, mediaEntity.getImageUri());
        args.putLong(MEDIA_ENTITY_ID_KEY, mediaEntity.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( @Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_option, null);
        builder.setView(view);

        ImageView imageView = view.findViewById( R.id.imageView);
        String imageUri = getArguments().getString(IMAGE_URI_KEY);
        Glide.with(this).load(imageUri).into(imageView);

        builder.setPositiveButton("編輯相片", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                long mediaEntityId = getArguments().getLong(MEDIA_ENTITY_ID_KEY);
                ((MainActivity) requireActivity()).intentIdToAnotherActivity( ImageEditActivity.class, "image_id", mediaEntityId);
            }
        });
        builder.setNegativeButton("錄音", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long mediaEntityId = getArguments().getLong(MEDIA_ENTITY_ID_KEY);
                ((MainActivity) requireActivity()).intentIdToAnotherActivity(AudioRecordActivity.class,"image_id", mediaEntityId);
            }
        });
        builder.setNeutralButton("新增", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
