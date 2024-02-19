package it.uniba.dib.sms232417.asilapp_container;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.HomeFragment;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.MeasureFragment;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.MyAccountFragment;
import it.uniba.dib.sms232417.asilapp_container.monitor.FirebaseMonitor;
import it.uniba.dib.sms232417.asilapp_container.utilities.StringUtils;

public class SensorActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity_layout);
        Intent intent = getIntent();
        Patient patient = (Patient) intent.getParcelableExtra("loggedPatient");
        String token = intent.getStringExtra("token");

        if(patient != null) {
            Log.d("Arriva il paziente", "onCreate: " + patient.toString());
            try {

                Log.d("File", "Creazione file");
                FileOutputStream fos = openFileOutput(StringUtils.PATIENT_LOGGED, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(patient);
                Log.d("Patient", "Patient logged: " + patient.toString());
            } catch(IOException e){
                e.printStackTrace();
            }
        }else {
            Log.d("Utente test", "onCreate: Utente di test");
            Patient patient1 = new Patient("4gKBptN3k6SF5FbqWfL0oQ6vXI52", "Sergio","Pinto","sergiopinto2501@gmail.com","25 Sep 2001", "Italy");
            try {

                Log.d("File", "Creazione file");
                FileOutputStream fos = openFileOutput(StringUtils.PATIENT_LOGGED, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(patient1);
                Log.d("Patient", "Patient logged: " + patient1.toString());
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        replaceFragment(new HomeFragment());

        //Avvio Thread per controllo connessione
        /*
        FirebaseMonitor monitor = new FirebaseMonitor(token,this);
        monitor.start();

         */
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_patient);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if(item.getItemId() == R.id.navigation_measure){
                selectedFragment = new MeasureFragment();
            }else if(item.getItemId() == R.id.navigation_my_account){
                selectedFragment = new MyAccountFragment();
            }else if(item.getItemId() == R.id.navigation_home){
                selectedFragment = new HomeFragment();
            }

            replaceFragment(selectedFragment);
            return true;
        });


    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
