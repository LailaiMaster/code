package me.ztiany.opencv.idrecognizing;

import android.content.Context;
import android.os.AsyncTask;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2020-07-23 18:18
 */
public class TessTwoUtils {

    private static final String LANGUAGE = "cn";

    public interface CallBack {

        void onProgressing();

        void onSuccess(TessBaseAPI result);

        void onError();

    }

    public static void initTess(Context context, CallBack callBack) {
        new AsyncTask<Void, Void, TessBaseAPI>() {
            @Override
            protected void onPreExecute() {
                callBack.onProgressing();
            }

            @Override
            protected void onPostExecute(TessBaseAPI result) {
                if (result != null) {
                    callBack.onSuccess(result);
                } else {
                    callBack.onError();
                }
            }

            @Override
            protected TessBaseAPI doInBackground(Void... params) {
                TessBaseAPI baseApi = new TessBaseAPI();
                try {
                    InputStream is = null;
                    is = context.getAssets().open(LANGUAGE + ".traineddata");
                    File file = new File("/sdcard/tess/tessdata/" + LANGUAGE + ".traineddata");
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[2048];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    }
                    is.close();

                    if (baseApi.init("/sdcard/tess", LANGUAGE)) {
                        return baseApi;
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}