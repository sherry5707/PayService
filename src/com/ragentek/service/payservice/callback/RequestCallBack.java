package com.ragentek.service.payservice.callback;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ragentek.service.payservice.response.BaseResponse;

public class RequestCallBack implements IRequestCallBack {
    private static final String TAG = "RequestCallBack";
    private Gson gson = new Gson();
    private String response = "";
    private final static boolean DEBUG = false;

    @Override
    public void onStart() {
        if (DEBUG) Log.d(TAG, "onStart");
    }

    @Override
    public void onSuccess(String s) {
        if (DEBUG) Log.d(TAG, "onSuccess-->" + s);
        response = s;
    }

    @Override
    public void onFailure(Throwable throwable, String s) {
        throwable.printStackTrace();
        Log.i(TAG, "RequestCallBack fail-->" + throwable.toString());
        if (DEBUG) Log.d(TAG, "onFailure -->" + s);
    }

    @Override
    public void onFinish() {
        if (DEBUG) Log.d(TAG, "onFinish");
    }

    public String getResponse() {
        return response;
    }

    public BaseResponse getV2BaseResponse() {
        try {
            if (!TextUtils.isEmpty(getResponse())) {
                return gson.fromJson(getResponse(), BaseResponse.class);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Log.d(TAG, "getV2BaseResponse fail-->" + e.toString());
        }
        return null;
    }

}
