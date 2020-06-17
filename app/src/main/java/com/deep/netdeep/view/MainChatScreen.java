package com.deep.netdeep.view;

import android.annotation.SuppressLint;

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
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.UserChatBean;
import com.deep.netdeep.socket.WsListener;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

@DpChild
@DpLayout(R.layout.main_chat_screen)
public class MainChatScreen extends TBaseScreen implements WsListener {

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
        Dove.flyLife(CoreApp.jobTask.userList(CoreApp.appBean.userBean.token),
                new Dover<BaseEn<List<UserChatBean>>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void don(Disposable d, BaseEn<List<UserChatBean>> loginBeanBaseEn) {
                        Lag.i(loginBeanBaseEn.msg);
                        userChatBeans.clear();
                        userChatBeans.addAll(loginBeanBaseEn.data);
                        dpAdapter.notifyDataSetChanged();
                        refreshLayout.finishRefresh();
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
    public void connected() {
        refreshLayout.autoRefresh(0);
    }

    @Override
    public void msg(String text) {

    }

    @Override
    public void disconnected() {

    }

    @Override
    public void failed() {

    }
}
