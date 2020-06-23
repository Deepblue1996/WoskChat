package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.annotation.DpMainScreen;
import com.deep.dpwork.annotation.DpStatus;
import com.deep.dpwork.dialog.DialogScreen;
import com.deep.dpwork.dialog.DpDialogScreen;
import com.deep.dpwork.util.DBUtil;
import com.deep.dpwork.util.Lag;
import com.deep.dpwork.util.ToastUtil;
import com.deep.dpwork.weight.DpTabManager;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.bean.ChatMsgBean;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.event.LoginSuccessEvent;
import com.deep.netdeep.event.MainSelectTabEvent;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.socket.WebSocketUtil;
import com.deep.netdeep.socket.WsListener;
import com.deep.netdeep.util.TouchExt;
import com.deep.netdeep.util.WebChatUtil;
import com.github.florent37.viewanimator.ViewAnimator;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

// 242bfdb156569ea8880932009f52779e007e0c81

@DpMainScreen
@DpStatus(blackFont = true)
@DpLayout(R.layout.main_screen)
public class MainScreen extends TBaseScreen implements WsListener {

    static {
        ClassicsHeader.REFRESH_HEADER_PULLDOWN = "Drop down to refresh";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "Refreshing...";
        ClassicsHeader.REFRESH_HEADER_LOADING = "Loading...";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "Release refresh now";
        ClassicsHeader.REFRESH_HEADER_FINISH = "Refresh to complete";
        ClassicsHeader.REFRESH_HEADER_FAILED = "Refresh the failure";
        ClassicsHeader.REFRESH_HEADER_LASTTIME = "The last update M-d HH:mm";
        ClassicsHeader.REFRESH_HEADER_LASTTIME = "'Last update' M-d HH:mm";
    }

    @BindView(R.id.chatTouch)
    LinearLayout chatTouch;
    @BindView(R.id.mineTouch)
    LinearLayout mineTouch;
    @BindView(R.id.chatImg)
    ImageView chatImg;
    @BindView(R.id.chatTv)
    TextView chatTv;
    @BindView(R.id.memImg)
    ImageView memImg;
    @BindView(R.id.memTv)
    TextView memTv;
    @BindView(R.id.logoImg)
    ImageView logoImg;
    @BindView(R.id.searchImg)
    ImageView searchImg;
    @BindView(R.id.logoTv)
    TextView logoTv;

    @BindView(R.id.messageTv)
    TextView messageTv;

    private DpTabManager tabManager;

