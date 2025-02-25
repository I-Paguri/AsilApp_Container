package it.uniba.dib.sms232417.asilapp_container.utilities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.adapter.DatabaseAdapter;
import it.uniba.dib.sms232417.asilapp_container.entity.BloodPressure;
import it.uniba.dib.sms232417.asilapp_container.entity.Glycemia;
import it.uniba.dib.sms232417.asilapp_container.entity.HeartRate;
import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.entity.Temperature;

public class DialogDetails extends DialogFragment {

    DatabaseAdapter dbAdapter;
    Context context;

    public void showCustomDialog(String title, String message, Context context, boolean esito, Object o) {
        // Inflate the custom layout

        this.context = context;
        Log.d("context", "showCustomDialog: " + context.toString());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_dialog_layout, null);

        // Access the TextViews and set their text
        TextView titleText = view.findViewById(R.id.title);
        TextView messageText = view.findViewById(R.id.textResult);
        ImageView image = view.findViewById(R.id.imageResult);

        if (!esito) {
            image.setImageResource(R.drawable.good_value);
        } else {
            image.setImageResource(R.drawable.bad_values);
        }

        image.setVisibility(View.VISIBLE);
        titleText.setText(title);
        messageText.setText(message);


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this.context);
        builder.setView(view);
        builder.setPositiveButton(R.string.records_value, (dialog, which) -> {
            Patient loggedPatient;
            File loggedPatientFile = new File(StringUtils.FILE_PATH_PATIENT_LOGGED);
            if (loggedPatientFile.exists()) {
                try {
                    FileInputStream fis = this.context
                            .openFileInput(StringUtils.PATIENT_LOGGED);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    loggedPatient = (Patient) ois.readObject();
                    Log.d("Patient_Take", "Patient logged: " + loggedPatient.toString());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                dbAdapter = new DatabaseAdapter();
                if (o instanceof HeartRate) {
                    Log.d("HeartRate", "is instance of HearRate");
                    HeartRate heartRate = (HeartRate) o;

                    dbAdapter.recordsValue(loggedPatient, heartRate);

                } else if (o instanceof Temperature) {
                    Log.d("Temperature", "is instance of Temperature");
                    Temperature temperature = (Temperature) o;
                    dbAdapter.recordsValue(loggedPatient, temperature);

                } else if (o instanceof BloodPressure) {
                    Log.d("BloodPressure", "is instance of BloodPressure");
                    BloodPressure bloodPressure = (BloodPressure) o;

                    dbAdapter.recordsValue(loggedPatient, bloodPressure);
                } else if (o instanceof Glycemia) {
                    Log.d("Glycemia", "is instance of Glycemia");
                    Glycemia glycemia = (Glycemia) o;
                    Log.d("Date", "Glycemia: " + glycemia.getDate());
                    Log.d("Date String", "Glycemia: " + glycemia.getStringDate());
                    dbAdapter.recordsValue(loggedPatient, glycemia);
                }
                Toast.makeText(context, R.string.value_added_explain, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.close, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }
}