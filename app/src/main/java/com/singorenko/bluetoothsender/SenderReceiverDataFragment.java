package com.singorenko.bluetoothsender;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.singorenko.bluetoothsender.helper.ChatListAdapter;
import com.singorenko.bluetoothsender.helper.Constants;
import com.singorenko.bluetoothsender.helper.DeviceListAdapter;
import com.singorenko.bluetoothsender.helper.IncomingMessageEvent;
import com.singorenko.bluetoothsender.helper.MessageModel;
import com.singorenko.bluetoothsender.helper.OutgoingMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SenderReceiverDataFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final String TAG = "SenderReceiver";

    @BindView(R.id.button_connect) ImageButton buttonConnect;
    @BindView(R.id.editText_text_to_send) EditText etTextToSend;
    @BindView(R.id.button_send_text) ImageButton buttonSendText;

    @BindView(R.id.button_onOff) ImageButton buttonOnOff;
    @BindView(R.id.button_enable_discoverable) ImageButton buttonEnableDiscoverable;
    @BindView(R.id.button_discover) ImageButton buttonDiscover;

    @BindView(R.id.lvNewDevices) ListView lvNewDevices;
    @BindView(R.id.lv_chat_box) ListView lvChatBox;

    private BluetoothAdapter mBluetoothAdapter;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public ArrayList<MessageModel> mMessageModels = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;

    private ChatListAdapter mChatListAdapter;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private Unbinder mUnbinder;
    BluetoothConnectionService mBluetoothConnection;
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

        //Broadcast when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        if(getActivity() != null) {
            getActivity().registerReceiver(mBroadcastReceiver4, filter);
        }

        buttonConnect.setOnClickListener(this);
        buttonSendText.setOnClickListener(this);

        buttonOnOff.setOnClickListener(this);
        buttonEnableDiscoverable.setOnClickListener(this);
        buttonDiscover.setOnClickListener(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothConnection = new BluetoothConnectionService(getContext());

        mBTDevices = new ArrayList<>();

        lvNewDevices.setOnItemClickListener(this);

        EventBus.getDefault().register(this);

        if(getContext()!= null) {
            mChatListAdapter = new ChatListAdapter(getContext(), R.layout.message_adapter_view, mMessageModels);
            lvChatBox.setAdapter(mChatListAdapter);
        }
        }

    @Override
    public void onResume(){
        super.onResume();
        buttonOnOff.setAlpha(1f);
        buttonOnOff.setEnabled(true);
        if(mBluetoothAdapter.isEnabled()){
            buttonOnOff.setBackground(getResources().getDrawable(R.drawable.ic_bluetooth_connected_black_24dp));
        }else{
            buttonOnOff.setBackground(getResources().getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called.");
        mUnbinder.unbind();
        if (getActivity() != null) {
            try {
                getActivity().unregisterReceiver(mBroadcastReceiver1);
                getActivity().unregisterReceiver(mBroadcastReceiver2);
                getActivity().unregisterReceiver(mBroadcastReceiver3);
                getActivity().unregisterReceiver(mBroadcastReceiver4);
            }catch (RuntimeException e){
             e.getStackTrace();
            }
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_connect:{
                startConnection();
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
            case R.id.button_enable_discoverable:{
                Log.d(TAG, "onClick: enable discoverable");
                    enableDisableDiscoverable();
            }
            case R.id.button_discover:{
                discover();
                break;
            }
        }
    }
    //***remember the connection will fail and app will crash if you haven't paired first
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

    public void enableDisableDiscoverable() {
        Log.d(TAG, "enableDisableDiscoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        if (getActivity() != null) {
            getActivity().registerReceiver(mBroadcastReceiver2, intentFilter);
        }
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

    public void discover(){
        Log.d(TAG, "onClick Discover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "buttonDiscover: Canceling discovery.");

            //check BT permissions in the manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);

            if (getActivity() != null) {
                getActivity().registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }
        } if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();

            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);

            if (getActivity() != null) {
                getActivity().registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     *
     */
    private void checkBTPermissions(){
        if(getActivity() != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
                if(permissionCheck != 0){
                    getActivity().requestPermissions(
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                            1001); //Any number
                }else{
                    Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP");
                }
            }
        }
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:{
                        Log.d(TAG, "onReceive: STATE_OFF");
                        buttonOnOff.setEnabled(true);
                        buttonOnOff.setAlpha(1f);
                        buttonOnOff.setBackground(getResources().getDrawable(R.drawable.ic_bluetooth_disabled_black_24dp));
                        break;
                    }
                    case BluetoothAdapter.STATE_TURNING_OFF:{
                        buttonOnOff.setEnabled(false);
                        buttonOnOff.setAlpha(0.5f);
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    }
                    case BluetoothAdapter.STATE_ON:{
                        buttonOnOff.setEnabled(true);
                        buttonOnOff.setAlpha(1f);
                        Log.d(TAG, "mBroadcastReceiver1: STATE_ON");
                        buttonOnOff.setBackground(getResources().getDrawable(R.drawable.ic_bluetooth_connected_black_24dp));
                        break;
                    }
                    case BluetoothAdapter.STATE_TURNING_ON:{
                        buttonOnOff.setEnabled(false);
                        buttonOnOff.setAlpha(0.5f);
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                    }
                }
            }
        }
    };

    /**
     *
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, mBluetoothAdapter.ERROR);

                switch (state){
                    //Deice is in Discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:{
                        buttonEnableDiscoverable.setEnabled(false);
                        buttonEnableDiscoverable.setAlpha(0.5f);
                        buttonEnableDiscoverable.setBackground(getResources().getDrawable(R.drawable.ic_visibility_black_24dp));
                        Log.d(TAG, "mBroadcastReceiver1: Discoverability Enabled.");
                        break;
                    }
                    //Device is in Discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:{
                        buttonEnableDiscoverable.setBackground(getResources().getDrawable(R.drawable.ic_visibility_off_black_24dp));
                        buttonEnableDiscoverable.setEnabled(true);
                        buttonEnableDiscoverable.setAlpha(1f);
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    }
                    case BluetoothAdapter.SCAN_MODE_NONE:{
                        buttonEnableDiscoverable.setEnabled(true);
                        buttonEnableDiscoverable.setAlpha(1f);
                        buttonEnableDiscoverable.setBackground(getResources().getDrawable(R.drawable.ic_visibility_off_black_24dp));
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    }
                    case BluetoothAdapter.STATE_CONNECTING:{
                        buttonEnableDiscoverable.setEnabled(false);
                        buttonEnableDiscoverable.setAlpha(0.5f);
                        Log.d(TAG, "mBroadcastReceiver2: Connecting...");
                        break;
                         }
                         case BluetoothAdapter.STATE_DISCONNECTING:{
                             buttonEnableDiscoverable.setEnabled(false);
                             buttonEnableDiscoverable.setAlpha(0.5f);
                             Log.d(TAG, "mBroadcastReceiver2: Disconnecting...");
                             break;
                         }
                    case BluetoothAdapter.STATE_CONNECTED:{
                        buttonEnableDiscoverable.setEnabled(false);
                        buttonEnableDiscoverable.setAlpha(0.5f);
                        buttonEnableDiscoverable.setBackground(getResources().getDrawable(R.drawable.ic_visibility_black_24dp));
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                    }
                }
            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by buttonDiscover() method.
     */

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND");

            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: "+device.getName() +": "+device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //3 cases
                //case1 : bonded already
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiving: BOND_BONDED");
                    mBTDevice = mDevice;
                }
                //case2: creating a bone
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BroadcastReceiving: BOND_BONDING");
                }
                //case3: breaking a bond
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BroadcastReceiving: BOND_NONE");
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //first cancel discovery because its very memory intensive
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();

        Log.d(TAG, "onItemClick: deviceName = "+deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = "+deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with "+deviceName);
            mBTDevices.get(position).createBond();

            mBTDevice = mBTDevices.get(position);
            mBluetoothConnection = new BluetoothConnectionService(getContext());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIncomingMessageEvent(IncomingMessageEvent event){
        if(getContext() != null) {
            mMessageModels.add(new MessageModel(event.getText(), Constants.senderUser));
            mChatListAdapter = new ChatListAdapter(getContext(), R.layout.message_adapter_view, mMessageModels);
            lvChatBox.invalidateViews();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void outgoingMessageEvent(OutgoingMessageEvent event){
        if(getContext() != null) {
            mMessageModels.add(new MessageModel(event.getOutgoingMessage(), Constants.receiverUser));
            mChatListAdapter = new ChatListAdapter(getContext(), R.layout.message_adapter_view, mMessageModels);
            lvChatBox.invalidateViews();
        }
    }
}
