package com.example.nwillis.colorjot;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nwillis.colorjot.colorpicker.ColorPickerSwatch;
import com.example.nwillis.colorjot.data.NoteContract;
import com.example.nwillis.colorjot.dialog.DeleteNoteDialogFragment;


/**
 * A placeholder fragment containing a simple view.
 */
public class EditNoteActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private TextView title, text;
    private String textSize;
    private int noteHeaderColor, noteBodyColor, textColor;
    private Context context;
    private Fragment fragment = this;
    NoteEditorUtility noteEditorUtility;
    int noteId;
    private boolean shouldBeSaved = true;
    private Utility utility;
    private ImageButton noteColorButton, textColorButton;

    public static final String EDIT_URI = "EDIT_URI";
    private Uri editNoteUri;

    private static final int NOTE_LOADER = 0;

    public EditNoteActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_delete){
            //open delete dialog
            DialogFragment dialog = DeleteNoteDialogFragment.newInstance(noteId);
            dialog.setTargetFragment(fragment, Utility.REQUEST_CODE_DELETE_NOTE);
            dialog.show(getFragmentManager(), DeleteNoteDialogFragment.class.getSimpleName());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_note_editor, container, false);

        Bundle arguments = getArguments();
        if(arguments != null){
            editNoteUri = arguments.getParcelable(EditNoteActivityFragment.EDIT_URI);
            noteId = Integer.parseInt(NoteContract.NoteEntry.getIdFromUri(editNoteUri));
        }

        context = getActivity().getApplicationContext();
        noteEditorUtility = new NoteEditorUtility(context);
        utility = new Utility();

        title = (TextView) view.findViewById(R.id.note_title);
        text = (TextView) view.findViewById(R.id.note_text);

        //set the listener for the text size button
        ImageButton textSizeButton = (ImageButton) view.findViewById(R.id.text_size_button);
        textSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteEditorUtility.showTextSizeDialog(fragment, getFragmentManager());
            }
        });

        //set the listener for the text color button
        textColorButton = (ImageButton) view.findViewById(R.id.text_color_button);
        textColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteEditorUtility.showTextColorDialog(textColor, textColorDialogListener, getActivity().getFragmentManager());
            }
        });

        //set the listener for the note color button
        noteColorButton = (ImageButton) view.findViewById(R.id.note_color_button);
        noteColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteEditorUtility.showNoteColorDialog(noteHeaderColor, noteColorDialogListener, getActivity().getFragmentManager());
            }
        });

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();

        if(shouldBeSaved){
            //edit the note in the database
            String titleEntry = title.getText().toString();
            String textEntry = text.getText().toString();

            editNoteInDatabase(titleEntry, textEntry, textSize, textColor, noteHeaderColor);
        } else {
            //delete the note
            getActivity().getApplicationContext().getContentResolver().delete(NoteContract.NoteEntry.CONTENT_URI, " _id= "+noteId, null);
        }
    }

    /**
     * Called whenever the user closes this Activity. Updates the opened note in the database with new
     * text and settings
     * @param title String the text in the note header
     * @param text String the text in the note body
     * @param textSize String the text size as a string small/medium/large
     * @param textColor int the color of the text
     * @param noteColor int the color of the note header
     */
    private void editNoteInDatabase(String title, String text, String textSize, int textColor, int noteColor){
        ContentValues noteValues = new ContentValues();
        noteValues.put(NoteContract.NoteEntry.COLUMN_TITLE, title);
        noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT, text);
        noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT_SIZE, textSize);
        noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT_COLOR, textColor);
        noteValues.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, noteColor);

        getActivity().getApplicationContext().getContentResolver().update(NoteContract.NoteEntry.CONTENT_URI, noteValues, " _id = "+noteId, null);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(NOTE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != editNoteUri) {
            return new CursorLoader(getActivity(), editNoteUri, Utility.NOTE_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null && data.moveToFirst()){

            //set the text in the note
            title.setText(data.getString(Utility.COL_TITLE));
            text.setText(data.getString(Utility.COL_TEXT));

            //set the text size
            textSize = data.getString(Utility.COL_TEXT_SIZE);
            textSize = noteEditorUtility.changeTextSize(Utility.textSizeStringToInt.get(textSize), title, text);

            //set the note color
            noteHeaderColor = data.getInt(Utility.COL_NOTE_COLOR);
            title.setBackgroundColor(noteHeaderColor);
            Utility utility = new Utility();
            noteBodyColor = utility.getNoteBodyColor(context, noteHeaderColor);
            text.setBackgroundColor(noteBodyColor);

            //set the text color
            textColor = data.getInt(Utility.COL_TEXT_COLOR);
            title.setTextColor(textColor);
            text.setTextColor(textColor);

            //set the note color button and text color button
            noteColorButton.setImageResource(utility.getNoteColorButtonImage(context, noteHeaderColor));
            textColorButton.setImageResource(utility.getTextColorButtonImage(context, textColor));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case Utility.REQUEST_CODE_TEXT_SIZE:
                textSize = noteEditorUtility.changeTextSize(resultCode, title, text);
                break;
            case Utility.REQUEST_CODE_DELETE_NOTE:
                shouldBeSaved = false;
                getActivity().finish();
                break;
            default:
                throw new IllegalArgumentException("Did not recognise " + resultCode + " in " + this.getClass().getSimpleName().toString());
        }
    }

    //Listener to detect when a note color has been selected and handle behaviour
    ColorPickerSwatch.OnColorSelectedListener noteColorDialogListener = new ColorPickerSwatch.OnColorSelectedListener(){
        @Override
        public void onColorSelected(int color) {
            noteHeaderColor = noteEditorUtility.changeNoteColor(color, title, text);
            noteColorButton.setImageResource(utility.getNoteColorButtonImage(context, noteHeaderColor));
        }
    };

    //Listener to detect when a text color has been selected and handle behaviour
    ColorPickerSwatch.OnColorSelectedListener textColorDialogListener = new ColorPickerSwatch.OnColorSelectedListener(){
        @Override
        public void onColorSelected(int color) {
            textColor = noteEditorUtility.changeTextColor(color, title, text);
            textColorButton.setImageResource(utility.getTextColorButtonImage(context, textColor));
        }
    };
}
