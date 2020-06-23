package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deep.dpwork.annotation.DpChild;
import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.base.BaseScreen;
import com.deep.dpwork.dialog.DialogScreen;
import com.deep.dpwork.dialog.DpDialogScreen;
import com.deep.dpwork.util.DBUtil;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.event.LoginSuccessEvent;
import com.deep.netdeep.event.MainSelectTabEvent;
import com.deep.netdeep.event.UpdateInfoEvent;
import com.deep.netdeep.socket.WebSocketUtil;
import com.deep.netdeep.util.ImgPhotoUtil;
import com.deep.netdeep.util.TouchExt;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

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

        if (CoreApp.appBean.tokenBean.token == null) {

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

        memberLin.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, () -> baseScreen.open(UserInfoScreen.class)));

    }

    @SuppressLint("SetTextI18n")
    public void upInfo() {
        userName.setText(CoreApp.appBean.tokenBean.userTable.getNickname());
        userId.setText("USER ID:" + CoreApp.appBean.tokenBean.userTable.getId());
        ImgPhotoUtil.getPhoto(_dpActivity, CoreApp.appBean.tokenBean.userTable.getHeaderPath(), headImg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginSuccessEvent event) {
        upInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateInfoEvent event) {
        upInfo();
    }

}
