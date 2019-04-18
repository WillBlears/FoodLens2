package wb.diss.foodlens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import wb.diss.foodlens.R;

public class Results extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        Boolean vegan = getIntent().getExtras().getBoolean("vegan");
        Boolean vegetarian = getIntent().getExtras().getBoolean("vegetarian");
        Boolean balanced = getIntent().getExtras().getBoolean("balanced");
        Boolean nut_free = getIntent().getExtras().getBoolean("nut_free");
        Boolean low_fat = getIntent().getExtras().getBoolean("low_fat");
        Boolean high_protein = getIntent().getExtras().getBoolean("high_protein");
        Boolean low_carb = getIntent().getExtras().getBoolean("low_carb");
        Boolean low_sugar = getIntent().getExtras().getBoolean("low_sugar");
        final ArrayList<String> health_labels = new ArrayList<String>();

        ((CollapsingToolbarLayout)findViewById(R.id.toolbar_layout)).setTitle(query);

        final int num_results = 10;
        final int num_text_views = 6;

        final RequestQueue[] queue = new RequestQueue[1];
        final String[] imageURLs = new String[num_results];

        final TextView[][] all_text = new TextView[num_text_views][num_results];
        final ImageView[] all_images = new ImageView[num_results];
        final TextView[] all_cals = new TextView[num_results];
        final RelativeLayout[] all_relatives = new RelativeLayout[num_results];
        final Bitmap[] images = new Bitmap[num_results];
        View[] seperators = new View[num_results - 1];

        all_text[0][0] = (TextView) this.findViewById(R.id.textView0);
        all_text[1][0] = (TextView) this.findViewById(R.id.textView1);
        all_text[2][0] = (TextView) this.findViewById(R.id.textView2);
        all_text[3][0] = (TextView) this.findViewById(R.id.textView3);
        all_cals[0] = (TextView) this.findViewById(R.id.calories0);
        all_images[0] = (ImageView) this.findViewById(R.id.image0);
        all_relatives[0] = (RelativeLayout) this.findViewById(R.id.relative0);

        all_text[0][1] = (TextView) this.findViewById(R.id.textView4);
        all_text[1][1] = (TextView) this.findViewById(R.id.textView5);
        all_text[2][1] = (TextView) this.findViewById(R.id.textView6);
        all_text[3][1] = (TextView) this.findViewById(R.id.textView7);
        all_cals[1] = (TextView) this.findViewById(R.id.calories1);
        all_images[1] = (ImageView) this.findViewById(R.id.image1);
        all_relatives[1] = (RelativeLayout) this.findViewById(R.id.relative1);

        all_text[0][2] = (TextView) this.findViewById(R.id.textView8);
        all_text[1][2] = (TextView) this.findViewById(R.id.textView9);
        all_text[2][2] = (TextView) this.findViewById(R.id.textView10);
        all_text[3][2] = (TextView) this.findViewById(R.id.textView11);
        all_cals[2] = (TextView) this.findViewById(R.id.calories2);
        all_images[2] = (ImageView) this.findViewById(R.id.image2);
        all_relatives[2] = (RelativeLayout) this.findViewById(R.id.relative2);

        all_text[0][3] = (TextView) this.findViewById(R.id.textView12);
        all_text[1][3] = (TextView) this.findViewById(R.id.textView13);
        all_text[2][3] = (TextView) this.findViewById(R.id.textView14);
        all_text[3][3] = (TextView) this.findViewById(R.id.textView15);
        all_cals[3] = (TextView) this.findViewById(R.id.calories3);
        all_images[3] = (ImageView) this.findViewById(R.id.image3);
        all_relatives[3] = (RelativeLayout) this.findViewById(R.id.relative3);

        all_text[0][4] = (TextView) this.findViewById(R.id.textView16);
        all_text[1][4] = (TextView) this.findViewById(R.id.textView17);
        all_text[2][4] = (TextView) this.findViewById(R.id.textView18);
        all_text[3][4] = (TextView) this.findViewById(R.id.textView19);
        all_cals[4] = (TextView) this.findViewById(R.id.calories4);
        all_images[4] = (ImageView) this.findViewById(R.id.image4);
        all_relatives[4] = (RelativeLayout) this.findViewById(R.id.relative4);

        all_text[0][5] = (TextView) this.findViewById(R.id.textView20);
        all_text[1][5] = (TextView) this.findViewById(R.id.textView21);
        all_text[2][5] = (TextView) this.findViewById(R.id.textView22);
        all_text[3][5] = (TextView) this.findViewById(R.id.textView23);
        all_cals[5] = (TextView) this.findViewById(R.id.calories5);
        all_images[5] = (ImageView) this.findViewById(R.id.image5);
        all_relatives[5] = (RelativeLayout) this.findViewById(R.id.relative5);

        all_text[0][6] = (TextView) this.findViewById(R.id.textView24);
        all_text[1][6] = (TextView) this.findViewById(R.id.textView25);
        all_text[2][6] = (TextView) this.findViewById(R.id.textView26);
        all_text[3][6] = (TextView) this.findViewById(R.id.textView27);
        all_cals[6] = (TextView) this.findViewById(R.id.calories6);
        all_images[6] = (ImageView) this.findViewById(R.id.image6);
        all_relatives[6] = (RelativeLayout) this.findViewById(R.id.relative6);

        all_text[0][7] = (TextView) this.findViewById(R.id.textView28);
        all_text[1][7] = (TextView) this.findViewById(R.id.textView29);
        all_text[2][7] = (TextView) this.findViewById(R.id.textView30);
        all_text[3][7] = (TextView) this.findViewById(R.id.textView31);
        all_cals[7] = (TextView) this.findViewById(R.id.calories7);
        all_images[7] = (ImageView) this.findViewById(R.id.image7);
        all_relatives[7] = (RelativeLayout) this.findViewById(R.id.relative7);

        all_text[0][8] = (TextView) this.findViewById(R.id.textView32);
        all_text[1][8] = (TextView) this.findViewById(R.id.textView33);
        all_text[2][8] = (TextView) this.findViewById(R.id.textView34);
        all_text[3][8] = (TextView) this.findViewById(R.id.textView35);
        all_cals[8] = (TextView) this.findViewById(R.id.calories8);
        all_images[8] = (ImageView) this.findViewById(R.id.image8);
        all_relatives[8] = (RelativeLayout) this.findViewById(R.id.relative8);

        all_text[0][9] = (TextView) this.findViewById(R.id.textView36);
        all_text[1][9] = (TextView) this.findViewById(R.id.textView37);
        all_text[2][9] = (TextView) this.findViewById(R.id.textView38);
        all_text[3][9] = (TextView) this.findViewById(R.id.textView39);
        all_cals[9] = (TextView) this.findViewById(R.id.calories9);
        all_images[9] = (ImageView) this.findViewById(R.id.image9);
        all_relatives[9] = (RelativeLayout) this.findViewById(R.id.relative9);

        seperators[0] = (View) this.findViewById(R.id.view);
        seperators[1] = (View) this.findViewById(R.id.view1);
        seperators[2] = (View) this.findViewById(R.id.view2);
        seperators[3] = (View) this.findViewById(R.id.view3);
        seperators[4] = (View) this.findViewById(R.id.view4);
        seperators[5] = (View) this.findViewById(R.id.view5);
        seperators[6] = (View) this.findViewById(R.id.view6);
        seperators[7] = (View) this.findViewById(R.id.view7);
        seperators[8] = (View) this.findViewById(R.id.view8);

        //create url
        String url = "https://api.edamam.com/search?q=" + query + "&app_id=f7ff52ad&app_key=732f1e9caa51e14c47e9aa639538ce33&from=0&to=" + String.valueOf(num_results);
        if (vegan) {
            url = url + "&health=vegan";
            health_labels.add("vegan");
        }
        if (vegetarian) {
            url = url + "&health=vegetarian";
            health_labels.add("vegetarian");
        }
        if (nut_free) {
            url = url + "&health=tree-nut-free";
            health_labels.add("nut-free");
        }
        if (low_sugar) {
            url = url + "&health=sugar-conscious";
            health_labels.add("low-sugar");
        }
        if (balanced) {
            url = url + "&diet=balanced";
            health_labels.add("balanced");
        }
        if (low_fat) {
            url = url + "&diet=low-fat";
            health_labels.add("low-fat");
        }
        if (high_protein) {
            url = url + "&diet=high-protein";
            health_labels.add("high-protein");
        }
        if (low_carb) {
            url = url + "&diet=low-carb";
            health_labels.add("low-carb");
        }

        int num_labels = health_labels.size();
        int label_split = num_labels / 2;
        final String[] label_strings = {"", ""};
        int iLabel;
        for (iLabel = 0; iLabel < num_labels - label_split; iLabel++) {
            label_strings[0] = " " + health_labels.get(iLabel) + label_strings[0];
        }
        for (iLabel = num_labels - label_split; iLabel < num_labels; iLabel++) {
            label_strings[1] = " " + health_labels.get(iLabel) + label_strings[1];
        }

        for (int i = 0; i < seperators.length; i++)
            seperators[i].setBackgroundColor(Color.rgb(230, 230, 230));

        try {
            queue[0] = Volley.newRequestQueue(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                JSONArray hits = null;
                final JSONObject[] recipes = new JSONObject[num_results];

                try {
                    JSONObject reader = new JSONObject(response);
                    hits = reader.getJSONArray("hits");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int result = 0; result < num_results; result++) {
                    try {
                        recipes[result] = hits.getJSONObject(result).getJSONObject("recipe");
                        imageURLs[result] = recipes[result].getString("image");
                        all_text[0][result].setText(recipes[result].getString("label"));

                        all_text[1][result].setText(label_strings[0]);
                        all_text[2][result].setText(label_strings[1]);

                        int yield = (Integer) recipes[result].getInt("yield");
                        all_text[3][result].setText("Serves: " + yield);
                        final int cals = (int) recipes[result].getDouble("calories") / yield;
                        all_cals[result].setText(cals + " calories");

                        final int final_result = result;
                        final String recipe_string = recipes[result].toString();

                        ImageRequest imageRequest = new ImageRequest(imageURLs[result], new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                all_images[final_result].setImageBitmap(bitmap);
                                images[final_result] = bitmap;
                            }

                        }, 0, 0, null, null,
                                new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        all_images[final_result].setImageResource(R.drawable.default_image_thumbnail);
                                    }
                                });
                        queue[0].add(imageRequest);

                        all_relatives[result].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent recipe_intent = new Intent(Results.this, Recipe.class);
                                recipe_intent.putExtra("recipe", recipe_string);
                                startActivity(recipe_intent);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (hits.length() == 0) {
                    all_text[0][0].setText("There are no results for your search.");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                all_text[0][0].setText("API free limit exceeded! Wait a minute.");
            }
        });

        // Add the request to the RequestQueue.
        queue[0].add(stringRequest);
    }
}
