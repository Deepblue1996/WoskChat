package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.annotation.DpStatus;
import com.deep.dpwork.dialog.DpEditTextDialogScreen;
import com.deep.dpwork.util.DBUtil;
import com.deep.dpwork.util.Lag;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.event.UpdateInfoEvent;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.UserTable;
import com.deep.netdeep.util.ImgPhotoUtil;
import com.deep.netdeep.util.TouchExt;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;

@DpStatus(blackFont = true)
@DpLayout(R.layout.user_info_screen)
public class UserInfoScreen extends TBaseScreen {

    @BindView(R.id.backTouch)
    ImageView backTouch;
    @BindView(R.id.headImg)
    ImageView headImg;
    @BindView(R.id.nickEt)
    LinearLayout nickEt;
    @BindView(R.id.contentEt)
    LinearLayout contentEt;
    @BindView(R.id.userId)
    TextView userId;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.contentName)
    TextView contentName;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void init() {

        if (CoreApp.appBean.tokenBean.token == null) {
            userName.setText("Click login");
        } else {
            try {
                upInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        backTouch.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, this::closeEx));

        headImg.setOnTouchListener((v, event) ->
                TouchExt.alpTouch(v, event, () ->
                        imgSelect(UserInfoScreen.this)));

        nickEt.setOnTouchListener((v, event) ->
                TouchExt.alpTouch(v, event, () ->
                        DpEditTextDialogScreen
                                .create()
                                .setTitle("Edit NickName")
                                .setHint(CoreApp.appBean.tokenBean.userTable.getNickname() == null ?
                                        "Please input your new nickname" : CoreApp.appBean.tokenBean.userTable.getNickname())
                                .addButton(_dpActivity, "Edit", (dialogScreen, s) -> {
                                    editInfo(s, CoreApp.appBean.tokenBean.userTable.getContent());
                                    dialogScreen.close();
                                })
                                .addButton(_dpActivity, "No", (dialogScreen, s) -> {
                                    dialogScreen.close();
                                }).open(fragmentManager())));

        contentEt.setOnTouchListener((v, event) ->
                TouchExt.alpTouch(v, event, () ->
                        DpEditTextDialogScreen
                                .create()
                                .setTitle("Edit Signature")
                                .setHint(CoreApp.appBean.tokenBean.userTable.getContent() == null ?
                                        "Please input your new signature" : CoreApp.appBean.tokenBean.userTable.getContent())
                                .addButton(_dpActivity, "Edit", (dialogScreen, s) -> {
                                    editInfo(CoreApp.appBean.tokenBean.userTable.getNickname(), s);
                                    dialogScreen.close();
                                })
                                .addButton(_dpActivity, "No", (dialogScreen, s) -> {
                                    dialogScreen.close();
                                }).open(fragmentManager())));
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
     *
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

                ImgPhotoUtil.getPhoto(_dpActivity, CoreApp.appBean.tokenBean.userTable.getHeaderPath(), headImg);
                EventBus.getDefault().post(new UpdateInfoEvent());
            }

            @Override
            public void die(Disposable d, Throwable throwable) {
                Lag.i("上传失败:" + throwable.getMessage());
            }
        });
    }

    /**
     * 修改信息
     *
     * @param newNickName 新的昵称
     * @param newContent  新的签名
     */
    private void editInfo(String newNickName, String newContent) {

        CoreApp.appBean.tokenBean.userTable.setNickname(newNickName);
        CoreApp.appBean.tokenBean.userTable.setContent(newContent);

        String msg = new Gson().toJson(CoreApp.appBean.tokenBean.userTable);

        Dove.flyLifeOnlyNet(CoreApp.jobTask.editInfo(msg), new Dover<BaseEn<UserTable>>() {
            @Override
            public void don(Disposable d, BaseEn<UserTable> stringBaseEn) {

                if (stringBaseEn.code == 200) {
                    Lag.i("修改成功");
                    DBUtil.save(CoreApp.appBean);

                    upInfo();

                    EventBus.getDefault().post(new UpdateInfoEvent());
                }

            }

            @Override
            public void die(Disposable d, Throwable throwable) {
                Lag.i("修改失败:" + throwable.getMessage());
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
        userId.setText("USER ID:" + CoreApp.appBean.tokenBean.userTable.getId() + "(" + CoreApp.appBean.tokenBean.userTable.getUsername() + ")");
        userName.setText(CoreApp.appBean.tokenBean.userTable.getNickname());
        contentName.setText(CoreApp.appBean.tokenBean.userTable.getContent());
        ImgPhotoUtil.getPhoto(_dpActivity, CoreApp.appBean.tokenBean.userTable.getHeaderPath(), headImg);
    }

}
