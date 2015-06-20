package com.example.nwillis.colorjot.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;


/**
 * Created by N Willis on 24/05/2015.
 */
public class NoteContract {

    //constants for the NoteProvider
    public static final String CONTENT_AUTHORITY = "com.example.nwillis.colorjot";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String  PATH_NOTES = "notes";

    /**
     * Not my method. Method taken from ProjectSunshine "WeatherContract"
     * class.
     * @param startDate
     * @return
     */
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class NoteEntry implements BaseColumns {

        //base uri for this table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        public static final String TABLE_NAME = "notes";

        // Title of the note, can't be null
        public static final String COLUMN_TITLE = "title";

        // Full text in the note
        public static final String COLUMN_TEXT = "text";

        // Text size is stored as text which would be small/medium/large
        public static final String COLUMN_TEXT_SIZE = "text_size";

        // Text color, saved as an integer code
        public static final String COLUMN_TEXT_COLOR = "text_color";

        // Background color of the note, saved as an integer code
        public static final String COLUMN_NOTE_COLOR = "note_color";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE_CREATED = "date_created";

        /**
         * Build a uri for the content provider that will contain the notes path with
         * an appended id for the note to select.
         * @param id long _id field of the note to select
         * @return Uri the uri for the note in the notes table
         */
        public static Uri buildNotesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Build a uri for the content provider that contains the notes path.
         * @return Uri the uri for all notes in the table
         */
        public static Uri buildNotesUri(){
            return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(CONTENT_AUTHORITY).appendPath(PATH_NOTES).build();
        }

        /**
         * Returns the id portion of the provided Uri.
         * @param uri Uri the uri to extract the id from
         * @return String the id that was provided in the uri
         */
        public static String getIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

}
