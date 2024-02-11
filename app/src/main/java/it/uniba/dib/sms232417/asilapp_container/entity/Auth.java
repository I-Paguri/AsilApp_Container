package it.uniba.dib.sms232417.asilapp_container.entity;

public class Auth {
    private String tokenQr;
    private String UUID;

    private boolean isConnect;

    public Auth(String tokenQr, String email, boolean isConnect) {
        this.tokenQr = tokenQr;
        this.UUID = email;
        this.isConnect = isConnect;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean isConnect) {
        this.isConnect = isConnect;
    }

    public String getTokenQr() {
        return tokenQr;
    }

    public void setTokenQr(String tokenQr) {
        this.tokenQr = tokenQr;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

}
