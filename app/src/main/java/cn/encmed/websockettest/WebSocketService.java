package cn.encmed.websockettest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by fanglin on 2020/4/23.
 */
public class WebSocketService extends Service {
    private static final String TAG = "websocket";
    private static final String WS = "ws://192.168.1.10/ws";

    public WebSocket getWebSocket() {
        return webSocket;
    }

    private WebSocket webSocket;
    private WebSocketCallback webSocketCallback;
    private int reconnectTimeout = 5000;
    private boolean connected = false;

    private Handler handler = new Handler();

    class LocalBinder extends Binder {
        WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                webSocket = connect();
            }
        }, 500);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            close();
        }
    }

    private WebSocket connect() {
        Log.d(TAG, "connect " + WS);
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(WS).build();
        return client.newWebSocket(request, new WebSocketHandler());
    }

    public void send(String text) {
        Log.d(TAG, "send " + text);
        if (webSocket != null) {
            webSocket.send(text);
        }
    }

    public void close() {
        if (webSocket != null) {
            boolean shutDownFlag = webSocket.close(1000, "manual close");
            Log.d(TAG, "shutDownFlag " + shutDownFlag);
            webSocket = null;
        }
    }

    private void reconnect() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "reconnect...");
                if (!connected) {
                    webSocket = connect();
                }
            }
        }, reconnectTimeout);
    }

    private class WebSocketHandler extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(TAG, "onOpen");
            if (webSocketCallback != null) {
                webSocketCallback.onOpen();
            }
            connected = true;
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d(TAG, "onMessage " + text);
            if (webSocketCallback != null) {
                webSocketCallback.onMessage(text);
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d(TAG, "onClosed");
            if (webSocketCallback != null) {
                webSocketCallback.onClosed();
            }
            connected = false;
            reconnect();
        }

        /**
         * Invoked when a web socket has been closed due to an error reading from or writing to the
         * network. Both outgoing and incoming messages may have been lost. No further calls to this
         * listener will be made.
         */
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.d(TAG, "onFailure " + t.getMessage());
            if (webSocketCallback != null) {
                webSocketCallback.onFailure();
            }
            connected = false;
            reconnect();
        }
    }


    /**
     * ????????????????????????????????????onFailure ?????????????????????????????????????????????????????????
     */

    public interface WebSocketCallback {
        void onMessage(String text);

        void onOpen();

        void onClosed();

        void onFailure();
    }

    public void setWebSocketCallback(WebSocketCallback webSocketCallback) {
        this.webSocketCallback = webSocketCallback;
    }
}
