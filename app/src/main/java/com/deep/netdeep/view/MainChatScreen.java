package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.deep.dpwork.adapter.DpAdapter;
import com.deep.dpwork.annotation.DpChild;
import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.base.BaseScreen;
import com.deep.dpwork.util.DBUtil;
import com.deep.dpwork.util.Lag;
import com.deep.dpwork.util.ToastUtil;
import com.deep.dpwork.weight.DpRecyclerView;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.bean.ChatMsgBean;
import com.deep.netdeep.bean.UserChatMsgBean;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.event.UpdateInfoEvent;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.UserTable;
import com.deep.netdeep.socket.WebSocketUtil;
import com.deep.netdeep.socket.WsListener;
import com.deep.netdeep.util.ImgPhotoUtil;
import com.deep.netdeep.util.WebChatUtil;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

@DpChild
@DpLayout(R.layout.main_chat_screen)
public class MainChatScreen extends TBaseScreen implements WsListener {

    @BindView(R.id.offlineLin)
    RelativeLayout offlineLin;
    @BindView(R.id.nullLineLin)
    RelativeLayout nullLineLin;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    DpRecyclerView recyclerView;

    private List<UserChatMsgBean> userChatBeans = new ArrayList<>();
    private List<UserTable> userTableOnLines = new ArrayList<>();

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

        userChatBeans.clear();
        userChatBeans.addAll(CoreApp.appBean.userChatMsgBeanList);
        dpAdapter.notifyDataSetChanged();
        refreshLayout.finishRefresh(0);

