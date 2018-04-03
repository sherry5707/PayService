
package com.ragentek.service.payservice;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.ragentek.service.payservice.requestParam.CreateOrderParam;
import com.ragentek.service.payservice.requestParam.VerifyOrderParam;
import com.ragentek.service.payservice.utils.AccountManagerUtil;
import com.ragentek.service.payservice.utils.DeviceUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.app.AlertDialog;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.app.ActionBar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.os.StrictMode;

public class MyCashierActivity extends Activity {
    private static final String TAG = "MyCashierActivity";
    private TextView remark;
    private TextView price;
    private TextView order_number;
    private TextView orer_discount;
    private LinearLayout alipayLayout;
    private LinearLayout wxpayLayout;
    private ImageView aliCheckbox;
    private ImageView wxCheckbox;
    private Button pay;
    private CreateOrderParam createOrderParam;
    private String out_trade_no;
    private boolean isAlipay = true;       //mark payType (default:alipay)
    private PayService myService;
    private ConnectionService connectionService = new ConnectionService();
    private WxPayResultReceiver wxPayResultReceiver = new WxPayResultReceiver();
    private boolean isWxInstall = true;
    private TextView wxpayLabel;
    private Looper mHandlerLooper;
    private mHandler myHandler;
    private boolean isBind = false;
    private boolean isRegisterReceiver = false;
    private Toolbar mToolbar;
    private TextView mTitle;
    private String ipAddress;
    private boolean canPay = false;
    private static final int GET_ACCOUNT_REQUEST_CODE = 2;
    private boolean hasPermission;
    private String broadcast_action;

    public static final String WXPAY_RESULT_ACTION = "com.ragentek.payservice.wxpay.result";

    private final class mHandler extends Handler {
        public mHandler(Looper looper) {
            super(looper);
        }

        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    Log.i(TAG, "resultStatus:" + resultStatus);
                    if (TextUtils.equals(resultStatus, "9000")) {
                        String resultInfo = payResult.getResult();// Synchronize to return information that needs to be verified
                        out_trade_no = AnalyzeResult(resultInfo);
                        // Try to call three times
                        final String account = AccountManagerUtil.getAccount(MyCashierActivity.this);
                        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(out_trade_no)) {
                            success();
                        }

