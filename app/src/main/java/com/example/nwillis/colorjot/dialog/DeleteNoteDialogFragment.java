package com.example.nwillis.colorjot.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.nwillis.colorjot.R;

/**
 * Created by N Willis on 02/06/2015.
 */
public class DeleteNoteDialogFragment extends DialogFragment {

    //variables used to store the note id, this needs to be passed out of the dialog
    long noteId;
    private static final String noteIdKey = "noteId";


    public static DeleteNoteDialogFragment newInstance(long noteId) {
        DeleteNoteDialogFragment deleteNoteDialogFragment = new DeleteNoteDialogFragment();

        Bundle args = new Bundle();
        args.putLong(noteIdKey, noteId);
        deleteNoteDialogFragment.setArguments(args);

        return deleteNoteDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        noteId = getArguments().getLong(noteIdKey);

        builder.setTitle(R.string.action_delete)
                .setMessage(R.string.dialog_delete_note)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(noteIdKey, noteId);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), which, intent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteNoteDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

}
