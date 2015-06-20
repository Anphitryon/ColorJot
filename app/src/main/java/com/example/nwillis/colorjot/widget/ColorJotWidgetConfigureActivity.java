package com.example.nwillis.colorjot.widget;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.nwillis.colorjot.NewNoteActivity;
import com.example.nwillis.colorjot.NoteAdapter;
import com.example.nwillis.colorjot.R;
import com.example.nwillis.colorjot.Utility;
import com.example.nwillis.colorjot.data.NoteContract;

import java.util.Date;

/**
 * The configuration screen for the {@link ColorJotWidget ColorJotWidget} AppWidget.
 */
public class ColorJotWidgetConfigureActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private NoteAdapter noteAdapter;
    private GridView gridView;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREFS_NAME = "com.example.nwillis.colorjot.widget.ColorJotWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    public static final String SAVED_NOTE_ID_KEY = "savedNoteId";

    public ColorJotWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.color_jot_widget_configure);

        //set listener for new note button
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        //Populate gridview with notes
        noteAdapter = new NoteAdapter(getApplicationContext(), null, 0);

        gridView = (GridView) findViewById(R.id.widget_grid_view);
        gridView.setAdapter(noteAdapter);

        gridView.setOnItemClickListener(widgetGridListener);

        getSupportLoaderManager().initLoader(0, null, this);



        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    //Click listener for the add note button, this will create a new note and a widget and take
    //the user to the new note activity
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ColorJotWidgetConfigureActivity.this;

            //add a note to the database
            long savedNoteId = addBlankNoteToDatabaseReturnId(context);

            //create a widget for the new note
            saveNoteIdPref(context, mAppWidgetId, savedNoteId);

            //make it the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ColorJotWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            //pass back the original appwidgetid
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);

            //open the new note activity so the user can edit the note by passing the note id in
            Intent newNoteActivity = new Intent(getActivity(), NewNoteActivity.class);
            newNoteActivity.putExtra(SAVED_NOTE_ID_KEY, (int) savedNoteId);
            startActivity(newNoteActivity);

        }
    };

    //Click listener for the notes, this will create a widget based on the note clicked
    AdapterView.OnItemClickListener widgetGridListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Context context = ColorJotWidgetConfigureActivity.this;

            //get the id of the note
            Cursor cursor = (Cursor) gridView.getAdapter().getItem(position);
            long noteId = cursor.getLong(Utility.COL_NOTE_ID);

            //save the id of the note
            saveNoteIdPref(context, mAppWidgetId, noteId);

            //make it the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ColorJotWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            //pass back the original appwidgetid
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    /**
     * Adds a dummy note to the database. This note will have blank fields where possible
     * @param context Context
     * @return int the id of the newly added note
     */
    private int addBlankNoteToDatabaseReturnId(Context context){
        ContentValues noteValues = new ContentValues();
        noteValues.put(NoteContract.NoteEntry.COLUMN_TITLE, "");
        noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT, "");
        noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT_SIZE, Utility.getPreferenceTextSize(context));
        noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT_COLOR, Utility.getPreferenceTextColor(context));
        noteValues.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, Utility.getPreferenceNoteColor(context));
        noteValues.put(NoteContract.NoteEntry.COLUMN_DATE_CREATED, new Date().getTime());

        //save note and record the savedNoteId in case device is rotated
        Uri newNoteUri = context.getContentResolver().insert(
                NoteContract.NoteEntry.CONTENT_URI, noteValues);
        return Integer.parseInt(NoteContract.NoteEntry.getIdFromUri(newNoteUri));
    }

    /**
     * Used to save the note id to the preferences using the widget id as a key
     * @param context Context
     * @param appWidgetId int the id of the widget
     * @param noteId int the id of the note
     */
    static void saveNoteIdPref(Context context, int appWidgetId, Long noteId){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId, noteId);
        prefs.commit();
    }

    /**
     * Used to load the note id from the preferences using the widget id as key
     * @param context Context
     * @param appWidgetId int id of the widget
     * @return long id of the note
     */
    static long loadNoteIdPref(Context context, int appWidgetId){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        long noteIdValue = prefs.getLong(PREF_PREFIX_KEY+appWidgetId, -1);
        if(noteIdValue != -1){
            return noteIdValue;
        } else {
            return -1;
        }
    }

    /**
     * Removes a widgetid-noteid pair from the preferences
     * @param context Context
     * @param appWidgetId int id of the widget
     */
    static void deleteNoteIdPref(Context context, int appWidgetId){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        Uri notesUri = NoteContract.NoteEntry.buildNotesUri();
        return new CursorLoader(this, notesUri, Utility.NOTE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        noteAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        noteAdapter.swapCursor(null);
    }

    private Activity getActivity(){
        return this;
    }
}

