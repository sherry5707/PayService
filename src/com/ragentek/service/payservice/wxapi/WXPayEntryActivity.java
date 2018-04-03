package com.ragentek.service.payservice.wxapi;

import com.ragentek.service.payservice.Config;
import com.ragentek.service.payservice.MyCashierActivity;
import com.ragentek.service.payservice.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;
    private TextView payresult;
    Handler mHandler = new Handler();
    private String result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Config.WX_APP_ID);
        api.handleIntent(getIntent(), this);
        payresult = (TextView) findViewById(R.id.payresult);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int code = resp.errCode;
            switch (code) {
                case 0:
                    Toast.makeText(this, R.string.pay_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MyCashierActivity.WXPAY_RESULT_ACTION);
                    intent.putExtra("result", true);
                    sendBroadcast(intent);
                    finish();
                    break;
                case -1:
                    Toast.makeText(this, R.string.pay_fail, Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(MyCashierActivity.WXPAY_RESULT_ACTION);
                    intent3.putExtra("result", false);
                    sendBroadcast(intent3);
                    finish();
                    break;
                case -2:
                    Toast.makeText(this, R.string.pay_cancel, Toast.LENGTH_SHORT).show();
                    Intent intent4 = new Intent(MyCashierActivity.WXPAY_RESULT_ACTION);
                    intent4.putExtra("result", false);
                    sendBroadcast(intent4);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}