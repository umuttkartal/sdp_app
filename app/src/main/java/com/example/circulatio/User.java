package com.example.circulatio;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Locale;

/**
 * This class is used to access and interact with information about the user.
 */
public class User {

    private static boolean dataLoaded;

    private static String name;
    private static String deviceID;
    private static String pin;
    private static boolean manualRead;

    public static String getName() {
        assert(dataLoaded) : "Data has not been loaded. Programming Error";

        return name;
    }

    public static boolean changeName(String newName, Context context) {
        boolean changed;
        if (!newName.isEmpty() && isValidName(newName)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "CirculatioUserData", Context.MODE_PRIVATE);

            Editor editor = sharedPreferences.edit();
            editor.putString("name", fixName(newName));

            editor.commit();
            loadUserData(context);
            changed = true;
        }
        else {
            changed = false;
        }

        return changed;
    }

    public static String getdeviceID() {
        assert(dataLoaded) : "Data has not been loaded. Programming Error";

        return deviceID;
    }

    public static String getPin() {
        assert(dataLoaded) : "Data has not been loaded. Programming Error";

        return pin;
    }

    public static boolean isManualRead() {
        assert(dataLoaded) : "Data has not been loaded. Programming Error";

        return manualRead;
    }

    /**
     * This method check if the users data is valid before a new user is created.
     *
     * @param name The name of the user.
     * @param deviceID The Circulatio Device ID
     * @param pin The Circulatio Device Pin
     * @param manualRead Confirmation User has Read Manual
     * @return True if user data is valid, otherwise false.
     * @see User#isValidName(String name)
     */
    public static boolean isValidUserData(String name, String deviceID, String pin, boolean manualRead) {
        boolean validName = isValidName(name);
        boolean validDeviceID = true;
        boolean validPin = isValidPin(pin);

        return (validName && validDeviceID && validPin && manualRead);
    }

    /**
     * This method is used to check if the name provided by the user is valid.
     *
     * @param name The name of the user.
     * @return True if the user name is valid, otherwise false.
     */
    private static boolean isValidName(String name) {
        boolean notBlank = !(name == "");
        boolean noSpaces = !(name.contains(" "));


        return notBlank && noSpaces && name.length() < 21;
    }

    /**
     * This method is used to check if the pin provided by the user is valid.
     *
     * @param pin The Circulatio Device Pin
     * @return True if the pin is valid, otherwise false.
     */
    private static boolean isValidPin(String pin) {
        boolean isLengthFive = pin.length() == 5;

        return isLengthFive;
    }

    /**
     * This method sets and stores the initial user data.
     *
     * @param name The name of the user.
     * @param deviceID The Circulatio Device ID
     * @param pin The Circulatio Device Pin
     * @param manualRead Confirmation User has Read Manual
     * @param context The applications context
     */
    public static void createUser(String name, String deviceID, String pin, boolean manualRead, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "CirculatioUserData", Context.MODE_PRIVATE);

        name = fixName(name);

        Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("deviceID",deviceID);
        editor.putString("pin", pin);
        editor.putBoolean("manualRead",manualRead);
        editor.commit();

        loadUserData(context);
    }

    /**
     * Capitalise the first letter of the name then makes the rest lowercase.
     *
     * @param name The name of the user.
     * @return The fixed name.
     */
    private static String fixName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
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
            pin = sharedPreferences.getString("pin",pin);
            manualRead = sharedPreferences.getBoolean("manualRead",manualRead);

            dataLoaded = true;
        }
        //User Does Not Exist Already
        else {
            dataLoaded = false;
        }

        return dataLoaded;
    }

    /**
     * This method deletes the user data.
     *
     * @param context The applications context
     */
    public static void deleteUserData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CirculatioUserData", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
