package com.singorenko.bluetoothsender;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class HomeFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.button_send_data) Button buttonSendData;
    @BindView(R.id.button_show_list) Button buttonShowList;
    @BindView(R.id.button_show_list_bind) Button buttonShowListBind;

    @BindView(R.id.button_start_tracking) Button buttonStartTracking;
    @BindView(R.id.button_stop_tracking) Button buttonStopTracking;

    @BindView(R.id.tv_activity_recognition_status)
    TextView tvActivityRecognitionStatus;

    private Unbinder mUnbinder;
    private OnHomeFragmentInteractionListener mHomeFragmentListener;

    public HomeFragment() { }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false); }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        mUnbinder = ButterKnife.bind(this, view);

        buttonShowList.setOnClickListener(this);
        buttonShowListBind.setOnClickListener(this);

        buttonSendData.setOnClickListener(this);

        buttonStartTracking.setOnClickListener(this);
        buttonStopTracking.setOnClickListener(this);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mHomeFragmentListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHomeFragmentListener = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_show_list:{
                mHomeFragmentListener.onShowList();
                break;
            }
            case R.id.button_show_list_bind:{
                mHomeFragmentListener.onShowListBind();
                break;
            }
            case R.id.button_send_data:{
                mHomeFragmentListener.onSendData();
                break;
            }
            case R.id.button_start_tracking:{
                mHomeFragmentListener.onStartTracking();
                break;
            }
            case R.id.button_stop_tracking:{
                mHomeFragmentListener.onStopTracking();
                break;
            }
        }
    }

   public interface OnHomeFragmentInteractionListener {
        void onShowList();
        void onShowListBind();
        void onSendData();

        void onStartTracking();
        void onStopTracking();
    }
}
