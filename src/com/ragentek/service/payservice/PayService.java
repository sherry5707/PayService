package com.ragentek.service.payservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.ragentek.service.payservice.callback.RequestCallBack;
import com.ragentek.service.payservice.requestParam.AliPayRequestParam;
import com.ragentek.service.payservice.requestParam.ByteOrStringHelper;
import com.ragentek.service.payservice.requestParam.CreateOrderParam;
import com.ragentek.service.payservice.requestParam.VerifyOrderParam;
import com.ragentek.service.payservice.requestParam.WXPayRequestParam;
import com.ragentek.service.payservice.httpService.ServiceAPI;
import com.ragentek.service.payservice.response.BaseResponse;
import com.ragentek.service.payservice.utils.IPUtil;
import com.ragentek.service.payservice.utils.NetworkUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.support.v4.app.ActivityCompat;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.widget.Toast;

/**
 * Created by dell on 2017/4/24.
 */

public class PayService extends Service {
    private static final String TAG = "PayService";
    private final static int MSG_DO_PAYMENT = 1;
    private final static int MSG_DO_GETIPADDRESS = 2;
    private IWXAPI api;
    private PayReq req;
    private WXPayRequestParam wxPayReq;
    private AliPayRequestParam alidata;
    private String IpAddress;

    @Override
    public void onCreate() {
        super.onCreate();
        api = WXAPIFactory.createWXAPI(PayService.this, Config.WX_APP_ID, false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public void pushPaymentData(final CreateOrderParam param, final AliCallback callback) {
        try {
            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                callback.solve(null);
                return;
            }

            boolean ret = ServiceAPI.API_MYUI_Request_Async(
                    ServiceAPI.API_MYUI_getParam(Config.getMethodPay(), param)
                    , new RequestCallBack() {
                        @Override
                        public void onFinish() {
                            try {
                                Log.i(TAG, "Payment response-->" + getResponse());
                                if (!TextUtils.isEmpty(getResponse())) {
                                    BaseResponse baseResponse = getV2BaseResponse();
                                    if (baseResponse != null && baseResponse.checkValid()) {
                                        String data = baseResponse.businessData;

                                        req = new PayReq();
                                        Gson gson = new Gson();
                                        wxPayReq = gson.fromJson(data, WXPayRequestParam.class);

                                        if (wxPayReq != null) {
                                            if (!TextUtils.isEmpty(wxPayReq.getPrepayId())) { // Indicates that this is the parameter of the WeChat payment
                                                req.appId = wxPayReq.getAppId();
                                                req.partnerId = wxPayReq.getPartnerId();
                                                req.prepayId = wxPayReq.getPrepayId();
                                                req.packageValue = wxPayReq.getPkg();
                                                req.nonceStr = wxPayReq.getNonceStr();
                                                req.timeStamp = wxPayReq.getTimestamp();
                                                req.sign = wxPayReq.getSign();

                                                // Send a payment request
                                                sendPayReq();

                                            } else {
                                                alidata = gson.fromJson(data,
                                                        AliPayRequestParam.class);

                                                String req = KV2String(alidata, param);
                                                callback.solve(req);
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                                Log.e(TAG, "pushpayment fail 1-->" + e.toString());
                                callback.solve(null);
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable, String s) {
                            Log.e(TAG, "paymentdata post failure");
                            callback.solve(null);
                        }
                    });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.i(TAG, "pushpayment fail 2-->" + e.toString());
            callback.solve(null);
        }

    }

    public interface AliCallback {
        public void solve(String result);
    }

    //Generate timestamps
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /*Send a micro credit payment request*/
    private void sendPayReq() {
        boolean b1 = api.registerApp(wxPayReq.getAppId());
        boolean b2 = api.sendReq(req);
        Log.i("registerApp", String.valueOf(b1));
        Log.i("sendReq", String.valueOf(b2));

        //GetPayResult();
    }


    //ali

    /**
     * Generate Alipay request parameters
     */
    private String KV2String(AliPayRequestParam data, CreateOrderParam param) {

        String orderInfo = "";
        try {
            String sign = URLEncoder.encode(data.getSign(), "utf-8");
            // Parameter encoding fixed value(√)
            orderInfo = "_input_charset=\"utf-8\"";
            // product details
            orderInfo += "&body=" + "\"" + param.getRemark() + "\"";
            // Set the timeout for unpaid transactions
            // default 30 minutes once the timeout the transaction will be automatically closed
            // Range 1m ~ 15d.
            //  m-minute h-hour d-day 1c-day (no matter when the transaction was created all closed at 0).
            //The parameter value does not accept decimal points such as 1.5h can be converted to 90m
            orderInfo += "&it_b_pay=\"30m\"";

            // The server informs the page path asynchronously（√)
            orderInfo += "&notify_url=" + "\"" + data.getNotifyUrl() + "\"";

            // Merchant site unique order number（√）
            orderInfo += "&out_trade_no=" + "\"" + data.getOrderNo() + "\"";
            // Signed Partner ID(√)
            orderInfo += "&partner=" + "\"" + data.getPartner() + "\"";
            // Payment type fixed value（√）
            orderInfo += "&payment_type=\"1\"";
            // Alipay processing request the current page jump to the designated page of the business path can be empty
            // orderInfo += "&return_url=\"m.alipay.com\"";
            orderInfo += "&return_url=" + "\"" + data.getReturnUrl() + "\"";
            // Sign the seller Alipay account（√）
            orderInfo += "&seller_id=" + "\"" + data.getSellerId() + "\"";
            // Service interface name fixed value(√)
            orderInfo += "&service=\"mobile.securitypay.pay\"";
            // product details（√）
            orderInfo += "&subject=" + "\"" + param.getRemark() + "\"";
            // The amount of goods (√) is accurate to two decimal places
            orderInfo += "&total_fee=" + "\"" + param.getOrderAmount() + "\"";
            // signature
            orderInfo += "&sign=" + "\"" + data.getSign() + "\"";

            // Signature type
            orderInfo += "&sign_type=" + "\"" + data.getSignType() + "\"";

            // Call bank card payment need to configure this parameter participate in the signature fixed value (need to sign "wireless bank card payment" to use)
            // orderInfo += "&paymethod=\"expressGateway\"";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderInfo;
    }

    //verify payment from server
    public String getPushVerifyData(String userId, String orderNo) {

        Map map = new HashMap();
        map.put("userId", userId);
        map.put("orderNo", orderNo);

        JSONObject cmdtextjs = new JSONObject(map);
        String registerstr = cmdtextjs.toString();
        byte[] cInfoRegister = ByteOrStringHelper.StringToByte(registerstr);
        String data = new String(cInfoRegister);
        Log.i(TAG, "222 data:" + data);
        return data;
    }

    /**
     * verify payment result from server
     */
    public void verifyPaymentResult(String userId, String orderNo, final VerifyCallback callback) {
        try {
            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                callback.solve(null);
                return;
            }

            String data = getPushVerifyData(userId, orderNo);
            boolean ret = ServiceAPI.API_MYUI_Request_Async(
                    ServiceAPI.API_MYUI_getVerifyPaymentRequestParams(data)
                    , new RequestCallBack() {
                        @Override
                        public void onFinish() {
                            try {
                                Log.i(TAG, "pushVerify response-->" + getResponse());
                                if (!TextUtils.isEmpty(getResponse())) {
                                    BaseResponse baseResponse = getV2BaseResponse();
                                    if (baseResponse != null && baseResponse.checkValid()) {
                                        String data = baseResponse.getDecodeBusinessData();
                                        Log.i(TAG, "pushVerify baseResponse-->data=" + data);
                                        callback.solve(data);
                                    }
                                } else {
                                    Log.e(TAG, "Get the parameter is empty");
                                    callback.solve(null);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                                Log.e(TAG, "pushVerify fail-->" + e.toString());
                            }
                        }
                    });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "pushVerify fail -->" + e.toString());
        }

    }

    public void verifyPaymentResult(VerifyOrderParam param, final VerifyCallback callback) {
        try {
            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                callback.solve(null);
                return;
            }

            boolean ret = ServiceAPI.API_MYUI_Request_Async(
                    ServiceAPI.API_MYUI_getParam(Config.getMethodPayVerify(), param)
                    , new RequestCallBack() {
                        @Override
                        public void onFinish() {
                            try {
                                Log.i(TAG, "pushVerify response-->" + getResponse());
                                if (!TextUtils.isEmpty(getResponse())) {
                                    BaseResponse baseResponse = getV2BaseResponse();
                                    if (baseResponse != null && baseResponse.checkValid()) {
                                        String data = baseResponse.getDecodeBusinessData();
                                        Log.i(TAG, "pushVerify baseResponse-->data=" + data);
                                        callback.solve(data);
                                    }
                                } else {
                                    Log.e(TAG, "Get the parameter is empty");
                                    callback.solve(null);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                                Log.e(TAG, "pushVerify fail-->" + e.toString());
                            }
                        }
                    });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "pushVerify fail -->" + e.toString());
        }
    }

    public void getIPFromServer(final GetIpCallback callback) {
        try {
            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                callback.solve(null);
                return;
            }

            boolean ret = ServiceAPI.API_MYUI_Request_Async(
                    ServiceAPI.API_MYUI_getParam(Config.getIPAdress())
                    , new RequestCallBack() {
                        @Override
                        public void onFinish() {
                            try {
                                Log.i(TAG, "getIp response-->" + getResponse());
                                if (!TextUtils.isEmpty(getResponse())) {
                                    BaseResponse baseResponse = getV2BaseResponse();
                                    if (baseResponse != null && baseResponse.checkValid()) {
                                        String data = baseResponse.getDecodeBusinessData();
                                        IpAddress = data;
                                        callback.solve(data);
                                    }
                                } else {
                                    IpAddress = getIpAddress();
                                    if (!TextUtils.isEmpty(IpAddress)) {
                                        callback.solve(IpAddress);
                                    } else {
                                        callback.solve(null);
                                        Log.e(TAG, "get ip from server is null");
                                    }
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                                Log.e(TAG, "getIP from server fail 1-->" + e.toString());
                                IpAddress = getIpAddress();
                                if (!TextUtils.isEmpty(IpAddress)) {
                                    callback.solve(IpAddress);
                                } else {
                                    callback.solve(null);
                                    Toast.makeText(PayService.this, R.string.connect_time_out, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "getIP from server fail 2 -->" + e.toString());
            IpAddress = getIpAddress();

            if (!TextUtils.isEmpty(IpAddress)) {
                callback.solve(IpAddress);
            } else {
                callback.solve(null);
            }
        }
    }

    public class MyBinder extends Binder {
        public PayService getPayService() {
            return PayService.this;
        }
    }

    public interface VerifyCallback {
        public void solve(String result);
    }

    public interface GetIpCallback {
        public void solve(String result);
    }

    private synchronized String getIpAddress() {
        if (TextUtils.isEmpty(IpAddress)) {
            IpAddress = IPUtil.getIpFromNetWork();
        }
        return IpAddress;
    }
}
