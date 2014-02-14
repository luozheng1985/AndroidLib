package com.androidlib.httpUtils;

public interface HttpProgressCallBack extends HttpResponseCallBack{
    
    public void onLoading(int progress);
    
}
