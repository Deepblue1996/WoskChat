package com.deep.netdeep.core;

import android.Manifest;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.deep.dpwork.DpWorkCore;
import com.deep.dpwork.annotation.DpPermission;
import com.deep.dpwork.util.Lag;

import java.util.List;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
