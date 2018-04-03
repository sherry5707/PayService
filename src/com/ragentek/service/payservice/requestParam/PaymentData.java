package com.ragentek.service.payservice.requestParam;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.Serializable;

public class PaymentData implements Serializable {

    public static final String MUSIC_TYPE_MEMBER = "musicMember";
    public static final String MUSIC_TYPE_SONGS = "musicSongs";
    public static final String MUSIC_TYPE_ALBUMS = "musicAlbums";
    public String goodsId; // 商品ID
    public String goodsName; // 商品名称
    public String goodsType; // 商品类型 会员为musicMember 歌曲为musicSongs专辑为 musicAlbums
    public double price; // 单价
    public double discount = 1.0; // 折扣

    // param theme needed
    public int qty = 1; // default goods number

    public PaymentData() {
        this(null);
    }

    // theme
    public PaymentData(String goodsId, String goodsName, double price, int qty, double discount) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.price = price;
        this.qty = qty;
        this.discount = discount;
    }

    // music
    public PaymentData(String goodsId, String goodsName, String goodsType, double price, double discount) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.goodsType = goodsType;
        this.price = price;
        this.discount = discount;
        this.qty = 1;
    }

    public PaymentData(PaymentData paymentData) {
        if (paymentData != null) {
            goodsId = paymentData.goodsId;
            goodsName = paymentData.goodsName;
            goodsType = paymentData.goodsType;
            price = paymentData.price;
            discount = paymentData.discount;
            qty = paymentData.qty;
        }
    }

    public String parseData() {
        try {
            PaymentData createOrderParam = new PaymentData(PaymentData.this);
            return new Gson().toJson(createOrderParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "goodsId," + goodsId
                + ";goodsName," + goodsName
                + ";goodsType," + goodsType
                + ";price," + price
                + ";discount," + discount
                + ";qty," + qty;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    /**
     * price * discount
     *
     * @return
     */
    public double getTotalPrice() {
        return price * qty* discount;
    }

}
