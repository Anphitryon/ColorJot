package com.example.nwillis.colorjot.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.nwillis.colorjot.data.NoteContract;
import com.example.nwillis.colorjot.widget.ColorJotWidget;

/**
 * Created by N Willis on 26/05/2015.
 */
public class NoteProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private NoteDbHelper noteDbHelper;

    static final int NOTES = 100;
    static final int NOTE_WITH_ID = 101;

    private static final SQLiteQueryBuilder notesQueryBuilder;

    static{
        notesQueryBuilder = new SQLiteQueryBuilder();
        notesQueryBuilder.setTables(NoteContract.NoteEntry.TABLE_NAME);
    }

    //for the NOTE_WITH_ID Uri, matches the id to a note in the table
    private static final String notesIdSelection =
            NoteContract.NoteEntry.TABLE_NAME + "." + NoteContract.NoteEntry._ID + " = ? ";

    /**
     * Gets a specific note from the notes table.
     * @param uri Uri of type NOTE_WITH_ID
     * @param projection String[] columns to retrieve
     * @param sortOrder String order to sort results into
     * @return Cursor containing the note with the matching id
     */
    private Cursor getNoteById(Uri uri, String[] projection, String sortOrder){
        String id = NoteContract.NoteEntry.getIdFromUri(uri);

        return notesQueryBuilder.query(noteDbHelper.getReadableDatabase(),
                projection,
                notesIdSelection,
                new String[]{id},
                null,
                null,
                sortOrder);
    }

    static UriMatcher buildUriMatcher(){

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String contentAuthority = NoteContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(contentAuthority, NoteContract.PATH_NOTES, NOTES);
        uriMatcher.addURI(contentAuthority, NoteContract.PATH_NOTES +"/#", NOTE_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate(){
        noteDbHelper = new NoteDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri){
        final int match = uriMatcher.match(uri);

        switch (match){
            case NOTES:
                return NoteContract.NoteEntry.CONTENT_TYPE;
            case NOTE_WITH_ID:
                return NoteContract.NoteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case NOTES:
                cursor = noteDbHelper.getReadableDatabase().query(
                        NoteContract.NoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case NOTE_WITH_ID:
                cursor = getNoteById(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = noteDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case NOTES: {
                normalizeDate(values);
                long _id = db.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);
                if(_id>0){
                    returnUri = NoteContract.NoteEntry.buildNotesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        notifyChange(uri);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = noteDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        if(null == selection){
            selection = "1";
        }

        switch (match){
            case NOTES:
                rowsDeleted = db.delete(NoteContract.NoteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0){
            notifyChange(uri);
        }

        return rowsDeleted;
    }

    /**
     * Uses normalizeDate on any columns that contain a date
     * @param values ContentValues the data to normalize
     */
    private void normalizeDate(ContentValues values) {
        if (values.containsKey(NoteContract.NoteEntry.COLUMN_DATE_CREATED)) {
            long dateValue = values.getAsLong(NoteContract.NoteEntry.COLUMN_DATE_CREATED);
            values.put(NoteContract.NoteEntry.COLUMN_DATE_CREATED, NoteContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = noteDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case NOTES:
                normalizeDate(values);
                rowsUpdated = db.update(NoteContract.NoteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        if(rowsUpdated != 0){
            notifyChange(uri);
        }

        return rowsUpdated;
    }

    /**
     * Tells the provider that data has been changed. Sends a broadcast out for the Widgets
     * to let them know data has been changed.
     * @param uri Uri
     */
    private void notifyChange(Uri uri){
        getContext().getContentResolver().notifyChange(uri, null);

        //in addition refresh any widgets
        getContext().sendBroadcast(ColorJotWidget.getRefreshBroadcastIntent(getContext()));
    }

}
