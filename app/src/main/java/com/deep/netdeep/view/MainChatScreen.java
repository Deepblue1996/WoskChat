package com.deep.netdeep.view;

import com.deep.dpwork.annotation.DpChild;
import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.weight.DpRecyclerView;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TBaseScreen;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import butterknife.BindView;

@DpChild
@DpLayout(R.layout.main_chat_screen)
public class MainChatScreen extends TBaseScreen {

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.recyclerView)
    DpRecyclerView recyclerView;

    /**
     * 懒加载
     */
    public static MainChatScreen newInstance() {
        return new MainChatScreen();
    }

    @Override
    public void init() {

        refreshLayout.setEnableLoadMore(false);

    }
}
