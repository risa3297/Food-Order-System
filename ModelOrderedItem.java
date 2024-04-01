package com.example.foodorder;

public class ModelOrderedItem {

    private String pID, name, price, quantity;

    public ModelOrderedItem() {
    }

    public ModelOrderedItem(String pID, String name, String price, String quantity) {
        this.pID = pID;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

