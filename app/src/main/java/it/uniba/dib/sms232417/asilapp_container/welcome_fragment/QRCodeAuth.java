package it.uniba.dib.sms232417.asilapp_container.welcome_fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import it.uniba.dib.sms232417.asilapp_container.R;
import it.uniba.dib.sms232417.asilapp_container.SensorActivity;
import it.uniba.dib.sms232417.asilapp_container.adapter.DatabaseAdapter;
import it.uniba.dib.sms232417.asilapp_container.entity.Patient;
import it.uniba.dib.sms232417.asilapp_container.interfaces.OnPatientCallbackInterface;

public class QRCodeAuth extends Fragment {

    DatabaseAdapter dbAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.qr_code_layout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView qrCode = (ImageView) getView().findViewById(R.id.qrCode);
        qrCode.setVisibility(View.GONE);
        Button btnQrCode = (Button) getView().findViewById(R.id.generaQR);
        btnQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID uuid = UUID.randomUUID();
                Bitmap bitmap = generateQRCode(uuid.toString());
                qrCode.setImageBitmap(bitmap);
                qrCode.setVisibility(View.VISIBLE);
                btnQrCode.setVisibility(View.GONE);

                dbAdapter = new DatabaseAdapter(getContext());
                dbAdapter.uploadUUIDToken(uuid.toString(), new OnPatientCallbackInterface() {
                    @Override
                    public void onCallback(Patient patient) {
                        Intent intent = new Intent(getContext(), SensorActivity.class);
                        intent.putExtra("loggedPatient", (Parcelable) patient);
                        intent.putExtra("token", uuid.toString());
                        startActivity(intent);
                    }

                    @Override
                    public void onCallbackError(Exception exception, String message) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                        builder.setTitle("Errore");
                        builder.setMessage(message);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                qrCode.setVisibility(View.GONE);
                                btnQrCode.setVisibility(View.VISIBLE);
                            }
                        });
                        builder.show();

                    }
                });
                //dbAdapter.checkLogin(uuid.toString());
            }
        });



    }

    public static Bitmap generateQRCode(String data) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    data,
                    BarcodeFormat.QR_CODE,
                    512, // larghezza del codice QR
                    512  // altezza del codice QR
            );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
