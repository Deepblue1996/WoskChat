package com.deep.netdeep.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageToStringUtil {

    //把bitmap转换成Base64字符串
    public static String bitmapToString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream btString = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, btString);
        byte[] bytes = btString.toByteArray();
        string = Base64.encodeToString(bytes, Base64.URL_SAFE);
        return string;
    }

    //把Base64字符串转换成bitmap
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] decode = Base64.decode(base64String.trim(), Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }

}
