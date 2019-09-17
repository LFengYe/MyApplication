package com.lfeng.pipingfactory.bean;

/**
 * Created by gpw on 2016/8/1.
 * --加油
 */
public class XlData {

    /**
     * orderId : 1
     * productLineShort : 高压管
     * productLineStructJson : 生产线表头json结构
     * stationStructJson : 下料工位表格json结构
     */

    private int orderId;
    private String productLineShort;
    private String productLineStructJson;
    private String stationStructJson;
    private int isGuanShu;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getProductLineShort() {
        return productLineShort;
    }

    public void setProductLineShort(String productLineShort) {
        this.productLineShort = productLineShort;
    }

    public String getProductLineStructJson() {
        return productLineStructJson;
    }

    public void setProductLineStructJson(String productLineStructJson) {
        this.productLineStructJson = productLineStructJson;
    }

    public String getStationStructJson() {
        return stationStructJson;
    }

    public void setStationStructJson(String stationStructJson) {
        this.stationStructJson = stationStructJson;
    }

    public int getIsGuanShu() {
        return isGuanShu;
    }

    public void setIsGuanShu(int isGuanShu) {
        this.isGuanShu = isGuanShu;
    }
}
