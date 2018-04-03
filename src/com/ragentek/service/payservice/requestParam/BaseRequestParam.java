package com.ragentek.service.payservice.requestParam;

import com.loopj.android.http.RequestParams;

/**
 * Created by JasWorkSpace on 15/10/16.
 */
public abstract class BaseRequestParam {
    public abstract boolean checkValid();

    public abstract String getRequestParam();
}
