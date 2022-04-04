package com.example.circulatio;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.os.IResultReceiver;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class MassageController {

    private static final int DEFAULT_MASSAGE_LENGTH = 300;
    private static final int DEFAULT_MASSAGE_TYPE = 14;
    private static final int DEFAULT_MASSAGE_INTENSITY = 2;

    private static final Map<String, Integer> DEFAULT_SETTINGS = createMap();

    private static Map<String, Integer> createMap() {
        Map<String, Integer> result = new HashMap<>();
        result.put("MassageLength", DEFAULT_MASSAGE_LENGTH);
        result.put("MassageType", DEFAULT_MASSAGE_TYPE);
        result.put("MassageIntensity", DEFAULT_MASSAGE_INTENSITY);
        return Collections.unmodifiableMap(result);
    }

    private static final String STOP_MESSAGE = "3";
    private static final String START_MESSAGE_HEADER = "2";

    public static String StopMessage() {
        return STOP_MESSAGE;
    }

    /**
     * Now redundant.
     *
     * @param context Applications Context
     * @return The message to send to the device,
     */
    public static String StartMessage(Context context) {
        String x = String.format("%02d", getType(context) + getIntensity(context));
        String y = String.format("%02d", getLength(context));

        return START_MESSAGE_HEADER + x + y;
    }

    /**
     * Gets the the type of massage that is to be performed.
     *
     * @param context The applications context
     * @return The type of massage.
     */
    public static int getType(Context context) {
        return loadData("MassageType",context);
    }

    /**
     * Sets the type of massage that is to be performed.
     *
     * @param type The type of massage.
     * @param context The applications context.
     */
    public static void setType(int type, Context context) {
        addData("MassageType", type, context);
    }

    /**
     * Gets the intensity of the massage that is to be performed.
     *
     * @param context The applications context.
     * @return The intensity of massage.
     */
    public static int getIntensity(Context context) {
        return loadData("MassageIntensity",context);
    }

    /**
     * Sets the intensity of the massage that is to be performed.
     *
     * @param intensity  The intensity of massage.
     * @param context The applications context.
     */
    public static void setIntensity(int intensity, Context context) {
        addData("MassageIntensity", intensity, context);
    }

    /**
     * Gets the length of the massage that is to be performed in seconds.
     *
     * @param context The applications context.
     * @return The length of the massage to be performed in seconds.
     */
    public static int getLength(Context context) {
        return loadData("MassageLength", context);
    }

    /**
     * Sets the length of massage that is to be performed in seconds.
     *
     * @param length The length of the massage in seconds.
     * @param context The applications context.
     */
    public static void setLength(int length, Context context) {
        addData("MassageLength", length, context);
    }

    /**
     * This method is used to change a setting in the massage settings.
     *
     * @param nameOfData The name of the data that is being set in the settings.
     * @param data The data that is being set in the settings.
     * @param context The applications context.
     */
    private static void addData(String nameOfData, int data, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "CirculatioMassageSettings", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(nameOfData, data);
        editor.commit();
    }

    /**
     * This method is used to get a setting from the massage settings.
     *
     * @param nameOfData The name of the data that is being retrieved from the settings.
     * @param context The applications context.
     * @return The setting
     */
    private static int loadData(String nameOfData, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "CirculatioMassageSettings", Context.MODE_PRIVATE);

        int data = sharedPreferences.getInt(nameOfData, DEFAULT_SETTINGS.get(nameOfData));

        return data;
    }

    /**
     * This method is used to delete the previous settings from the massage.
     *
     * @param context The applications context.
     */
    public static void deleteMassageSettings(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CirculatioUserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}