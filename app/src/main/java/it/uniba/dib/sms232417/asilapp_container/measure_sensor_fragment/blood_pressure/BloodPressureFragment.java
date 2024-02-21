package it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.blood_pressure;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.Random;

import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.entity.BloodPressure;
import it.uniba.dib.sms232417.asilapp_container.utilities.DialogDetails;

public class BloodPressureFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_bloodpressure_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton btnMeasure = view.findViewById(R.id.buttonMeasure);
        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = requireView().findViewById(R.id.bloodPressure_image);
                Glide.with(getContext()).load(R.drawable.blood_pressure).into(imageView);
                RelativeLayout relativeLayout = requireView().findViewById(R.id.measure_loading_layout);
                relativeLayout.setVisibility(View.VISIBLE);

                ProgressBar progressBar = requireView().findViewById(R.id.progressBar);
                progressBar.setMax(100);
                new CountDownTimer(7000, 100) { // 10000 milliseconds = 10 seconds, 100 milliseconds interval
                    public void onTick(long millisUntilFinished) {
                        int progress = (int) ((7000 - millisUntilFinished) / 100);
                        progressBar.setProgress(progress);

                    }

                    public void onFinish() {
                        progressBar.setProgress(100);
                        relativeLayout.setVisibility(View.GONE);
                        calculateBloodPressure();
                    }
                }.start();
            }

        });
    }

    private void calculateBloodPressure() {
        Random random = new Random();
        int lowLimit;
        int highLimit;
        int systolic;
        int diastolic;
        do {
            lowLimit = 90;
            highLimit = 160;
            systolic = lowLimit + random.nextInt(highLimit - lowLimit + 1);

            lowLimit = 60;
            highLimit = 100;
            diastolic = lowLimit + random.nextInt(highLimit - lowLimit + 1);
        }while(systolic < diastolic);
        //La tua pressione è di sistolica/diastolica
        boolean esito = false;
        String message = getResources().getString(R.string.blood_pressure_explain) +" " + systolic + " / " + diastolic + " mmHg";

        if(systolic < 90 && diastolic < 60){
            esito = true;
            message += "\n\n" + getResources().getString(R.string.low_blood_pressure);
        }else if(systolic > 90 && systolic <=120 && diastolic > 60 && diastolic <= 80){
            message += "\n\n" + getResources().getString(R.string.normal_blood_pressure);
        }else if(systolic > 120 && systolic <= 140 && diastolic > 80 && diastolic <= 90){
            esito = true;
            message += "\n\n" + getResources().getString(R.string.high_blood_pressure_slight);
        }else if(systolic > 140 && systolic <= 160 && diastolic > 90 && diastolic <= 100){
            esito = true;
            message += "\n\n" + getResources().getString(R.string.high_blood_pressure_moderate);
        }else if(systolic > 160 && diastolic > 100){
            esito = true;
            message += "\n\n" + getResources().getString(R.string.high_blood_pressure_grave);
        }
        message += "\n\n" + getResources().getString(R.string.question_value);
        DialogDetails dialogDetails = new DialogDetails();
        dialogDetails.showCustomDialog(getResources().getString(R.string.blood_pressure), message, getContext(), esito, new BloodPressure(systolic, diastolic));
    }
}
