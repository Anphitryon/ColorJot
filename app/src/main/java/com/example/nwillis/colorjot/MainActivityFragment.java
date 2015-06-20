package com.example.nwillis.colorjot;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.nwillis.colorjot.data.NoteContract;
import com.example.nwillis.colorjot.dialog.DeleteNoteDialogFragment;
import com.example.nwillis.colorjot.dialog.NoteOptionsDialogFragment;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int NOTE_LOADER=0;

    public static final int BY_COLOR = 0;
    public static final int BY_CREATION_DATE = 1;

    private NoteAdapter noteAdapter;
    private final Fragment fragment = this;

    private GridView gridView;
    private int gridPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    public MainActivityFragment() {
    }

    public interface Callback{
        public void onItemSelected(Uri noteIdUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        noteAdapter = new NoteAdapter(getActivity(), null, 0);

        gridView = (GridView) rootView.findViewById(R.id.grid_view_notes);
        gridView.setAdapter(noteAdapter);

        gridView.setLongClickable(true);

         gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 // CursorAdapter returns a cursor at the correct position for getItem(), or null
                 // if it cannot seek to that position.
                 Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                 if (cursor != null) {
                     ((Callback) getActivity())
                             .onItemSelected(NoteContract.NoteEntry.buildNotesUri(cursor.getInt(Utility.COL_NOTE_ID)));
                 }
             }
         });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //open the options dialog for this note
                Cursor cursor = (Cursor) gridView.getAdapter().getItem(position);
                long noteId = cursor.getLong(Utility.COL_NOTE_ID);
                DialogFragment dialog = NoteOptionsDialogFragment.newInstance(noteId);
                dialog.setTargetFragment(fragment, Utility.REQUEST_CODE_NOTE_OPTIONS);
                dialog.show(getFragmentManager(), NoteOptionsDialogFragment.class.getSimpleName());
                return true;
            }
        });

        if(savedInstanceState !=null && savedInstanceState.containsKey(SELECTED_KEY)){
            gridPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        if(gridPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, gridPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(Utility.REQUEST_CODE_NOTE_OPTIONS==requestCode){
            //the user has come from the note options dialog
            switch (resultCode){
                case 0:
                    //delete option was selected confirm the user would like to delete
                    DialogFragment dialog = DeleteNoteDialogFragment.newInstance(data.getLongExtra(Utility.noteIdKey, -1));
                    dialog.setTargetFragment(fragment, Utility.REQUEST_CODE_DELETE_NOTE);
                    dialog.show(getFragmentManager(), DeleteNoteDialogFragment.class.getSimpleName());
                    break;
                default:
                    throw new IllegalArgumentException("Did not recognise " + resultCode + " in " + this.getClass().getSimpleName().toString());
            }
        } else if(Utility.REQUEST_CODE_DELETE_NOTE == requestCode){
            //the user would like to delete the note
            getActivity().getApplicationContext().getContentResolver().delete(NoteContract.NoteEntry.CONTENT_URI, " _id= "+data.getLongExtra(Utility.noteIdKey, -1), null);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(NOTE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        Uri notesUri = NoteContract.NoteEntry.buildNotesUri();
        return new CursorLoader(getActivity(), notesUri, Utility.NOTE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        noteAdapter.swapCursor(cursor);
        if(gridPosition != GridView.INVALID_POSITION){
            gridView.post(new Runnable() {
                @Override
                public void run() {
                    gridView.smoothScrollToPosition(gridPosition);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        noteAdapter.swapCursor(null);
    }

    /**
     * Launches the sorting AyscTask
     * @param choice
     */
    public void startSort(int choice){
        new SortNotesTask().execute(choice);
    }

    /**
     * Sorts the notes in the database according to different criteria based on
     * the pasted in Integer argument.
     */
    public class SortNotesTask extends AsyncTask<Integer, Void, Cursor>{

        @Override
        protected Cursor doInBackground(Integer... params) {

            int sortCode = params[0];
            Cursor cursor = null;

            switch (sortCode){
                case BY_COLOR:
                    //sort by color
                    cursor = getActivity().getApplicationContext().getContentResolver().query(NoteContract.NoteEntry.CONTENT_URI, null, null, null, NoteContract.NoteEntry.COLUMN_NOTE_COLOR);
                    break;
                case BY_CREATION_DATE:
                    //sort by creation date
                    cursor = getActivity().getApplicationContext().getContentResolver().query(NoteContract.NoteEntry.CONTENT_URI, null, null, null, NoteContract.NoteEntry.COLUMN_DATE_CREATED);
                    break;
                default:
                    throw new IllegalArgumentException("Did not recognise sort type in " + this.getClass().getSimpleName().toString());
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor){
            noteAdapter.swapCursor(cursor);
        }
    }

}
