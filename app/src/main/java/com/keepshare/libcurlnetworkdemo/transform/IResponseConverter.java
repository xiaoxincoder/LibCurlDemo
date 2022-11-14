package com.keepshare.libcurlnetworkdemo.transform;


import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public interface IResponseConverter {

    Response toResponse(Request request, OkHttpBridgeRequestCallback callback) throws IOException;
}
