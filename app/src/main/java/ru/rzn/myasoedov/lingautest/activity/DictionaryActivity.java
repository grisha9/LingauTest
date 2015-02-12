package ru.rzn.myasoedov.lingautest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import ru.rzn.myasoedov.lingautest.R;
import ru.rzn.myasoedov.lingautest.fragment.WordListFragment;


public class DictionaryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new WordListFragment())
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
