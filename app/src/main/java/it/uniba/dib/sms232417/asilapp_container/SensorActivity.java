package it.uniba.dib.sms232417.asilapp_container;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.uniba.dib.sms232417.asilapp_container.entity.Patient;

public class SensorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity_layout);
        Intent intent = getIntent();
        Patient patient = (Patient) intent.getSerializableExtra("loggedPatient");

        TextView textView = findViewById(R.id.informazioni);
        textView.setText("Benvenuto " + patient.getNome() + " " + patient.getCognome() + "!");

    }
}