                        final VerifyOrderParam verifyOrderParam = new VerifyOrderParam(account, out_trade_no);
                        myService.verifyPaymentResult(verifyOrderParam, new PayService.VerifyCallback() {

                            @Override
                            public void solve(String result) {
                                if ("paid".equals(result)) {
                                    success();
                                } else {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    myService.verifyPaymentResult(verifyOrderParam,
                                            new PayService.VerifyCallback() {

                                                @Override
                                                public void solve(String result) {
                                                    if ("paid".equals(result)) {
                                                        success();
                                                    } else {
                                                        try {
                                                            Thread.sleep(3000);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }

                                                        myService.verifyPaymentResult(verifyOrderParam,
                                                                new PayService.VerifyCallback() {

                                                                    @Override
                                                                    public void solve(String result) {
                                                                        success();

                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        });

                    } else {
                        // resultStatus is not "9000" represents a possible payment failure
                        // "8000" results represents the reasons for payment channels or system reasons leads to still waiting for payment results.To confirm the final transaction is successful is based to the server asynchronous notification prevailing (small probability state)
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(MyCashierActivity.this, R.string.pay_result_confirming, Toast.LENGTH_SHORT).show();

                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            Toast.makeText(MyCashierActivity.this, R.string.pay_cancel, Toast.LENGTH_SHORT).show();
                            if (!TextUtils.isEmpty(broadcast_action)) {
                                Intent intent = new Intent(broadcast_action);
                                intent.putExtra("result", false);
                                intent.putExtra("from", createOrderParam.getChannel());
                                sendBroadcast(intent);
                            }
                        } else {
                            // Other values ​​can be judged as a failure to pay including the user to cancel the payment or the system returns the error
                            Toast.makeText(MyCashierActivity.this, R.string.pay_fail, Toast.LENGTH_SHORT).show();
                            if (!TextUtils.isEmpty(broadcast_action)) {
                                Intent intent = new Intent(broadcast_action);
                                intent.putExtra("result", false);
                                intent.putExtra("from", createOrderParam.getChannel());
                                sendBroadcast(intent);
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_my_cashier);

        String jsonData = getIntent().getStringExtra("json_data");
        broadcast_action = getIntent().getStringExtra("action");
        if (TextUtils.isEmpty(jsonData)) {
            finish();
            return;
        }

        createOrderParam = new Gson().fromJson(jsonData, CreateOrderParam.class);
        checkCreateOrderParam(createOrderParam);

        initView();

        initHandlerThread();

        bindService();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WXPAY_RESULT_ACTION);
        registerReceiver(wxPayResultReceiver, intentFilter);
        isRegisterReceiver = true;
    }

    private void checkCreateOrderParam(CreateOrderParam param) {
        String account = AccountManagerUtil.getAccount(getApplicationContext());
        if (TextUtils.isEmpty(account)) {
            Intent intent = new Intent(this, AlertDialogActivity.class);
            startActivity(intent);
        }

        param.setUserId(account);
        param.setDeviceId(DeviceUtil.getIMEI(getApplicationContext()));
        param.setOrderAmount();

//        // test order amount
//        DecimalFormat decimalFormat = new DecimalFormat(".000");
//        int x = (int) (param.getOrderAmount());
//        double amount = 0;
//        try {
//            amount = Double.parseDouble(decimalFormat.format((double) x / 1000));
//        } catch (NumberFormatException e) {
//            amount = 0;
//            e.printStackTrace();
//        }
//        param.setOrderAmount(amount);
    }

    public void initHandlerThread() {
        //init handlerThread
        HandlerThread thread = new HandlerThread("MyCashierClient");
        thread.start();
        mHandlerLooper = thread.getLooper();
        myHandler = new mHandler(mHandlerLooper);
    }

    @Override
    public void onBackPressed() {
        setResult(2);
        finish();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.title_activity_cashier);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });
        setActionBar(mToolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        remark = (TextView) findViewById(R.id.order_remark);
        price = (TextView) findViewById(R.id.price);
        order_number = (TextView) findViewById(R.id.order_number);
        orer_discount = (TextView) findViewById(R.id.order_discount);
        alipayLayout = (LinearLayout) findViewById(R.id.alipay);
        wxpayLayout = (LinearLayout) findViewById(R.id.wxpay);
        aliCheckbox = (ImageView) findViewById(R.id.alicheckbox);
        wxCheckbox = (ImageView) findViewById(R.id.wxcheckbox);
        pay = (Button) findViewById(R.id.pay);
        wxpayLabel = (TextView) findViewById(R.id.wxtextlabel);
        if (!isWeixinAvilible(this)) {
            wxpayLabel.setText(R.string.wx_uninstalled);
            isWxInstall = false;
        }

        if (createOrderParam == null) {
            Log.e(TAG, "initView get null order data");
        } else {
            order_number.setText(String.valueOf(createOrderParam.getOrderNumber()));
            if (createOrderParam.getDiscount() == 1.0 || createOrderParam.getDiscount() == 0) {
                orer_discount.setText(R.string.none);
            } else {
                orer_discount.setText(String.valueOf(createOrderParam.getDiscount()));
            }

            remark.setText(createOrderParam.getRemark());
            price.setText(String.valueOf(createOrderParam.getOrderAmount()));
            alipayLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!isAlipay) {
                        isAlipay = true;
                        aliCheckbox.setImageResource(R.drawable.pay_selection_checkbox);
                        wxCheckbox.setImageResource(R.drawable.pay_selection_checkbox2);
                    }
                }
            });
            wxpayLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isAlipay) {
                        isAlipay = false;
                        wxCheckbox.setImageResource(R.drawable.pay_selection_checkbox);
                        aliCheckbox.setImageResource(R.drawable.pay_selection_checkbox2);
                    }
                }
            });
            pay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    checkPermission();
                    if (!hasPermission) {
                        return;
                    }
                    if (isAlipay) {
                        canPay = true;
                        createOrderParam.setPayType("alipay");
                    } else {
                        createOrderParam.setPayType("wxpay");
                        if (!isWxInstall) {
                            canPay = false;
                            AlertDialog.Builder dlg = new AlertDialog.Builder(MyCashierActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                            dlg.setTitle(R.string.app_tip);
                            dlg.setMessage(getResources().getString(R.string.wx_uninstalled2));
                            dlg.setPositiveButton(R.string.sure, null);
                            dlg.show();
                        } else {
                            canPay = true;
                        }
                    }

                    if (canPay) {
                        //getIp
                        myService.getIPFromServer(new PayService.GetIpCallback() {
                            @Override
                            public void solve(String result) {
                                Log.i(TAG, "getIP result:" + result);
                                if (!TextUtils.isEmpty(result) || ipAddress != null) {
                                    //pay
                                    if (!TextUtils.isEmpty(result)) {
                                        ipAddress = result;
                                    }

                                    createOrderParam.setIpAddress(ipAddress);
                                    myService.pushPaymentData(createOrderParam, new PayService.AliCallback() {

                                        @Override
                                        public void solve(final String alireq) {
                                            if (!TextUtils.isEmpty(alireq)) {
                                                Runnable payRunnable = new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        // Construct the PayTask object
                                                        PayTask alipay = new PayTask(MyCashierActivity.this);
                                                        // Call the payment interface to get the payment result
                                                        String result = alipay.pay(alireq, true);

                                                        Message msg = new Message();
                                                        msg.what = 1;
                                                        msg.obj = result;
                                                        myHandler.sendMessage(msg);
                                                    }
                                                };

                                                // Must be invoked asynchronously
                                                Thread payThread = new Thread(payRunnable);
                                                payThread.start();
                                            } else {
                                                Log.e(TAG, "get pay params is null");
                                                Toast.makeText(MyCashierActivity.this, R.string.pay_error, Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    });
                                } else {
                                    Toast.makeText(MyCashierActivity.this, R.string.pay_error, Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "ipAdress is null");
                                }
                            }
                        });
                    }

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wxpayLabel != null) {
            if (isWeixinAvilible(this)) {
                wxpayLabel.setText(R.string.wxlabel);
                isWxInstall = true;
            } else {
                wxpayLabel.setText(R.string.wx_uninstalled);
                isWxInstall = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind) {
            unBindService();
            isBind = false;
        }
        if (isRegisterReceiver) {
            unregisterReceiver(wxPayResultReceiver);
        }
    }

    //analyze aliy Synchronize
    public String AnalyzeResult(String result) {
        String[] s1 = result.split("&");
        if (s1 == null || s1.length < 5) {
            return null;
        }
        String s2 = s1[4];    //out_trade_no
        String[] s3 = s2.split("\"");
        if (s3 == null || s3.length < 2) {
            return null;
        }
        String out_trade_no = s3[1];
        if ("out_trade_no=".equals(s3[0])) {
            Log.i("AnalyzeResult", "out_trade_no:" + out_trade_no);
            return out_trade_no;
        }
        return "";
    }

    private void bindService() {
        Intent intent = new Intent(this, PayService.class);
        isBind = bindService(intent, connectionService, Context.BIND_AUTO_CREATE);
    }

    private void unBindService() {
        unbindService(connectionService);
    }

    class ConnectionService implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ((PayService.MyBinder) service).getPayService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }

    }

    public void success() {
        Toast.makeText(this, R.string.pay_success,
                Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(broadcast_action)) {
            Intent intent = new Intent(broadcast_action);
            intent.putExtra("result", true);
            intent.putExtra("from", createOrderParam.getChannel());
            sendBroadcast(intent);
        } 

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pay.setEnabled(false);
                pay.setText(R.string.pay_success);
            }
        });
        finish();
    }

    public class WxPayResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean result = intent.getBooleanExtra("result", false);
            Log.i(TAG, "getReceiver from wx,result:" + result);
            if (!TextUtils.isEmpty(broadcast_action)) {
                Intent wxintent = new Intent(broadcast_action);
                wxintent.putExtra("result", result);
                wxintent.putExtra("from", createOrderParam.getChannel());
                sendBroadcast(intent);
                finish();
            }
        }

    }

    public static boolean isWeixinAvilible(Context context) {

        final PackageManager packageManager = context.getPackageManager();// Get packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// Gets package information for all installed programs
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if ("com.tencent.mm".equals(pn)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void checkPermission() {
        //checkoutPermission
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("NewPushService", "read account no permission!");
            //ask for permission if not remember,return false;
            if (ActivityCompat.shouldShowRequestPermissionRationale(MyCashierActivity.this,
                    Manifest.permission.GET_ACCOUNTS)) {
                ActivityCompat.requestPermissions(MyCashierActivity.this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        GET_ACCOUNT_REQUEST_CODE);
            } else {//remember the deny
                showDialog();
            }

        } else {
            hasPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GET_ACCOUNT_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
                break;
            }
        }
    }

    private void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyCashierActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.setTitle(R.string.permission_setting);
        dialog.setMessage(R.string.permission_msg);
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        dialog.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent localIntent = new Intent();
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(localIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).show();
    }
}
