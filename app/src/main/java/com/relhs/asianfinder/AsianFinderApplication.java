package com.relhs.asianfinder;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Settings.Secure;
import android.widget.TextView;

import com.relhs.asianfinder.data.SpinnerItems;


public class AsianFinderApplication extends Application {

    public static String TAG_OS = "android";
    public static String TAG_STATUS = "status";
    public static String TAG_MESSAGE = "message";
    public static String TAG_DATA = "data";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    public static String getCurrentTimeStamp() {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();

        return ts;
    }

    public String getDeviceId() {
        return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;

    public static ArrayList<SpinnerItems> populateGender(){
        final ArrayList<SpinnerItems> spinnerItems = new ArrayList<SpinnerItems>();
        spinnerItems.add(new SpinnerItems("Gender","", false));
        spinnerItems.add(new SpinnerItems("Male","m", true));
        spinnerItems.add(new SpinnerItems("Female","f", true));
        return spinnerItems;
    }

//    public static ArrayList<SpinnerItems> populateAgeRangeDefault(String title, int startAge){
//        final ArrayList<SpinnerItems> spinnerItems = new ArrayList<SpinnerItems>();
//        spinnerItems.add(new SpinnerItems(title, "", false));
//        return spinnerItems;
//    }

    public static ArrayList<SpinnerItems> populateAgeRange(String title, int startAge){
        final ArrayList<SpinnerItems> spinnerItems = new ArrayList<SpinnerItems>();
        spinnerItems.add(new SpinnerItems(title, "", false));

        for (int x = startAge; x <= 99; x++) {
            spinnerItems.add(new SpinnerItems(x+" yrs Old",""+x, true));
        }

        return spinnerItems;
    }

    public static BitmapDrawable getTextAsBitmap(Context context, TextView upTextView, String text) {
        upTextView.setText(text);
        upTextView.measure(0, 0);
        upTextView.layout(0, 0, upTextView.getMeasuredWidth(),
                upTextView.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(upTextView.getMeasuredWidth(),
                upTextView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        upTextView.draw(canvas);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);

        return bitmapDrawable;
    }





}
