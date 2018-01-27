package com.example.nettychattest;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    String ip = "115.71.232.230"; // IP
    int port = 8888; // PORT번호
    SocketChannel socketChannel;
    Handler handler;

    EditText etUserId;
    Button btnEnroll;
    Button btnFirstRoom;
    Button btnSecondRoom;
    TextView tvRoomName;
    ListView lvMsg;
    EditText etTypeMsg;
    Button btnSendMsg;

    MsgAdapter adapter;

    JSONObject sobj;
    String sdata;
    String currentRoom;

    MsgReceiver msgReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sobj = new JSONObject();

        msgReceiver = new MsgReceiver(getApplication());
        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress(ip, port));
                }catch (Exception e){
                    e.printStackTrace();
                }
                msgReceiver.start();
            }
        }).start();


        etUserId = (EditText)findViewById(R.id.etUserId);
        btnEnroll = (Button)findViewById(R.id.btnEnroll);
        btnFirstRoom = (Button)findViewById(R.id.btnFirstRoom);
        btnSecondRoom = (Button)findViewById(R.id.btnSecondRoom);
        tvRoomName = (TextView)findViewById(R.id.tvRoomName);
        lvMsg = (ListView)findViewById(R.id.lvMsg);
        etTypeMsg = (EditText)findViewById(R.id.etTypeText);
        btnSendMsg = (Button)findViewById(R.id.btnSendMsg);

        adapter = new MsgAdapter(getApplication());
        lvMsg.setAdapter(adapter);

        btnEnroll.setOnClickListener(btnClickListener);
        btnFirstRoom.setOnClickListener(btnClickListener);
        btnSecondRoom.setOnClickListener(btnClickListener);
        btnSendMsg.setOnClickListener(btnClickListener);
    }

    Button.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnEnroll :
                    try {
                        sobj.put("userId", etUserId.getText().toString());
                        sobj.put("type", "join");

                        sdata = sobj.toString();
                        socketChannel.socket().getOutputStream().write(sdata.getBytes("EUC-KR"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.btnFirstRoom :
                    tvRoomName.setText("1번 방");

                    currentRoom = "first";
                    sobj = new JSONObject();
                    try {
                        sobj.put("userId", etUserId.getText().toString());
                        sobj.put("type", "enter_room");
                        sobj.put("roomId", "first");
                        sdata = sobj.toString();
                        socketChannel.socket().getOutputStream().write(sdata.getBytes("EUC-KR"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btnSecondRoom :
                    tvRoomName.setText("2번 방");

                    currentRoom = "second";
                    sobj = new JSONObject();
                    try {
                        sobj.put("userId", etUserId.getText().toString());
                        sobj.put("type", "enter_room");
                        sobj.put("roomId", "second");
                        sdata = sobj.toString();
                        socketChannel.socket().getOutputStream().write(sdata.getBytes("EUC-KR"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btnSendMsg :
                    sobj = new JSONObject();
                    try {
                        sobj.put("userId", etUserId.getText().toString());
                        sobj.put("type", "send_room");
                        sobj.put("currentRoom", currentRoom);
                        sobj.put("content", etTypeMsg.getText().toString());
                        sdata = sobj.toString();
                        socketChannel.socket().getOutputStream().write(sdata.getBytes("EUC-KR"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Msg msg = new Msg(etUserId.getText().toString(), etTypeMsg.getText().toString());
                            adapter.addItem(msg);
                            lvMsg.setAdapter(adapter);
                        }
                    });

                    etTypeMsg.setText(null);
                    break;
            }
        }
    };

    class MsgReceiver extends Thread{
        Context context;
        Handler handler = new Handler();

        String userid;
        String content;

        public MsgReceiver(Context ctx){ context = ctx; }

        public void run(){
            while(true){
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                try {
                    int readByteCount = socketChannel.read(byteBuffer);
                    Log.d("MsgRec:readByteCount:", readByteCount+"");

                    if(readByteCount==-1){
                        throw new IOException();
                    }
                    byteBuffer.flip();
                    Charset charset = Charset.forName("EUC-KR");
                    JSONObject ob = new JSONObject(charset.decode(byteBuffer).toString());
                    userid = ob.getString("userId");
                    content = ob.getString("content");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Msg msg = new Msg(userid, content);
                            adapter.addItem(msg);
                            lvMsg.setAdapter(adapter);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}
