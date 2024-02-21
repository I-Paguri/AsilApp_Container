package it.uniba.dib.sms232417.asilapp_container.interfaces;

public interface OnProfileImageCallback {
    void onCallback(String profileImageUrl);
    void onCallbackError(Exception e);
}
