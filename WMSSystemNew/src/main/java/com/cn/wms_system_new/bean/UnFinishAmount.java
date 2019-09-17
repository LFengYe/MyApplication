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
public class UnFinishAmount {
    @FieldDescription(description = "一工厂备货")
    private int bh1;
    @FieldDescription(description = "二工厂备货")
    private int bh2;
    @FieldDescription(description = "三工厂备货")
    private int bh3;
    @FieldDescription(description = "一工厂领货")
    private int lh1;
    @FieldDescription(description = "二工厂领货")
    private int lh2;
    @FieldDescription(description = "三工厂领货")
    private int lh3;
    @FieldDescription(description = "一工厂上线")
    private int sx1;
    @FieldDescription(description = "二工厂上线")
    private int sx2;
    @FieldDescription(description = "三工厂上线")
    private int sx3;
    @FieldDescription(description = "报检信息")
    private int bj;
    @FieldDescription(description = "非生产领料")
    private int fjh;
    @FieldDescription(description = "临时调货")
    private int lsdh;
    @FieldDescription(description = "部品退货")
    private int th;
    @FieldDescription(description = "终端退库")
    private int tk;
    @FieldDescription(description = "返修入库")
    private int fxIn;
    @FieldDescription(description = "返修出库")
    private int fxOut;

    public int getBh1() {
        return bh1;
    }

    public void setBh1(int bh1) {
        this.bh1 = bh1;
    }

    public int getBh2() {
        return bh2;
    }

    public void setBh2(int bh2) {
        this.bh2 = bh2;
    }

    public int getBh3() {
        return bh3;
    }

    public void setBh3(int bh3) {
        this.bh3 = bh3;
    }

    public int getLh1() {
        return lh1;
    }

    public void setLh1(int lh1) {
        this.lh1 = lh1;
    }

    public int getLh2() {
        return lh2;
    }

    public void setLh2(int lh2) {
        this.lh2 = lh2;
    }

    public int getLh3() {
        return lh3;
    }

    public void setLh3(int lh3) {
        this.lh3 = lh3;
    }

    public int getSx1() {
        return sx1;
    }

    public void setSx1(int sx1) {
        this.sx1 = sx1;
    }

    public int getSx2() {
        return sx2;
    }

    public void setSx2(int sx2) {
        this.sx2 = sx2;
    }

    public int getSx3() {
        return sx3;
    }

    public void setSx3(int sx3) {
        this.sx3 = sx3;
    }

    public int getBj() {
        return bj;
    }

    public void setBj(int bj) {
        this.bj = bj;
    }

    public int getFjh() {
        return fjh;
    }

    public void setFjh(int fjh) {
        this.fjh = fjh;
    }

    public int getTh() {
        return th;
    }

    public void setTh(int th) {
        this.th = th;
    }

    public int getTk() {
        return tk;
    }

    public void setTk(int tk) {
        this.tk = tk;
    }

    public int getFxIn() {
        return fxIn;
    }

    public void setFxIn(int fxIn) {
        this.fxIn = fxIn;
    }

    public int getFxOut() {
        return fxOut;
    }

    public void setFxOut(int fxOut) {
        this.fxOut = fxOut;
    }

    public int getLsdh() {
        return lsdh;
    }

    public void setLsdh(int lsdh) {
        this.lsdh = lsdh;
    }
}
