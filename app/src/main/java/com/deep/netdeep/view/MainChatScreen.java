package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.deep.dpwork.adapter.DpAdapter;
import com.deep.dpwork.annotation.DpChild;
import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.base.BaseScreen;
import com.deep.dpwork.util.Lag;
import com.deep.dpwork.util.ToastUtil;
import com.deep.dpwork.weight.DpRecyclerView;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.bean.ChatMsgBean;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.UserChatBean;
import com.deep.netdeep.socket.WebSocketUtil;
import com.deep.netdeep.socket.WsListener;
import com.deep.netdeep.util.FileToBase64Util;
import com.deep.netdeep.util.ImgPhotoUtil;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

@DpChild
@DpLayout(R.layout.main_chat_screen)
public class MainChatScreen extends TBaseScreen implements WsListener {

    @BindView(R.id.offlineLin)
    RelativeLayout offlineLin;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    DpRecyclerView recyclerView;

    private List<UserChatBean> userChatBeans = new ArrayList<>();

    private DpAdapter dpAdapter;

    private BaseScreen baseScreen;

    /**
     * 懒加载
     */
    public static MainChatScreen newInstance() {
        return new MainChatScreen();
    }

    public void setFather(BaseScreen baseScreen) {
        this.baseScreen = baseScreen;
    }

    public void getOnlineUser() {
        if (!WebSocketUtil.get().isConnected()) {
            ((MainScreen) baseScreen).loginAutoConnect();
            return;
        }

        offlineLin.setVisibility(View.GONE);

        Dove.flyLifeOnlyNet(CoreApp.jobTask.userList(CoreApp.appBean.tokenBean.token),
                new Dover<BaseEn<List<UserChatBean>>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void don(Disposable d, BaseEn<List<UserChatBean>> loginBeanBaseEn) {
                        Lag.i(loginBeanBaseEn.msg);
                        userChatBeans.clear();
                        userChatBeans.addAll(loginBeanBaseEn.data);
                        dpAdapter.notifyDataSetChanged();
                        refreshLayout.finishRefresh(0);
                    }

                    @Override
                    public void die(Disposable d, Throwable throwable) {
                        ToastUtil.showError("NetWork is Error");
                    }
                });
    }

    @Override
    public void init() {

        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setOnRefreshListener(refreshLayout -> getOnlineUser());

        dpAdapter = DpAdapter.newLine(getContext(), userChatBeans, R.layout.main_chat_item_layout)
                .itemView((universalViewHolder, i) -> {
                    universalViewHolder.setText(R.id.userName, userChatBeans.get(i).userTable.getUsername());
                    for (int j = 0; j < CoreApp.appBean.userChatMsgBeanList.size(); j++) {
                        if (CoreApp.appBean.userChatMsgBeanList.get(j).userChatBean.userTable.getId() == userChatBeans.get(i).userTable.getId()) {
                            ChatMsgBean chatMsgBean = CoreApp.appBean.userChatMsgBeanList.get(j).chatMsgBeans.get(j);
                            if (chatMsgBean.type == 0) {
                                universalViewHolder.setText(R.id.userContent, (String) (chatMsgBean.data));
                            }
                        }
                    }

                    ImgPhotoUtil.getPhoto(userChatBeans.get(i).userTable.getHeaderPath(), (ImageView) universalViewHolder.vbi(R.id.headImg));
                })
                .itemClick((view, i) -> {
                    ChatScreen chatScreen = ChatScreen.newInstance();
                    chatScreen.setUserChatBean(userChatBeans.get(i));
                    baseScreen.open(chatScreen);
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(dpAdapter);
    }

    @Override
    public void showScreen() {
        dpAdapter.notifyDataSetChanged();
    }

    @Override
    public void connected() {
        refreshLayout.autoRefresh(0);

        offlineLin.setVisibility(View.GONE);
    }

    @Override
    public void msg(String text) {

    }

    @Override
    public void disconnected() {
        userChatBeans.clear();
        dpAdapter.notifyDataSetChanged();
        refreshLayout.finishRefresh(0);
        offlineLin.setVisibility(View.VISIBLE);
    }

    @Override
    public void failed() {
        userChatBeans.clear();
        dpAdapter.notifyDataSetChanged();
        refreshLayout.finishRefresh(0);
        offlineLin.setVisibility(View.VISIBLE);
    }
}
