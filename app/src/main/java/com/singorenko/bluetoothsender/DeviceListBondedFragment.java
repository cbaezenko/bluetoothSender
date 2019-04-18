package com.singorenko.bluetoothsender;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

public class DeviceListBondedFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private static final String TAG = "DeviceListBonded";

    public DeviceListBondedFragment() {
        // Required empty public constructor
    }

    public static DeviceListBondedFragment newInstance() {
        DeviceListBondedFragment fragment = new DeviceListBondedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_list_bonded, container, false);
    }


    @Override
    public void onStart(){
        super.onStart();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
        //device doesn't support bluetooth
        }else{
            //bluetooth is off, ask user to on it.
            if(!bluetoothAdapter.isEnabled()){
                Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableAdapter, 0);
            }

            Set<BluetoothDevice> all_devices = bluetoothAdapter.getBondedDevices();
            if(all_devices.size() >0){
                for (BluetoothDevice currentDevice : all_devices){
                    Log.d(TAG, "Device name "+currentDevice.getName());
                }
            }
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
