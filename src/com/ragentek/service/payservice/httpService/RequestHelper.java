package com.ragentek.service.payservice.httpService;

import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.ragentek.service.payservice.callback.IRequestCallBack;
import com.ragentek.service.payservice.utils.LogUtil;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JasWorkSpace on 15/11/2.
 */
public class RequestHelper {
    private static final String TAG = "RequestHelper";
    private static AsyncHttpClient client = new AsyncHttpClient();

    //add by zhengguang.yang@20160920 start for socket timeout exception
    static {
        client.setTimeout(30000);
        client.setConnectTimeout(30000);
        client.setResponseTimeout(30000);
    }
    //add by zhengguang.yang end
    public static boolean Request_Sync(String url, RequestParams requestParams, final IRequestCallBack callBack) throws Exception {
        return Request_Async(new SyncHttpClient(), url, requestParams, callBack);
    }

    public static boolean Request_Async(String url, RequestParams requestParams, final IRequestCallBack callBack) throws Exception {
        return Request_Async(client, url, requestParams, callBack);
    }

    public static boolean Request_Async(AsyncHttpClient asyncHttpClient, String url, RequestParams baseRequestParam, final IRequestCallBack callBack) throws Exception {
        try {
            if (TextUtils.isEmpty(url)) {
                throw new Exception("invalid url !!!");
            }
            LogUtil.i("RequestHelper", "url:" + url);
            LogUtil.d(TAG, "Request_Async-->" + url + (baseRequestParam == null ? "" : ("?" + baseRequestParam.toString())));
            String requestUrl = url + (baseRequestParam == null ? "" : ("?" + baseRequestParam.toString()));

            asyncHttpClient.post(url, baseRequestParam, new AsyncHttpResponseHandler() {
                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                      Throwable throwable) {
                    if (arg2 != null) {
                        String s = new String(arg2);
                        if (callBack != null) {
                            callBack.onFailure(throwable, s);
                        }
                    }
                }

                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                    String s = new String(arg2);
                    if (callBack != null) {
                        callBack.onSuccess(s);
                    }
                }
                //modified by zhengguang.yang 2016.02.18 end.

                @Override
                public void onStart() {
                    super.onStart();
                    if (callBack != null) {
                        callBack.onStart();
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (callBack != null) {
                        callBack.onFinish();
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.e(TAG, "RequestHelper  Request_Async fail -->" + e.toString());
        }
        throw new Exception("unknow fail");
    }

}
