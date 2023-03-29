package com.example.homework2rewrite;

import adapter.MyAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import data.MediaEntity;
import etc.MyViewModel;
import etc.OptionDialogFragment;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnImageClickListener
{
    private MyViewModel mViewModel;
    private MyAdapter.OnImageClickListener onImageClickListener;
    private MediaEntity mediaEntity;
    private static final int REQUEST_CODE_CAMERA = 1001;
    private static final int REQUEST_CODE_GALLERY = 2;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1002;
    private Uri photoUri;

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

        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
                builder.setTitle( "選擇操作" );
                builder.setItems( new CharSequence[]{"開啟相機", "選擇圖片"}, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick( DialogInterface dialog, int which )
                    {
                        switch ( which )
                        {
                            case 0:
                                Intent cameraIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                                if ( cameraIntent.resolveActivity( getPackageManager() ) != null )
                                {
                                    File photoFile = null;
                                    try
                                    {
                                        photoFile = createImageFile();
                                    }
                                    catch ( IOException ex )
                                    {
                                        ex.printStackTrace();
                                    }
                                    if ( photoFile != null )
                                    {
                                        photoUri = FileProvider.getUriForFile( MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", photoFile );
                                        cameraIntent.putExtra( MediaStore.EXTRA_OUTPUT, photoUri );
                                        startActivityForResult( cameraIntent, REQUEST_CODE_CAMERA );
                                    }
                                }
                                break;
                            case 1:
                                //跳轉到圖片選擇頁面並獲取所選圖片
                                Intent galleryIntent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                                startActivityForResult( galleryIntent, REQUEST_CODE_GALLERY );
                                break;
                        }
                    }
                } );
                builder.show();
            }
        } );
        permissionsCheck();
    }
    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir( Environment.DIRECTORY_PICTURES );
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }
    public void permissionsCheck()
    {
        List<String> permissionsNeeded = new ArrayList<>();
        if ( ContextCompat.checkSelfPermission( MainActivity.this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED )
        {
            permissionsNeeded.add( Manifest.permission.CAMERA );
        }
        if ( ContextCompat.checkSelfPermission( MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED )
        {
            permissionsNeeded.add( Manifest.permission.READ_EXTERNAL_STORAGE );
        }
        if ( ContextCompat.checkSelfPermission( MainActivity.this, Manifest.permission.RECORD_AUDIO ) != PackageManager.PERMISSION_GRANTED )
        {
            permissionsNeeded.add( Manifest.permission.RECORD_AUDIO );
        }
        if ( !permissionsNeeded.isEmpty() )
        {
            ActivityCompat.requestPermissions( MainActivity.this, permissionsNeeded.toArray( new String[0] ), REQUEST_CODE_READ_EXTERNAL_STORAGE );
        }
    }

    @Override
    public void onImageClick( long id )
    {
        MediaEntity mediaEntity = new MediaEntity( null,null );

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
    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );

        if ( resultCode == RESULT_OK )
        {
            switch ( requestCode )
            {
                case REQUEST_CODE_CAMERA:
                    String imageUriCamera = saveImageToGallery( photoUri );
                    //創建一個MediaEntity並初始化
                    createMediaEntity( imageUriCamera );
                    break;
                case REQUEST_CODE_GALLERY:
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query( uri, projection, null, null, null );
                    int columnIndex = cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA );
                    cursor.moveToFirst();
                    String imageUriGallery = cursor.getString( columnIndex );
                    cursor.close();
                    //創建一個MediaEntity並初始化
                    createMediaEntity( imageUriGallery );
                    break;
            }
        }
    }

    private void createMediaEntity( String filePath )
    {
        MediaEntity mediaEntity = new MediaEntity(null,null);
        mViewModel.setImageUri( mediaEntity,filePath );
        long id = mViewModel.insert( mediaEntity );
        mediaEntity.setId( id );
        showOptionDialog( mediaEntity );
    }

    private void showPermissionAlertDialog( String message, DialogInterface.OnDismissListener dismissListener )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "權限需求" );
        builder.setCancelable(false);
        builder.setMessage( message );
        builder.setPositiveButton( "確定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick( DialogInterface dialog, int which )
            {
                dialog.dismiss();
            }
        } );
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(dismissListener);
        alertDialog.show();
    }

    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults )
    {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if ( requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE )
        {
            for ( int i = 0; i < permissions.length; i++ )
            {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if ( Manifest.permission.CAMERA.equals( permission ) )
                {
                    if ( grantResult != PackageManager.PERMISSION_GRANTED )
                    {
                        showPermissionAlertDialog("應用需要相機權限才能正常運行。請在設置中開啟相機權限。", new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });
                        break;
                    }

                }
                else if ( Manifest.permission.READ_EXTERNAL_STORAGE.equals( permission ) )
                {
                    if ( grantResult != PackageManager.PERMISSION_GRANTED )
                    {
                        showPermissionAlertDialog("應用需要讀取儲存器權限才能正常運行。請在設置中開啟讀取儲存器權限。" , new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });
                        break;
                    }
                }
                else if ( Manifest.permission.RECORD_AUDIO.equals( permission ) )
                {
                    if ( grantResult != PackageManager.PERMISSION_GRANTED )
                    {
                        showPermissionAlertDialog("應用需要錄音權限才能正常運行。請在設置中開啟錄音權限。" , new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });
                        break;
                    }
                }
            }
        }
    }


    public void intentIdToAnotherActivity( Class<?> cls, String key, long id )
    {
        Intent intent = new Intent( MainActivity.this, cls );
        intent.putExtra( key, id );
        startActivity( intent );
    }
    private String saveImageToGallery( Uri photoUri )
    {
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        ContentValues values = new ContentValues();
        values.put( MediaStore.Images.Media.TITLE, fileName );
        values.put( MediaStore.Images.Media.MIME_TYPE, "image/jpeg" );
        Uri uri = getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values );

        try
        {
            InputStream inputStream = getContentResolver().openInputStream( photoUri );
            OutputStream outputStream = getContentResolver().openOutputStream( uri );
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ( (bytesRead = inputStream.read( buffer )) != -1 )
            {
                outputStream.write( buffer, 0, bytesRead );
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            return uri.toString();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return null;
    }
}