package com.example.foodlens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    ImageButton take_photo;
    ImageView photo;
    TextView result1;
    TextView result2;
    TextView result3;
    TextView result4;
    TextView result5;

    CheckBox vegan;
    CheckBox vegetarian;
    CheckBox balanced;
    CheckBox high_protein;
    CheckBox low_carb;
    CheckBox low_fat;
    CheckBox low_sugar;
    CheckBox nut_free;

    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;

    float[] top_5_prob;
    String[] top_5_name;
    FirebaseModelInterpreter firebaseInterpreter;
    FirebaseModelInputOutputOptions inputOutputOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        take_photo = (ImageButton) findViewById(R.id.take_photo);
        photo = (ImageView) findViewById(R.id.photo);

        result1 = (TextView) findViewById(R.id.result1);
        result2 = (TextView) findViewById(R.id.result2);
        result3 = (TextView) findViewById(R.id.result3);
        result4 = (TextView) findViewById(R.id.result4);
        result5 = (TextView) findViewById(R.id.result5);

        vegan = (CheckBox) findViewById(R.id.checkBox);
        vegetarian = (CheckBox) findViewById(R.id.checkBox1);
        balanced = (CheckBox) findViewById(R.id.checkBox2);
        high_protein = (CheckBox) findViewById(R.id.checkBox3);
        low_carb = (CheckBox) findViewById(R.id.checkBox4);
        low_fat = (CheckBox) findViewById(R.id.checkBox5);
        low_sugar = (CheckBox) findViewById(R.id.checkBox6);
        nut_free = (CheckBox) findViewById(R.id.checkBox7);

        top_5_prob = new float[5];
        top_5_name = new String[5];

        photo.setBackgroundColor(Color.rgb(230, 230, 230));

        take_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        result1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launch_search(top_5_name[0]);
            }
        });
        result2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launch_search(top_5_name[1]);
            }
        });
        result3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launch_search(top_5_name[2]);
            }
        });
        result4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launch_search(top_5_name[3]);
            }
        });
        result5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launch_search(top_5_name[4]);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bitmap = getBitmap();
                thinking_textviews();
                photo.setImageBitmap(resizeBitmap(bitmap));
                firebase_setup();
                get_top_5(bitmap);
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
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
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

    public Bitmap resizeBitmap(Bitmap bitmap) {
        int targetW = photo.getWidth();
        int targetH = photo.getHeight();
        int bmW = bitmap.getWidth();
        int bmH = bitmap.getHeight();

        //make it a right shape
        double ratioH = (double) targetH / (double) bmH;
        double ratioW = (double) targetW / (double) bmW;
        Bitmap transformedBitmap;
        int excess;

        if (ratioH < ratioW) {
            transformedBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bmW * ratioW), (int) (bmH * ratioW), false);
            excess = (int) (transformedBitmap.getHeight() - targetH);
            transformedBitmap = Bitmap.createBitmap(transformedBitmap, 0, excess / 2, transformedBitmap.getWidth(), targetH);
        } else {
            transformedBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bmW * ratioH), (int) (bmH * ratioH), false);
            excess = (int) (transformedBitmap.getWidth() - targetW);
            transformedBitmap = Bitmap.createBitmap(transformedBitmap, excess / 2, 0, targetW, transformedBitmap.getHeight());
        }

        return transformedBitmap;
    }

    private void firebase_setup() {
        int input_size = 299;

        FirebaseLocalModel localSource =
                new FirebaseLocalModel.Builder("food_classifier")  // Assign a name to this model
                        .setAssetFilePath("model_quantized.tflite")
                        .build();
        FirebaseModelManager.getInstance().registerLocalModel(localSource);

        FirebaseModelOptions options = new FirebaseModelOptions.Builder()
                .setLocalModelName("food_classifier")
                .build();
        try {
            firebaseInterpreter = FirebaseModelInterpreter.getInstance(options);
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, input_size, input_size, 3})
                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 101})
                    .build();
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
    }

    private void get_top_5(Bitmap bitmap) {

        final SortedMap<Float, String> probabilities = new TreeMap<Float, String>(Collections.reverseOrder());
        int input_dims = 299;
        int BMH = bitmap.getHeight();
        int BMW = bitmap.getWidth();
        if(BMH > BMW)
            bitmap = bitmap.createBitmap(bitmap, 0, (int)((BMH-BMW)/2), BMW, BMW);
        else
            bitmap = bitmap.createBitmap(bitmap, (int)((BMW-BMH)/2), 0, BMH, BMH);

        bitmap = Bitmap.createScaledBitmap(bitmap, input_dims, input_dims, true);
        float[][][][] input = new float[1][input_dims][input_dims][3];
        for (int x = 0; x < input_dims; x++) {
            for (int y = 0; y < input_dims; y++) {
                int pixel = bitmap.getPixel(x, y);
                input[0][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                input[0][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                input[0][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
            }
        }

 //       FirebaseModelInputs inputs = null;

        try {
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder().add(input).build();

            firebaseInterpreter.run(inputs, inputOutputOptions)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseModelOutputs>() {
                                @Override
                                public void onSuccess(FirebaseModelOutputs result) {
                                    float[][] output = result.getOutput(0);
                                    float[] prob_output = output[0];
                                    try {
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
                                        for (int cat = 0; cat < 101; cat++) {
                                            probabilities.put(prob_output[cat], reader.readLine());
                                        }
                                        reader.close();
                                        for(int i = 0; i < 5; i++){
                                            float key = probabilities.firstKey();
                                            top_5_prob[i] = key;
                                            top_5_name[i] = probabilities.get(key);
                                            probabilities.remove(key);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    set_result_textviews();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
    }

    protected void set_result_textviews(){
        DecimalFormat df = new DecimalFormat("#.##");
        result1.setText(top_5_name[0] + "? - " + String.valueOf(df.format(top_5_prob[0]*100)) + "%");
        result2.setText(top_5_name[1] + "? - " + String.valueOf(df.format(top_5_prob[1]*100)) + "%");
        result3.setText(top_5_name[2] + "? - " + String.valueOf(df.format(top_5_prob[2]*100)) + "%");
        result4.setText(top_5_name[3] + "? - " + String.valueOf(df.format(top_5_prob[3]*100)) + "%");
        result5.setText(top_5_name[4] + "? - " + String.valueOf(df.format(top_5_prob[4]*100)) + "%");
    }

    protected void thinking_textviews(){
        result1.setText("thinking...");
        result2.setText("");
        result3.setText("");
        result4.setText("");
        result5.setText("");
    }

    protected void launch_search(String query){
        Intent query_intent = new Intent(MainActivity.this, Results.class);
        query_intent.putExtra("query", query);
        query_intent.putExtra("vegan", vegan.isChecked());
        query_intent.putExtra("vegetarian", vegetarian.isChecked());
        query_intent.putExtra("balanced", balanced.isChecked());
        query_intent.putExtra("nut_free", nut_free.isChecked());
        query_intent.putExtra("high_protein", high_protein.isChecked());
        query_intent.putExtra("low_carb", low_carb.isChecked());
        query_intent.putExtra("low_sugar", low_sugar.isChecked());
        query_intent.putExtra("sugar_free", low_fat.isChecked());
        startActivity(query_intent);
    }
}