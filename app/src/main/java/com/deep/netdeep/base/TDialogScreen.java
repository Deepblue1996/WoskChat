package com.deep.netdeep.base;

import com.deep.dpwork.dialog.DialogScreen;

import butterknife.ButterKnife;

/**
 * Class - 弹窗基类
 * <p>
 * Created by Deepblue on 2019/9/29 0029.
 */
public abstract class TDialogScreen extends DialogScreen {

    @Override
    protected void initView() {
        // 默认使用 ButterKnife
        ButterKnife.bind(this, superView);
        init();
    }

    public abstract void init();
}
