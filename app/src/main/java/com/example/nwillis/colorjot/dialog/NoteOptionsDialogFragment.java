package com.example.nwillis.colorjot.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.nwillis.colorjot.R;

/**
 * Created by N Willis on 03/06/2015.
 */
public class NoteOptionsDialogFragment extends DialogFragment {

    //variables to store the note id which needs to be passed out of the dialog
    long noteId;
    private static final String noteIdKey = "noteId";

    public static NoteOptionsDialogFragment newInstance(long noteId) {
        NoteOptionsDialogFragment noteOptionsDialogFragment = new NoteOptionsDialogFragment();

        Bundle args = new Bundle();
        args.putLong(noteIdKey, noteId);
        noteOptionsDialogFragment.setArguments(args);

        return noteOptionsDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        noteId = getArguments().getLong(noteIdKey);

        builder.setTitle(getActivity().getResources().getString(R.string.note_options))
                .setItems(R.array.note_dialog_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(noteIdKey, noteId);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), which, intent);
                    }
                });

        return builder.create();
    }

}
