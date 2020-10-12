package com.example.fihradar;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    public static Handler mHandler = new Handler(Looper.getMainLooper());
    //連線端變數定義
    private Thread socket;
    private Socket clientSocket;
    private BufferedWriter bw;
    private BufferedReader br;
    private String tmp;
    //UI define
    private TextView constate,Test;
    private Button start,stop;

    private Runnable Client = new Runnable() {
        @Override
        public void run() {
            InetAddress serverIp;
            try {
                serverIp = InetAddress.getByName("192.168.10.150");//wifi AP
                int serverPort = 5050;
                clientSocket = new Socket(serverIp, serverPort);

                //Get Inputstream
//                DataInputStream br = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

                if (clientSocket.isConnected()) {
                    final String connect = "connect!";
                    Runnable updateState = new Runnable() {
                        @Override
                        public void run() {
                            constate.setText(connect);
                        }
                    };
                    mHandler.post(updateState);
                }

                //send msg
                String msg="Hello server!";
                DataOutputStream bw = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                for(int i=0;i<5;i++) {
                    SystemClock.sleep(1000);
                    Log.d(MainActivity.class.getSimpleName(),msg+i);
                    bw.writeBytes(msg + i+'\n');
                    bw.flush();
                }
            } catch (IOException e) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constate = (TextView) findViewById(R.id.constate);
//        Test = (TextView) findViewById(R.id.test);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket = new Thread(Client);
                socket.start();
                Log.d(MainActivity.class.getSimpleName(),"socket start!");
                Intent startIntent = new Intent(MainActivity.this,mmWaveService.class);
                startService(startIntent);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent stopIntent = new Intent(MainActivity.this,mmWaveService.class);
//                stopService(stopIntent);
                socket.stop();
            }
        });
    }
}