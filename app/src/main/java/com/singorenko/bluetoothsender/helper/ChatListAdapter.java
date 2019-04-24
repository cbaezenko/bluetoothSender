package com.singorenko.bluetoothsender.helper;

import androidx.annotation.NonNull;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.singorenko.bluetoothsender.R;

import java.util.ArrayList;

public class ChatListAdapter extends ArrayAdapter<MessageModel> {

    private LayoutInflater mLayoutInflater;
    private int mViewResourceId;
    private ArrayList<MessageModel> mMessages;
    private Context mContext;

    public ChatListAdapter(@NonNull Context context, int tvResourceId, ArrayList<MessageModel> messages) {
        super(context, tvResourceId, messages);
        this.mContext = context;
        this.mMessages = messages;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);
        MessageModel messageModel = mMessages.get(position);

        if (messageModel != null) {
            TextView tvMessage = convertView.findViewById(R.id.tvMessage);
            TextView tvSender = convertView.findViewById(R.id.tvSender);

            if (tvSender != null && tvMessage != null) {
                tvMessage.setText(messageModel.getMessage());
                tvSender.setText(messageModel.getSender());
                if (messageModel.getSender().equals(Constants.senderUser)) {
                    tvSender.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                    tvSender.setGravity(Gravity.START);
                    tvMessage.setGravity(Gravity.START);
                }else{
                    tvSender.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    tvSender.setGravity(Gravity.END);
                    tvMessage.setGravity(Gravity.END);
                }
            }
        }
        return convertView;
    }
}
