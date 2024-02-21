package it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.glycemia;

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
import it.uniba.dib.sms232417.asilapp_container.entity.Glycemia;
import it.uniba.dib.sms232417.asilapp_container.utilities.DialogDetails;

public class GlycemiaFragment extends Fragment {
    Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_glycemia_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton btnMeasure = view.findViewById(R.id.buttonMeasure);
        context = getContext();
        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ImageView imageView = requireView().findViewById(R.id.glycemia_image);
                    Glide.with(getContext()).load(R.drawable.glycemia).into(imageView);
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
                            calculateGlycemia();

                        }
                    }.start();
            }

        });
    }

    private void calculateGlycemia() {
        Random random = new Random();
        double lowGlycemia = 50.0;
        double highGlycemia = 200.0;
        double glycemia = lowGlycemia + (highGlycemia - lowGlycemia) * random.nextDouble();
        glycemia = Math.round(glycemia * 10) / 10.0;
        boolean esito = false;


        String message  = getResources().getString(R.string.glycemia_value_explain) + " " + glycemia + " mg/dL";
        if(glycemia < 70.0){
            message += "\n" + getResources().getString(R.string.low_glycemia);
            esito = true;
        }else if(glycemia >125 && glycemia <= 200){
            message += "\n" + getResources().getString(R.string.high_glycemia);
            esito = true;
        }else if(glycemia >= 70.0 && glycemia <100) {
            message += "\n" + getResources().getString(R.string.normal_glycemia);
        }else if(glycemia >= 100 && glycemia <= 125) {
            message += "\n" + getResources().getString(R.string.pre_diabetes);
            esito = true;
        }
        message += "\n\n" + getResources().getString(R.string.question_value);
        DialogDetails dialogDetails = new DialogDetails();
        dialogDetails.showCustomDialog(getResources().getString(R.string.glycemia_title), message,context , esito, new Glycemia(glycemia));
    }
}
