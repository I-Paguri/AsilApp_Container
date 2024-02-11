package it.uniba.dib.sms232417.asilapp_container.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicInteger;

import it.uniba.dib.sms232417.asilapp_container.entity.Auth;
import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.interfaces.OnPatientCallbackInterface;

public class DatabaseAdapter {
    Context context;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    boolean credential = false;


    public DatabaseAdapter(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }
    public void uploadUUIDToken(String token, OnPatientCallbackInterface callback){
        Auth auth = new Auth(token, "");
        db = FirebaseFirestore.getInstance();

        db.collection("qr_code_container")
                .document(token)
                .set(auth);

        checkLogin(token, callback);
    }

    public void checkLogin(String token, OnPatientCallbackInterface callback) {
        AtomicInteger i= new AtomicInteger();
        new Thread(() -> {
            Log.d("UUID", "Thread started");

            credential = false;
            db = FirebaseFirestore.getInstance();
            int attemps = 0;
            while (!credential && attemps < 9000) {
                Log.d("i:", String.valueOf(i.get()));
                db.collection("qr_code_container")
                        .document(token)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Log.d("UUID", "Checking login...");

                            if (documentSnapshot.exists()) {
                                Log.d("UUID", "Document exists");
                                String UUID = (String) documentSnapshot.get("uuid");

                                if (!UUID.isEmpty()) {
                                    credential = true;
                                    db.collection("patient")
                                            .document(UUID)
                                            .get()
                                            .addOnSuccessListener(documentSnapshot1 -> {
                                                if (documentSnapshot1.exists()) {
                                                   Log.d("UUID", "Patient exists");
                                                        String nome = (String) documentSnapshot1.get("nome");
                                                        String cognome = (String) documentSnapshot1.get("cognome");
                                                        String email = (String) documentSnapshot1.get("email");
                                                        String dataNascita = (String) documentSnapshot1.get("dataNascita");
                                                        String regione = (String) documentSnapshot1.get("regione");

                                                        Patient patient = new Patient(UUID, nome, cognome, email, dataNascita, regione);
                                                        callback.onCallback(patient);
                                                    } else {
                                                        Log.d("UUID", "Patient does not exist");
                                                        callback.onCallbackError(new Exception(), "Patient does not exist");
                                                }
                                            });
                                    db.collection("qr_code_container")
                                            .document(token)
                                            .delete();

                                }
                            }
                        });
                attemps++;

                try {
                    Thread.sleep(1000); // Wait for 1 minute before ending the thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i.getAndIncrement();
            }
        }).start();
    }
}
