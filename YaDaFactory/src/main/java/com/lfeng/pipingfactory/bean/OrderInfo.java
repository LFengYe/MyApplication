/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lfeng.pipingfactory.bean;

/**
 *
 * @author LFeng
 */
public class OrderInfo {
    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    
    private int orderId;
    private int productLineId;
    private String productCommand;
    private String productName;
    private String productCode;
    private String graphicCode;
    private String customerName;
    private int planNum;
    private String productColor;
    private String planFinishTime;
    private String productUnit;
    private String productStandard;
    private String cardNum;

    public int getProductLineId() {
        return productLineId;
    }

    public void setProductLineId(int productLineId) {
        this.productLineId = productLineId;
    }

    public String getProductCommand() {
        return productCommand;
    }

    public void setProductCommand(String productCommand) {
        this.productCommand = productCommand;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getGraphicCode() {
        return graphicCode;
    }

    public void setGraphicCode(String graphicCode) {
        this.graphicCode = graphicCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getPlanNum() {
        return planNum;
    }

    public void setPlanNum(int planNum) {
        this.planNum = planNum;
    }

    public String getProductColor() {
        return productColor;
    }

    public void setProductColor(String productColor) {
        this.productColor = productColor;
    }

    public String getPlanFinishTime() {
        return planFinishTime;
    }

    public void setPlanFinishTime(String planFinishTime) {
        this.planFinishTime = planFinishTime;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductStandard() {
        return productStandard;
    }

    public void setProductStandard(String productStandard) {
        this.productStandard = productStandard;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
}
