package com.lfeng.pipingfactory.bean;

/**
 * Created by gpw on 2016/7/27.
 * --加油
 */
public class RowData {
    private String fieldName;
    private String fieldValue;
    private int fieldType;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public String toString() {
        return "{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldValue='" + fieldValue + '\'' +
                ", fieldType=" + fieldType +
                '}';
    }
}
