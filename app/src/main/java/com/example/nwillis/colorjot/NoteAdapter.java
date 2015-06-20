package com.example.nwillis.colorjot;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by N Willis on 02/06/2015.
 */
public class NoteAdapter extends CursorAdapter {

    public NoteAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_note, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.list_item_note);
        TextView title = (TextView) linearLayout.findViewById(R.id.list_item_note_title_textview);
        TextView text = (TextView) linearLayout.findViewById(R.id.list_item_note_text_textview);

        //get the entries for the note
        String noteTitle = cursor.getString(Utility.COL_TITLE);
        String noteText = cursor.getString(Utility.COL_TEXT);
        int textColor = cursor.getInt(Utility.COL_TEXT_COLOR);
        int noteColor = cursor.getInt(Utility.COL_NOTE_COLOR);

        //set the text size
        NoteEditorUtility noteEditorUtility = new NoteEditorUtility(context);
        String textSize = cursor.getString(Utility.COL_TEXT_SIZE);
        noteEditorUtility.changeTextSize(Utility.textSizeStringToInt.get(textSize), title, text);

        //set the title box
        title.setBackgroundColor(noteColor);
        title.setText(noteTitle);
        title.setTextColor(textColor);

        //set the text box
        Utility utility = new Utility();
        text.setBackgroundColor(utility.getNoteBodyColor(context, noteColor));
        text.setText(noteText);
        text.setTextColor(textColor);
    }
}
