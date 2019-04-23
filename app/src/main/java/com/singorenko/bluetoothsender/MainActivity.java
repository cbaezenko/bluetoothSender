package com.singorenko.bluetoothsender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.singorenko.bluetoothsender.helper.BackgroundDetectedActivitiesService;
import com.singorenko.bluetoothsender.helper.Constants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener,
        SensorEventListener {

    private String TAG = getClass().getSimpleName();
    BroadcastReceiver mBroadcastReceiver;
    Sensor mSensor;
    SensorManager mSensorManager;
    private boolean isRunning = false;

    @BindView(R.id.tv_recognized_status_activity) TextView tvRecognizedStatusActivity;
    @BindView(R.id.tv_confidence_status_activity) TextView tvConfidenceStatusActivity;
    @BindView(R.id.tv_steps) TextView tvSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, HomeFragment.newInstance())
                    .commit();
        }
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if(mSensor!= null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)){
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };
    }

    private void handleUserActivity(int type, int confidence){
        String label = "Unknown";
        switch (type){
            case DetectedActivity.IN_VEHICLE:{ label = "en vehiculo";
                break;
            }
            case DetectedActivity.ON_BICYCLE:{ label = "en bicicleta";
                break;
            }
            case DetectedActivity.ON_FOOT:{ label = "a pie";
                break;
            }
            case DetectedActivity.RUNNING:{ label = "corriendo";
                break;
            }
            case DetectedActivity.STILL:{ label = "parado";
                break;
            }
            case DetectedActivity.TILTING:{ label = "girando";
                break;
            }
            case DetectedActivity.WALKING:{ label = "caminando";
                break;
            }
            case DetectedActivity.UNKNOWN:{ label = "desconocido";
                break;
            }
        }

        tvRecognizedStatusActivity.setText(label);
        tvConfidenceStatusActivity.setText(Integer.toString(confidence));
    }

    @Override
    protected void onResume(){
        super.onResume();
        isRunning = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        isRunning = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        mSensorManager.unregisterListener(this);
    }

    private void startTracking(){
        Intent intent = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent);
    }

    private void stopTracking(){
        Intent intent = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent);
    }

    @Override
    public void onShowList() {
     getSupportFragmentManager().beginTransaction()
             .replace(R.id.fragment_container, DeviceListFragment.newInstance())
             .commit();
    }

    @Override
    public void onShowListBind() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, DeviceListBondedFragment.newInstance())
                .commit();
    }

    @Override
    public void onSendReceiveData() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, SenderReceiverDataFragment.newInstance())
                .commit();
    }

    @Override
    public void onStartTracking() {
        Log.d(TAG, "On Start Tracking Recognizes Activities");
        startTracking();
    }

    @Override
    public void onStopTracking() {
        Log.d(TAG, "On Stop Tracking Recognizes Activities");
        stopTracking();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
      if(isRunning) {
          tvSteps.setText("" + event.values[0]); }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
