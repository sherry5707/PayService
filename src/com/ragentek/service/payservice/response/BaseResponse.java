package com.ragentek.service.payservice.response;

import android.text.TextUtils;
import android.util.Log;

import com.ragentek.service.payservice.httpService.ServiceHelper;

import java.io.UnsupportedEncodingException;

public class BaseResponse {
    public String gwResult = "";
    public String gwMessage = "";
    public String businessResult = "";
    public String businessData = "";

    public boolean checkValid() {
        if (TextUtils.equals("success", gwResult)
                && TextUtils.equals("success", businessResult)) {
            return true;
        }
        return false;
    }

    public String getFailMessage() {
        if (!TextUtils.equals("success", gwResult)) {
            return gwMessage;
        }
        if (!TextUtils.equals("success", businessResult)) {
            return businessData;
        }
        return "Response unknow fail";
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("BaseResponse{")
                .append("gwResult=" + gwResult)
                .append(", gwMessage=" + gwMessage)
                .append(", businessResult=" + businessResult)
                .append(", businessData=" + getDecodeBusinessData())
                .append("}");
        return sb.toString();
    }

    public String getDecodeBusinessData() {
        try {
            if (TextUtils.isEmpty(businessData)) return "";
            return ServiceHelper.getDecodeParam(businessData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("DecodeBusData fail-->", e.toString());
        }
        return businessData;
    }
}
