package it.uniba.dib.sms232417.asilapp_container.entity;

import android.os.Parcelable;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Patient implements Parcelable, Serializable {
    private String UUID;
    private String nome;
    private String cognome;
    private String email;
    private String dataNascita;
    private String regione;

    public Patient() {
    }

    public Patient(String UUID, String nome, String cognome, String email, String dataNascita, String regione){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.dataNascita = dataNascita;
        this.regione = regione;

        this.UUID = UUID;

    }

    protected Patient(Parcel in) {
        UUID = in.readString();
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
        dataNascita = in.readString();
        regione = in.readString();
        }

        public static final Creator<Patient> CREATOR = new Creator<Patient>() {
            @Override
            public Patient createFromParcel(Parcel in) {
                return new Patient(in);
            }

            @Override
            public Patient[] newArray(int size) {
                return new Patient[size];
            }
        };

        public void setUUID(String UUID) {
            this.UUID = UUID;
        }

        public String getUUID() {
            return UUID;
        }

        public String getCognome() {
            return cognome;
        }

        public String getNome() {
            return nome;
        }

        public String getEmail() {
            return email;
        }

        public String getDataNascita() {
            return dataNascita;
        }

        public int getAge() {
            String birthdayString = this.getDataNascita();
            try {
                // Parse the birthday string into a Date object
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

                Date birthDate = sdf.parse(birthdayString);

                // Get the current date
                Calendar today = Calendar.getInstance();

                // Convert the birth date into a Calendar object
                Calendar birthDay = Calendar.getInstance();
                birthDay.setTimeInMillis(birthDate.getTime());

                // Calculate the age
                int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

                // If the birthday has not occurred this year, subtract one from the age
                if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }
                return age;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public String getRegione() {
            return regione;
        }


        @Override
        public String toString() {
            return "Patient{" +
                    "UUID='" + UUID + '\'' +
                    "nome='" + nome + '\'' +
                    ", cognome='" + cognome + '\'' +
                    ", email='" + email + '\'' +
                    ", dataNascita='" + dataNascita + '\'' +
                    ", regione='" + regione + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(UUID);
            dest.writeString(nome);
            dest.writeString(cognome);
            dest.writeString(email);
            dest.writeString(dataNascita);
            dest.writeString(regione);
        }

}


