package it.uniba.dib.sms232417.asilapp_container;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


import it.uniba.dib.sms232417.asilapp_container.adapter.DatabaseAdapter;
import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.HomeFragment;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.MeasureFragment;

import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.MyAccountFragment;
import it.uniba.dib.sms232417.asilapp_container.interfaces.OnProfileImageCallback;
import it.uniba.dib.sms232417.asilapp_container.measure_sensor_fragment.heartbeat.HeartBeatFragment;
import it.uniba.dib.sms232417.asilapp_container.monitor.FirebaseMonitor;
import it.uniba.dib.sms232417.asilapp_container.thread_connection.InternetCheckThread;
import it.uniba.dib.sms232417.asilapp_container.utilities.StringUtils;

public class SensorActivity extends AppCompatActivity {

    String token;
    Handler handler;
    FirebaseMonitor threadFirebaseMonitor;
    DatabaseAdapter dbAdapter;
    private boolean doubleBackToExitPressedOnce = false;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity_layout);
        Intent intent = getIntent();
        Patient patient = (Patient) intent.getParcelableExtra("loggedPatient");
        this.token = intent.getStringExtra("token");

        dbAdapter = new DatabaseAdapter();
        dbAdapter.getProfileImage(patient.getUUID(), new OnProfileImageCallback() {
            @Override
            public void onCallback(String profileImageUrl) {
                saveImageToInternalStorage(profileImageUrl);
            }

            @Override
            public void onCallbackError(Exception e) {

            }

        });
        updateIconProfileImage();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                RelativeLayout relativeLayout = findViewById(R.id.noConnectionLayout);
                if (msg.what == 0) {
                    Toast.makeText(SensorActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    threadFirebaseMonitor.stopThread();
                    Intent intent = new Intent(SensorActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });


        InternetCheckThread internetCheckThread = new InternetCheckThread(getContext(),handler);
        internetCheckThread.start();

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

        threadFirebaseMonitor = new FirebaseMonitor(token,this);
        threadFirebaseMonitor.start();

        
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

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        if (currentFragment instanceof MeasureFragment || currentFragment instanceof MyAccountFragment){
            // If the current fragment is HealthcareFragment, MyPatientsFragment, or MyAccountFragment, navigate to HomeFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } else {
            if (currentFragment instanceof HeartBeatFragment) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, new MeasureFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }else {
                if (currentFragment instanceof HomeFragment) {
                    if (doubleBackToExitPressedOnce) {
                        finish();
                        return;
                    }

                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getResources().getString(R.string.press_back_again), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                } else {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        super.onBackPressed();
                    }
                }
            }
        }
    }
    public Context getContext(){
        return this;
    }
    public FirebaseMonitor getThreadFirebaseMonitor(){
        return threadFirebaseMonitor;
    }
    public void updateIconProfileImage() {
        File file = new File(StringUtils.IMAGE_ICON);
        if (file.exists()) {
            Log.d("File_Image", "File exists");
            Bitmap bitmap = new BitmapDrawable(getResources(), StringUtils.IMAGE_ICON).getBitmap();
            Drawable iconImage = new BitmapDrawable(getResources(), bitmap);

            BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
            Glide.with(this)
                    .load(iconImage)
                    .circleCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            bottomNavigationView.getMenu().findItem(R.id.navigation_my_account).setIcon(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

            bottomNavigationView.setItemIconTintList(null);
        }else {

        }
    }

    public void saveImageToInternalStorage(String filename) {
        Glide.with(getContext())
                .asBitmap()
                .load(filename)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            // Creare un file in una directory specifica
                            File file = new File(StringUtils.IMAGE_ICON);
                            if (file.exists()) {
                                file.delete();
                            }
                            file.createNewFile();

                            //   Creare un FileOutputStream con il file
                            FileOutputStream out = new FileOutputStream(file);

                            // Comprimere il bitmap in un formato specifico e scrivere sul FileOutputStream
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);

                            // Chiudere il FileOutputStream
                            out.close();
                            Log.d("MyAccountFragment", "Profile image saved to file: " + file.getAbsolutePath());
                            updateIconProfileImage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Questo metodo viene chiamato quando l'immagine non è più necessaria
                    }
                });
    }

}
