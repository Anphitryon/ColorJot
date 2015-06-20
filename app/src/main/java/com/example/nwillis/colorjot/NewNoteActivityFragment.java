package com.example.nwillis.colorjot;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.example.nwillis.colorjot.widget.ColorJotWidgetConfigureActivity;

import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class NewNoteActivityFragment extends Fragment{

    private TextView title, text;
    private String textSize;
    private int noteHeaderColor, noteBodyColor, textColor;
    private Context context;
    private Fragment fragment = this;
    private boolean shouldBeSaved = true;
    private ImageButton noteColorButton, textColorButton;
    private Utility utility;

    private final String TEXT_SIZE_TAG = "TEXT_SIZE";
    private final String HEADER_COLOR_TAG = "HEADER_COLOR";
    private final String TEXT_COLOR_TAG = "TEXT_COLOR_TAG";
    private final String HEADER_TEXT_TAG = "HEADER_TEXT";
    private final String BODY_TEXT_TAG = "BODY_TEXT";
    private final String SAVED_NOTE_ID_TAG = "NOTE_ID";
    private int savedNoteId = -1;


    NoteEditorUtility noteEditorUtility;

    public NewNoteActivityFragment() {
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
            DialogFragment dialog = DeleteNoteDialogFragment.newInstance(-1);
            dialog.setTargetFragment(fragment, Utility.REQUEST_CODE_DELETE_NOTE);
            dialog.show(getFragmentManager(), DeleteNoteDialogFragment.class.getSimpleName());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_note_editor, container, false);

        context = getActivity().getApplicationContext();
        noteEditorUtility = new NoteEditorUtility(context);
        utility = new Utility();

        title = (TextView) view.findViewById(R.id.note_title);
        text = (TextView) view.findViewById(R.id.note_text);

        if(savedInstanceState == null){
            //first time fragment is opened/no rotation happened yet
            Bundle arguments = getArguments();
            if(arguments !=null){
                savedNoteId = arguments.getInt(ColorJotWidgetConfigureActivity.SAVED_NOTE_ID_KEY, -1);
            }

            //get settings from the preferences
            textSize = Utility.getPreferenceTextSize(context);
            noteHeaderColor = Utility.getPreferenceNoteColor(context);
            textColor = Utility.getPreferenceTextColor(context);

        } else {
            //device has been rotated so instead load details from state
            textSize = savedInstanceState.getString(TEXT_SIZE_TAG);
            noteHeaderColor = savedInstanceState.getInt(HEADER_COLOR_TAG);
            textColor = savedInstanceState.getInt(TEXT_COLOR_TAG);

            title.setText(savedInstanceState.getString(HEADER_TEXT_TAG));
            text.setText(savedInstanceState.getString(BODY_TEXT_TAG));

            savedNoteId = savedInstanceState.getInt(SAVED_NOTE_ID_TAG);
        }

        //finish set up of the note
        textSize = noteEditorUtility.changeTextSize(Utility.textSizeStringToInt.get(textSize), title, text);
        title.setBackgroundColor(noteHeaderColor);
        noteBodyColor = utility.getNoteBodyColor(context, noteHeaderColor);
        text.setBackgroundColor(noteBodyColor);
        title.setTextColor(textColor);
        text.setTextColor(textColor);


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
        textColorButton.setImageResource(utility.getTextColorButtonImage(context, textColor));
        textColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteEditorUtility.showTextColorDialog(textColor, textColorDialogListener, getActivity().getFragmentManager());
            }
        });

        //set the listener for the note color button
        noteColorButton = (ImageButton) view.findViewById(R.id.note_color_button);
        noteColorButton.setImageResource(utility.getNoteColorButtonImage(context, noteHeaderColor));
        noteColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteEditorUtility.showNoteColorDialog(noteHeaderColor, noteColorDialogListener, getActivity().getFragmentManager());
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        //save everything that will be required to re-build note upon device rotation
        outState.putString(TEXT_SIZE_TAG, textSize);
        outState.putInt(HEADER_COLOR_TAG, noteHeaderColor);
        outState.putInt(TEXT_COLOR_TAG, textColor);
        outState.putString(HEADER_TEXT_TAG, title.getText().toString());
        outState.putString(BODY_TEXT_TAG, text.getText().toString());
        outState.putInt(SAVED_NOTE_ID_TAG, savedNoteId);
    }

    @Override
    public void onPause(){
        super.onPause();
        String titleEntry = title.getText().toString();
        String textEntry = text.getText().toString();

        long dateCreated = new Date().getTime();

        saveNoteToDatabase(titleEntry, textEntry, textSize, textColor, noteHeaderColor, dateCreated);
    }

    /**
     * Called everytime the phone is rotated or the user navigates away from this Activity.
     * @param title String the text in the note header
     * @param text String the text in the note body
     * @param textSize String the text size as a string small/medium/large
     * @param textColor int the color of the text
     * @param noteColor int the color of the note header
     * @param dateCreated long todays date
     */
    private void saveNoteToDatabase(String title, String text, String textSize, int textColor, int noteColor, long dateCreated){
        //get the id from the saved instance state
        if(savedNoteId == -1){
            //this note has not been saved before
            if(shouldBeSaved){
                //dont save the note if it contains no title/text
                if(!(title.equals("") && text.equals(""))){
                    ContentValues noteValues = new ContentValues();

                    noteValues.put(NoteContract.NoteEntry.COLUMN_TITLE, title);
                    noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT, text);
                    noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT_SIZE, textSize);
                    noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT_COLOR, textColor);
                    noteValues.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, noteColor);
                    noteValues.put(NoteContract.NoteEntry.COLUMN_DATE_CREATED, dateCreated);

                    //save note and record the savedNoteId in case device is rotated
                    Uri newNoteUri = getActivity().getApplicationContext().getContentResolver().insert(
                            NoteContract.NoteEntry.CONTENT_URI, noteValues);
                    savedNoteId = Integer.parseInt(NoteContract.NoteEntry.getIdFromUri(newNoteUri));
                }
            }
        } else {
            //this note has already been saved on rotation so now that field should be edited
            if(!shouldBeSaved || (text.equals("")&&title.equals(""))){
                //the user has either selected delete or erased any text so the note in the db should be deleted
                //delete the note
                getActivity().getApplicationContext().getContentResolver().delete(NoteContract.NoteEntry.CONTENT_URI, " _id= "+savedNoteId, null);
                savedNoteId = -1;
            } else {
                //the note in the db must be updated
                ContentValues noteValues = new ContentValues();

                noteValues.put(NoteContract.NoteEntry.COLUMN_TITLE, title);
                noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT, text);
                noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT_SIZE, textSize);
                noteValues.put(NoteContract.NoteEntry.COLUMN_TEXT_COLOR, textColor);
                noteValues.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, noteColor);

                getActivity().getApplicationContext().getContentResolver().update(NoteContract.NoteEntry.CONTENT_URI, noteValues, " _id = "+savedNoteId, null);
            }
        }
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
