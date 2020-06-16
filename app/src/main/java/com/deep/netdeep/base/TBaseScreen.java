package com.deep.netdeep.base;

import com.deep.dpwork.base.BaseScreen;

import butterknife.ButterKnife;

/**
 * Class - 界面基类
 * <p>
 * Created by Deepblue on 2019/9/29 0029.
 */
public abstract class TBaseScreen extends BaseScreen {

    @Override
    protected void initView() {
        // 默认使用 ButterKnife
        ButterKnife.bind(this, superView);
        init();
    }

    public abstract void init();
}
