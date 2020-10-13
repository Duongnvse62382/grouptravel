package com.fpt.gta.webService;

public interface CallBackData <T> {
    void onSuccess(T t);
    void onSuccessString (String mess);
    void onFail(String message);
}
