package com.example.circulatio;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This class is used to access and interact with information about the user.
 */
public class User {

    private static boolean dataLoaded;

    private static String name;
    private static String deviceID;
    private static boolean manualRead;

    /**
     * This method check if the users data is valid before a new user is created.
     *
     * @param name The name of the user.
     * @param deviceID The Circulatio Device ID
     * @param manualRead Confirmation User has Read Manual
     * @return True if user data is valid, otherwise false.
     */
    public static boolean isValidUserData(String name, String deviceID, boolean manualRead) {
        boolean validName = !(name == "");
        boolean validDeviceID = true;

        return (validName && validDeviceID && manualRead);
    }

    /**
     * This method sets and stores the initial user data.
     *
     * @param name The name of the user.
     * @param deviceID The Circulatio Device ID
     * @param manualRead Confirmation User has Read Manual
     * @param context The applications context
     */
    public static void createUser(String name, String deviceID, boolean manualRead, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "CirculatioUserData", Context.MODE_PRIVATE);

        Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("deviceID",deviceID);
        editor.putBoolean("manualRead",manualRead);
        editor.commit();

        loadUserData(context);
    }

    /**
     * This method loads the user data if it exists otherwise it indicates its not existence.
     *
     * @param context The applications context
     * @return True if loading user data was successful otherwise False.
     */
    public static boolean loadUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "CirculatioUserData", Context.MODE_PRIVATE);

        //User Exists Already
        if (sharedPreferences.contains("name")) {
            name = sharedPreferences.getString("name", name);
            deviceID = sharedPreferences.getString("deviceID",deviceID);
            manualRead = sharedPreferences.getBoolean("manualRead",manualRead);

            return true;
        }
        //User Does Not Exist Already
        else {
            return false;
        }
    }
}
