package ru.rzn.myasoedov.lingautest.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.HashSet;
import java.util.Set;

import ru.rzn.myasoedov.lingautest.R;
import ru.rzn.myasoedov.lingautest.adapter.WordCursorAdapter;
import ru.rzn.myasoedov.lingautest.db.DictionaryProvider;
import ru.rzn.myasoedov.lingautest.db.TranslateWrapper;

/**
 * Created by grisha on 11.02.15.
 */
public class WordListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AbsListView.MultiChoiceModeListener {
    public static final String SEARCH_STRING = "search_string";
    private Set<Integer> ids;
    private String search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            search = savedInstanceState.getString(SEARCH_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getLoaderManager().initLoader(DictionaryProvider.URI_ALL_WORDS, null, this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getResources().getString(R.string.no_words));
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareActionBar();
        prepareListView();
    }

    private void prepareActionBar() {
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(R.string.app_name);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }

    private void prepareListView() {
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dictionary, menu);
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.action_search);
        prepareSearchView(menuItem);
    }

    private void prepareSearchView(MenuItem menuItem) {
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                restartLoader(null);
                return true;
            }
        });

        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                restartLoader(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                restartLoader(newText);
                return false;
            }
        });

        if (!TextUtils.isEmpty(search)) {
            searchView.post(new Runnable() {
                @Override
                public void run() {
                    searchView.setQuery(search, false);
                    searchView.setIconified(false);
                }
            });

        }
    }

    private void restartLoader(String searchString) {
        Bundle bundle = new Bundle();
        if (searchString != null) {
            search = searchString;
            bundle.putString(SEARCH_STRING, searchString);
        }
        getLoaderManager().restartLoader(DictionaryProvider.URI_ALL_WORDS, bundle, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add_word:
                Fragment fragment = new WordFragment();
                getFragmentManager().beginTransaction().replace(R.id.container, fragment)
                        .addToBackStack(null).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TranslateWrapper item = ((WordCursorAdapter) getListAdapter()).getItem(position);
        Fragment fragment = new WordFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(WordFragment.WORD_PARAM, item);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment)
                .addToBackStack(null).commit();
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case DictionaryProvider.URI_ALL_WORDS:
                String selection = (bundle != null)
                        ? DictionaryProvider.prepareSelectionForSearch(bundle.getString(SEARCH_STRING))
                        : null;
                return new CursorLoader(
                        getActivity(), DictionaryProvider.DICTIONARY_CONTENT_URI, null, selection,
                        null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        setListAdapter(new WordCursorAdapter(getActivity(), cursor, 0));
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        final int checkedCount = getListView().getCheckedItemCount();
        mode.setTitle(checkedCount + " " + getString(R.string.label_selected));
        TranslateWrapper item = ((WordCursorAdapter) getListAdapter()).getItem(position);
        if (checked) {
            ids.add(item.getId());
        } else {
            ids.remove(item.getId());
        }

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.delete, menu);
        ids = new HashSet<>();
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (!ids.isEmpty()) {
                    getActivity().getContentResolver().delete(DictionaryProvider.DICTIONARY_CONTENT_URI,
                            DictionaryProvider.prepareSelectionForDelete(ids), null);
                }
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_STRING, search);
    }
}
