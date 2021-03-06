package com.deep.netdeep.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.deep.dpwork.util.ImageUtil;
import com.deep.dpwork.util.Lag;
import com.deep.dpwork.util.ShPrefUtil;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.net.bean.BaseEn;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;

import java.io.File;

import io.reactivex.disposables.Disposable;

/**
 * 服务器获取
 */
public class ImgPhotoUtil {

//    /**
//     * 获取图片
//     */
//    public static void getPhoto(String headPath, ImageView imageView) {
//
//        if(headPath == null) {
//            return;
//        }
//
//        String msg = ShPrefUtil.getPrefString(headPath, "");
//
//        if (msg != null && !msg.equals("")) {
//            File file = FileToBase64Util.base64ToFile(msg);
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            imageView.setImageBitmap(bitmap);
//            Lag.i("获取本地图像:" + headPath);
//            return;
//        }
//
//        Lag.i("获取网络图像");
//
//        Dove.flyLife(CoreApp.jobTask.rePhoto(headPath), new Dover<BaseEn<String>>() {
//            @Override
//            public void don(Disposable d, BaseEn<String> stringBaseEn) {
//                Lag.i("获取成功");
//                ShPrefUtil.setPrefString(headPath, stringBaseEn.data);
//                File file = FileToBase64Util.base64ToFile(stringBaseEn.data);
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                imageView.setImageBitmap(bitmap);
//            }
//
//            @Override
//            public void die(Disposable d, Throwable throwable) {
//                Lag.i("获取失败:" + throwable.getMessage());
//            }
//        });
//    }

    /**
     * 获取图片
     */
    public static void getPhoto(Activity activity, String headPath, ImageView imageView) {

        if(headPath == null) {
            return;
        }

        String msg = ShPrefUtil.getPrefString(headPath, "");

        if (msg != null && !msg.equals("")) {
            File file = FileToBase64Util.base64ToFile(msg);
            ImageExtUtil.show(activity, file, imageView);
            Lag.i("获取本地图像:" + headPath);
            return;
        }

        Lag.i("获取网络图像");

        Dove.flyLife(CoreApp.jobTask.rePhoto(headPath), new Dover<BaseEn<String>>() {
            @Override
            public void don(Disposable d, BaseEn<String> stringBaseEn) {
                Lag.i("获取成功");
                ShPrefUtil.setPrefString(headPath, stringBaseEn.data);
                File file = FileToBase64Util.base64ToFile(stringBaseEn.data);
                ImageExtUtil.show(activity, file, imageView);
            }

            @Override
            public void die(Disposable d, Throwable throwable) {
                Lag.i("获取失败:" + throwable.getMessage());
            }
        });
    }

}
