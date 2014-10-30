package com.relhs.asianfinder;

import android.app.Application;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.provider.Settings.Secure;


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


}
