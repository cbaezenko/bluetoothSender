package com.singorenko.bluetoothsender;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SenderReceiverDataFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "SenderReceiver";

    @BindView(R.id.button_connect) Button buttonConnect;
    @BindView(R.id.button_refresh) Button buttonRefresh;
    @BindView(R.id.tv_chat_box) TextView tvChatBox;
    @BindView(R.id.editText_text_to_send) EditText etTextToSend;
    @BindView(R.id.button_send_text) Button buttonSendText;
    @BindView(R.id.button_onOff) Button buttonOnOff;

    private BluetoothAdapter mBluetoothAdapter;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private Unbinder mUnbinder;
    BluetoothConnectionServer mBluetoothConnection;

    BluetoothDevice mBTDevice;


    public SenderReceiverDataFragment() { }

    public static SenderReceiverDataFragment newInstance() {
        return new SenderReceiverDataFragment();
    }

    @Override public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sender_receiver_data, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override public void onStart(){
        super.onStart();
        buttonConnect.setOnClickListener(this);
        buttonSendText.setOnClickListener(this);
        buttonRefresh.setOnClickListener(this);
        buttonOnOff.setOnClickListener(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothConnection = new BluetoothConnectionServer(getContext());
    }

    @Override public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called.");
        mUnbinder.unbind();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver1);
        }
    }

    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_connect:{
                startConnection();
                break;
            }
            case R.id.button_refresh:{
                break;
            }
            case R.id.button_send_text:{
                byte[] bytes = etTextToSend.getText().toString().getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes);
                break;
            }
            case R.id.button_onOff:{
                Log.d(TAG, "onClick: enabling/disabling bluetooth");
                enableDisableBT();
                break;
            }
        }
    }
    //***remember the conncction will fail and app will crash if you haven't paired first
    public void startConnection(){
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }
    /**
     * starting chat service method
     */

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }

    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            if(getActivity() != null) {
                getActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
            }
        }
        if(mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            if (getActivity() != null) {
                getActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
            }
        }

    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:{
                        Log.d(TAG, "onReceive: STATE_OFF");
                        break;
                    }
                    case BluetoothAdapter.STATE_TURNING_OFF:{
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    }
                    case BluetoothAdapter.STATE_ON:{
                        Log.d(TAG, "mBroadcastReceiver1: STATE_ON");
                        break;
                    }
                    case BluetoothAdapter.STATE_TURNING_ON:{
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                    }
                }
            }
        }
    };
}
