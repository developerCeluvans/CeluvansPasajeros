package com.imaginamos.usuariofinal.taxisya.models;

/**
 * Created by leo on 10/15/16.
 */

public class Card {
    String name;
    String number;
    String reference;
    String type;
    String expireYear;
    String expireMonth;
    boolean prefered;

    public Card(String name, String number, String reference, String type, String expireMonth, String expireYear, boolean prefered) {
        this.name = name;
        this.number = number;
        this.reference = reference;
        this.type = type;
        this.expireMonth = expireMonth;
        this.expireYear = expireYear;
        this.prefered = prefered;
    }

    public String getName() {
        return name;
    }

    public void setNamAAe(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public boolean isPrefered() {
        return prefered;
    }

    public void setPrefered(boolean prefered) {
        this.prefered = prefered;
    }
}
