package it.uniba.dib.sms232417.asilapp_container.entity;

public class Auth {
    private String tokenQr;
    private String email;
    private String password;

    public Auth(String tokenQr, String email, String password) {
        this.tokenQr = tokenQr;
        this.email = email;
        this.password = password;
    }

    public String getTokenQr() {
        return tokenQr;
    }

    public void setTokenQr(String tokenQr) {
        this.tokenQr = tokenQr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
