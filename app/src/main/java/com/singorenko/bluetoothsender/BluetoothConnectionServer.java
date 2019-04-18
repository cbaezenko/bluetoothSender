package com.singorenko.bluetoothsender;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectionServer {

    private static final String TAG = "BluetoothConnectionServ";

    private static final String appName = "MYAPP";

    private static final UUID MY_UUI_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private AcceptThread mInsecureAccptThread;

    Context mContext;
    private final BluetoothAdapter mBluetoothAdapter;

    public BluetoothConnectionServer(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    /*
    * This thread runs while listening for incoming connections. It bahves
    * like a ser-side client. It runs until a connection is acccepted
    * (or until cancelled).
    * */
    private class AcceptThread extends Thread {
        //Local server socket
        private final BluetoothServerSocket mmServerSocket;


    public AcceptThread(){
        BluetoothServerSocket tmp = null;

        //Create a new listening server socket
        try {
            tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUI_INSECURE);
            Log.d(TAG, "AcceptThread: Setting up the Server using: "+ MY_UUI_INSECURE);
        }catch (IOException e){
            Log.e(TAG, "AcceptThread: IOException: "+e.getMessage());
        }
        mmServerSocket = tmp;
    }

    public void run(){
        Log.d(TAG, "run: AcceptThread Running");

        BluetoothSocket socket = null;

        try {
            //This is a blocking call and will only return on a
            //successful connection or an exception
            Log.d(TAG, "run: RFCOM server socket start.....");
            socket = mmServerSocket.accept();

            Log.d(TAG, "run: RFCOM server socket accepter connection");
        }catch (IOException e){
            Log.e(TAG, "AcceptThread: IOException: "+e.getMessage());
        }

        //talk about this on the 3 video
        //TODO
//        if(socket != null){
//            connected(socket, mmDevice);
//        }

        Log.i(TAG, "END mAcceptThread");
    }

    public void cancel(){
        Log.d(TAG, "cancel: Canceling AcceptThread.");
        try{
            mmServerSocket.close();
        }catch (IOException e){
            Log.e(TAG, "cancel: Close AcceptThread ServerSocket failed. "+e.getMessage());
        }
    }

    }
}
