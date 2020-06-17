package com.deep.netdeep.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppBean implements Serializable  {
    public UserBean userBean = new UserBean();
    public List<UserChatMsgBean> userChatMsgBeanList = new ArrayList<>();
}
