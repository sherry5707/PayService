package com.ragentek.service.payservice.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

/**
 * Created by RGK on 2017/10/27.
 */

public class AccountManagerUtil {

    private static final String TAG = AccountManagerUtil.class.getSimpleName();
    public static final String ACCOUNT_TYPE = "com.greenorange.android.myuiaccount";
    public static final String ACTION_ADDACCOUNT = "com.greenorange.music.addmyuiaccount";
    public static final String ACTION_USERINFO = "com.greenorange.music.myuiaccountinfo";

    private static AccountManagerUtil instance;

    private AccountManagerUtil() {
    }

    public static AccountManagerUtil getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (AccountManagerUtil.class) {
                instance = new AccountManagerUtil();
            }
            return instance;
        }
    }

    public static String getAccount(Context context) {
        String accountName = null;
        if (checkAccountPermission(context)) {
            final Account[] accounts = AccountManager.get(context).getAccountsByType(ACCOUNT_TYPE);
            if (accounts != null && accounts.length > 0) {
                accountName = accounts[0].name;
            }
        }
        return accountName;
    }

    public static boolean hasAccount(Context context) {
        String accountName = null;
        if (checkAccountPermission(context)) {
            final Account[] accounts = AccountManager.get(context).getAccountsByType(ACCOUNT_TYPE);
            if (accounts != null && accounts.length > 0) {
                accountName = accounts[0].name;
            }
        }
        return !TextUtils.isEmpty(accountName);
    }

    public static boolean checkAccountPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public static Intent createLoginAccountIntent(){
        return new Intent(ACTION_ADDACCOUNT);
    }

    public static Intent createUserInfoIntent(){
        Intent intent = new Intent(ACTION_USERINFO);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return intent;
    }

}
