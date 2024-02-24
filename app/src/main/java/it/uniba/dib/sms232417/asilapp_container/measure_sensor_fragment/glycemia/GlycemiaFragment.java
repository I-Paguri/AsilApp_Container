package it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.glycemia;

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
import it.uniba.dib.sms232417.asilapp_container.entity.Glycemia;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.MeasureFragment;
import it.uniba.dib.sms232417.asilapp_container.utilities.DialogDetails;

public class GlycemiaFragment extends Fragment {

    private ImageView imageView;

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
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.blood_glucose));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        imageView = view.findViewById(R.id.glycemia_image);

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
                        .load(R.drawable.glycemia)
                        .into((ImageView) requireView().findViewById(R.id.glycemia_image));
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
                        // relativeLayout.setVisibility(View.GONE);
                        calculateGlycemia();

                        progressBar.setVisibility(View.GONE);
                        loadingText.setVisibility(View.GONE);

                        // Cambia l'immagine con la nuova risorsa
                        imageView.setImageResource(R.drawable.glycemia_static);
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


        String message = getResources().getString(R.string.glycemia_value_explain) + " " + glycemia + " mg/dL";
        if (glycemia < 70.0) {
            message += "\n\n" + getResources().getString(R.string.low_glycemia);
            esito = true;
        } else if (glycemia > 125 && glycemia <= 200) {
            message += "\n\n" + getResources().getString(R.string.high_glycemia);
            esito = true;
        } else if (glycemia >= 70.0 && glycemia < 100) {
            message += "\n\n" + getResources().getString(R.string.normal_glycemia);
        } else if (glycemia >= 100 && glycemia <= 125) {
            message += "\n\n" + getResources().getString(R.string.pre_diabetes);
            esito = true;
        }
        message += "\n\n" + getResources().getString(R.string.question_value);
        DialogDetails dialogDetails = new DialogDetails();
        dialogDetails.showCustomDialog(getResources().getString(R.string.glycemia_title), message, getContext(), esito, new Glycemia(glycemia));
    }
}