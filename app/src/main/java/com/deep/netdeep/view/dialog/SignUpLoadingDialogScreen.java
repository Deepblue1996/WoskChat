package com.deep.netdeep.view.dialog;

import com.deep.dpwork.annotation.DpLayout;
import com.deep.dpwork.util.Lag;
import com.deep.dpwork.util.ToastUtil;
import com.deep.netdeep.R;
import com.deep.netdeep.base.TDialogScreen;
import com.deep.netdeep.bean.UserLoginEventBean;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.event.LoginSuccessEvent;
import com.deep.netdeep.event.SignUpSuccessEvent;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.LoginBean;
import com.prohua.dove.Dove;
import com.prohua.dove.Dover;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.disposables.Disposable;

@DpLayout(R.layout.signup_loading_dialog_screen)
public class SignUpLoadingDialogScreen extends TDialogScreen {

    @Override
    public void init() {
        Dove.flyLifeOnlyNet(CoreApp.jobTask.register(((UserLoginEventBean) baseData).username, ((UserLoginEventBean) baseData).password),
                new Dover<BaseEn<String>>() {
                    @Override
                    public void don(Disposable d, BaseEn<String> loginBeanBaseEn) {
                        Lag.i(loginBeanBaseEn.msg);
                        if (loginBeanBaseEn.code == 200) {
                            ToastUtil.showSuccess("Sign up success");
                            EventBus.getDefault().post(new SignUpSuccessEvent());
                        } else {
                            ToastUtil.showError(loginBeanBaseEn.msg);
                        }
                        SignUpLoadingDialogScreen.this.close();
                    }

                    @Override
                    public void die(Disposable d, Throwable throwable) {
                        ToastUtil.showError("NetWork is Error");
                        SignUpLoadingDialogScreen.this.close();
                    }
                });
    }
}
