package com.example.pocketplanter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    static final String API_KEY = "aWtRM3FSWHQ3MUtqYmxrVVVpUEJqZz09";
    static final String API_URL = "https://trefle.io";
    EditText plantSearch;
    TextView results;
    Switch drought;
    Switch depth;
    Switch ph;
    Switch growth;
    Switch precip;
    Button search;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        results = (TextView) findViewById(R.id.textView4);
        plantSearch = (EditText) findViewById(R.id.editText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        search = (Button) findViewById(R.id.button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });
    }

    /**
     * this is modified code from https://www.androidauthority.com/use-remote-web-api-within-android-app-617869/ to help figure out how to work the web API
     * not really doing what we want it to, I don't know how to parse the results with StringBuilder
     * replace this with Volley???
     * first search the species API parse results and find main_species_ID
     * use main_species_ID to search under trefle.io/api/plantsID?token=API_KEY
     * parse those results and return the plant's Class, Division, Family, and Genus
     */

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        private Exception e;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            results.setText("");
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected String doInBackground(Void... urls) {
            String plant = plantSearch.getText().toString();
            try {
                URL url = new URL(API_URL + "/api/species?q=" + plant + "&token=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // me trying stuff (didn't work)
                //JSONArray array = new JSONArray(urlConnection.getInputStream());
                //int mainSpeciesID = 0;
                //for (int i = 0; i < array.length(); i++) {
                //    JSONObject o = array.getJSONObject(i);
                //    mainSpeciesID = o.getInt("main_species_id");
                //}
                //URL url2 = new URL(API_URL + "/api/plants" + mainSpeciesID + "?token="+ API_KEY);
                //HttpURLConnection url2Connection = (HttpURLConnection) url2.openConnection();
                //JSONArray array1 = new JSONArray(url2Connection.getInputStream());
                //String lastUpdated = "it either didn't work or the page hasn't been updated";
                //for (int i = 0; i < array1.length(); i++) {
                //    JSONObject o = array1.getJSONObject(i);
                //    lastUpdated = o.getString("last_update");
                //}
                //this is from android authority
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                if ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                urlConnection.disconnect();

                String jsongarbage = stringBuilder.toString();
                int index = jsongarbage.indexOf("main_species_id");
                return jsongarbage.substring(index + 17, index + 23);

                //woohoo!! we get the mainID (sometimes) and now we can use that to do other cool stuff
                // i should point out that trefle.io is kind of incomplete and there are a lot of null data points so I'm only going to try to pull the scientific name

            }
            catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "there was a problem";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            /**
             try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = array.getJSONObject(i);
                    mainSpeciesID = o.getInt("main_species_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
             */
            results.setText(response);
        }
    }
}
