package it.uniba.dib.sms232417.asilapp_container.thread_connection;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import it.uniba.dib.sms232417.asilapp_container.R;

public class InternetCheckThread extends Thread {
    private Context context;
    boolean isDialogShow = false;
    private Handler handler;
    public InternetCheckThread(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    Log.d("Internet", "Connected");
                    handler.sendEmptyMessage(1);
                    isDialogShow = false;
                } else {
                    Log.d("Internet", "Not connected");
                    if(!isDialogShow) {
                        handler.sendEmptyMessage(0);
                        isDialogShow = true;
                    }


                }
                // Sleep for 5 seconds before checking again
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}