package com.keepshare.libcurlnetworkdemo.transform;

import com.keepshare.libcurlnetworkdemo.curl.CurlRequestBody;

import java.io.IOException;

import okhttp3.RequestBody;

public interface IRequestBodyConverter {

    CurlRequestBody convertRequestBody(RequestBody requestBody) throws IOException;
}
