package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deep.dpwork.adapter.DpAdapter;
import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.annotation.DpStatus;
import com.deep.dpwork.util.DBUtil;
import com.deep.dpwork.util.DTimeUtil;
import com.deep.dpwork.util.DisplayUtil;
import com.deep.dpwork.util.InputManagerUtil;
import com.deep.dpwork.util.SoftListenerUtil;
import com.deep.dpwork.weight.DpRecyclerView;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.bean.ChatMsgBean;
import com.deep.netdeep.bean.UserChatMsgBean;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.UserChatBean;
import com.deep.netdeep.net.bean.UserTable;
import com.deep.netdeep.socket.WebSocketUtil;
import com.deep.netdeep.socket.WsListener;
import com.deep.netdeep.util.ImgPhotoUtil;
import com.deep.netdeep.util.TouchExt;
import com.deep.netdeep.util.WebChatUtil;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.gson.Gson;
import com.prohua.roundlayout.RoundAngleFrameLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

@DpStatus(blackFont = true)
@DpLayout(R.layout.chat_screen)
public class ChatScreen extends TBaseScreen implements WsListener {

    @BindView(R.id.backLin)
    ConstraintLayout backLin;
    @BindView(R.id.backTouch)
    ImageView backTouch;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.contentEdit)
    EditText contentEdit;
    @BindView(R.id.sendBt)
    RoundAngleFrameLayout sendBt;
    @BindView(R.id.centerBottom)
    LinearLayout centerBottom;
    @BindView(R.id.moreDoTouch)
    ImageView moreDoTouch;
    @BindView(R.id.inputImg)
    ImageView inputImg;
    @BindView(R.id.moreLin)
    LinearLayout moreLin;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    DpRecyclerView recyclerView;

    private List<ChatMsgBean> chatMsgBeans = new ArrayList<>();

    private DpAdapter dpAdapter;

    private boolean hasTeSuHeight = false;
    private boolean isShowSoft = false;

    private UserChatBean userChatBean;

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    public void setUserChatBean(UserChatBean userChatBean) {
        this.userChatBean = userChatBean;
    }

    /**
     * 懒加载
     */
    public static ChatScreen newInstance() {
        return new ChatScreen();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void init() {

        initData();

        userName.setText(userChatBean.userTable.getUsername());
        backTouch.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, this::closeEx));
        sendBt.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, () -> {
            ChatMsgBean<String> stringChatMsgBean = new ChatMsgBean<>();
            UserTable userTable = new UserTable();
            userTable.setUsername(CoreApp.appBean.tokenBean.userTable.getUsername());
            stringChatMsgBean.userTableMine = userTable;
            stringChatMsgBean.userTableHere = userChatBean.userTable;
            stringChatMsgBean.type = 0;
            stringChatMsgBean.data = contentEdit.getText().toString();
            chatMsgBeans.add(0, stringChatMsgBean);
            dpAdapter.notifyDataSetChanged();
            contentEdit.setText("");
            recyclerView.scrollToPosition(0);

            BaseEn<ChatMsgBean<String>> baseEn = new BaseEn<>();
            baseEn.code = 30000;
            baseEn.msg = "client msg";
            baseEn.data = stringChatMsgBean;
            WebSocketUtil.get().send(new Gson().toJson(baseEn));

            saveData();
        }));

        contentEdit.setOnClickListener(v -> InputManagerUtil.showSoftInputFromWindow(_dpActivity, contentEdit));

        contentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (contentEdit.getText().toString().length() > 0) {
                    sendBt.setVisibility(View.VISIBLE);
                    inputImg.setVisibility(View.GONE);
                } else {
                    sendBt.setVisibility(View.GONE);
                    inputImg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        onGlobalLayoutListener = SoftListenerUtil.listener(Objects.requireNonNull(getActivity()), backLin, i -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) centerBottom.getLayoutParams();
            if (i < DisplayUtil.dip2px(_dpActivity, 20)) {
                if (i > 0) {
                    hasTeSuHeight = true;
                    layoutParams.bottomMargin = i - DisplayUtil.dip2px(_dpActivity, 12);
                } else {
                    layoutParams.bottomMargin = i;
                }
                if (isShowSoft) {
                    recyclerView.scrollToPosition(0);
                }
                isShowSoft = false;
            } else {
                if (moreLin.getVisibility() == View.VISIBLE) {
                    moreLin.setVisibility(View.GONE);
                    ViewAnimator.animate(moreDoTouch).rotation(90, 0f).duration(300).start();
                }
                if (hasTeSuHeight) {
                    layoutParams.bottomMargin = i - DisplayUtil.dip2px(_dpActivity, 12);
                } else {
                    layoutParams.bottomMargin = i;
                }
                if (!isShowSoft) {
                    recyclerView.scrollToPosition(0);
                    isShowSoft = true;
                }
            }
            centerBottom.setLayoutParams(layoutParams);

        });

        moreDoTouch.setOnTouchListener((v, event) ->
                TouchExt.alpTouch(v, event, () -> {
                    if (moreLin.getVisibility() == View.VISIBLE) {
                        moreLin.setVisibility(View.GONE);
                        ViewAnimator.animate(moreDoTouch).rotation(90, 0f).duration(300).start();
                    } else {
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) centerBottom.getLayoutParams();
                        if (layoutParams.bottomMargin > DisplayUtil.dip2px(_dpActivity, 20)) {
                            InputManagerUtil.hiddenSoftInputFromWindow(_dpActivity, contentEdit);
                            DTimeUtil.run(200, () -> {
                                moreLin.setVisibility(View.VISIBLE);
                                ViewAnimator.animate(moreDoTouch).rotation(0, 90f).duration(300).start();
                            });
                        } else {
                            moreLin.setVisibility(View.VISIBLE);
                            ViewAnimator.animate(moreDoTouch).rotation(0, 90f).duration(300).start();
                        }
                    }
                }));

        // --------------------------------------

        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(0);
        });

        dpAdapter = DpAdapter.newLine(getContext(), chatMsgBeans, R.layout.chat_screen_item_layout)
                .itemView((universalViewHolder, i) -> {
                    universalViewHolder.vbi(R.id.leftMsg).setVisibility(View.GONE);
                    universalViewHolder.vbi(R.id.rightMsg).setVisibility(View.GONE);
                    if (chatMsgBeans.get(i).userTableMine.getUsername().equals(CoreApp.appBean.tokenBean.userTable.getUsername())) {
                        universalViewHolder.vbi(R.id.rightMsg).setVisibility(View.VISIBLE);
                        universalViewHolder.setText(R.id.contentRightText, (String) (chatMsgBeans.get(i).data));
                        ImgPhotoUtil.getPhoto(CoreApp.appBean.tokenBean.userTable.getHeaderPath(), (ImageView) universalViewHolder.vbi(R.id.rightHead));
                    } else {
                        universalViewHolder.vbi(R.id.leftMsg).setVisibility(View.VISIBLE);
                        universalViewHolder.setText(R.id.contentLeftText, (String) (chatMsgBeans.get(i).data));
                        ImgPhotoUtil.getPhoto(chatMsgBeans.get(i).userTableHere.getHeaderPath(), (ImageView) universalViewHolder.vbi(R.id.leftHead));
                    }
                })
                .itemClick((view, i) -> {
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, true));
        recyclerView.setAdapter(dpAdapter);

        recyclerView.scrollToPosition(0);

        WebSocketUtil.get().addListener(this);
    }

    private void initData() {

        boolean haveChat = false;

        for (int i = 0; i < CoreApp.appBean.userChatMsgBeanList.size(); i++) {
            if (CoreApp.appBean.userChatMsgBeanList.get(i).userChatBean.userTable.getId() == userChatBean.userTable.getId()) {
                chatMsgBeans.addAll(CoreApp.appBean.userChatMsgBeanList.get(i).chatMsgBeans);
                haveChat = true;
                break;
            }
        }

        if (!haveChat) {
            UserChatMsgBean userChatMsgBean = new UserChatMsgBean();
            userChatMsgBean.userChatBean = userChatBean;
            userChatMsgBean.chatMsgBeans = new ArrayList<>();
            CoreApp.appBean.userChatMsgBeanList.add(userChatMsgBean);
            DBUtil.save(CoreApp.appBean);
        }
    }

    private void saveData() {

        for (int i = 0; i < CoreApp.appBean.userChatMsgBeanList.size(); i++) {
            if (CoreApp.appBean.userChatMsgBeanList.get(i).userChatBean.userTable.getId() == userChatBean.userTable.getId()) {
                CoreApp.appBean.userChatMsgBeanList.get(i).chatMsgBeans = chatMsgBeans;
                DBUtil.save(CoreApp.appBean);
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WebSocketUtil.get().removeListener(this);
        backLin.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    @Override
    public void connected() {

    }

    @SuppressWarnings("all")
    @Override
    public void msg(String text) {
        WebChatUtil.get(text, object -> {
            BaseEn<ChatMsgBean<?>> stringChatMsgBean = (BaseEn<ChatMsgBean<?>>) object;
            chatMsgBeans.add(0, stringChatMsgBean.data);
            dpAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(0);
            saveData();
        });
    }

    @Override
    public void disconnected() {

    }

    @Override
    public void failed() {

    }
}
