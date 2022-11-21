package cn.encmed.websockettest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.encmed.websockettest.models.TupperIO;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {
    WebSocketService webSocketService;
    private RecyclerView recyclerView;
    private ListaTupperIOAdapter listaTupperIOAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declaracion del adapter
        recyclerView = findViewById(R.id.recyclerView);
        listaTupperIOAdapter = new ListaTupperIOAdapter(this);
        recyclerView.setAdapter(listaTupperIOAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        bindService(new Intent(this, WebSocketService.class), serviceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            webSocketService = ((WebSocketService.LocalBinder) service).getService();
            Log.d("websocket", webSocketService.toString());
            webSocketService.setWebSocketCallback(webSocketCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            webSocketService = null;
        }
    };


    private WebSocketService.WebSocketCallback webSocketCallback = new WebSocketService.WebSocketCallback() {

        @Override
        public void onMessage(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    TupperIO[] userArray = gson.fromJson(message, TupperIO[].class);
                    ArrayList<TupperIO> lista = new ArrayList<>(Arrays.asList(userArray));
                    listaTupperIOAdapter.adicionarListaTupperIO(lista);
                }
            });
        }

        @Override
        public void onOpen() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listaTupperIOAdapter.setWebSocket(webSocketService.getWebSocket());
                    Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorGreen)));
                }
            });
        }

        @Override
        public void onClosed() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorRed)));
                }
            });
        }

        @Override
        public void onFailure() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorRed)));
                }
            });
        }

    };
}
