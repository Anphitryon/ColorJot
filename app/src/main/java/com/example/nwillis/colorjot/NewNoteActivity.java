package com.example.nwillis.colorjot;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nwillis.colorjot.widget.ColorJotWidgetConfigureActivity;

public class NewNoteActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        getSupportActionBar().setElevation(0);

        if(savedInstanceState == null){
            //add fragment to activity layout
            NewNoteActivityFragment newNoteActivityFragment = new NewNoteActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ColorJotWidgetConfigureActivity.SAVED_NOTE_ID_KEY, getIntent().getIntExtra(ColorJotWidgetConfigureActivity.SAVED_NOTE_ID_KEY, -1));
            newNoteActivityFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.edit_fragment, newNoteActivityFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
