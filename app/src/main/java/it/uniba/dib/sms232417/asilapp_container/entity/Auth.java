package it.uniba.dib.sms232417.asilapp_container.entity;

public class Auth {
    private String tokenQr;
    private String UUID;

    public Auth(String tokenQr, String email) {
        this.tokenQr = tokenQr;
        this.UUID = email;
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
