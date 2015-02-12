package ru.rzn.myasoedov.lingautest.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.Collection;
import java.util.List;


public class DictionaryProvider extends ContentProvider {
    public static final String SEARCH_SELECTION = DictionaryDBHelper.COLUMN_WORD + " like('%%%s%%') OR "
            + DictionaryDBHelper.COLUMN_TRANSLATION + " like('%%%s%%')";
    public static final String DELETE_SELECTION = BaseColumns._ID + " IN (%s)";
    public static final String AUTHORITY = "ru.rzn.gmyasoedov.lingautest";
    public static final int URI_ALL_WORDS = 1;
    public static final int URI_WORD_ID = 2;
    public static final Uri DICTIONARY_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + DictionaryDBHelper.TABLE_NAME);
    private static final UriMatcher uriMatcher;
    private DictionaryDBHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, DictionaryDBHelper.TABLE_NAME, URI_ALL_WORDS);
        uriMatcher.addURI(AUTHORITY, DictionaryDBHelper.TABLE_NAME + "/#", URI_WORD_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DictionaryDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        if (TextUtils.isEmpty(sort)) {
            sort = DictionaryDBHelper.COLUMN_WORD;
        }
        switch (uriMatcher.match(uri)) {
            case URI_ALL_WORDS:
                break;
            case URI_WORD_ID:
                selection = " _ID = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        String orderBy = DictionaryDBHelper.COLUMN_WORD;
        Cursor cursor = dbHelper.getWritableDatabase()
                .query(DictionaryDBHelper.TABLE_NAME, projection, selection, selectionArgs, null,
                        null, sort);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ALL_WORDS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ru.rzn.myasoedov.db.words";
            case URI_WORD_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vvnd.ru.rzn.myasoedov.db.words";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = dbHelper.getWritableDatabase().insert(DictionaryDBHelper.TABLE_NAME, null, contentValues);
        Uri resultUri = ContentUris.withAppendedId(DICTIONARY_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cnt = dbHelper.getWritableDatabase().delete(DictionaryDBHelper.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int cnt = dbHelper.getWritableDatabase().update(DictionaryDBHelper.TABLE_NAME, contentValues,
                selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public static String prepareSelectionForSearch(String search) {
        String selection = null;
        if (!TextUtils.isEmpty(search)) {
            selection = String.format(DictionaryProvider.SEARCH_SELECTION, search, search);
        }
        return selection;
    }

    public static String prepareSelectionForDelete(Iterable ids) {
        return String.format(DictionaryProvider.DELETE_SELECTION, TextUtils.join(",", ids));
    }

}
