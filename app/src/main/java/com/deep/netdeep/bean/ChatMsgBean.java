package com.deep.netdeep.bean;

import com.deep.netdeep.net.bean.UserTable;

import java.io.Serializable;

public class ChatMsgBean<T> implements Serializable {
    public UserTable userTableMine;
    public UserTable userTableHere;
    public int type;
    public long time;
    public boolean isRead = false;
    public T data;
}
