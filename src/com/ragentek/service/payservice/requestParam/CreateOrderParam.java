package com.ragentek.service.payservice.requestParam;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by RGK on 17/11/21.
 */
public class CreateOrderParam extends BaseRequestParam {

    // request param
    public String userId; // 用户Id
    public double orderAmount; // 订单金额 0.01
    public double discount = 1.0; // 折扣 1
    public String payType; // 支付类型 alipay wxpay
    public String channel = "myui_music"; // 提交订单的客户端 myui_music
    public String ipAddress; // Ip地址 158.236.15.5
    public String remark = ""; // 备注 音乐会员购买
    public List<PaymentData> items; // 商品明细
    public String notifyUrl = ""; // 客户端通知Url music.notify.payforVip 注：只有在购买会员时才需该参数
    public String deviceId; // imei

    public CreateOrderParam() {
        this(null);
    }

    public CreateOrderParam(CreateOrderParam createOrderParam) {
        if (createOrderParam != null) {
            userId = createOrderParam.userId;
            orderAmount = createOrderParam.orderAmount;
            discount = createOrderParam.discount;
            payType = createOrderParam.payType;
            channel = createOrderParam.channel;
            ipAddress = createOrderParam.ipAddress;
            remark = createOrderParam.remark;
            items = createOrderParam.items;
            notifyUrl = createOrderParam.notifyUrl;
            deviceId = createOrderParam.deviceId;
        }
    }

    public CreateOrderParam(String channel,
                            double discount,
                            String remark,
                            List<PaymentData> items,
                            String notifyUrl) {
        this.channel = channel;
        this.discount = discount;
        this.remark = remark;
        this.items = items;
        this.notifyUrl = notifyUrl;
    }

    @Override
    public boolean checkValid() {
        // TODO
        return true;
    }

    @Override
    public String getRequestParam() {
        try {
            CreateOrderParam createOrderParam = new CreateOrderParam(CreateOrderParam.this);
            return new Gson().toJson(createOrderParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CreateOrderParam {")
                .append("userId=" + userId)
                .append(", orderAmount=" + orderAmount)
                .append(", discount=" + discount)
                .append(", payType=" + payType)
                .append(", channel=" + channel)
                .append(", ipAddress=" + ipAddress)
                .append(", remark=" + remark)
                .append(", items=" + items)
                .append(", notifyUrl=" + notifyUrl)
                .append(", deviceId=" + deviceId)
                .append("}");
        return sb.toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public double setOrderAmount() {
        if (orderAmount == 0 && items != null && items.size() > 0) {
            for (PaymentData data : items) {
                orderAmount += data.getTotalPrice();
            }
            orderAmount = orderAmount * discount;
        }
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        int x = (int) (orderAmount * 100);
        double result = 0;
        try {
            result = Double.parseDouble(decimalFormat.format((double) x / 100));
        } catch (NumberFormatException e) {
            result = 0;
            e.printStackTrace();
        }
        return result;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<PaymentData> getItems() {
        return items;
    }

    public void setItems(List<PaymentData> items) {
        this.items = items;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getOrderNumber() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }
}
