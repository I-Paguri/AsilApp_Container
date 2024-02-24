package it.uniba.dib.sms232417.asilapp_container.fragment_sensor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.SensorActivity;
import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.utilities.StringUtils;

public class HomeFragment extends Fragment {

    private Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_home_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("HomeFragment", "onViewCreated: " + "HomeFragment");



        toolbar = requireActivity().findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        BadgeDrawable badgeDrawable = BadgeDrawable.create(requireContext());
        badgeDrawable.setNumber(10);


        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.btn_home));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));


        Patient loggedPatient = checkPatientLogged();
        TextView textView = view.findViewById(R.id.txtUser_Name);
        if (loggedPatient != null) {
            Log.d("HomeFragment", "onViewCreated: " + loggedPatient.getNome());
            textView.setText(loggedPatient.getNome());
        }

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.nav_view);

        MaterialCardView cardView = view.findViewById(R.id.cardViewMeasures);
        cardView.setOnClickListener(v -> {
            ((SensorActivity) requireActivity()).replaceFragment(new MeasureFragment());
            bottomNavigationView.setSelectedItemId(R.id.navigation_measure);
        });

        MaterialCardView howToMeasure = view.findViewById(R.id.howToMeasure);
        howToMeasure.setOnClickListener(v -> {
            // Check if device language is italian
            String url;

            if(getResources().getConfiguration().locale.getLanguage().equals("it")) {
                url = "https://youtu.be/dohHToygtfs";
            } else {
                url = "https://youtu.be/RSHqcuuh3L4";
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });


    }

    public Patient checkPatientLogged() {
        Patient loggedPatient;
        File loggedPatientFile = new File(StringUtils.FILE_PATH_PATIENT_LOGGED);
        if (loggedPatientFile.exists()) {
            Log.d("File", "File esiste");
            try {
                FileInputStream fis = requireActivity().openFileInput(StringUtils.PATIENT_LOGGED);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loggedPatient = (Patient) ois.readObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return loggedPatient;
        } else {
            Log.d("File", "File non esiste");
            return null;
        }
    }


}
