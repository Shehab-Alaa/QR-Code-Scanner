package com.example.dell.qrcodescanner;

import java.io.Serializable;
import java.util.ArrayList;

public class ArrayListHolder implements Serializable {
    private ArrayList<String> items;

    public ArrayListHolder(ArrayList<String> items) {
        this.items = items;
    }

    public ArrayList<String> getItems() {
        return items;
    }
}
