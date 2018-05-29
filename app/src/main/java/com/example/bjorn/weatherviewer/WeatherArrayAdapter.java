package com.example.bjorn.weatherviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Child class of ArrayAdapter, which binds list objects with listviews.
 *
 * @author Bjorn
 * @version 2.0
 */
public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    // Logging tag
    private final static String TAG = "[WeatherArrayAdapter] MyLOGS";

    // HashMap of bitmaps
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    WeatherArrayAdapter(Context context, List<Weather> forecast) {
        super(context, -1, forecast);
        Log.d(TAG, "WeatherArrayAdapter.super(context, -1, forecast)");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, "getView() weatherList.getItem(position)");
        Weather day = getItem(position);

        ViewHolder viewHolder;

        Log.d(TAG, "getView() convertView == null" + (convertView == null));
        // Проверить возможность повтороного использования ViewHolder
        // для элемента, вышедшего за граниы экрана
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.conditionImageView = convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = convertView.findViewById(R.id.hiTextView);
            viewHolder.humidity = convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(viewHolder);
        } else {  // Существующий объект ViewHolder используется заново
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*
        Если значок погодных условй уже загружен, использует его:
        в противном случае загрузить в отдельном потоке
        */

        if (day != null && bitmaps.containsKey(day.iconURL)) {
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(day.iconURL));
        } else if(day != null){
            // Download and output logo of weather condition
            new LoadImageTask(viewHolder.conditionImageView).execute(day.iconURL);
        }

        // Get data from Weather`s object and fill views
        Context context = getContext();
        if(day != null){
            viewHolder.dayTextView.setText(context.getString(R.string.day_description, day.dayOfWeek, day.description));
            viewHolder.lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
            viewHolder.hiTextView.setText(context.getString(R.string.high_temp, day.maxTemp));
            viewHolder.humidity.setText(context.getString(R.string.humidity, day.humidity));
        }

        return convertView;
    }

    /**
     * ViewHolder  is a class which contains of views, which will be used with adapter.
     *
     * @author Bjorn
     */
    private static class ViewHolder {
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidity;

        ViewHolder() {
            Log.d(TAG, "ViewHolder()");
        }
    }


    /**
     * Class works in background and binds ImageView with bitmaps from weather site.
     *
     * @author Bjorn
     */
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        public static final String TAG_LoadImageTask = "LoadImageTask";
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            Log.d(TAG_LoadImageTask, "LoadImageTask(ImageView)");
            this.imageView = imageView;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG_LoadImageTask, "onPostExecute() setImageBitmap");
            imageView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d(TAG_LoadImageTask, "doInBackground() connecting to server for getting images for weather");
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();

                try (InputStream inputStream = connection.getInputStream();) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0], bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect(); // close HttpURLConnection
            }

            return bitmap;
        }
    }
}
