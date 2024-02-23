package it.uniba.dib.sms232417.asilapp_container.adapter;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MemoryLruGcSettings;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import it.uniba.dib.sms232417.asilapp_container.entity.Auth;
import it.uniba.dib.sms232417.asilapp_container.entity.BloodPressure;
import it.uniba.dib.sms232417.asilapp_container.entity.Glycemia;
import it.uniba.dib.sms232417.asilapp_container.entity.HeartRate;
import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.entity.Temperature;
import it.uniba.dib.sms232417.asilapp_container.interfaces.OnGetNumberOfRecordCallbackInterface;
import it.uniba.dib.sms232417.asilapp_container.interfaces.OnGetValueFromDBInterface;
import it.uniba.dib.sms232417.asilapp_container.interfaces.OnPatientCallbackInterface;
import it.uniba.dib.sms232417.asilapp_container.interfaces.OnProfileImageCallback;


public class DatabaseAdapter {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    boolean credential = false;


    public DatabaseAdapter() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
    public void uploadUUIDToken(String token, OnPatientCallbackInterface callback){
        String uuid = "";
        boolean isConnect = false;
        Auth auth = new Auth(token, uuid, isConnect);
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
    public void deleteUUIDToken(String token){
        db = FirebaseFirestore.getInstance();
        db.collection("qr_code_container")
                .document(token)
                .delete();
    }

    public interface OnIsConnectedCallback {
        void onCallback(boolean isConnected);
    }
    public void checkIsConnected(String token, OnIsConnectedCallback callback) {
        AtomicBoolean isConnected = new AtomicBoolean(true);
        db = FirebaseFirestore.getInstance();
        db.collection("qr_code_container")
                .document(token)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onCallback((boolean) documentSnapshot.get("isConnect"));
                        Log.d("DB: isConnect", String.valueOf(documentSnapshot.get("isConnect")));
                    }
                });
    }

    public void recordsValue(Patient patient, Object o){

        if(o instanceof HeartRate){

            getNumeberOfRecords(patient,"heart_rate", new OnGetNumberOfRecordCallbackInterface() {
                @Override
                public void onCallback(int value) {
                    value++;
                    db = FirebaseFirestore.getInstance();
                    String collection_type = "heart_rate";

                    HeartRate heartRate = (HeartRate) o;
                    Date data_excecution = heartRate.getDate();
                    String document_data = data_excecution.toString();
                    Log.d("HeartRateData", "Data: " + document_data);


                    db.collection("patient")
                            .document(patient.getUUID())
                            .collection(collection_type)
                            .document(collection_type + "_" + value)
                            .set(o);
                    Log.d("DB: recordsValue", "Record added to database");
                }
            });
        }else if(o instanceof Temperature){
            getNumeberOfRecords(patient,"temperature", new OnGetNumberOfRecordCallbackInterface() {
                @Override
                public void onCallback(int value) {
                    value++;
                    db = FirebaseFirestore.getInstance();
                    String collection_type = "temperature";

                    Temperature temperature = (Temperature) o;
                    Date data_excecution = temperature.getDate();
                    String document_data = data_excecution.toString();
                    Log.d("TemperatureData", "Data: " + document_data);
                    db.collection("patient")
                            .document(patient.getUUID())
                            .collection(collection_type)
                            .document(collection_type + "_" + value)
                            .set(o);
                    Log.d("DB: recordsValue", "Record added to database");
                }
            });

        }else if(o instanceof BloodPressure){
            getNumeberOfRecords(patient,"blood_pressure", new OnGetNumberOfRecordCallbackInterface() {
                @Override
                public void onCallback(int value) {
                    value++;
                    db = FirebaseFirestore.getInstance();
                    String collection_type = "blood_pressure";

                    BloodPressure bloodPressure = (BloodPressure) o;
                    Date data_excecution = bloodPressure.getDate();
                    String document_data = data_excecution.toString();
                    Log.d("BloodPressureData", "Data: " + document_data);
                    db.collection("patient")
                            .document(patient.getUUID())
                            .collection(collection_type)
                            .document(collection_type + "_" + value)
                            .set(o);
                    Log.d("DB: recordsValue", "Record added to database");
                }
            });
        }else if(o instanceof Glycemia){
            getNumeberOfRecords(patient,"glycemia", new OnGetNumberOfRecordCallbackInterface() {
                @Override
                public void onCallback(int value) {
                    value++;
                    db = FirebaseFirestore.getInstance();
                    String collection_type = "glycemia";

                    Glycemia glycemia = (Glycemia) o;
                    Date data_excecution = glycemia.getDate();
                    String document_data = data_excecution.toString();
                    Log.d("GlycemiaData", "Data: " + document_data);
                    db.collection("patient")
                            .document(patient.getUUID())
                            .collection(collection_type)
                            .document(collection_type + "_" + value)
                            .set(o);
                    Log.d("DB: recordsValue", "Record added to database");
                }
            });
        }



    }
    public void getNumeberOfRecords(Patient patient, String collection_type, OnGetNumberOfRecordCallbackInterface callback){


        db = FirebaseFirestore.getInstance();
        db.collection("patient")
                .document(patient.getUUID())
                .collection(collection_type)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                            callback.onCallback(queryDocumentSnapshots.size());
                            Log.d("DB: DB", "Number of records: " + queryDocumentSnapshots.size());
                });




    }
    /*
    PER MARCO RECUPERO DATI DAL DB
    public void takeValueFromDB(Patient patient, String collection_type, OnGetValueFromDBInterface callback){
        db = FirebaseFirestore.getInstance();

        db.collection("patient")
                .document(patient.getUUID())
                .collection(collection_type)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if(collection_type.equals("heart_rate")) {
                        ArrayList<HeartRate> heartRates = new ArrayList<>();
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            HeartRate hr = queryDocumentSnapshots.getDocuments().get(i).toObject(HeartRate.class);
                            heartRates.add(hr);
                            callback.onCallback(heartRates);
                        }
                    }else if(collection_type.equals("temperature")) {
                        ArrayList<Temperature> temperatures = new ArrayList<>();
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            Temperature t = queryDocumentSnapshots.getDocuments().get(i).toObject(Temperature.class);
                            temperatures.add(t);
                            callback.onCallback(temperatures);

                        }
                    }else if(collection_type.equals("blood_pressure")) {
                        ArrayList<BloodPressure> bloodPressures = new ArrayList<>();
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            BloodPressure bp = queryDocumentSnapshots.getDocuments().get(i).toObject(BloodPressure.class);
                            bloodPressures.add(bp);
                            callback.onCallback(bloodPressures);
                        }
                    }else if(collection_type.equals("glycemia")) {
                        ArrayList<Glycemia> glycemias = new ArrayList<>();
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            Glycemia g = queryDocumentSnapshots.getDocuments().get(i).toObject(Glycemia.class);
                            glycemias.add(g);
                            callback.onCallback(glycemias);
                        }
                    }

                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(e, "Error while getting data from database");
                });
    }

     */
    public void getProfileImage(String userUUID, OnProfileImageCallback callback) {
        db.collection("patient")
                .document(userUUID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        Log.d("MyAccountFragment", "Profile image URL: " + profileImageUrl);
                        callback.onCallback(profileImageUrl);
                    } else {
                        callback.onCallbackError(new Exception("No profile image found for this user."));
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onCallbackError(e);
                });
    }
}
