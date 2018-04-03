package com.ragentek.service.payservice.requestParam;

import com.google.gson.Gson;

/**
 * Created by RGK on 17/10/25.
 */
public class VerifyOrderParam extends BaseRequestParam {

    public String userId; // 用户Id
    public String orderNo; // 订单编号

    public VerifyOrderParam() {
        this(null);
    }

    public VerifyOrderParam(VerifyOrderParam verifyOrderParam) {
        if (verifyOrderParam != null) {
            userId = verifyOrderParam.userId;
            orderNo = verifyOrderParam.orderNo;
        }
    }

    public VerifyOrderParam(String userId, String orderNo) {
        this.userId = userId;
        this.orderNo = orderNo;
    }

    @Override
    public boolean checkValid() {
        // TODO
        return true;
    }

    @Override
    public String getRequestParam() {
        try {
            VerifyOrderParam verifyOrderParam = new VerifyOrderParam(VerifyOrderParam.this);
            return new Gson().toJson(verifyOrderParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("VerifyOrderParam {")
                .append("userId = " + userId)
                .append(", orderNo = " + orderNo)
                .append("}");
        return sb.toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
