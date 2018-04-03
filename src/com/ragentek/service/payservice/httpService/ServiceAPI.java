package com.ragentek.service.payservice.httpService;

import com.loopj.android.http.RequestParams;
import com.ragentek.service.payservice.Config;
import com.ragentek.service.payservice.callback.IRequestCallBack;
import com.ragentek.service.payservice.requestParam.BaseRequestParam;
import com.ragentek.service.payservice.utils.LogUtil;

import android.text.TextUtils;

/**
 * Created by JasWorkSpace on 15/10/15.
 */
public class ServiceAPI {
    private static final String TAG = "ServiceAPI";

    public static boolean API_MYUI_Request_Async(RequestParams requestParams, IRequestCallBack callBack) throws Exception {
        return RequestHelper.Request_Async(Config.getServiceUrl(), requestParams, callBack);
    }
    public static boolean API_MYUI_Request_normal_Async(RequestParams requestParams, IRequestCallBack callBack) throws Exception {
        return RequestHelper.Request_Async(Config.getNormalServiceUrl(), requestParams, callBack);
    }

    public static RequestParams API_MYUI_getParam(String method, BaseRequestParam param) {
        try {
            if (param.checkValid()) {
                return ServiceHelper.getServiceRequestParams(method, param);
            } else {
                LogUtil.e("API_MYUI__getParam param is invalid!!! " + param.toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.e( "RequestParams API_MYUI_getParam fail-->" + e.toString());
        }
        return null;
    }

    public static RequestParams API_MYUI_getParam(String method) {
        try {
            return ServiceHelper.getServiceRequestParams(method);
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.e("RequestParams API_MYUI_getParam fail-->" + e.toString());
        }
        return null;
    }

    //add by sherry for payment
    public static RequestParams API_MYUI_getPaymentRequestParams(String data) {
        try {
            if (!TextUtils.isEmpty(data)) {
                return ServiceHelper.getPaymentRequestParams(Config.getMethodPay(), data);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.d("API_MYUI_getPayRequestParams fail" + e.toString());
        }
        return null;
    }

    //add by sherry for ip
    public static RequestParams API_MYUI_getIPRequestParams() {
        try {
            return ServiceHelper.getIPParams(Config.getIPAdress());
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.d("API_MYUI_getIPParams fail" + e.toString());
        }
        return null;
    }

    public static RequestParams API_MYUI_getOrderListParams(String data) {
        try {
            if (!TextUtils.isEmpty(data)) {
                return ServiceHelper.getPaymentRequestParams("common.order.list", data);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.d("API_MYUI_getOrderListParams fail" + e.toString());
        }
        return null;
    }

    public static RequestParams API_MYUI_getVerifyPaymentRequestParams(String data) {
        try {
            if (!TextUtils.isEmpty(data)) {
                return ServiceHelper.getPaymentRequestParams(Config.getMethodPayVerify(), data);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.d( "API_MYUI_getPayRequestParams fail" + e.toString());
        }
        return null;
    }
    //end by sherry
}
