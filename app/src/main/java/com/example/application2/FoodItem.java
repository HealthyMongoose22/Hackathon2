package com.example.application2;

public class FoodItem {

    private String Inv_ID;
    private int NBD_No;

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private String displayName;
    private double amount;
    private String shortDesc;
    private String units;

    public String getUnits() {
        return this.units;
    }

    public void setUnits(String units) {
        this.units = units;
    }



    public FoodItem(String inv_ID, int NBD_No, String displayName, double amount, String shortDesc, String units) {
        this.Inv_ID = inv_ID;
        this.NBD_No = NBD_No;
        this.displayName = displayName;
        this.amount = amount;
        this.shortDesc = shortDesc;
        this.units = units;
    }


    public FoodItem(String shortDesc, double amount) {
        this.shortDesc = shortDesc;
        this.amount = amount;
    }

    public String getInv_ID() {
        return Inv_ID;
    }

    public void setInv_ID(String inv_ID) {
        Inv_ID = inv_ID;
    }

    public int getNBD_No() {
        return NBD_No;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setNBD_No(int NBD_No) {
        this.NBD_No = NBD_No;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }
}
