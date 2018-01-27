package com.example.nettychattest;

/**
 * Created by kimhj on 2018-01-27.
 */

public class Msg {

    String userId;
    String content;

    Msg(String u, String c){
        userId = u;
        content = c;
    }

    public String getUserId(){ return userId; }

    public String getContent() { return content; }
}
