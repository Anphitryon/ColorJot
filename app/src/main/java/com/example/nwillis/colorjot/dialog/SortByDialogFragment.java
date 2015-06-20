package com.example.nwillis.colorjot.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.nwillis.colorjot.R;

/**
 * Created by N Willis on 03/06/2015.
 */
public class SortByDialogFragment extends DialogFragment {

    public SortByDialogFragment(){

    }

    //Interface to handle the selected sort option
    public interface SortByDialogFragmentListener {
        public void handleSortChoice(int choice);
    }

    SortByDialogFragmentListener sortByDialogFragmentListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getActivity().getResources().getString(R.string.action_sort))
                .setItems(R.array.sort_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sortByDialogFragmentListener.handleSortChoice(which);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            sortByDialogFragmentListener = (SortByDialogFragmentListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement DeleteNoteDialogFragmentListener");
        }
    }

}
