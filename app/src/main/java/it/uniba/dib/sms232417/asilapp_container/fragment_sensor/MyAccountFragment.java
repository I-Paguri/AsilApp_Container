package it.uniba.dib.sms232417.asilapp_container.fragment_sensor;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.blongho.country_data.World;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import it.uniba.dib.sms232417.asilapp_container.MainActivity;
import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.SensorActivity;
import it.uniba.dib.sms232417.asilapp_container.adapter.DatabaseAdapter;
import it.uniba.dib.sms232417.asilapp_container.entity.HeartRate;
import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.HomeFragment;
import it.uniba.dib.sms232417.asilapp_container.interfaces.OnGetValueFromDBInterface;
import it.uniba.dib.sms232417.asilapp_container.interfaces.OnProfileImageCallback;
import it.uniba.dib.sms232417.asilapp_container.monitor.FirebaseMonitor;
import it.uniba.dib.sms232417.asilapp_container.utilities.StringUtils;


public class MyAccountFragment extends Fragment {
    Toolbar toolbar;
    Patient loggedPatient;

    final String NAME_FILE = "automaticLogin";
    DatabaseAdapter dbAdapter;

    BottomNavigationView bottomNavigationView;

    private StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_account, container, false);


        storageReference = FirebaseStorage.getInstance().getReference();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loggedPatient = checkPatientLogged();


        // Inizializza dbAdapterPatient e dbAdapterDoctor
        dbAdapter = new DatabaseAdapter();


        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        Button btnLogout = getView().findViewById(R.id.btn_logout);

        // Verifica se il Bundle degli argomenti è null
        if (getArguments() != null) {
            // Ottieni l'URL dell'immagine del profilo dal bundle
            String profileImageUrl = getArguments().getString("profile_image_url");

            // Aggiorna l'immagine del profilo
            ImageView profileImageView = getView().findViewById(R.id.my_account);
            Glide.with(this)
                    .load(profileImageUrl)
                    .circleCrop()
                    .into(profileImageView);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggedPatient != null) {
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



        // PER MARCO - Prova recupero dati dal DB
        /*
        Button btnProvaRecupero = getView().findViewById(R.id.btnProvaRecupero);
        btnProvaRecupero.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dbAdapter.takeValueFromDB(loggedPatient, "heart_rate", new OnGetValueFromDBInterface() {
                    @Override
                    public void onCallback(ArrayList<?> listOfValue) {
                        for (int i = 0; i < listOfValue.size(); i++) {
                            if(listOfValue.get(i) instanceof HeartRate) {
                                HeartRate heartRate = (HeartRate) listOfValue.get(i);
                                Log.d("MyAccountFragment", "Heart rate: " + heartRate.toString());
                            }
                        }
                    }

                    @Override
                    public void onCallbackError(Exception exception, String message) {
                        OnGetValueFromDBInterface.super.onCallbackError(exception, message);
                    }
                });
            }
        });
        */

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
            TextView txtBirthday = getView().findViewById(R.id.txtBirthday);
            TextView txtEmail = getView().findViewById(R.id.txtEmail);
            TextView txtAge = getView().findViewById(R.id.txtAge);
            ImageView imgFlag = getView().findViewById(R.id.flag);

            txtName.setText(loggedPatient.getNome());
            txtSurname.setText(loggedPatient.getCognome());
            txtRegion.setText(loggedPatient.getRegione());
            String dataNascita = loggedPatient.getDataNascita();

            txtBirthday.setText(dataNascita);
            txtEmail.setText(loggedPatient.getEmail());

            txtAge.setText("(" + loggedPatient.getAge() + " " + getResources().getQuantityString(R.plurals.age, loggedPatient.getAge(), loggedPatient.getAge()) + ")");

            // Set flag icon
            World.init(requireContext());
            final int flag = World.getFlagOf(loggedPatient.getRegione().toLowerCase());
            imgFlag.setImageResource(flag);


            // Gestione del click sull'immagine di profil

            // Ottieni l'URL dell'immagine del profilo dal database
            dbAdapter.getProfileImage(loggedPatient.getUUID(), new OnProfileImageCallback() {
                @Override
                public void onCallback(String imageUrl) {
                    // Check if the profile image URL exists and is not empty before loading it
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(imageUrl)
                                .circleCrop()
                                .into((ImageView) getView().findViewById(R.id.my_account));
                        saveImageToInternalStorage(imageUrl);
                    } else {
                        // If the profile image URL does not exist or is empty, load the default profile image
                        Glide.with(getContext())
                                .load(R.drawable.default_profile_image)
                                .circleCrop()
                                .into((ImageView) getView().findViewById(R.id.my_account));
                    }
                }

                @Override
                public void onCallbackError(Exception e) {
                    // If there is an error getting the profile image URL, load the default profile image
                    Glide.with(getContext())
                            .load(R.drawable.default_profile_image)
                            .circleCrop()
                            .into((ImageView) getView().findViewById(R.id.my_account));
                }
            });

        }

    }


    public void onLogout(View v, String email) throws Exception {

        dbAdapter = new DatabaseAdapter();


        Toast.makeText(getContext(),
                getResources().getString(R.string.logout_successful),
                Toast.LENGTH_SHORT).show();
        ((SensorActivity) requireActivity()).getThreadFirebaseMonitor().stopThread();

        File file = new File(StringUtils.FILE_PATH_PATIENT_LOGGED);
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (isDeleted) {
                Log.d("File", "File deleted successfully");
            } else {
                Log.d("File", "Failed to delete file");
            }
        }

        Intent intent = new Intent(getContext(), MainActivity.class);
        getContext().startActivity(intent);
        getActivity().finish();
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

                            // Creare un FileOutputStream con il file
                            FileOutputStream out = new FileOutputStream(file);

                            // Comprimere il bitmap in un formato specifico e scrivere sul FileOutputStream
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);

                            // Chiudere il FileOutputStream
                            out.close();
                            Log.d("MyAccountFragment", "Profile image saved to file: " + file.getAbsolutePath());
                            ((SensorActivity) requireActivity()).updateIconProfileImage();
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