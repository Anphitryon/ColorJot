package com.example.nwillis.colorjot;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.nwillis.colorjot.colorpicker.ColorPickerDialog;
import com.example.nwillis.colorjot.colorpicker.ColorPickerSwatch;
import com.example.nwillis.colorjot.dialog.TextSizeDialogFragment;

/**
 * Created by N Willis on 02/06/2015.
 */
public class NoteEditorUtility {

    private static final int TEXT_SIZE_SMALL = 0;
    private static final int TEXT_SIZE_MEDIUM = 1;
    private static final int TEXT_SIZE_LARGE = 2;

    private Context context;

    public NoteEditorUtility(Context context){
        this.context = context;
    }

    /**
     * Open the Text Size dialog
     * @param fragment Fragment this will be the target fragment
     * @param fragmentManager FragmentManager
     */
    public void showTextSizeDialog(Fragment fragment, FragmentManager fragmentManager){
        DialogFragment dialog = new TextSizeDialogFragment();
        dialog.setTargetFragment(fragment, Utility.REQUEST_CODE_TEXT_SIZE);
        dialog.show(fragmentManager, TextSizeDialogFragment.class.getSimpleName());
    }

    /**
     * Open the dialog to allow the user to select a note color
     * @param noteHeaderColor int currentley selected note color
     * @param noteColorDialogListener OnColorSelectedListener the listener for the dialog
     * @param fragmentManager FragmentManager
     */
    public void showNoteColorDialog(int noteHeaderColor, ColorPickerSwatch.OnColorSelectedListener noteColorDialogListener, android.app.FragmentManager fragmentManager){
        int[] colorPallette = Utility.ColorUtils.colorChoice(context);
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title, colorPallette,
                noteHeaderColor, 3, Utility.isTablet(context) ? ColorPickerDialog.SIZE_LARGE
                        : ColorPickerDialog.SIZE_SMALL);

        colorPickerDialog.setOnColorSelectedListener(noteColorDialogListener);
        colorPickerDialog.show(fragmentManager, "NoteColorDialog");
    }

    /**
     * Open the dialog to allow the user to select a text color
     * @param textColor int the currently selected text color
     * @param textColorDialogListener OnColorSelectedListener the listener for the dialog
     * @param fragmentManager FragmentManager
     */
    public void showTextColorDialog(int textColor, ColorPickerSwatch.OnColorSelectedListener textColorDialogListener, android.app.FragmentManager fragmentManager){
        int[] colorPallette = Utility.ColorUtils.colorChoice(context);
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title, colorPallette,
                textColor, 3, Utility.isTablet(context) ? ColorPickerDialog.SIZE_LARGE
                        : ColorPickerDialog.SIZE_SMALL);

        colorPickerDialog.setOnColorSelectedListener(textColorDialogListener);
        colorPickerDialog.show(fragmentManager, "TextColorDialog");
    }

    /**
     * Takes the size code from the Text Size dialog and uses this to set the text sizes of the passed
     * in TextViews appropriately
     * @param newSizeCode int the code from the TextSize dialog
     * @param title TextView the note header
     * @param text TextView the note body
     * @return
     */
    public String changeTextSize(int newSizeCode, TextView title, TextView text){
        String textSize = "";
        switch(newSizeCode){
            case TEXT_SIZE_SMALL:
            case Utility.TEXT_SIZE_SMALL_INT:
                textSize = Utility.TEXT_SIZE_SMALL_STRING;
                break;
            case TEXT_SIZE_MEDIUM:
            case Utility.TEXT_SIZE_MEDIUM_INT:
                textSize = Utility.TEXT_SIZE_MEDIUM_STRING;
                break;
            case TEXT_SIZE_LARGE:
            case Utility.TEXT_SIZE_LARGE_INT:
                textSize = Utility.TEXT_SIZE_LARGE_STRING;
                break;
            default:
                throw new IllegalArgumentException("Did not recognise " + newSizeCode + " from TextSize Dialog");

        }
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utility.textSizeStringToInt.get(textSize));
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utility.textSizeStringToInt.get(textSize));
        return textSize;
    }

    /**
     * Given the new color to use, this method changes the color of the note header and
     * body.
     * @param newColor int note header color to use
     * @param title TextView note header
     * @param text TextView note body
     * @return
     */
    public int changeNoteColor(int newColor, TextView title, TextView text){
        title.setBackgroundColor(newColor);
        Utility utility = new Utility();
        text.setBackgroundColor(utility.getNoteBodyColor(context, newColor));
        return newColor;
    }

    /**
     * Given the new color to use, this method changes the color of the text in the note
     * header and body.
     * @param newColor int the text color to use
     * @param title TextView note header
     * @param text TextView note body
     * @return
     */
    public int changeTextColor(int newColor, TextView title, TextView text){
        title.setTextColor(newColor);
        text.setTextColor(newColor);
        return newColor;
    }
}
