package it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.temperature;

import android.content.Context;
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
import it.uniba.dib.sms232417.asilapp_container.entity.Temperature;
import it.uniba.dib.sms232417.asilapp_container.utilities.DialogDetails;

public class TemperatureFragment extends Fragment{
    Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_temperature_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        MaterialButton btnTemperature = view.findViewById(R.id.buttonMeasure);
        btnTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = requireView().findViewById(R.id.thermo_image);
                Glide.with(getContext()).load(R.drawable.termomether).into(imageView);
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
                        calculateTemperature();
                    }
                }.start();
            }

        });
    }

    private void calculateTemperature() {
        Random random = new Random();
        double lowLimit = 34.0;
        double highLimit = 42.0;
        double randomTemperature = lowLimit + (highLimit - lowLimit) * random.nextDouble();
        // Utilizza il valore randomTemperature come desideri
        randomTemperature = Math.round(randomTemperature * 10.0) / 10.0;
        String title = getResources().getString(R.string.temperature);
        String message = getResources().getString(R.string.temperature_value_explain) + " " + randomTemperature + " °C";
        boolean esito = false;
        if(randomTemperature <= 35) {
            message += "\n\n" + getResources().getString(R.string.low_temperature);
            esito = true;
        } else if(randomTemperature > 37.5) {
            message += "\n\n" +getResources().getString(R.string.high_temperature);
            esito = true;
        } else {
            message += "\n\n" +getResources().getString(R.string.normal_temperature);
        }
        message += "\n\n" + getResources().getString(R.string.question_value);
        DialogDetails dialogDetails = new DialogDetails();
        dialogDetails.showCustomDialog(title, message,context,esito, new Temperature(randomTemperature));
    }
}