        if (CoreApp.appBean.userChatMsgBeanList.size() == 0) {
            nullLineLin.setVisibility(View.VISIBLE);
        } else {
            nullLineLin.setVisibility(View.GONE);
        }
        Dove.flyLifeOnlyNet(CoreApp.jobTask.userList(CoreApp.appBean.tokenBean.token),
                new Dover<BaseEn<List<UserTable>>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void don(Disposable d, BaseEn<List<UserTable>> loginBeanBaseEn) {
                        Lag.i(loginBeanBaseEn.msg);
                        userTableOnLines.clear();
                        userTableOnLines.addAll(loginBeanBaseEn.data);
                        dpAdapter.notifyDataSetChanged();
                        refreshLayout.finishRefresh(0);
                    }

                    @Override
                    public void die(Disposable d, Throwable throwable) {
                        ToastUtil.showError("NetWork is Error");
                        nullLineLin.setVisibility(View.GONE);
                        offlineLin.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void init() {

        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setOnRefreshListener(refreshLayout -> getOnlineUser());

        userChatBeans.addAll(CoreApp.appBean.userChatMsgBeanList);

        dpAdapter = DpAdapter.newLine(getContext(), userChatBeans, R.layout.main_chat_item_layout)
                .itemView((universalViewHolder, i) -> {
                    universalViewHolder.setText(R.id.userName, userChatBeans.get(i).userTable.getNickname() == null ?
                            userChatBeans.get(i).userTable.getUsername() : userChatBeans.get(i).userTable.getNickname());

                    if (CoreApp.appBean.userChatMsgBeanList.size() == 0) {
                        universalViewHolder.setText(R.id.userContent, userChatBeans.get(i).userTable.getContent());
                        universalViewHolder.vbi(R.id.weiDuLin).setVisibility(View.GONE);
                    }

                    boolean isOnline = false;
                    for (int j = 0; j < userTableOnLines.size(); j++) {
                        if(userTableOnLines.get(j).getId() == userChatBeans.get(i).userTable.getId()) {
                            isOnline = true;
                            break;
                        }
                    }
                    if(isOnline) {
                        universalViewHolder.setImgRes(R.id.icLineIg, R.mipmap.ic_mem_online);
                    } else {
                        universalViewHolder.setImgRes(R.id.icLineIg, R.mipmap.ic_mem_offline);
                    }

                    universalViewHolder.vbi(R.id.btnTop).setOnClickListener(v -> {

                        for (int j = 0; j < CoreApp.appBean.userChatMsgBeanList.size(); j++) {
                            if(CoreApp.appBean.userChatMsgBeanList.get(j).userTable.getId() == userChatBeans.get(i).userTable.getId()) {
                                CoreApp.appBean.userChatMsgBeanList.remove(j);
                                break;
                            }
                        }
                        DBUtil.save(CoreApp.appBean);

                        userChatBeans.remove(i);
                        dpAdapter.notifyItemRemoved(i);
                        dpAdapter.notifyDataSetChanged();
                        if (userChatBeans.size() == 0) {
                            nullLineLin.setVisibility(View.VISIBLE);
                        }
                    });


                    universalViewHolder.vbi(R.id.contentBg).setOnClickListener(v -> {
                        ChatScreen chatScreen = ChatScreen.newInstance();
                        chatScreen.setUserTable(userChatBeans.get(i).userTable);
                        baseScreen.open(chatScreen);
                    });

                    for (int j = 0; j < CoreApp.appBean.userChatMsgBeanList.size(); j++) {
                        if (CoreApp.appBean.userChatMsgBeanList.get(j).userTable.getId() == userChatBeans.get(i).userTable.getId()) {
                            if (CoreApp.appBean.userChatMsgBeanList.get(j).chatMsgBeans.size() > 0) {
                                ChatMsgBean chatMsgBean = CoreApp.appBean.userChatMsgBeanList.get(j).chatMsgBeans.get(0);
                                if (chatMsgBean.type == 0) {
                                    universalViewHolder.setText(R.id.userContent, (String) (chatMsgBean.data));
                                }
                                int numWeiDu = 0;
                                for (int k = 0; k < CoreApp.appBean.userChatMsgBeanList.get(j).chatMsgBeans.size(); k++) {
                                    if (!CoreApp.appBean.userChatMsgBeanList.get(j).chatMsgBeans.get(k).isRead) {
                                        numWeiDu++;
                                    }
                                }
                                if (numWeiDu > 0) {
                                    universalViewHolder.vbi(R.id.weiDuLin).setVisibility(View.VISIBLE);
                                    universalViewHolder.setText(R.id.weiDuTv, String.valueOf(numWeiDu));
                                } else {
                                    universalViewHolder.vbi(R.id.weiDuLin).setVisibility(View.GONE);
                                }
                            } else {
                                // 没聊天消息显示签名
                                universalViewHolder.setText(R.id.userContent, userChatBeans.get(i).userTable.getContent());
                                universalViewHolder.vbi(R.id.weiDuLin).setVisibility(View.GONE);
                            }
                        }
                    }

                    ImageView vs = (ImageView) universalViewHolder.vbi(R.id.headImg);
                    if (!userChatBeans.get(i).userTable.getHeaderPath().equals(vs.getTag(R.id.headImg))) {
                        vs.setTag(R.id.headImg, userChatBeans.get(i).userTable.getHeaderPath());
                        ImgPhotoUtil.getPhoto(_dpActivity, userChatBeans.get(i).userTable.getHeaderPath(), (ImageView) universalViewHolder.vbi(R.id.headImg));
                    }

                });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(dpAdapter);

        if (CoreApp.appBean.userChatMsgBeanList.size() == 0) {
            nullLineLin.setVisibility(View.VISIBLE);
            offlineLin.setVisibility(View.GONE);
        } else {
            nullLineLin.setVisibility(View.GONE);
            offlineLin.setVisibility(View.GONE);
        }

        getOnlineUser();
    }

    @Override
    public void showScreen() {
        getOnlineUser();
    }

    @Override
    public void connected() {
        refreshLayout.autoRefresh(0);

        offlineLin.setVisibility(View.GONE);
    }

    @SuppressWarnings("all")
    @Override
    public void msg(String text) {
        WebChatUtil.get(text, 30000, object -> {
            BaseEn<ChatMsgBean<?>> stringChatMsgBean = (BaseEn<ChatMsgBean<?>>) object;
            for (int i = 0; i < CoreApp.appBean.userChatMsgBeanList.size(); i++) {
                // 判断是否同一个人
                if (stringChatMsgBean.data.userTableMine.getId() == CoreApp.appBean.userChatMsgBeanList.get(i).userTable.getId()) {
                    // 判断最后一条是否一样
                    if (CoreApp.appBean.userChatMsgBeanList.get(i).chatMsgBeans.size() == 0
                            || CoreApp.appBean.userChatMsgBeanList.get(i).chatMsgBeans.get(0).time != stringChatMsgBean.data.time) {
                        CoreApp.appBean.userChatMsgBeanList.get(i).chatMsgBeans.add(0, stringChatMsgBean.data);
                        DBUtil.save(CoreApp.appBean);
                        dpAdapter.notifyItemInserted(i);
                        Lag.i("主会话增加消息");
                    } else {
                        Lag.i("主会话增加消息，消息已记录");
                    }
                    break;
                }
            }
        });
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateInfoEvent event) {
        for (int i = 0; i < userChatBeans.size(); i++) {
            if (userChatBeans.get(i).userTable.getId() == CoreApp.appBean.tokenBean.userTable.getId()) {
                userChatBeans.get(i).userTable = CoreApp.appBean.tokenBean.userTable;
            }
        }
        dpAdapter.notifyDataSetChanged();
    }
}
