package it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.blood_pressure;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.Random;

import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.entity.BloodPressure;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.MeasureFragment;
import it.uniba.dib.sms232417.asilapp_container.utilities.DialogDetails;

public class BloodPressureFragment extends Fragment {

    private ImageView imageView;

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
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.blood_pressure));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        imageView = view.findViewById(R.id.bloodPressure_image);

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Navigate to HomeFragment
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                MeasureFragment measureFragment = new MeasureFragment(); // Create new instance of HomeFragment
                transaction.replace(R.id.nav_host_fragment_activity_main, measureFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Mostra la ProgressBar e la TextView
                ProgressBar progressBar = view.findViewById(R.id.progressBar);
                TextView loadingText = view.findViewById(R.id.loading_text);


                Glide.with(getContext())
                        .load(R.drawable.blood_pressure)
                        .into((ImageView) requireView().findViewById(R.id.bloodPressure_image));
                RelativeLayout relativeLayout = requireView().findViewById(R.id.measure_loading_layout);
                relativeLayout.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(100);
                loadingText.setVisibility(View.VISIBLE);

                new CountDownTimer(7000, 100) { // 10000 milliseconds = 10 seconds, 100 milliseconds interval
                    public void onTick(long millisUntilFinished) {
                        int progress = (int) ((7000 - millisUntilFinished) / 100);
                        progressBar.setProgress(progress);
                    }

                    public void onFinish() {
                        progressBar.setProgress(100);
                        calculateBloodPressure();

                        progressBar.setVisibility(View.GONE);
                        loadingText.setVisibility(View.GONE);

                        // Cambia l'immagine con la nuova risorsa
                        imageView.setImageResource(R.drawable.statico_blood_pressure);
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
            lowLimit = 70;
            highLimit = 160;
            systolic = lowLimit + random.nextInt(highLimit - lowLimit + 1);

            lowLimit = 60;
            highLimit = 130;
            diastolic = lowLimit + random.nextInt(highLimit - lowLimit + 1);
        } while (systolic < diastolic);
        //La tua pressione è di sistolica/diastolica
        boolean esito = false;
        String message = getResources().getString(R.string.blood_pressure_value_explain) + " " + systolic + " / " + diastolic + " mmHg";

        if (systolic < 90 && diastolic < 60) {
            esito = true;
            message += "\n\n" + getResources().getString(R.string.low_blood_pressure);
        } else if (systolic > 90 && systolic <= 120 && diastolic > 60 && diastolic <= 80) {
            message += "\n\n" + getResources().getString(R.string.normal_blood_pressure);
        } else if (systolic > 120 && systolic <= 140 && diastolic > 80 && diastolic <= 90) {
            esito = true;
            message += "\n\n" + getResources().getString(R.string.high_blood_pressure_slight);
        } else if (systolic > 140 && systolic <= 160 && diastolic > 90 && diastolic <= 100) {
            esito = true;
            message += "\n\n" + getResources().getString(R.string.high_blood_pressure_moderate);
        } else if (systolic > 160 && diastolic > 100) {
            esito = true;
            message += "\n\n" + getResources().getString(R.string.high_blood_pressure_grave);
        }
        message += "\n\n" + getResources().getString(R.string.question_value);
        DialogDetails dialogDetails = new DialogDetails();
        dialogDetails.showCustomDialog(getResources().getString(R.string.blood_pressure), message, getContext(), esito, new BloodPressure(systolic, diastolic));
    }
}
