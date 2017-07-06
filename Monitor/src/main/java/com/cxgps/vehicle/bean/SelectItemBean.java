package com.cxgps.vehicle.bean;

import java.io.Serializable;

/**
 * Created by taosong on 16/12/27.
 */

public class SelectItemBean implements Serializable {

   private String itemKey;

    private String itemName;


    public SelectItemBean(String k, String n){

        this.itemKey = k;

        this.itemName = n;

    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
