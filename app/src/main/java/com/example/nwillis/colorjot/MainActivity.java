package com.example.nwillis.colorjot;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nwillis.colorjot.dialog.SortByDialogFragment;


public class MainActivity extends ActionBarActivity implements SortByDialogFragment.SortByDialogFragmentListener, MainActivityFragment.Callback{

    private final String NEW_FRAGMENT_TAG = "NFTAG";
    private final String EDIT_FRAGMENT_TAG = "EFTAG";
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        //check to see if this is a one pane or two pane layout
        //(two pane for tablets)
        if(findViewById(R.id.edit_fragment)!=null){

            twoPane = true;

            //start with a new note fragment in the right hand pane
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.edit_fragment, new NewNoteActivityFragment(), NEW_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            twoPane = false;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //open settings activity
            startActivity(new Intent(this, PreferenceActivity.class));
        } else if(id == R.id.action_add_note){

            if(twoPane){
                //put the new fragment in the display
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.edit_fragment, new NewNoteActivityFragment(), NEW_FRAGMENT_TAG)
                        .commit();
            } else {
                //open the new fragment in a new activity
                startActivity(new Intent(this, NewNoteActivity.class));
            }

        } else if(id == R.id.action_sort){
            //open dialog for sort options
            DialogFragment sortByDialog = new SortByDialogFragment();
            sortByDialog.show(getSupportFragmentManager(), SortByDialogFragment.class.getSimpleName());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleSortChoice(int choice) {
        //sent the selected type of sort to the fragment
        MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        fragment.startSort(choice);
    }

    @Override
    public void onItemSelected(Uri noteUri){
        if(twoPane){
            //replace right hand pane with edit fragment and pass it the uri for the selected note
            Bundle args = new Bundle();
            args.putParcelable(EditNoteActivityFragment.EDIT_URI, noteUri);

            EditNoteActivityFragment fragment = new EditNoteActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.edit_fragment, fragment, EDIT_FRAGMENT_TAG)
                    .commit();
        } else {
            //open the EditNoteActivity
            Intent intent = new Intent(this, EditNoteActivity.class)
                    .setData(noteUri);
            startActivity(intent);
        }
    }
}
