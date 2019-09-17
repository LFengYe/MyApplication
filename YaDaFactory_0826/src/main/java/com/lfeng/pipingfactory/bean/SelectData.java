package com.lfeng.pipingfactory.bean;

/**
 * Created by gpw on 2016/8/1.
 * --加油
 */
public class SelectData {

    /**
     * status : 0
     * message :
     * data : {"orderId":1,"productLineShort":"高压管","productLineStructJson":"生产线表头json结构","stationStructJson":"下料工位表格json结构"}
     */

    private int status;
    private String message;
    /**
     * orderId : 1
     * productLineShort : 高压管
     * productLineStructJson : 生产线表头json结构
     * stationStructJson : 下料工位表格json结构
     */

    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int orderId;
        private String productLineShort;
        private String productLineStructJson;
        private String stationStructJson;
        private int isGuanshu;//是否为管束

        public int getIsGuanshu() {
            return isGuanshu;
        }

        public void setIsGuanshu(int isGuanshu) {
            this.isGuanshu = isGuanshu;
        }

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
    }
}
