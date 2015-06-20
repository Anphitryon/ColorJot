package com.example.nwillis.colorjot.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.nwillis.colorjot.data.NoteContract.NoteEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N Willis on 24/05/2015.
 */
public class NoteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 8;

    static final String DATABASE_NAME = "notes.db";

    public NoteDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String CREATE_NOTES_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY, " +
                NoteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_TEXT + " TEXT, " +
                NoteEntry.COLUMN_TEXT_SIZE + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_TEXT_COLOR + " INTEGER NOT NULL, " +
                NoteEntry.COLUMN_NOTE_COLOR + " INTEGER NOT NULL, " +
                NoteEntry.COLUMN_DATE_CREATED + " INTEGER NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
