package com.deep.netdeep.bean;

import com.deep.dpwork.data.BaseData;

public class UserLoginEventBean extends BaseData {
    public String username;
    public String password;

    public UserLoginEventBean(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
