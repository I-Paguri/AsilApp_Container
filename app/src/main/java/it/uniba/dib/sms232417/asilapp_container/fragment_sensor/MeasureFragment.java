package it.uniba.dib.sms232417.asilapp_container.fragment_sensor;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.SensorActivity;
import it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.blood_pressure.BloodPressureFragment;
import it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.glycemia.GlycemiaFragment;
import it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.heartbeat.HeartBeatFragment;
import it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.temperature.TemperatureFragment;

public class MeasureFragment extends Fragment {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.fragment_measure_layout, container, false);
        return inflater.inflate(R.layout.fragment_measure_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view); // Initialize bottomNavigationView

        toolbar = requireActivity().findViewById(R.id.toolbar);
        // Initialize bottomNavigationView

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set home icon as back button
        Drawable homeIcon = getResources().getDrawable(R.drawable.home, null);
        // Set color filter
        homeIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(homeIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.measure));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Navigate to HomeFragment
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                HomeFragment homeFragment = new HomeFragment(); // Create new instance of HomeFragment
                transaction.replace(R.id.nav_host_fragment_activity_main, homeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        MaterialCardView cardViewHeartBeat = view.findViewById(R.id.cardViewHearthBeat);
        MaterialCardView cardViewTemperature = view.findViewById(R.id.cardViewTemperature);
        MaterialCardView cardViewPressure = view.findViewById(R.id.cardViewPressure);
        MaterialCardView cardViewGlycemia = view.findViewById(R.id.cardViewGlycemia);

        cardViewHeartBeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SensorActivity) getActivity()).replaceFragment(new HeartBeatFragment());
            }
        });
        cardViewTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SensorActivity) getActivity()).replaceFragment(new TemperatureFragment());
            }
        });
        cardViewPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SensorActivity) getActivity()).replaceFragment(new BloodPressureFragment());
            }
        });
        cardViewGlycemia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SensorActivity) getActivity()).replaceFragment(new GlycemiaFragment());
            }
        });

    }


}
