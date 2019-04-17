package com.example.foodlens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;

public class Recipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView recipe_image_view = (ImageView) findViewById(R.id.recipe_image);
        TextView title_view = (TextView) findViewById(R.id.title);
        TextView portion_view = (TextView) findViewById(R.id.portion);
        TextView labels_view = (TextView) findViewById(R.id.labels);
        TextView fat_view = (TextView) findViewById(R.id.fat);
        TextView sugar_view = (TextView) findViewById(R.id.sugar);
        TextView salt_view = (TextView) findViewById(R.id.salt);
        TextView ingredients_view = (TextView) findViewById(R.id.ingredients);
        TextView link_view = (TextView) findViewById(R.id.recipe_link);

        try {
            JSONObject recipe = new JSONObject(getIntent().getStringExtra("recipe"));

            //image
            Picasso.get().load(recipe.getString("image")).into(recipe_image_view);

            //title
            String title = recipe.getString("label");
            title_view.setText(title);

            //portions
            int yield = recipe.getInt("yield");
            int portion_weight = (int) (recipe.getDouble("totalWeight") / yield);
            portion_view.setText("This recipe will serve " + yield + " x " + portion_weight + "g portions.");

            //labels
            JSONArray label_array = recipe.getJSONArray("dietLabels");
            JSONArray health_array = recipe.getJSONArray("healthLabels");
            for (int i = 0; i < health_array.length(); i++)
                label_array.put(health_array.getString(i));
            String labels_string = "";
            for (int label = 0; label < label_array.length(); label++)
                labels_string = labels_string + "\r\n" + label_array.getString(label);
            labels_view.setText(labels_string);

            //GDAs
            double fat = 0;
            double sugar = 0;
            double salt = 0;
            DecimalFormat df = new DecimalFormat("#.#");
            JSONObject all_nutrients = recipe.getJSONObject("totalNutrients");

            fat = all_nutrients.getJSONObject("FAT").getDouble("quantity") / yield;
            fat_view.setText("fat: " + df.format(fat) + "g");
            sugar =all_nutrients.getJSONObject("SUGAR").getDouble("quantity") / yield;
            sugar_view.setText("sugar: " + df.format(sugar) + "g");
            salt =all_nutrients.getJSONObject("NA").getDouble("quantity") / yield;
            salt_view.setText("salt: " + df.format(salt) + "mg");

            if(fat/portion_weight < 0.03)
                fat_view.setBackgroundColor(Color.parseColor("#80008E09"));
            else if(fat/portion_weight < 0.175)
                fat_view.setBackgroundColor(Color.parseColor("#80FFBF00"));
            else
                fat_view.setBackgroundColor(Color.parseColor("#80FA2A00"));

            if(sugar/portion_weight < 0.05)
                sugar_view.setBackgroundColor(Color.parseColor("#80008E09"));
            else if(sugar/portion_weight < 0.225)
                sugar_view.setBackgroundColor(Color.parseColor("#80FFBF00"));
            else
                sugar_view.setBackgroundColor(Color.parseColor("#80FA2A00"));

            if(salt/portion_weight < 0.003)
                salt_view.setBackgroundColor(Color.parseColor("#80008E09"));
            else if(salt/portion_weight < 0.015)
                salt_view.setBackgroundColor(Color.parseColor("#80FFBF00"));
            else
                salt_view.setBackgroundColor(Color.parseColor("#80FA2A00"));

            //ingredients
            JSONArray ingredients_array = recipe.getJSONArray("ingredients");
            String ingredients_string = "";
            for (int ing = 0; ing < ingredients_array.length(); ing++)
                ingredients_string += ingredients_array.getJSONObject(ing).getString("text") + "\r\n";
            ingredients_view.setText(ingredients_string);

            //URL
            final String url = recipe.getString("url");
            link_view.setText("See the full recipe here:\r\n" + url);
            link_view.setPaintFlags(link_view.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            link_view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            portion_view.setText("Error");
        }
    }
}