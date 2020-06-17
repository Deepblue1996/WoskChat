package com.deep.netdeep.bean;

import com.deep.netdeep.net.bean.UserChatBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserChatMsgBean implements Serializable {
    public List<ChatMsgBean> chatMsgBeans = new ArrayList<>();
    public UserChatBean userChatBean;
}
