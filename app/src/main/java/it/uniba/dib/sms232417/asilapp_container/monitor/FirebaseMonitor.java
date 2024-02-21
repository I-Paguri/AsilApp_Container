package it.uniba.dib.sms232417.asilapp_container.monitor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import it.uniba.dib.sms232417.asilapp_container.MainActivity;
import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.adapter.DatabaseAdapter;
import it.uniba.dib.sms232417.asilapp_container.entity.Auth;
import it.uniba.dib.sms232417.asilapp_container.utilities.StringUtils;

public class FirebaseMonitor extends Thread {

    private String token;
    private Context context;
    int i = 0;
    boolean isConnect = true;
    DatabaseAdapter dbAdapter;

    public FirebaseMonitor(String token, Context context) {
        this.token = token;
        this.context = context;
        dbAdapter = new DatabaseAdapter();
    }

    @Override
    public void run() {
        while (isConnect) {
            dbAdapter.checkIsConnected(token, new DatabaseAdapter.OnIsConnectedCallback() {
                @Override
                public void onCallback(boolean isConnect) {
                    Log.d("Firebase Monitor: isConnect", String.valueOf(isConnect));
                    if (!isConnect) {
                        dbAdapter.deleteUUIDToken(token);
                        File file = new File(StringUtils.FILE_PATH_PATIENT_LOGGED);
                        if (file.exists()) {
                            boolean isDeleted = file.delete();
                            if (isDeleted) {
                                Log.d("File", "File deleted successfully");
                            } else {
                                Log.d("File", "Failed to delete file");
                            }
                        }
                        FirebaseMonitor.this.isConnect = false;
                        Toast.makeText(context, R.string.interrupt_session, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void stopThread() {
        isConnect = false;

    }
}