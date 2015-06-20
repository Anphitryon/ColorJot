package com.example.nwillis.colorjot.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.nwillis.colorjot.R;

/**
 * Created by N Willis on 28/05/2015.
 */
public class TextSizeDialogFragment extends DialogFragment {

    public TextSizeDialogFragment(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getActivity().getResources().getString(R.string.text_size))
                .setItems(R.array.text_sizes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), which, null);
                    }
                });

        return builder.create();
    }

}
