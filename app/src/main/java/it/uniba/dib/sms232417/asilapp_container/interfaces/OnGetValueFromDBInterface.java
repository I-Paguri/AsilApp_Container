package it.uniba.dib.sms232417.asilapp_container.interfaces;

import com.bumptech.glide.signature.ObjectKey;

import java.util.ArrayList;

public interface OnGetValueFromDBInterface {
    public default void onCallback(ArrayList<?> listOfValue) {    }
    public default void onCallbackError(Exception exception, String message) {}
}
