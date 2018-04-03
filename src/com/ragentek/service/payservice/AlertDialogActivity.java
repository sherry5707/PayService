package com.ragentek.service.payservice;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sherry on 17-4-13.
 */

public class AlertDialogActivity extends Activity {
    private static final String TAG = "AlertDialogActivity";
    private TextView goLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertdlg);
        goLogin = (TextView) findViewById(R.id.go_login);
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.greenorange.android.addmyuiaccount");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getAccount())) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public String getAccount() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("NewPushService", "read account no permission!");
            return null;
        }
        final Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType("com.greenorange.android.myuiaccount");
        if(accounts!=null&&accounts.length>0){
            String account_name = accounts[0].name;
            return account_name;
        }
        Log.e(TAG, "account is null");
        return null;
    }
}
