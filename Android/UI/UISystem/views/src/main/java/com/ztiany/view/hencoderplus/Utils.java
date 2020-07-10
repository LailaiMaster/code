package com.ztiany.view.hencoderplus;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ztiany.view.R;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2019-10-02 17:08
 */
public class Utils {

    public static Bitmap getAvatar(Resources res, int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, R.drawable.avatar_rengwuxian, options);
        options.inJustDecodeBounds = false;
        options.inDensity = options.outWidth;
        options.inTargetDensity = width;
        return BitmapFactory.decodeResource(res, R.drawable.avatar_rengwuxian, options);
    }

    public static int getZForCamera() {
        return (int) (- 6 * Resources.getSystem().getDisplayMetrics().density);
    }

}
