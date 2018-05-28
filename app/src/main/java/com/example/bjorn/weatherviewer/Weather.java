package com.example.bjorn.weatherviewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 *  @author Bjorn
 */
public class Weather {

    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;

    /**
     *
     * @param timeStamp
     * @param minTemp
     * @param maxTemp
     * @param humidity
     * @param description
     * @param iconName
     */
    public Weather(long timeStamp, double minTemp, double maxTemp, double humidity, String description, String iconName){

        // NumberFormat для форматирования температур в целое число
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayOfWeek = convertTimeStampToDay(timeStamp);
        this.minTemp = numberFormat.format(minTemp)+"\u00B0F";
        this.maxTemp = numberFormat.format(maxTemp)+"\u00B0F";
        this.humidity = NumberFormat.getPercentInstance().format(humidity/100.0);
        this.description = description;
        this.iconURL = "http://openweathermap.org/img/w/"+ iconName + ".png";
    }

    private static String convertTimeStampToDay(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp*1000);
        TimeZone timeZone = TimeZone.getDefault(); // Device`s Timezone

        // Collaboration to devices actual timezone
        calendar .add(Calendar.MILLISECOND, timeZone.getOffset(calendar.getTimeInMillis()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");

        return dateFormat.format(calendar.getTime());
    }

    public Weather(String dayOfWeek, String minTemp, String maxTemp, String humidity, String description, String iconURL) {
        this.dayOfWeek = dayOfWeek;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.description = description;
        this.iconURL = iconURL;
    }



}
