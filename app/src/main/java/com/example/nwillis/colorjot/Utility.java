package com.example.nwillis.colorjot;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.example.nwillis.colorjot.data.NoteContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by N Willis on 29/05/2015.
 */
public class Utility{
    //holds string values for text size and the int size of text
    public static Map<String, Integer> textSizeStringToInt;

    public static final String TEXT_SIZE_SMALL_STRING = "small";
    public static final String TEXT_SIZE_MEDIUM_STRING="medium";
    public static final String TEXT_SIZE_LARGE_STRING="large";
    public static final int TEXT_SIZE_SMALL_INT = 14;
    public static final int TEXT_SIZE_MEDIUM_INT = 18;
    public static final int TEXT_SIZE_LARGE_INT = 22;

    //codes for Dialog fragments
    public static final int REQUEST_CODE_TEXT_SIZE = 00;
    public static final int REQUEST_CODE_NOTE_OPTIONS = 01;
    public static final int REQUEST_CODE_DELETE_NOTE = 02;

    public static final String[] NOTE_COLUMNS = {
            NoteContract.NoteEntry._ID,
            NoteContract.NoteEntry.COLUMN_TITLE,
            NoteContract.NoteEntry.COLUMN_TEXT,
            NoteContract.NoteEntry.COLUMN_TEXT_SIZE,
            NoteContract.NoteEntry.COLUMN_TEXT_COLOR,
            NoteContract.NoteEntry.COLUMN_NOTE_COLOR
    };

    public static final int COL_NOTE_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_TEXT = 2;
    public static final int COL_TEXT_SIZE = 3;
    public static final int COL_TEXT_COLOR = 4;
    public static final int COL_NOTE_COLOR = 5;

    //key used whenever the id of a note is passed in an intent or dialog
    public static final String noteIdKey = "noteId";

    static{
        textSizeStringToInt = new HashMap<String, Integer>();
        textSizeStringToInt.put(TEXT_SIZE_SMALL_STRING, TEXT_SIZE_SMALL_INT);
        textSizeStringToInt.put(TEXT_SIZE_MEDIUM_STRING, TEXT_SIZE_MEDIUM_INT);
        textSizeStringToInt.put(TEXT_SIZE_LARGE_STRING, TEXT_SIZE_LARGE_INT);
    }

