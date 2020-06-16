package com.deep.netdeep.socket;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.deep.dpwork.util.Lag;
import com.deep.netdeep.net.bean.TokenBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Class - WebSocket
 * <p>
 * Created by Deepblue on 2019/8/2 0002.
 */

public class WebSocketUtil {

    private static WebSocketUtil webSocketUtil;

    public static WebSocketUtil get() {
        if (webSocketUtil == null) {
            webSocketUtil = new WebSocketUtil();
        }
        return webSocketUtil;
    }

    private final int NORMAL_CLOSURE_STATUS = 1000;

    private OkHttpClient sClient;
    private WebSocket sWebSocket;

    private boolean isConnected = false;

    public synchronized void connect() {
        if (sClient == null) {
            sClient = new OkHttpClient();
        }
        if (sWebSocket == null) {
            Request request = new Request.Builder()
                    .url("ws://192.168.0.112:8087")
                    .build();

            EchoWebSocketListener listener = new EchoWebSocketListener();
            sWebSocket = sClient.newWebSocket(request, listener);
        }
    }

    public synchronized void connect(TokenBean tokenBean) {
        if (sClient == null) {
            sClient = new OkHttpClient();
        }
        if (sWebSocket == null) {
            Request request = new Request.Builder()
                    .url("ws://192.168.0.112:8080")
                    .header("token", tokenBean.token)
                    .build();

            EchoWebSocketListener listener = new EchoWebSocketListener();
            sWebSocket = sClient.newWebSocket(request, listener);
        }
    }

    public synchronized boolean isConnected() {
        return isConnected;
    }

    public synchronized void send(String str) {
        sWebSocket.send(str);
    }

    private List<WsListener> wsListeners = new ArrayList<>();

    public void addListener(WsListener wsListener) {
        if (!wsListeners.contains(wsListener)) {
            wsListeners.add(wsListener);
        }
    }

    public void removeListener(WsListener wsListener) {
        wsListeners.remove(wsListener);
    }

    private final static int CONNECT = 100;
    private final static int DISCONNECT = 101;
    private final static int MESSAGE = 200;
    private final static int FAILE = 404;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECT:
                    for (int i = 0; i < wsListeners.size(); i++) {
                        wsListeners.get(i).connected();
                    }
                    break;
                case DISCONNECT:
                    for (int i = 0; i < wsListeners.size(); i++) {
                        wsListeners.get(i).disconnected();
                    }
                    break;
                case MESSAGE:
                    for (int i = 0; i < wsListeners.size(); i++) {
                        wsListeners.get(i).msg(msg.obj.toString());
                    }
                    break;
                case FAILE:
                    for (int i = 0; i < wsListeners.size(); i++) {
                        wsListeners.get(i).failed();
                    }
                    break;
            }
        }
    };

    private class EchoWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            Lag.i("连接成功");
            Message message = new Message();
            message.what = CONNECT;
            mHandler.sendMessage(message);
            isConnected = true;
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull final String text) {
            Message message = new Message();
            message.what = MESSAGE;
            message.obj = text;
            mHandler.sendMessage(message);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull final ByteString bytes) {
            Message message = new Message();
            message.what = MESSAGE;
            message.obj = bytes.toString();
            mHandler.sendMessage(message);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            Lag.i("连接断开中");
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            Lag.i("连接断开");
            Message message = new Message();
            message.what = DISCONNECT;
            mHandler.sendMessage(message);
            closeWebSocket();

            isConnected = false;
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
            Lag.i("连接失败");
            Message message = new Message();
            message.what = FAILE;
            mHandler.sendMessage(message);
            closeWebSocket();

            isConnected = false;
        }
    }

    public synchronized void closeWebSocket() {
        if (sWebSocket != null) {
            sWebSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
            sWebSocket = null;
        }
    }

    public synchronized void destroy() {
        if (sClient != null) {
            sClient.dispatcher().executorService().shutdown();
            sClient = null;
        }
    }
}
