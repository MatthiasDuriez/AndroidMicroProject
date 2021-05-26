package com.example.chat2021;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.graphics.ColorUtils;

import static java.lang.Integer.parseInt;

public class ColorHandler {
    private int backgroundColor;
    private int secondColor;
    private int textColor;
    private int complementaryColor;

    public ColorHandler() { }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getSecondColor() {
        return secondColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getComplementaryColor() {
        return complementaryColor;
    }

    /**
     * This will generate the other colours that will be user in the app
     * @param
     */
    public void generateOther(int background){

        float[] hsv = new float[3];
        int newColor;

        backgroundColor = background;
        secondColor = lighter(background,0.5f);
        textColor = backgroundColor;

        String hexColor = String.format("#%06X", (0xFFFFFF & background));

        if(isTooLight(hexColor))
            textColor = darkenColor(backgroundColor,0.4f);

        complementaryColor = getComplementaryColor(backgroundColor);
    }

    private int getComplementaryColor(int backgroundColor) {

        int R = backgroundColor & 255;
        int G = (backgroundColor >> 8) & 255;
        int B = (backgroundColor >> 16) & 255;
        int A = (backgroundColor >> 24) & 255;

        R = 255 - R;
        G = 255 - G;
        B = 255 - B;

        return R + (G << 8) + ( B << 16) + ( A << 24);
    }
    /**
     * Lightens a color by a given factor.
     *
     * @param color
     *            The color to lighten
     * @param factor
     *            The factor to lighten the color. 0 will make the color unchanged. 1 will make the
     *            color white.
     * @return lighter version of the specified color.
     */
    public static int lighter(int color, float factor) {
        return ColorUtils.blendARGB(color, Color.WHITE, factor);
    }
    public static boolean isTooLight(String hexColor){

        int r = parseInt(hexColor.substring(1,3),16);
        int g = parseInt(hexColor.substring(3,5),16);
        int b = parseInt(hexColor.substring(5,7),16);

        int yiq = ((r*299)+(g*587)+(b*114))/1000;

        return yiq >= 128;
    }
    /**
     * determines if the color is dark or not, thanks to the luminance formula
     * @param color the color to examine
     * @return true if it's dark, false if not
     */
    public boolean isColorDark(int color){
        double darkness = 1-(0.299* Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;

        if(darkness<0.5){
            Log.i("LE4","LIGHT");
            return false; // It's a light color
        }
        else {
            Log.i("LE4","DARK");
            return true; // It's a dark color
        }
    }

    public int darkenColor(int color,float factor){

        return ColorUtils.blendARGB(color, Color.BLACK, factor);
    }
}