    /**
     * Gets the preference value corresponding to "text_size_key"
     * @param context Context
     * @return String value of text size
     */
    public static String getPreferenceTextSize(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_text_size_key), context.getString(R.string.pref_text_size_default));
    }

    /**
     * Gets the preference value corresponding to "calendar_colorkey". This
     * is the note header color
     * @param context Context
     * @return int color
     */
    public static int getPreferenceNoteColor(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(context.getString(R.string.note_color_key), context.getResources().getColor(R.color.orange_header));
    }

    /**
     * Gets the preference value coresponding to "text_color_key"
     * @param context Context
     * @return int color
     */
    public static int getPreferenceTextColor(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(context.getResources().getString(R.string.text_color_key), context.getResources().getColor(R.color.white));
    }

    /**
     * Given the note header color returns the corresponding note body color
     * @param context Context
     * @param noteHeaderColor int color of note header
     * @return int note body color
     */
    public int getNoteBodyColor(Context context, int noteHeaderColor){
        Map<Integer, Integer> headerToBodyColors = new HashMap<Integer, Integer>();
        Resources resources = context.getResources();
        headerToBodyColors.put(resources.getColor(R.color.yellow_header), resources.getColor(R.color.yellow_body));
        headerToBodyColors.put(resources.getColor(R.color.blue_header), resources.getColor(R.color.blue_body));
        headerToBodyColors.put(resources.getColor(R.color.red_header), resources.getColor(R.color.red_body));
        headerToBodyColors.put(resources.getColor(R.color.green_header), resources.getColor(R.color.green_body));
        headerToBodyColors.put(resources.getColor(R.color.orange_header), resources.getColor(R.color.orange_body));
        headerToBodyColors.put(resources.getColor(R.color.purple_header), resources.getColor(R.color.purple_body));
        headerToBodyColors.put(resources.getColor(R.color.light_grey_header), resources.getColor(R.color.light_grey_body));
        headerToBodyColors.put(resources.getColor(R.color.grey_header), resources.getColor(R.color.grey_body));
        headerToBodyColors.put(resources.getColor(R.color.black_header), resources.getColor(R.color.black_body));
        return headerToBodyColors.get(noteHeaderColor);
    }

    /**
     * Given the note header color, returns the corresponding drawable for the note color button
     * @param context Context
     * @param noteColor the note header color
     * @return int the drawable for the button
     */
    public int getNoteColorButtonImage(Context context, int noteColor){
        Map<Integer, Integer> colorToDrawable = new HashMap<Integer, Integer>();
        Resources resources = context.getResources();
        colorToDrawable.put(resources.getColor(R.color.yellow_header), R.drawable.yellow_circle);
        colorToDrawable.put(resources.getColor(R.color.blue_header), R.drawable.blue_circle);
        colorToDrawable.put(resources.getColor(R.color.red_header), R.drawable.red_circle);
        colorToDrawable.put(resources.getColor(R.color.green_header), R.drawable.green_circle);
        colorToDrawable.put(resources.getColor(R.color.orange_header), R.drawable.orange_circle);
        colorToDrawable.put(resources.getColor(R.color.purple_header), R.drawable.purple_circle);
        colorToDrawable.put(resources.getColor(R.color.light_grey_header), R.drawable.white_circle);
        colorToDrawable.put(resources.getColor(R.color.grey_header), R.drawable.grey_circle);
        colorToDrawable.put(resources.getColor(R.color.black_header), R.drawable.black_circle);
        return colorToDrawable.get(noteColor);
    }

    /**
     * Given the text color, returns the corresponding drawable for the text color button
     * @param context Context
     * @param textColor the text color
     * @return int drawable for the button
     */
    public int getTextColorButtonImage(Context context, int textColor){
        Map<Integer, Integer> colorToDrawable = new HashMap<Integer, Integer>();
        Resources resources = context.getResources();
        colorToDrawable.put(resources.getColor(R.color.yellow_header), R.drawable.yellow_font);
        colorToDrawable.put(resources.getColor(R.color.blue_header), R.drawable.blue_font);
        colorToDrawable.put(resources.getColor(R.color.red_header), R.drawable.red_font);
        colorToDrawable.put(resources.getColor(R.color.green_header), R.drawable.green_font);
        colorToDrawable.put(resources.getColor(R.color.orange_header), R.drawable.orange_font);
        colorToDrawable.put(resources.getColor(R.color.purple_header), R.drawable.purple_font);
        colorToDrawable.put(resources.getColor(R.color.light_grey_header), R.drawable.white_font);
        colorToDrawable.put(resources.getColor(R.color.grey_header), R.drawable.grey_font);
        colorToDrawable.put(resources.getColor(R.color.black_header), R.drawable.black_font);
        return colorToDrawable.get(textColor);
    }




    //NOT MY WORK
    //Class used from https://github.com/gabrielemariotti/colorpickercollection
    /**
     * Utility class for colors
     *
     * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
     *
     */
    public static class ColorUtils{

        /**
         * Create an array of int with colors
         *
         * @param context
         * @return
         */
        public static int[] colorChoice(Context context){

            int[] mColorChoices=null;
            String[] color_array = context.getResources().getStringArray(R.array.note_color_choice_values);

            if (color_array!=null && color_array.length>0) {
                mColorChoices = new int[color_array.length];
                for (int i = 0; i < color_array.length; i++) {
                    mColorChoices[i] = Color.parseColor(color_array[i]);
                }
            }
            return mColorChoices;
        }

    }
    //NOT MY WORK
    //Method used from https://github.com/gabrielemariotti/colorpickercollection
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


}
