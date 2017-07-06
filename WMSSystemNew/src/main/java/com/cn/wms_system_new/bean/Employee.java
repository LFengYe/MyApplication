/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.wms_system_new.bean;

/**
 *
 * @author LFeng
 */
public class Employee {

    private static int recordCount;

    public static int getRecordCount() {
        return recordCount;
    }

    public static void setRecordCount(int aRecordCount) {
        recordCount = aRecordCount;
    }
    @FieldDescription(description = "雇员姓名")
    private String employeeName;
    @FieldDescription(description = "雇员登录密码")
    private String employeePassword;
    @FieldDescription(description = "雇员类型代码")
    private int employeeTypeCode;
    @FieldDescription(description = "雇员类型")
    private String employeeType;
    @FieldDescription(description = "雇员手机")
    private String employeePhone;
    @FieldDescription(description = "雇员身份证号")
    private String employeeIdentityCard;
    @FieldDescription(description = "备注")
    private String employeeRemark;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getEmployeePhone() {
        return employeePhone;
    }

    public void setEmployeePhone(String employeePhone) {
        this.employeePhone = employeePhone;
    }

    public String getEmployeeIdentityCard() {
        return employeeIdentityCard;
    }

    public void setEmployeeIdentityCard(String employeeIdentityCard) {
        this.employeeIdentityCard = employeeIdentityCard;
    }

    public String getEmployeeRemark() {
        return employeeRemark;
    }

    public void setEmployeeRemark(String employeeRemark) {
        this.employeeRemark = employeeRemark;
    }

    public int getEmployeeTypeCode() {
        return employeeTypeCode;
    }

    public void setEmployeeTypeCode(int employeeTypeCode) {
        this.employeeTypeCode = employeeTypeCode;
    }

    public String getEmployeePassword() {
        return employeePassword;
    }

    public void setEmployeePassword(String employeePassword) {
        this.employeePassword = employeePassword;
    }
}
