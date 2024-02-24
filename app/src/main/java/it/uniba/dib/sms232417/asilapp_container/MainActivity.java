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
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.HomeFragment;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.MeasureFragment;
import it.uniba.dib.sms232417.asilapp_container.fragment_sensor.MyAccountFragment;
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
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(currentFragment instanceof QRCodeAuth){
            finish();
        }else {
            if (currentFragment instanceof HomeFragment || currentFragment instanceof MeasureFragment || currentFragment instanceof MyAccountFragment) {
                // If the current fragment is HomeFragment, MeasureFragment, or MyAccountFragment, navigate to QRCodeAuth
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new QRCodeAuth());
                transaction.addToBackStack(null);
                transaction.commit();
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
            }

           // super.onBackPressed();
        }

    }


    /*

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        if (currentFragment instanceof VideosFragment || currentFragment instanceof MyPatientsFragment
                || currentFragment instanceof MyAccountFragment || currentFragment instanceof QRCodeAuth || (currentFragment instanceof PatientFragment && loggedPatient != null)) {
            // If the current fragment is HealthcareFragment, MyPatientsFragment, or MyAccountFragment, navigate to HomeFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } else {
            if (currentFragment instanceof TreatmentFormGeneralFragment) {
                // If the current fragment is TreatmentFormGeneralFragment, show a dialog
                new MaterialAlertDialogBuilder(this, R.style.CustomMaterialDialog)
                        .setTitle(getResources().getString(R.string.going_back))
                        .setMessage(getResources().getString(R.string.unsaved_changes))
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to negative button press
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.go_back), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to positive button press
                                // Navigate back
                                getSupportFragmentManager().popBackStack();
                            }
                        })
                        .create()
                        .show();
            } else {
                if (currentFragment instanceof HomeFragment) {
                    if (doubleBackToExitPressedOnce) {
                        finish();
                        return;
                    }

                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getResources().getString(R.string.press_back_again), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                } else {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        super.onBackPressed();
                    }
                }
            }
        }
    }
     */

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

