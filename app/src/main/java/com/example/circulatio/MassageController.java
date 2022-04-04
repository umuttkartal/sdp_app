package com.example.circulatio;

import android.content.Context;
import android.content.SharedPreferences;

public class MassageController {

    private static final int DEFAULT_MASSAGE_LENGTH = 300;
    private static final int DEFAULT_MASSAGE_TYPE = 0;
    private static final int DEFAULT_MASSAGE_INTENSITY = 2;

    private static final String STOP_MESSAGE = "3";
    private static final String START_MESSAGE_HEADER = "2";

    public static String StopMessage() {
        return STOP_MESSAGE;
    }

    public static String StartMessage(Context context) {
        String x = String.format("%02d", getType(context) + getIntensity(context));
        String y = String.format("%02d", getLength(context));

        return START_MESSAGE_HEADER + x + y;
    }

    public static int getType(Context context) {
        return loadData("MassageType",context);
    }

    public static void setType(int type, Context context) {
        addData("MassageType", type, context);
    }

    public static int getIntensity(Context context) {
        return loadData("MassageIntensity",context);
    }

    public static void setIntensity(int intensity, Context context) {
        addData("MassageIntensity", intensity, context);
    }

    public static Integer getLength(Context context) {
        return loadData("MassageLength", context);
    }

    public static void setLength(Integer length, Context context) {
        addData("MassageLength", length, context);
    }

    public static void addData(String nameOfData, int data, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "CirculatioMassageSettings", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(nameOfData, data);
        editor.commit();
    }

    public static int loadData(String nameOfData, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "CirculatioMassageSettings", Context.MODE_PRIVATE);

        int data = sharedPreferences.getInt(nameOfData, 0);

        return data;
    }

    public static void deleteMassageSettings(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CirculatioUserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}