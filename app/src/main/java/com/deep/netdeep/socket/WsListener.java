package com.deep.netdeep.socket;

/**
 * Class -
 * <p>
 * Created by Deepblue on 2019/8/2 0002.
 */

public interface WsListener {
    void connected();
    void msg(String text);
    void disconnected();
    void failed();
}
