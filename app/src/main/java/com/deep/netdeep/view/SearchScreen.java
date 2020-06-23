package com.deep.netdeep.view;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.deep.dpwork.adapter.DpAdapter;
import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.annotation.DpStatus;
import com.deep.dpwork.util.InputManagerUtil;
import com.deep.dpwork.util.Lag;
import com.deep.dpwork.util.ToastUtil;
import com.deep.dpwork.weight.DpRecyclerView;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.UserTable;
import com.deep.netdeep.util.ImgPhotoUtil;
import com.deep.netdeep.util.TouchExt;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

@DpStatus(blackFont = true)
@DpLayout(R.layout.search_screen)
public class SearchScreen extends TBaseScreen {

    @BindView(R.id.inputEt)
    EditText inputEt;
    @BindView(R.id.searchBt)
    LinearLayout searchBt;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    DpRecyclerView recyclerView;

    @BindView(R.id.nullLineLin)
    RelativeLayout nullLineLin;

    private List<UserTable> userTables = new ArrayList<>();

    private DpAdapter dpAdapter;

    public void getOnlineUser() {

        userTables.clear();
        dpAdapter.notifyDataSetChanged();
        refreshLayout.finishRefresh(0);

        if (CoreApp.appBean.userChatMsgBeanList.size() == 0) {
            nullLineLin.setVisibility(View.VISIBLE);
        } else {
            nullLineLin.setVisibility(View.GONE);
        }
        Dove.flyLifeOnlyNet(CoreApp.jobTask.findUser(inputEt.getText().toString()),
                new Dover<BaseEn<List<UserTable>>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void don(Disposable d, BaseEn<List<UserTable>> userTableBaseEn) {
                        if (userTableBaseEn.code == 200) {
                            Lag.i(userTableBaseEn.msg);
                            userTables.clear();
                            userTables.addAll(userTableBaseEn.data);
                            dpAdapter.notifyDataSetChanged();
                            refreshLayout.finishRefresh(0);
                            if (userTables.size() > 0) {
                                nullLineLin.setVisibility(View.GONE);
                            }
                        } else {
                            Lag.i(userTableBaseEn.msg);
                            userTables.clear();
                            dpAdapter.notifyDataSetChanged();
                            refreshLayout.finishRefresh(0);
                            nullLineLin.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void die(Disposable d, Throwable throwable) {
                        ToastUtil.showError("NetWork is Error");
                        userTables.clear();
                        dpAdapter.notifyDataSetChanged();
                        refreshLayout.finishRefresh(0);
                        nullLineLin.setVisibility(View.VISIBLE);
                    }
                });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void init() {

        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setOnRefreshListener(refreshLayout -> getOnlineUser());

        dpAdapter = DpAdapter.newLine(getContext(), userTables, R.layout.search_item_layout)
                .itemView((universalViewHolder, i) -> {
                    universalViewHolder.setText(R.id.userName, userTables.get(i).getNickname() == null ?
                            userTables.get(i).getUsername() : userTables.get(i).getNickname());
                    universalViewHolder.setText(R.id.userContent, userTables.get(i).getContent());
                    ImageView vs = (ImageView) universalViewHolder.vbi(R.id.headImg);
                    if (!userTables.get(i).getHeaderPath().equals(vs.getTag(R.id.headImg))) {
                        vs.setTag(R.id.headImg, userTables.get(i).getHeaderPath());
                        ImgPhotoUtil.getPhoto(_dpActivity, userTables.get(i).getHeaderPath(), (ImageView) universalViewHolder.vbi(R.id.headImg));
                    }
                    universalViewHolder.vbi(R.id.chatTouch).setOnClickListener(v -> {
                        ChatScreen chatScreen = ChatScreen.newInstance();
                        chatScreen.setUserTable(userTables.get(i));
                        openAndClose(chatScreen);
                    });
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(dpAdapter);

        searchBt.setOnTouchListener((v, event) -> TouchExt.alpTouch(v, event, () -> {
            refreshLayout.autoRefresh();
            InputManagerUtil.hiddenSoftInputFromWindow(_dpActivity, inputEt);
        }));

        inputEt.setOnClickListener(v -> InputManagerUtil.showSoftInputFromWindow(_dpActivity, inputEt));

        if (CoreApp.appBean.userChatMsgBeanList.size() == 0) {
            nullLineLin.setVisibility(View.VISIBLE);
        } else {
            nullLineLin.setVisibility(View.GONE);
        }

        InputManagerUtil.showSoftInputFromWindow(_dpActivity, inputEt);
    }

    @Override
    public SCREEN screenOpenAnim() {
        return SCREEN.Horizontal;
    }
}
