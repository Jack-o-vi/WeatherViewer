package com.example.bjorn.weatherviewer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Weather> weatherList = new ArrayList<>();


    // Adapter binds Weather`s objects with ListView elements
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherListView;
    public final static String TAG = "[MainActivity] MyLOGS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
       Log.d(TAG, "Find ListView and set to it the weatherArrayAdapter");

        weatherListView = (ListView) findViewById(R.id.weatherListView);
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);

        Log.d(TAG, "Add element to list and call notifyDataChanged");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Get EditView and URL");
                EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
                URL url = createURL(locationEditText.getText().toString());
                Log.d(TAG, "URL == " + url.toString());
                // Hide keyboard and run GetWeatherTask for getting weather data from OpenWeatherMap in the separate thread
                if (url != null) {
                    Log.d(TAG, "Dismiss keyboard and run teh GetWeatherTask");
                    dismissKeyboard(locationEditText);
                    GetWeatherTask getlocalWeatherTask = new GetWeatherTask();
                    getlocalWeatherTask.execute(url);
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private URL createURL(String city) {
        String apiKey = getString(R.string.api_key);
        String baseURL = getString(R.string.web_service_url);

        // units can take imperial (F), metric (C), standard (K)
        // cnt - days in the weather condition
        // APPID - key of te API
        final String requestCityProp = "&units=metric&cnt=16&APPID=";
        try {
            String encCity = URLEncoder.encode(city, "UTF-8");
            String urlString = baseURL + encCity + requestCityProp + apiKey;
            return new URL(urlString);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {
        private final static String TAG_GetWeatherTask = "[MainActivity.GetWeatherTask]";

        @Override
        protected JSONObject doInBackground(URL... urls) {
            Log.d(TAG_GetWeatherTask, "doInBackground ");
            HttpURLConnection connection = null;

            try {
                Log.d(TAG_GetWeatherTask, "doInBackground() url[0].openConnection() " + urls[0].toString());
                connection = (HttpURLConnection) urls[0].openConnection();
                int response = connection.getResponseCode();

                Log.d(TAG_GetWeatherTask, "doInBackground responseCode == " + response);
                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader((connection.getInputStream())))) {
                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        Log.d(TAG_GetWeatherTask, "doInBackground() JSON doc: " + builder.toString());
                    } catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    return new JSONObject(builder.toString());
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.d(TAG_GetWeatherTask, "onPostExecute()  convertJSONtoArrayList");
            //super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                convertJSONtoArrayList(jsonObject);

                Log.d(TAG_GetWeatherTask, "onPostExecute()  weatherArrayAdapter.notifyDataSetChanged();");
                weatherArrayAdapter.notifyDataSetChanged(); // Bind with ListView
                //weatherArrayAdapter.getView()
                weatherListView.smoothScrollToPosition(0);
                Log.d(TAG_GetWeatherTask, "onPostExecute()  weatherArrayAdapter.notifyDataSetChanged() Done");
                //Log.d(TAG_GetWeatherTask, "onPostExecute()  try to set adapter");
              //  weatherListView.setAdapter(weatherArrayAdapter);
            } else
                Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show();

        }


        // Creating Weather`s objects using JSONObject with condition
        private void convertJSONtoArrayList(JSONObject forecast) {
            Log.d(TAG_GetWeatherTask, "convertJSONtoArrayList()  weatherList.clear(), elements: " + weatherList.toString());
            weatherList.clear();

            try {
                JSONArray list = forecast.getJSONArray("list");

                for (int i = 0; i < list.length(); ++i) {
                    JSONObject day = list.getJSONObject(i); // data of the day
                    JSONObject main = day.getJSONObject("main");
                    //   JSONObject temperatures = main.getJSONObject("temp");

                    JSONObject weather = day.getJSONArray("weather").getJSONObject(0);

                    weatherList.add(new Weather(
                            day.getLong("dt"),
                            main.getDouble("temp_min"),
                            main.getDouble("temp_max"),
                            main.getDouble("humidity"),
                            weather.getString("description"),
                            weather.getString("icon")));
                    Log.d(TAG_GetWeatherTask, "convertJSONtoArrayList()  weatherList.add(), elements: " + Arrays.toString(new List[]{weatherList}));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
