package com.keepshare.libcurlnetworkdemo.jni;

import java.nio.ByteBuffer;

public interface JNICallback {

    // 具体看哪种数据比较方便
//    void onResponse(String headerData, long receivedHeaderByteCount, String responseData);
    void onResponse(ByteBuffer headerData, long receivedHeaderByteCount, ByteBuffer responseData);

    // originalMethod: curl_easy_strerror(res);
    void onError(int nativeError, int errorString);
}
