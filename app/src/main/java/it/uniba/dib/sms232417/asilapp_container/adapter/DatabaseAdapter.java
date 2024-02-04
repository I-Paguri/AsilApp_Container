package it.uniba.dib.sms232417.asilapp_container.adapter;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms232417.asilapp_container.entity.Auth;

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
    public void uploadUUIDToken(String token){
        Auth auth = new Auth(token, null, null);
        db = FirebaseFirestore.getInstance();

        db.collection("qr_code_container")
                .document(token)
                .set(auth);

        checkLogin(token);
    }

    public void checkLogin(String token) {
        new Thread(() -> {
            credential = false;
            db = FirebaseFirestore.getInstance();
            int attemps = 0;
            while (!credential && attemps < 30) {
                db.collection("qr_code_container")
                        .document(token)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String email = (String) documentSnapshot.get("email");
                                String password = (String) documentSnapshot.get("password");
                                if (email != null && password != null) {
                                    credential = true;
                                    Toast.makeText(context, "Ci sono", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                attemps++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(60000); // Wait for 1 minute before ending the thread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
