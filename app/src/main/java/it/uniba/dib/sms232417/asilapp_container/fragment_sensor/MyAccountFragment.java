package it.uniba.dib.sms232417.asilapp_container.fragment_sensor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;


import it.uniba.dib.sms232417.asilapp_container.MainActivity;
import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.adapter.DatabaseAdapter;
import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.HomeFragment;
import it.uniba.dib.sms232417.asilapp_container.utilities.StringUtils;

public class MyAccountFragment extends Fragment {
    Toolbar toolbar;
    Patient loggedPatient;
    DatabaseAdapter dbAdapterPatient;
    BottomNavigationView bottomNavigationView;

    private StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_account_layout, container, false);

        ImageView addProfilePicImageView = view.findViewById(R.id.add_profile_pic);


        storageReference = FirebaseStorage.getInstance().getReference();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loggedPatient = checkPatientLogged();

        // Inizializza dbAdapterPatient e dbAdapterDoctor
        dbAdapterPatient = new DatabaseAdapter(getContext());


        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        Button btnLogout = getView().findViewById(R.id.btn_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loggedPatient != null) {
                    try {
                        onLogout(v, loggedPatient.getEmail());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        toolbar = requireActivity().findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set home icon as back button
        Drawable homeIcon = getResources().getDrawable(R.drawable.home, null);
        // Set color filter
        homeIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(homeIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.my_account));
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
                transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        if (loggedPatient != null) {
            TextView txtName = getView().findViewById(R.id.txt_name);
            TextView txtSurname = getView().findViewById(R.id.txt_surname);
            TextView txtRegion = getView().findViewById(R.id.txt_region);

            // TextView txtage = getView().findViewById(R.id.txt_age);
            txtName.setText(loggedPatient.getNome());
            txtSurname.setText(loggedPatient.getCognome());
            txtRegion.setText(loggedPatient.getRegione());
            String dataNascita = loggedPatient.getDataNascita();

            // Gestione del click sull'immagine di profilo
            ImageView addProfilePic = getView().findViewById(R.id.add_profile_pic);

            /*
            dbAdapterPatient.getProfileImage(loggedPatient.getUUID(), new OnProfileImageCallback() {
                @Override
                public void onCallback(String profileImageUrl) {
                    // Aggiorna l'immagine del profilo con l'URL recuperato
                    updateProfileImage(profileImageUrl);
                }

                @Override
                public void onCallbackError(Exception e) {
                    // Gestisci l'errore
                    Toast.makeText(getContext(), "Error loading profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

             */

        }

    }

    public void onLogout(View v, String email) throws Exception {

        File loggedPatientFile = new File(StringUtils.FILE_PATH_PATIENT_LOGGED);
        if (loggedPatientFile.exists()) {
            loggedPatientFile.delete();
        }

        Intent esci = new Intent(getContext(), MainActivity.class);
        startActivity(esci);
        requireActivity().finish();
    }


    public Patient checkPatientLogged() {
        Patient loggedPatient;
        File loggedPatientFile = new File(StringUtils.FILE_PATH_PATIENT_LOGGED);
        if (loggedPatientFile.exists()) {
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
        } else
            return null;

    }
}