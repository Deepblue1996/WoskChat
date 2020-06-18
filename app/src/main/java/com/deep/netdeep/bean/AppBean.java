package com.deep.netdeep.bean;

import com.deep.netdeep.net.bean.TokenBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppBean implements Serializable  {
    public TokenBean tokenBean = new TokenBean();
    public List<UserChatMsgBean> userChatMsgBeanList = new ArrayList<>();
}
