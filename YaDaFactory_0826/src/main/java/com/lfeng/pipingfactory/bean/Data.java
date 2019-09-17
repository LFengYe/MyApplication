package com.lfeng.pipingfactory.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/20.
 */
public class Data {
    private String fieldName;
    private String fieldValue;
    private int childRowNum;
    private int viewType;
    private int fieldType;
    private int width;
    private boolean textHorizon;
    private ArrayList<ArrayList<Data>> rowData =new ArrayList<ArrayList<Data>>();


    public Data(String fieldName, String fieldValue, int childRowNum, boolean textHorizon, int width, ArrayList<ArrayList<Data>> rowData){
        this.fieldName= fieldName;
        this.fieldValue = fieldValue;
        this.childRowNum =childRowNum;
        this.width = width;
        this.textHorizon = textHorizon;

        this.rowData.addAll(rowData);
    }

    public Data(String fieldName, String fieldValue,int viewType,int fieldType, int childRowNum, boolean textHorizon, int width, ArrayList<ArrayList<Data>> rowData ){
        this.fieldName= fieldName;
        this.fieldValue = fieldValue;
        this.viewType = viewType;
        this.fieldType = fieldType;
        this.childRowNum =childRowNum;
        this.textHorizon = textHorizon;
        this.width = width;

        this.rowData.addAll(rowData);

    }

    public Data(String fieldName, String fieldValue, int viewType,int fieldType,int childRowNum, boolean textHorizon, int width){
        this.fieldName= fieldName;
        this.fieldValue = fieldValue;
        this.viewType = viewType;
        this.fieldType = fieldType;
        this.childRowNum =childRowNum;
        this.textHorizon = textHorizon;
        this.width = width;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isTextHorizon() {
        return textHorizon;
    }

    public void setTextHorizon(boolean textHorizon) {
        this.textHorizon = textHorizon;
    }

    public ArrayList<ArrayList<Data>> getRowData() {
        return rowData;
    }

    public void setRowData(ArrayList<ArrayList<Data>> rowData) {
        this.rowData = rowData;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewfieldType(int viewfieldType) {
        this.viewType = viewfieldType;
    }

    public int getChildRowNum() {
        return childRowNum;
    }

    public void setChildRowNum(int childRowNum) {
        this.childRowNum = childRowNum;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public String toString() {
        return "Data{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldValue='" + fieldValue + '\'' +
                ", childRowNum=" + childRowNum +
                ", viewType=" + viewType +
                ", fieldType=" + fieldType +
                ", width=" + width +
                ", textHorizon=" + textHorizon +
                ", rowData=" + rowData +
                '}';
    }
}


