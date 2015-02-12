package ru.rzn.myasoedov.lingautest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ru.rzn.myasoedov.lingautest.R;
import ru.rzn.myasoedov.lingautest.db.DictionaryDBHelper;
import ru.rzn.myasoedov.lingautest.db.TranslateWrapper;

/**
 * Created by grisha on 12.02.15.
 */
public class WordCursorAdapter extends CursorAdapter {
    private LayoutInflater inflater;
    public WordCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(android.R.layout.simple_list_item_activated_2, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.word = (TextView) view.findViewById(android.R.id.text1);
        viewHolder.translate = (TextView) view.findViewById(android.R.id.text2);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        TranslateWrapper wrapper = getWrapper(cursor);
        viewHolder.word.setText(wrapper.getWord());
        viewHolder.translate.setText(wrapper.getLanguageCode() + ": "
                + wrapper.getTranslateDBFormat());
    }

    @Override
    public TranslateWrapper getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        return getWrapper(cursor);
    }

    private TranslateWrapper getWrapper(Cursor cursor) {
        TranslateWrapper translateWrapper = new TranslateWrapper();
        translateWrapper.setId(cursor.getInt(cursor
                .getColumnIndex(BaseColumns._ID)));
        translateWrapper.setWord(cursor.getString(cursor
                .getColumnIndex(DictionaryDBHelper.COLUMN_WORD)));
        translateWrapper.setTranslateDBFormat(cursor.getString(cursor
                .getColumnIndex(DictionaryDBHelper.COLUMN_TRANSLATION)));
        translateWrapper.setLanguageCode(cursor
                .getString(cursor.getColumnIndex(DictionaryDBHelper.COLUMN_LANGUAGE)));
        return translateWrapper;
    }

    private class ViewHolder {
        TextView word;
        TextView translate;
    }

}
