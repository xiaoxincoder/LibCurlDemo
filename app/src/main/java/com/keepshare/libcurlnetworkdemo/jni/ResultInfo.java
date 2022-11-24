package com.keepshare.libcurlnetworkdemo.jni;

import java.nio.ByteBuffer;

public class ResultInfo {

    public int nativeError;
    public int errorString;
    public long receivedHeaderByteCount;
    public ByteBuffer headerData;
    public ByteBuffer responseData;
//    public String headerData;
//    public String responseData;
}
