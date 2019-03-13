package com.apps.apene.quicktrade.model;


/**
 * Clase User gestiona los datos de usuario que son tratados en la aplicación principalmente para
 * crear, loguear y modificar datos del mismo
 * */
public class User {

    // Atributos de User
    private String email;
    private String pass;
    private String name;
    private String family_name;
    private String country;
    private String zip;
    private String uid;

    // Sobrecarga de Constructores. Diferentes según los usos dentro de la aplicación
    public User (){
    }

    public User (String email){
        this.email = email;
    }

    public User (String email, String pass) {
        this.email = email;
        this.pass= pass;
    }

    public User(String email, String pass, String name, String family_name, String country, String zip) {
        this.email = email;
        this.pass= pass;
        this.name = name;
        this.family_name = family_name;
        this.country = country;
        this.zip = zip;

    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Método toString
    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", family_name='" + family_name + '\'' +
                ", country='" + country + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }
}
