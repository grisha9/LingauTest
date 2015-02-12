package ru.rzn.myasoedov.lingautest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import ru.rzn.myasoedov.lingautest.BuildConfig;

public class DictionaryDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "DICTIONARY";
    public static final String COLUMN_WORD = "WORD";
    public static final String COLUMN_LANGUAGE = "LANGUAGE";
    public static final String COLUMN_TRANSLATION = "TRANSLATION";


    public DictionaryDBHelper(Context context) {
        super(context, BuildConfig.DB_NAME, null, BuildConfig.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_NAME + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_WORD + " TEXT NOT NULL, " +
                COLUMN_LANGUAGE + " TEXT NOT NULL, " +
                COLUMN_TRANSLATION + " TEXT, " +
                "UNIQUE ("+ COLUMN_WORD +", "+ COLUMN_LANGUAGE +") ON CONFLICT IGNORE);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
