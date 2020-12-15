package com.example.miroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class reservationAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<reservationData> sample;

    public reservationAdapter(Context context, ArrayList<reservationData> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public reservationData getItem(int position) {
        return sample.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item2, null);

        TextView reserve_id = (TextView)view.findViewById(R.id.reserv_num);
        TextView date = (TextView)view.findViewById(R.id.date);
        TextView time = (TextView)view.findViewById(R.id.time);
        TextView layer = (TextView)view.findViewById(R.id.layer);
        TextView room_name = (TextView)view.findViewById(R.id.room_name);

        reserve_id.setText(sample.get(position).getReserve_id());
        date.setText(sample.get(position).getDate());
        time.setText(sample.get(position).getTime());
        layer.setText(sample.get(position).getLayer());
        room_name.setText(sample.get(position).getRoom_name());

        return view;
    }
}
