package com.androidlib.httpUtils;

public class HttpControl {

    public static final String task_cancel = "cancel task";
    
    private boolean cancel;

    public HttpControl(boolean cancel) {
        super();
        this.cancel = cancel;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
    
}
