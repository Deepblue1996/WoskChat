package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.deep.dpwork.annotation.DpChild;
import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.base.BaseScreen;
import com.deep.dpwork.dialog.DialogScreen;
import com.deep.dpwork.dialog.DpDialogScreen;
import com.deep.dpwork.util.DBUtil;
import com.deep.dpwork.util.ImageUtil;
import com.deep.dpwork.util.Lag;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.event.LoginSuccessEvent;
import com.deep.netdeep.event.MainSelectTabEvent;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.socket.WebSocketUtil;
import com.deep.netdeep.util.FileToBase64Util;
import com.deep.netdeep.util.ImageToStringUtil;
import com.deep.netdeep.util.ImgPhotoUtil;
import com.deep.netdeep.util.TouchExt;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;

@DpChild
@DpLayout(R.layout.main_men_screen)
public class MainMenScreen extends TBaseScreen {

    @BindView(R.id.memberLin)
    LinearLayout memberLin;
    @BindView(R.id.signOutTouch)
    LinearLayout signOutTouch;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.userId)
    TextView userId;
    @BindView(R.id.headImg)
    ImageView headImg;

    private BaseScreen baseScreen;

    /**
     * 懒加载
     */
    public static MainMenScreen newInstance() {
        return new MainMenScreen();
    }

    public void setFather(BaseScreen baseScreen) {
        this.baseScreen = baseScreen;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void init() {

        if (CoreApp.appBean.tokenBean.token == null || CoreApp.tokenChatUBean.tokenChatBean == null) {

            userName.setText("点击登陆");

        } else {
            try {
                upInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        signOutTouch.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, () ->
                DpDialogScreen.create()
                        .setMsg("Are you sure to sign out?")
                        .addButton(getContext(), "Yes", dialogScreen -> {
                            CoreApp.appBean.tokenBean.token = null;
                            DBUtil.save(CoreApp.appBean);
                            WebSocketUtil.get().closeWebSocket();
                            EventBus.getDefault().post(new MainSelectTabEvent(0));
                            dialogScreen.close();
                        })
                        .addButton(getContext(), "No", DialogScreen::close)
                        .open(fragmentManager())));

        memberLin.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, () -> imgSelect(this)));

        ImgPhotoUtil.getPhoto(CoreApp.appBean.tokenBean.userTable.getHeaderPath(), headImg);
    }


    public static void imgSelect(Fragment fragment) {

        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(fragment)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .compressSavePath(Objects.requireNonNull(fragment.getActivity()).getCacheDir().getPath())
                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(true)// 是否显示gif图片 true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /**
     * 上传头像
     * @param pathName
     */
    private void upLoadHead(String pathName) {
        MultipartBody.Part filePartList = Dove.filePart("file", pathName);
        Dove.flyLifeOnlyNet(CoreApp.jobTask.fileUploadHeadPortrait(CoreApp.appBean.tokenBean.token, filePartList), new Dover<BaseEn<String>>() {
            @Override
            public void don(Disposable d, BaseEn<String> stringBaseEn) {
                Lag.i("上传成功");

                CoreApp.appBean.tokenBean.userTable.setHeaderPath(stringBaseEn.data);
                DBUtil.save(CoreApp.appBean);

                ImgPhotoUtil.getPhoto(CoreApp.appBean.tokenBean.userTable.getHeaderPath(), headImg);
            }

            @Override
            public void die(Disposable d, Throwable throwable) {
                Lag.i("上传失败:" + throwable.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    String path = selectList.get(0).getCompressPath();

                    Lag.i("上传路径:" + path);

                    upLoadHead(path);

                    break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void upInfo() {
        userName.setText(CoreApp.appBean.tokenBean.userTable.getUsername());
        userId.setText("ACC ID:" + CoreApp.tokenChatUBean.tokenChatBean.asLongText);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginSuccessEvent event) {
        upInfo();
    }

}
