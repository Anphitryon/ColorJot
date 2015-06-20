package com.example.nwillis.colorjot.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.example.nwillis.colorjot.EditNoteActivity;
import com.example.nwillis.colorjot.R;
import com.example.nwillis.colorjot.Utility;
import com.example.nwillis.colorjot.data.NoteContract;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ColorJotWidgetConfigureActivity ColorJotWidgetConfigureActivity}
 */
public class ColorJotWidget extends AppWidgetProvider {

    private static final String REFRESH =
            "android.appwidget.action.REFRESH";

    public static Intent getRefreshBroadcastIntent(Context context){
        //notify that widgets should be updated
        return new Intent(REFRESH)
                .setComponent(new ComponentName(context, ColorJotWidget.class));
    }

    @Override
    public void onReceive(final Context context, Intent widgetIntent){
        final String action = widgetIntent.getAction();

        if(REFRESH.equals(action)){
            //update widgets
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, ColorJotWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            ColorJotWidgetConfigureActivity.deleteNoteIdPref(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    /**
     * Loads the data for the note in the widget, this data is then displayed on the widget.
     * If the corresponding note has been deleted the widget is changed to reflect this.
     * @param context Context
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetId int the id of the widget to update
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        long noteId = ColorJotWidgetConfigureActivity.loadNoteIdPref(context, appWidgetId);

        if(noteId == -1){
            return;
        }

        //attempt to get the data for the note
        Cursor data = context.getContentResolver().query(NoteContract.NoteEntry.buildNotesUri(noteId),
                Utility.NOTE_COLUMNS, null, null, null, null);

        if(data == null){
            return;
        }

        if(!data.moveToFirst()){
            data.close();

            //this note no longer exists - display dud widget
            //remove the preference
            ColorJotWidgetConfigureActivity.deleteNoteIdPref(context, appWidgetId);
            //set up display
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.color_jot_widget);
            views.setTextViewText(R.id.appwidget_title, "ColorJot");
            views.setTextColor(R.id.appwidget_title, context.getResources().getColor(R.color.white));
            views.setInt(R.id.appwidget_title, "setBackgroundColor", context.getResources().getColor(R.color.red_header));
            views.setFloat(R.id.appwidget_title, "setTextSize", Utility.textSizeStringToInt.get((Utility.getPreferenceTextSize(context))));
            views.setTextViewText(R.id.appwidget_text, "Corresponding note has been deleted");
            views.setTextColor(R.id.appwidget_text, context.getResources().getColor(R.color.white));
            views.setInt(R.id.appwidget_text, "setBackgroundColor", context.getResources().getColor(R.color.red_header));
            views.setFloat(R.id.appwidget_text, "setTextSize", Utility.textSizeStringToInt.get((Utility.getPreferenceTextSize(context))));
            //set intent to null so widget doesn't open app
            views.setOnClickPendingIntent(R.id.widget_note, null);
            //update
            appWidgetManager.updateAppWidget(appWidgetId, views);

            return;
        }

        //note exists so fetch its data and display
        //extract note data from the cursor
        String header = data.getString(Utility.COL_TITLE);
        String body = data.getString(Utility.COL_TEXT);
        int textColor = data.getInt(Utility.COL_TEXT_COLOR);
        int headerColor = data.getInt(Utility.COL_NOTE_COLOR);
        String textSize = data.getString(Utility.COL_TEXT_SIZE);
        Utility utility = new Utility();
        int bodyColor = utility.getNoteBodyColor(context, headerColor);
        float textSizeFloat = (float) Utility.textSizeStringToInt.get(textSize);

        data.close();

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.color_jot_widget);
        views.setTextViewText(R.id.appwidget_title, header);
        views.setTextColor(R.id.appwidget_title, textColor);
        views.setInt(R.id.appwidget_title, "setBackgroundColor", headerColor);
        views.setFloat(R.id.appwidget_title, "setTextSize", textSizeFloat);
        views.setTextViewText(R.id.appwidget_text, body);
        views.setTextColor(R.id.appwidget_text, textColor);
        views.setInt(R.id.appwidget_text, "setBackgroundColor", bodyColor);
        views.setFloat(R.id.appwidget_text, "setTextSize", textSizeFloat);

        //set the intent if the widget is clicked
        Intent launchIntent = new Intent(context, EditNoteActivity.class).setData(NoteContract.NoteEntry.buildNotesUri(noteId));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_note, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}

