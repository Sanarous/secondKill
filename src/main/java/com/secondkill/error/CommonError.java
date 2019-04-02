package com.secondkill.error;

public interface CommonError {
    int getErrorCode();

    String getErrMsg();

    CommonError setErrMsg(String errMsg);
}
