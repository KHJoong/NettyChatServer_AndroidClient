package com.example.nettychattest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kimhj on 2018-01-27.
 */

public class MsgAdapter extends BaseAdapter {

    Context context;

    ArrayList<Msg> msgs;

    TextView tvId;
    TextView tvContent;

    MsgAdapter(Context ctx){
        context = ctx;
        msgs = new ArrayList<Msg>();
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.text_layout, null);
        String userId = msgs.get(position).getUserId();

        tvId = (TextView)view.findViewById(R.id.userId);
        tvContent = (TextView)view.findViewById(R.id.msg);

        tvId.setText(msgs.get(position).getUserId());
        tvContent.setText(msgs.get(position).getContent());

        return view;
    }

    public void addItem(Msg m){ msgs.add(m);}
}
