package com.keepshare.libcurlnetworkdemo.transform;

import androidx.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

abstract class CurlConvertResponseBody extends ResponseBody {

    private ResponseBody delegate;

    public CurlConvertResponseBody(ResponseBody delegate) {
        this.delegate = delegate;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return this.delegate.contentType();
    }

    @Override
    public long contentLength() {
        return this.delegate.contentLength();
    }

    @Override
    public BufferedSource source() {
        return this.delegate.source();
    }

    abstract void customCloseHook();
}
