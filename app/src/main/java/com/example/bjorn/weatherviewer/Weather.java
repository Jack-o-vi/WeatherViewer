package com.example.bjorn.weatherviewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * POJO class, which keeps information about dayOfWeek, low and high temperature of the time
 humidity, iconURL and brief description of the sky view
 *
 * @author Bjorn
 * @version 2.0
 */
public class Weather {

    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;


    /** Unicode symbol of celsius */
    public static final String CELSIUS = "\u00B0C";

    /**
     * @param timeStamp
     * @param minTemp
     * @param maxTemp
     * @param humidity
     * @param description
     * @param iconName
     */
    public Weather(long timeStamp, double minTemp, double maxTemp, double humidity, String description, String iconName) {

        // NumberFormat for formatting temperatures to integer
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayOfWeek = convertTimeStampToDay(timeStamp);
        this.minTemp = numberFormat.format(minTemp) + CELSIUS;
        this.maxTemp = numberFormat.format(maxTemp) + CELSIUS;
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.description = description;
        this.iconURL = "http://openweathermap.org/img/w/" + iconName + ".png";
    }

    /**
     *
     * @param dayOfWeek
     * @param minTemp
     * @param maxTemp
     * @param humidity
     * @param description
     * @param iconURL
     */
    public Weather(String dayOfWeek, String minTemp, String maxTemp, String humidity, String description, String iconURL) {
        this.dayOfWeek = dayOfWeek;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.description = description;
        this.iconURL = iconURL;
    }

    /**
     * Converts long tieStamp to String as a dayOfWeek, with considering an actual timezone of the device.
     * @param timeStamp is long-type timeStamp
     * @return string representation
     */
    private static String convertTimeStampToDay(long timeStamp) {
        Calendar calendar = Calendar.getInstance();

        // Get
        calendar.setTimeInMillis(timeStamp * 1000);
        TimeZone timeZone = TimeZone.getDefault(); // Device`s Timezone

        // Collaboration to devices actual timezone
        calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.getTimeInMillis()));

        // Formatter for date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");

        return dateFormat.format(calendar.getTime());
    }

    @Override
    public String toString() {
        return "Weather{" +
                "dayOfWeek='" + dayOfWeek + '\'' +
                ", minTemp='" + minTemp + '\'' +
                ", maxTemp='" + maxTemp + '\'' +
                ", humidity='" + humidity + '\'' +
                ", description='" + description + '\'' +
                ", iconURL='" + iconURL + '\'' +
                '}';
    }
}
