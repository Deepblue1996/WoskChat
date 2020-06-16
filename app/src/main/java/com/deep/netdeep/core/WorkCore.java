package com.deep.netdeep.core;

import android.Manifest;

import com.deep.dpwork.DpWorkCore;
import com.deep.dpwork.annotation.DpDataBase;
import com.deep.dpwork.annotation.DpPermission;
import com.deep.dpwork.annotation.net.DoveInit;
import com.deep.netdeep.bean.AppBean;
import com.deep.netdeep.net.JobTask;
import com.prohua.dove.Dove;

/**
 * Class - 主活动类
 * <p>
 * Created by Deepblue on 2019/9/29 0029.
 */
@DpPermission({
        Manifest.permission.INTERNET,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE
})
//@DpDebug
public class WorkCore extends DpWorkCore {

    @Override
    protected void initCore() {

    }


    @Override
    protected void permissionComplete(boolean b) {

    }

}