    private MainChatScreen mainChatScreen = MainChatScreen.newInstance();
    private MainMenScreen mainMenScreen = MainMenScreen.newInstance();

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void init() {

        if (CoreApp.appBean.tokenBean.token == null) {
            messageTv.setText("offline");
            open(LoginScreen.class);
        } else {
            messageTv.setText("connecting...");
            WebSocketUtil.get().addListener(this);
            loginAutoConnect();
        }

        chatTouch.setOnClickListener(v -> {
            tabManager.show(0);
            selectTabUI(0);
        });
        mineTouch.setOnClickListener(v -> {
            if (CoreApp.appBean.tokenBean.token == null) {
                open(LoginScreen.class);
            } else {
                tabManager.show(1);
                selectTabUI(1);
            }
        });
        searchImg.setOnClickListener(v -> open(SearchScreen.class));

        logoImg.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, this::logoAnim));

        mainChatScreen.setFather(this);
        mainMenScreen.setFather(this);

        tabManager = DpTabManager.get(R.id.childFragment, fragmentManager())
                .add(mainChatScreen)
                .add(mainMenScreen)
                .create();
    }

    private void selectTabUI(int item) {
        switch (item) {
            case 0:
                chatImg.setImageResource(R.mipmap.ic_tab_chat_s);
                memImg.setImageResource(R.mipmap.ic_tab_men);
                chatTv.setTextColor(Color.parseColor("#1C1D1C"));
                memTv.setTextColor(Color.parseColor("#828186"));
                break;
            case 1:
                chatImg.setImageResource(R.mipmap.ic_tab_chat);
                memImg.setImageResource(R.mipmap.ic_tab_men_s);
                chatTv.setTextColor(Color.parseColor("#828186"));
                memTv.setTextColor(Color.parseColor("#1C1D1C"));
                break;
        }
    }

    public void loginAutoConnect() {
        if (CoreApp.appBean.tokenBean.token != null) {
            Dove.addGlobalHeader("token", CoreApp.appBean.tokenBean.token);
        }
        Dove.flyLifeOnlyNet(CoreApp.jobTask.loginEffective(CoreApp.appBean.tokenBean.token),
                new Dover<BaseEn<String>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void don(Disposable d, BaseEn<String> loginBeanBaseEn) {
                        Lag.i(loginBeanBaseEn.msg);
                        if (loginBeanBaseEn.code == 200) {
                            WebSocketUtil.get().connect();
                        } else {
                            messageTv.setText("offline");
                            ToastUtil.showError("身份过期，请重新登陆");
                            open(LoginScreen.class);
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void die(Disposable d, Throwable throwable) {
                        ToastUtil.showError("NetWork is Error");
                        messageTv.setText("offline");
                        mainChatScreen.disconnected();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WebSocketUtil.get().closeWebSocket();
    }

    /**
     * 彩蛋
     */
    private void logoAnim() {
        ViewAnimator.animate(logoImg).rotation(0f, 10f, 0f).interpolator(new AnticipateOvershootInterpolator()).duration(2000).start();
        ViewAnimator.animate(logoTv).translationX(0f, 10f, 0f).interpolator(new AnticipateOvershootInterpolator()).duration(2000).start();
    }

    @Override
    public void onBack() {
        DpDialogScreen.create().setMsg("Do you want to exit the program?")
                .addButton(getContext(), "Yes", dialogScreen -> _dpActivity.finish())
                .addButton(getContext(), "No", DialogScreen::close)
                .open(fragmentManager());
    }

    @Override
    public void hideScreen() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showScreen() {
        Lag.i("添加监听");
        if (WebSocketUtil.get().isConnected()) {
            messageTv.setText("online");
        } else {
            messageTv.setText("offline");
        }
        mainChatScreen.showScreen();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginSuccessEvent event) {
        WebSocketUtil.get().addListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MainSelectTabEvent event) {
        tabManager.show(event.select);
        selectTabUI(event.select);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void connected() {
        messageTv.setText("online");
        WebChatUtil.put(10000, null);
    }

    @SuppressWarnings("all")
    @Override
    public void msg(String text) {
        Lag.i("接收到消息:" + text);
        WebChatUtil.get(text, 10000, null);
        WebChatUtil.get(text, 30000, new WebChatUtil.EndListener() {
            @Override
            public void end(Object object) {
                BaseEn<ChatMsgBean<?>> stringChatMsgBean = (BaseEn<ChatMsgBean<?>>) object;
                for (int i = 0; i < CoreApp.appBean.userChatMsgBeanList.size(); i++) {
                    // 判断是否同一个人
                    if (stringChatMsgBean.data.userTableMine.getId() == CoreApp.appBean.userChatMsgBeanList.get(i).userTable.getId()) {
                        // 判断最后一条是否一样
                        if (CoreApp.appBean.userChatMsgBeanList.get(i).chatMsgBeans.size() > 0 && CoreApp.appBean.userChatMsgBeanList.get(i).chatMsgBeans.get(0).time == stringChatMsgBean.data.time) {
                            Lag.i("主会话增加消息，消息已记录");
                            break;
                        } else {
                            CoreApp.appBean.userChatMsgBeanList.get(i).chatMsgBeans.add(0, stringChatMsgBean.data);
                            DBUtil.save(CoreApp.appBean);
                            Lag.i("主会话增加消息");
                            break;
                        }
                    }
                }
            }
        });
        mainMenScreen.upInfo();
        mainChatScreen.getOnlineUser();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void disconnected() {
        messageTv.setText("offline");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void failed() {
        messageTv.setText("offline");
        ToastUtil.showError("服务器连接断开，请重新登陆");
    }
}
