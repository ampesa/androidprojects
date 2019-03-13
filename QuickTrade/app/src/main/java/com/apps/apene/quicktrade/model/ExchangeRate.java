package com.apps.apene.quicktrade.model;

public class ExchangeRate {

    // Atributos
    private String date = null;
    private Object rates = null;
    private String usd = null;
    private String base = null;

    // constructores
    public ExchangeRate (){
    }

    public ExchangeRate (String date, Object rates, String usd, String base){
        this.date = date;
        this.rates = rates;
        this.usd = usd;
        this.base = base;
    }

    // Getters y Setters

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Object getRates() {
        return rates;
    }

    public void setRates(Object rates) {
        this.rates = rates;
    }

    public String getUsd() {
        return usd;
    }

    public void setUsd(String usd) {
        this.usd = usd;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    // Método toString

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "date='" + date + '\'' +
                ", rates=" + rates +
                ", usd='" + usd + '\'' +
                ", base='" + base + '\'' +
                '}';
    }
}
