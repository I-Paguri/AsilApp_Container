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
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

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
                RelativeLayout relativeLayout = findViewById(R.id.noConnectionLayout);
                if (msg.what == 0) {
                    showNoInternetDialog();
                }else if(msg.what == 1){
                    deleteMsgError();
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
        showMsgError();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.no_connection_title);
        builder.setMessage(R.string.no_connection_explain);
        builder.setPositiveButton(R.string.no_connection_button, (dialog, which) -> {
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        });
        builder.setNegativeButton(R.string.no_connection_button_cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void deleteMsgError() {
        RelativeLayout relativeLayout = findViewById(R.id.noConnectionLayout);
        relativeLayout.setVisibility(RelativeLayout.GONE);
    }
    private void showMsgError() {
        RelativeLayout relativeLayout = findViewById(R.id.noConnectionLayout);
        TextView textView = findViewById(R.id.noConnectionEditText);
        textView.setText(R.string.no_connection);
        relativeLayout.setVisibility(RelativeLayout.VISIBLE);
    }
}

