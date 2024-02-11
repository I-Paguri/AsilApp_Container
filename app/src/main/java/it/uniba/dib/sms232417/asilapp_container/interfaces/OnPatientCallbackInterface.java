package it.uniba.dib.sms232417.asilapp_container.interfaces;

import it.uniba.dib.sms232417.asilapp_container.entity.Patient;

public interface OnPatientCallbackInterface {
    void onCallback(Patient doctor);
    void onCallbackError(Exception exception,String message);
}
