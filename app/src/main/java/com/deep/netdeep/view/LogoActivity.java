package com.deep.netdeep.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import androidx.annotation.Nullable;

import com.deep.dpwork.util.DTimeUtil;
import com.deep.netdeep.R;
import com.deep.netdeep.core.WorkCore;
import com.github.florent37.viewanimator.ViewAnimator;
import com.gyf.barlibrary.ImmersionBar;

/**
 * Class - 启动图
 * <p>
 * Created by Deepblue on 2018/8/23.
 */

public class LogoActivity extends Activity {

    View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isTaskRoot()) {
            finish();
            return;
        }

        setContentView(R.layout.logo_screen);

        view = findViewById(R.id.logo);

        ViewAnimator.animate(view).rotation(0f, 10f, 0f).interpolator(new AnticipateOvershootInterpolator()).duration(2000).start();

        // 状态栏透明和间距处理
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                // 原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，
                // 如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .init();

        DTimeUtil.run(1000, this::in);

    }

    public void in() {
        Intent intent = new Intent();
        intent.setClass(LogoActivity.this, WorkCore.class);
        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        new Handler().postDelayed(LogoActivity.this::finish, 100);
    }

}
