package com.ragentek.service.payservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PushServerSettingReceiver extends BroadcastReceiver {
    private static final String TAG = "PushServerSettingReceiver";

    private static final String DATA_COLLECTION_REG_SETTING_ACTION =
            "android.provider.Telephony.SECRET_CODE";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e(TAG, "onReceive start");
        Log.e(TAG, "action = " + intent.getAction());

        if (intent.getAction().equals(DATA_COLLECTION_REG_SETTING_ACTION)) {
            Intent localIntent = new Intent(context, SettingActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(localIntent);
        }
    }
}

