package it.uniba.dib.sms232417.asilapp_container;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.uniba.dib.sms232417.asilapp_container.thread_connection.InternetCheckThread;
import it.uniba.dib.sms232417.asilapp_container.welcome_fragment.QRCodeAuth;

public class MainActivity extends AppCompatActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 0) {
                    showNoInternetDialog();
                }
                return true;
            }
        });
        InternetCheckThread internetCheckThread = new InternetCheckThread(this, handler);
        internetCheckThread.start();
        setContentView(R.layout.activity_main);
        replaceFragment(new QRCodeAuth());

    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.fragment_container,fragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(currentFragment instanceof QRCodeAuth){
            finish();
        }else {
            super.onBackPressed();
        }

    }
    private void showNoInternetDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please check your internet connection and try again");
        builder.setPositiveButton("OK", (dialog, which) -> {
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        });
        builder.show();
    }
}

