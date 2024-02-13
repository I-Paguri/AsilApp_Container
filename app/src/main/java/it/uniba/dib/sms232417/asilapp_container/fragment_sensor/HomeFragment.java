package it.uniba.dib.sms232417.asilapp_container.fragment_sensor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.utilities.StringUtils;

public class HomeFragment extends Fragment {
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
        Patient loggedPatient = checkPatientLogged();
        TextView textView = view.findViewById(R.id.txtUser_Name);
        if (loggedPatient != null) {
            Log.d("HomeFragment", "onViewCreated: " + loggedPatient.getNome());
            textView.setText(loggedPatient.getNome());
        }


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
