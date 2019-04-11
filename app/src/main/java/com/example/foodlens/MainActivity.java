package com.example.foodlens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageButton take_photo;
    ImageView photo;
    TextView result1;

    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        take_photo = (ImageButton) findViewById(R.id.take_photo);
        photo = (ImageView) findViewById(R.id.photo);
        result1 = (TextView) findViewById(R.id.result1);
        photo.setImageResource(R.drawable.default_image_thumbnail);

        take_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = getBitmap();
                photo.setImageBitmap(resizeBitmap(bitmap));
            }
        }
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Bitmap getBitmap() {

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);//, bmOptions);
        bitmap = orientateBitmap(bitmap);
        return bitmap;
    }

    private Bitmap orientateBitmap(Bitmap bitmap) {
        ExifInterface ei = null;

        try {
            ei = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public Bitmap resizeBitmap(Bitmap bitmap){
        int targetW = photo.getWidth();
        int targetH = photo.getHeight();
        int bmW = bitmap.getWidth();
        int bmH = bitmap.getHeight();

        //make it a right shape
        double ratioH = (double)targetH/(double)bmH;
        double ratioW = (double)targetW/(double)bmW;
        Bitmap transformedBitmap;
        int excess;

        if(ratioH<ratioW){
            transformedBitmap = Bitmap.createScaledBitmap(bitmap, (int)(bmW*ratioW), (int)(bmH*ratioW), false);
            excess = (int)(transformedBitmap.getHeight() - targetH);
            transformedBitmap = Bitmap.createBitmap(transformedBitmap, 0,excess/2, transformedBitmap.getWidth(), targetH);
        }else{
            transformedBitmap = Bitmap.createScaledBitmap(bitmap, (int)(bmW*ratioH), (int)(bmH*ratioH), false);
            excess = (int)(transformedBitmap.getWidth() - targetW);
            transformedBitmap = Bitmap.createBitmap(transformedBitmap, excess/2,0, targetW, transformedBitmap.getHeight());
        }

       /* if(bmW > bmH){
            excess = bmW-bmH;
            transformedBitmap = Bitmap.createBitmap(bitmap, excess/2,0,bmH, bmH);
        }
        else{
            excess = bmH-bmW;
            transformedBitmap = Bitmap.createBitmap(bitmap, 0,excess/2, bmW, bmW);
        }

        transformedBitmap = Bitmap.createScaledBitmap(transformedBitmap, targetW, targetH, false);*/

        return transformedBitmap;
    }

}