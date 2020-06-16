package com.deep.netdeep.core;

import com.deep.dpwork.DpWorkApplication;
import com.deep.dpwork.annotation.DpBugly;
import com.deep.dpwork.annotation.DpCrash;
import com.deep.dpwork.annotation.DpDataBase;
import com.deep.dpwork.annotation.DpLang;
import com.deep.dpwork.annotation.net.DoveInit;
import com.deep.dpwork.lang.LanguageType;
import com.deep.netdeep.bean.AppBean;
import com.deep.netdeep.net.JobTask;
import com.deep.netdeep.net.bean.TokenChatUBean;

/**
 * Class - 主类
 * <p>
 * Created by Deepblue on 2019/9/29 0029.
 */
@DpCrash
@DpBugly("0fbc31ba0d")
@DpLang(LanguageType.LANGUAGE_FOLLOW_SYSTEM)
public class CoreApp extends DpWorkApplication {

    // 全局对话id
    public static TokenChatUBean tokenChatUBean = new TokenChatUBean();

    // 数据保存
    @DpDataBase(AppBean.class)
    public static AppBean appBean;

    // 服务器地址
    @DoveInit(url = "http://192.168.0.112:8080/",
            interfaceClass = JobTask.class)

    // 网络框架
    public static JobTask jobTask;

    /**
     * 初始化函数
     * Bugly ID
     */
    @Override
    protected void initApplication() {
    }

}
