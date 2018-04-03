package com.ragentek.service.payservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by JasWorkSpace on 15/10/14.
 */
public class Config {
    private static final String TAG = "Config";

    /////////////////////////////////////////////////
    private final static boolean DEBUG = false;
    //域名
    private final static String SERVICE_URL = "https://apiv2.kindui.com/gateway/call";
    private final static String SERVICE_URL_TEST = "http://test.apiv2.kindui.com/gateway/call";//"http://gateway.dcv2.kindui.com/gateway/call";//"http://test.kindui.com/gateway/call";
    //channel
    private final static String CHANNEL = "myui_userservice";//"myui_account";
    private final static String CHANNELKEY = "81x2b22A5I80e84a1U6d9f301ec6Y512";//"IGSNVI8upAPnQBCIZhVFTaNmzUKTLzYC";
    //version
    private final static String VERSION = "2.0";

    public static String getVersion(String method) {
        return VERSION;
    }

    //add by sherry for ip
    private final static String METHOD_GETIPADDRESS = "common.net.ip.getExternalIp";

    public static String getIPAdress() {
        return METHOD_GETIPADDRESS;
    }
    //end by sherry

    private final static String METHOD_PAY = "common.order.create";
    private final static String METHOD_PAY_VERIFY = "common.order.notifyPaid";
    public static String WX_APP_ID = "wxde7ccc4e9cb9067a";
    //ip
    public static final String[] IP_ADDRS = {
            "http://www.qingcheng.com/plugin/getip",
            "http://ip.taobao.com/service/getIpInfo2.php?ip=myip",
            "http://city.ip138.com/ip2city.asp"};
    public static final String IP_PATTERN = "\\b([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}\\b";

    // add by sherry for pay
    public static String getMethodPay() {
        return METHOD_PAY;
    }

    public static String getMethodPayVerify() {
        return METHOD_PAY_VERIFY;
    }
    //end by sherry

    public static String getServiceUrl() {
        boolean isBusiServer = getBusinessServerSetting(MyApplication.getInstance());
        return isBusiServer ? SERVICE_URL : SERVICE_URL_TEST;
    }

    public static String getNormalServiceUrl() {
        return SERVICE_URL;
    }

    public static String getChannel() {
        return CHANNEL;
    }

    public static String getChannelkey() {
        return CHANNELKEY;
    }

    public static boolean hasNetWork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable());
    }

    //md5加密
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString().toUpperCase();//service use uppercase. so change it.
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        } catch (Throwable e) {
            throw new RuntimeException("Huh, Throwable?", e);
        }
    }

    public static boolean getBusinessServerSetting(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean result = sp.getBoolean("pref_key_set_business_server", true);
        Log.i(TAG, "result:" + result);
        return result;
    }
}
