package com.ragentek.service.payservice.httpService;

import android.text.TextUtils;

import com.loopj.android.http.RequestParams;
import com.ragentek.service.payservice.Config;
import com.ragentek.service.payservice.requestParam.BaseRequestParam;
import com.ragentek.service.payservice.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ServiceHelper {
    private static final String TAG = "ServiceHelper";

    public static final String ENCODING = "utf-8";

    public static String getEncodeParam(String param) throws UnsupportedEncodingException {
        return URLEncoder.encode(param, ENCODING);
    }

    public static String getDecodeParam(String param) throws UnsupportedEncodingException {
        return URLDecoder.decode(param, ENCODING);
    }

    //add by sherry for ip
    public static String getToken(String method, String version, String param, String timestamp) throws Exception {
        return Config.md5(Config.getChannel() + method + version + param + timestamp + Config.getChannelkey());
    }

    public static RequestParams getServiceRequestParams(String method, BaseRequestParam baseRequestParam) throws Exception {
        if (baseRequestParam.checkValid()) {
            return getServiceRequestParams(method, baseRequestParam.getRequestParam());
        }
        throw new Exception("invalid param");
    }

    public static RequestParams getServiceRequestParams(String method, String param) throws Exception {
        if (TextUtils.isEmpty(method)) throw new Exception("invalid method");
        if (TextUtils.isEmpty(param)) throw new Exception("invalid param");
        LogUtil.d("getServiceRequestParams param = " + param);
        String encodeparam = param;
        try {
            encodeparam = getEncodeParam(encodeparam);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("invalid param");
        }
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String version = Config.getVersion(method);
            RequestParams requestParams = new RequestParams();
            requestParams.put("channel", Config.getChannel());
            requestParams.put("method", method);
            requestParams.put("params", encodeparam);
            requestParams.put("version", version);
            requestParams.put("timestamp", timestamp);
            requestParams.put("token", getToken(method, version, encodeparam, timestamp));
            return requestParams;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("unknown exception");
        }
    }

    public static RequestParams getServiceRequestParams(String method) throws Exception {
        if (TextUtils.isEmpty(method)) throw new Exception("invalid method");
        String encodeparam = "{}";
        try {
            encodeparam = getEncodeParam(encodeparam);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("invalid param");
        }
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String version = Config.getVersion(method);
            RequestParams requestParams = new RequestParams();
            requestParams.put("channel", Config.getChannel());
            requestParams.put("method", method);
            requestParams.put("params", encodeparam);
            requestParams.put("version", version);
            requestParams.put("timestamp", timestamp);
            requestParams.put("token", getToken(method, version, encodeparam, timestamp));
            return requestParams;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("unknown exception");
        }
    }

    //add by sherry  for pay
    public static RequestParams getPaymentRequestParams(String method, String param) throws Exception {
        try {
            return getPaymentsRequestParams(method, param);
        } catch (Throwable e) {
        }
        throw new Exception("invalid param");
    }

    //add by sherry for ip
    public static RequestParams getIPParams(String method) throws Exception {
        try {
            return getIPRequestParams(method);
        } catch (Throwable e) {
        }
        throw new Exception("invalid param");
    }

    //add by sheryy for pay
    private static RequestParams getPaymentsRequestParams(String method, String param) throws Exception {
        if (TextUtils.isEmpty(method)) throw new Exception("invalid method");
        if (TextUtils.isEmpty(param)) throw new Exception("invalid param");
        String encodeparam = param;
        try {
            encodeparam = getEncodeParam(encodeparam);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("invalid param");
        }
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String version = Config.getVersion(method);
            RequestParams requestParams = new RequestParams();
            requestParams.put("channel", Config.getChannel());
            requestParams.put("method", method);
            requestParams.put("version", version);
            requestParams.put("params", encodeparam);
            requestParams.put("timestamp", timestamp);
            requestParams.put("token", getToken(method, version, encodeparam, timestamp));
            return requestParams;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("unkonw exception");
        }
    }

    //add by sheryy for ip
    private static RequestParams getIPRequestParams(String method) throws Exception {
        if (TextUtils.isEmpty(method)) throw new Exception("invalid method");
        String encodeparam = "{}";
        try {
            encodeparam = getEncodeParam(encodeparam);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("invalid param");
        }
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String version = Config.getVersion(method);
            RequestParams requestParams = new RequestParams();
            requestParams.put("channel", Config.getChannel());
            requestParams.put("method", method);
            requestParams.put("version", version);
            requestParams.put("params", encodeparam);
            requestParams.put("timestamp", timestamp);
            requestParams.put("token", getToken(method, version, encodeparam, timestamp));
            return requestParams;
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.e("getIPParams", "exception:" + e.getMessage());
            throw new Exception("unkonw exception");
        }
    }
}
