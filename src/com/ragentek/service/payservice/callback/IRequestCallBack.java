package com.ragentek.service.payservice.callback;

/**
 * Created by JasWorkSpace on 15/10/29.
 */
public interface IRequestCallBack {
    public void onStart();

    public void onSuccess(String s);

    public void onFailure(Throwable throwable, String s);

    public void onFinish();
}
