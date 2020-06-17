package com.deep.netdeep.util;

import com.deep.netdeep.bean.ChatMsgBean;
import com.deep.netdeep.core.CoreApp;
import com.deep.netdeep.net.bean.BaseEn;
import com.deep.netdeep.net.bean.TokenChatBean;
import com.deep.netdeep.net.bean.TokenChatUBean;
import com.deep.netdeep.socket.WebSocketUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class WebChatUtil {

    /**
     * 推送信息
     *
     * @param code 推送代码
     */
    public static void put(int code, EndListener endListener) {

        switch (code) {
            case 10000:
                /**
                 * 请求会话id
                 */
                BaseEn<TokenChatUBean> baseEn = new BaseEn<>();
                baseEn.code = 10000;
                baseEn.msg = "login success to connect";
                TokenChatUBean tokenChatUBean = new TokenChatUBean();
                TokenChatBean tokenChatBean = new TokenChatBean();
                tokenChatBean.token = CoreApp.appBean.userBean.token;
                tokenChatUBean.tokenChatBean = tokenChatBean;
                baseEn.data = tokenChatUBean;
                WebSocketUtil.get().send(new Gson().toJson(baseEn));
                if(endListener!=null) {
                    endListener.end(null);
                }
                break;
            case 20000:
                /**
                 * 发送登陆Token
                 */
                BaseEn<TokenChatUBean> tokenChatUBeanBaseEn = new BaseEn<>();
                tokenChatUBeanBaseEn.code = 20000;
                tokenChatUBeanBaseEn.msg = "client get service 10000";
                tokenChatUBeanBaseEn.data = CoreApp.tokenChatUBean;
                WebSocketUtil.get().send(new Gson().toJson(tokenChatUBeanBaseEn));
                if(endListener!=null) {
                    endListener.end(null);
                }
                break;
        }
    }

    /**
     * 收到的信息
     * @param msg 内容
     */
    public static void get(String msg, EndListener endListener) {

        BaseEn<?> baseEn = new Gson().fromJson(msg, BaseEn.class);

        switch (baseEn.code) {
            case 10000:
                /**
                 * 获取对话id
                 */
                Type type = new TypeToken<BaseEn<TokenChatUBean>>(){}.getType();
                BaseEn<TokenChatUBean> tokenChatUBeanBaseEn = new Gson().fromJson(msg, type);
                CoreApp.tokenChatUBean = tokenChatUBeanBaseEn.data;
                CoreApp.tokenChatUBean.tokenChatBean.token = CoreApp.appBean.userBean.token;
                put(20000, endListener);
                break;
            case 20000:
                /**
                 * 不会回复，无效断开服务器会话
                 */
                break;
            case 30000:
                /**
                 * 消息
                 */
                Type type2 = new TypeToken<BaseEn<ChatMsgBean<?>>>(){}.getType();
                BaseEn<ChatMsgBean<?>> tokenChatUBeanBaseEn2 = new Gson().fromJson(msg, type2);
                if(endListener!=null) {
                    endListener.end(tokenChatUBeanBaseEn2);
                }
                break;
        }
    }

    public interface EndListener {
        void end(Object object);
    }
}
