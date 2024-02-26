package it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.heartbeat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.entity.HeartRate;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.MeasureFragment;
import it.uniba.dib.sms232417.asilapp_container.utilities.DialogDetails;

public class HeartBeatFragment extends Fragment implements SensorEventListener{


    ArrayList<Float> heartValue = new ArrayList<>(10);
    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    ObjectAnimator heartAnimation;
    ObjectAnimator hearRateAnimation;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_heart_rate_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.nav_view); // Initialize bottomNavigationView

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        // Initialize bottomNavigationView

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set home icon as back button
        Drawable homeIcon = getResources().getDrawable(R.drawable.arrow_back, null);
        // Set color filter
        homeIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(homeIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.heart_rate));
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Navigate to HomeFragment
                //bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                MeasureFragment measureFragment = new MeasureFragment(); // Create new instance of HomeFragment
                transaction.replace(R.id.nav_host_fragment_activity_main, measureFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        if (mSensorManager != null) {
            mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        ImageView heartImageView = getView().findViewById(R.id.heart_image);
        heartAnimation = ObjectAnimator.ofPropertyValuesHolder(heartImageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));

        heartAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        heartAnimation.setRepeatMode(ObjectAnimator.REVERSE);

        TextView heartRateTextView = getView().findViewById(R.id.heart_rate);
        hearRateAnimation = ObjectAnimator.ofPropertyValuesHolder(heartRateTextView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));

        hearRateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        hearRateAnimation.setRepeatMode(ObjectAnimator.REVERSE);


        MaterialButton startButton = view.findViewById(R.id.buttonMeasure);
           startButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   // Mostra la ProgressBar e la TextView
                   ProgressBar progressBar = view.findViewById(R.id.progressBar);
                   TextView loadingText = view.findViewById(R.id.loading_text);

                   progressBar.setVisibility(View.VISIBLE);
                   progressBar.setMax(100);
                   loadingText.setVisibility(View.VISIBLE);

                   RelativeLayout relativeLayout = view.findViewById(R.id.measure_loading_layout);
                   relativeLayout.setVisibility(View.VISIBLE);

                   heartAnimation.start();
                   hearRateAnimation.start();

                   // Set the maximum value of the progress bar to 100
                   // Create a CountDownTimer to fill the progress bar in 10 seconds
                   new CountDownTimer(7000, 100) { // 10000 milliseconds = 10 seconds, 100 milliseconds interval
                       public void onTick(long millisUntilFinished) {
                           int progress = (int) ((7000 - millisUntilFinished) / 100);
                           progressBar.setProgress(progress);
                           onStartSensor();
                       }

                       public void onFinish() {
                           progressBar.setProgress(100);
                           onStopSensor();
                           heartAnimation.end();
                           hearRateAnimation.end();
                           calcuateHeartRateAvarege();
                           // relativeLayout.setVisibility(View.GONE);
                           progressBar.setVisibility(View.GONE);
                           loadingText.setVisibility(View.GONE);
                       }
                   }.start();
               }
           });
    }

    private void calcuateHeartRateAvarege() {
        float sum = 0;
        for (int i = 0; i < heartValue.size(); i++) {
            sum += heartValue.get(i);
        }
        float average = sum / heartValue.size();
        int averageInt = (int) average;

        String message;
        boolean esito = false;
        message = getResources().getString(R.string.heart_rate_value_explain)+ " " + averageInt + " bpm";

        if(averageInt<60) {
            message += "\n\n" + getResources().getString(R.string.bradycardia);
            esito = true;
        }else if(averageInt>100) {
            message += "\n\n" + getResources().getString(R.string.tachycardia);
            esito = true;
        }else
            message += "\n\n" + getResources().getString(R.string.normal_heart_rate);

        message += "\n\n" + getResources().getString(R.string.question_value);
        if(isAdded()) {
            DialogDetails dialogDetails = new DialogDetails();
            dialogDetails.showCustomDialog(getResources().getString(R.string.heart_rate), message, getContext(), esito, new HeartRate(averageInt));
        }

    }

    public void onStartSensor(){
        super.onStart();
        if (mLightSensor != null) {
            mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("HeartBeatFragment", "Sensor started");
        }
    }
    public void onStopSensor() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        Log.d("HeartBeatFragment", "Sensor stopped");
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            // Otteniamo la luminosità
            float lightValue = event.values[0];
            float heartRate = calculateHeartRate(lightValue);
            TextView heartRateTextView = getView().findViewById(R.id.heart_rate);
            heartRateTextView.setText(""+heartRate);
            heartValue.add(heartRate);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private float calculateHeartRate(float lightValue) {
        final float MIN_LIGHT_VALUE = 100; // Minimum possible light value
        final float MAX_LIGHT_VALUE = SensorManager.LIGHT_SUNLIGHT_MAX; // Maximum possible light value
        final int MIN_HEART_RATE = 70; // Minimum possible heart rate
        final int MAX_HEART_RATE = 200; // Maximum possible heart rate

        float heartRate = 0;

        //Interpolazione lineare formula della retta
        heartRate = MIN_HEART_RATE + (lightValue - MIN_LIGHT_VALUE) * ((MAX_HEART_RATE - MIN_HEART_RATE) / (MAX_LIGHT_VALUE - MIN_LIGHT_VALUE));

        return (float) Math.ceil(heartRate);
    }

}
