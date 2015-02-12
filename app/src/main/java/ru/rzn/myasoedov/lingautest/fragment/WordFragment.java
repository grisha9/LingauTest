package ru.rzn.myasoedov.lingautest.fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.apache.http.Header;

import java.io.Serializable;

import ru.rzn.myasoedov.lingautest.R;
import ru.rzn.myasoedov.lingautest.activity.DictionaryActivity;
import ru.rzn.myasoedov.lingautest.db.DictionaryProvider;
import ru.rzn.myasoedov.lingautest.db.Translate;
import ru.rzn.myasoedov.lingautest.db.TranslateWrapper;
import ru.rzn.myasoedov.lingautest.requester.TranslateRequester;

/**
 * Created by grisha on 11.02.15.
 */
public class WordFragment extends Fragment implements ActionBar.OnNavigationListener {
    public static final String WORD_PARAM = "word_param";
    private static final String TAG = WordFragment.class.getSimpleName();

    private int languageId;
    private boolean isView;
    private TranslateWrapper translateWrapper;
    private EditText word;
    private ListView listView;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        languageId = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragmetn_word, container, false);
        getArgument();
        word = (EditText) view.findViewById(R.id.word);
        if (isView) {
            word.setEnabled(false);
        }
        listView = (ListView) view.findViewById(R.id.translate_list);
        return view;
    }

    private void getArgument() {
        if (getArguments() != null
                && getArguments().getSerializable(WORD_PARAM) instanceof TranslateWrapper) {
            translateWrapper = (TranslateWrapper) getArguments().getSerializable(WORD_PARAM);
            isView = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareActionBar();
        setData();
    }

    private void setData() {
        if (translateWrapper != null) {
            listView.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    translateWrapper.getTranslate()));
            word.setText(translateWrapper.getWord());
        }
    }

    private void prepareActionBar() {
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            if (isView) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(translateWrapper.getLanguageCode());
            } else {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.languages));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(R.string.translate_code);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                actionBar.setListNavigationCallbacks(adapter, this);
                actionBar.setSelectedNavigationItem(languageId);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isView) {
            inflater.inflate(R.menu.menu_word, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_translate_word:
                translateWord();
                return true;
            case R.id.action_add_word:
                addToDictionary();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void translateWord() {
        if (TextUtils.isEmpty(word.getText())) {
            Toast.makeText(getActivity(), R.string.empty_word, Toast.LENGTH_LONG).show();
        } else {
            TranslateRequester.getTranslate(word.getText().toString(),
                    getResources().getStringArray(R.array.languages)[languageId],
                    new JSONTranslateResponseHandler());
        }
    }

    private void addToDictionary() {
        if (translateWrapper != null) {
            getActivity().getContentResolver().insert(
                    DictionaryProvider.DICTIONARY_CONTENT_URI,
                    translateWrapper.getContentValues());
            getActivity().getFragmentManager().popBackStack();
        } else {
            Toast.makeText(getActivity(), R.string.empty_translate, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        languageId = itemPosition;
        return false;
    }

    public void hideProgressDialog() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void showProgressDialog() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
            dialog = ProgressDialog.show(getActivity(), null, getString(R.string.wait));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public class JSONTranslateResponseHandler extends BaseJsonHttpResponseHandler<Translate> {
        private final int RESPONSE_OK_CODE = 200;

        @Override
        public void onStart() {
            super.onStart();
            showProgressDialog();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            hideProgressDialog();
        }

        @Override
        public void onSuccess(int i, Header[] headers, String s, Translate translate) {
            try {
                translateWrapper = new TranslateWrapper(translate, word.getText().toString());
                listView.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        translateWrapper.getTranslate()));
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, Throwable throwable, String s, Translate translate) {
            try {
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            Log.e(TAG, throwable.toString());
        }

        @Override
        protected Translate parseResponse(String s, boolean b) throws Throwable {
            Translate translate = new Gson().fromJson(s, Translate.class);
            if (translate.getCode() != RESPONSE_OK_CODE) {
                throw new IllegalStateException(translate.getMessage());
            }
            return translate;
        }

    }
}
