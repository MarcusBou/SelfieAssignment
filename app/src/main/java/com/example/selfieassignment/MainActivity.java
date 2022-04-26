package com.example.selfieassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = this.findViewById(R.id.picView);
    }

    /**Method for when button is clicked*/
    public void onClickButton(View view){
        dispatchCameraPictureIntent();
    }

    /** Take a Picture with the phones camera.*/
    private void dispatchCameraPictureIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();

            }
            catch (IOException e){
                Log.i("Error", e.getMessage());
            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    /**Portray a bitmap off the photo just taken.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setPic();
    }


    /**Method for upscaling an image that has just been taken!*/
    private void setPic() {
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bitmapOptions);
        int photoW = bitmapOptions.outWidth;
        int photoH = bitmapOptions.outHeight;
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;
        bitmapOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bitmapOptions);
        imageView.setImageBitmap(bitmap);
    }

    /**Creates An image file and saves it on the device*/
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.i("Error",e.getMessage());
        }
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**A method for saving image down to the phones gallery || NOT WORKING AS INTENDED YET*/
    public void onClickSavePhoto(View view){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contenturi = Uri.fromFile(f);
        mediaScanIntent.setData(contenturi);
        this.sendBroadcast(mediaScanIntent);
        Log.i("info", mediaScanIntent.getDataString());
    }
}