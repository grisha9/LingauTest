package ru.rzn.myasoedov.lingautest.db;

import android.content.ContentValues;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by grisha on 11.02.15.
 */
public class TranslateWrapper implements Serializable {
    private static final String DELIMITER = "-";
    private int id;
    private String word;
    private String languageCode;
    private String translateDBFormat;

    public TranslateWrapper() {
    }

    public TranslateWrapper(Translate translate, String word) {
        this.word = word;
        this.translateDBFormat = TextUtils.join(DELIMITER, translate.getText());
        languageCode = translate.getLang().split(DELIMITER)[1];
    }

    public String getWord() {
        return word;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void setTranslateDBFormat(String translateDBFormat) {
        this.translateDBFormat = translateDBFormat;
    }

    public String getTranslateDBFormat() {
        return translateDBFormat;
    }

    public String[] getTranslate() {
        return TextUtils.split(translateDBFormat, DELIMITER);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DictionaryDBHelper.COLUMN_WORD, word);
        contentValues.put(DictionaryDBHelper.COLUMN_LANGUAGE, languageCode);
        contentValues.put(DictionaryDBHelper.COLUMN_TRANSLATION, translateDBFormat);

        return contentValues;
    }
}
