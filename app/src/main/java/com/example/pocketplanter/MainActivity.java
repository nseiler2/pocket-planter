package com.example.pocketplanter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        private Exception e;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            results.setText("");
        }

        protected String doInBackground(Void... urls) {
            String plant = plantSearch.getText().toString();
            try {
                URL url = null;
                url = new URL(API_URL + "/api/species?q=" + plant + "&token=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("/n");
                }
                bufferedReader.close();
                urlConnection.disconnect();
                return stringBuilder.toString();
            }
            catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "There was a problem";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            results.setText(response);
        }
    }
}
