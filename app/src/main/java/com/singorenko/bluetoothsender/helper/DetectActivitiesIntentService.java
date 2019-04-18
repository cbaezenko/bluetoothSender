package com.singorenko.bluetoothsender.helper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DetectActivitiesIntentService extends IntentService {

    protected static final String TAG = DetectActivitiesIntentService.class.getSimpleName();

    public DetectActivitiesIntentService() {
        //User the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        //Get the list of the probable activities associated with the current state of the
        //device. Each activity is associated with a confidence level, which is an int between
        //0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        for(DetectedActivity activity : detectedActivities){
            Log.d(TAG, "Detected activity: "+ activity.getType() + ", "+activity.getConfidence());
            broadcastActivity(activity);
        }
    }

    private void broadcastActivity(DetectedActivity activity){
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
