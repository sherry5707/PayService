package com.ragentek.service.payservice.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by RGK on 2017/9/26.
 */
public class DeviceUtil {
    private static String mClientVersion;

    public static String getClientVersion(Context context) {
        if (TextUtils.isEmpty(mClientVersion)) {
            try {
                PackageInfo PkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                return PkgInfo.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "unknown";
    }

    private static String mImei;

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (!TextUtils.isEmpty(imei)) {
            return mImei = imei;
        }
        return TextUtils.isEmpty(mImei) ? "" : mImei;
    }

    private static String mIp;

    public static String getIp() {
        return IPUtil.getIpFromNetWork();
    }
}
