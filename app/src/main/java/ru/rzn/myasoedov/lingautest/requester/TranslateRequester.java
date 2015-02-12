package ru.rzn.myasoedov.lingautest.requester;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import ru.rzn.myasoedov.lingautest.BuildConfig;

/**
 * Http client
 */
public class TranslateRequester {
    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    public static final String KEY = "key";
    public static final String TEXT = "text";
    public static final String LANGUAGE_CODE = "lang";

    private static AsyncHttpClient client = new AsyncHttpClient();


    public static void getTranslate(String word, String languageCode,
                                    ResponseHandlerInterface responseHandlerInterface) {
        RequestParams requestParams = new RequestParams();
        requestParams.add(KEY, BuildConfig.YANDEX_API_KEY);
        requestParams.add(TEXT, word);
        requestParams.add(LANGUAGE_CODE, languageCode);
        client.get(BASE_URL, requestParams, responseHandlerInterface);
    }


}
