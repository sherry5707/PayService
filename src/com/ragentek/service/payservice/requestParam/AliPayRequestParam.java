package com.ragentek.service.payservice.requestParam;

public class AliPayRequestParam {
    private String partner;
    private String sign;
    private String signType;
    private String orderNo;
    private String userId;
    private String notifyUrl;
    private String sellerId;
    private String returnUrl;

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }


    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    @Override
    public String toString() {
        return "AliPayRequestParam [partner=" + partner + ", sign=" + sign + ", signType="
                + signType + ", orderNo=" + orderNo + ", userId=" + userId + ", notifyUrl="
                + notifyUrl + ", sellerId=" + sellerId + ", returnUrl=" + returnUrl + "]";
    }


}
