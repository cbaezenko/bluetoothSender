package com.singorenko.bluetoothsender.helper;

import androidx.annotation.NonNull;
import android.content.Context;
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

public ChatListAdapter(@NonNull Context context, int tvResourceId, ArrayList<MessageModel> messages) {
        super(context, tvResourceId, messages);
        this.mMessages = messages;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId =tvResourceId;
        }

public View getView(int position, View convertView, ViewGroup parent){
        convertView = mLayoutInflater.inflate(mViewResourceId, null);
        MessageModel messageModel = mMessages.get(position);

        if(messageModel != null){
        TextView tvMessage = convertView.findViewById(R.id.tvMessage);
        TextView tvSender = convertView.findViewById(R.id.tvSender);

        if(tvMessage != null){
        tvMessage.setText(messageModel.getMessage());
        }
        if(tvSender != null){
        tvSender.setText(messageModel.getSender());
        }
        }
        return convertView;
        }
        }
