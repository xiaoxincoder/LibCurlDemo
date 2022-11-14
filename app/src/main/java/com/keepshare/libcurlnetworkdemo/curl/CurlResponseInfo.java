package com.keepshare.libcurlnetworkdemo.curl;

import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class CurlResponseInfo {

    private Headers headers;

    public Map<String, List<String>> getAllHeaders() {
        return null;
    }

    public List<Map.Entry<String, String>> getAllHeadersAsList() {
        return null;
    }

    public int getHttpStatusCode() {
        return 200;
    }

    public String getHttpStatusText() {
        return null;
    }

    public String getNegotiatedProtocol() {
        return "HTTP/1.1";
    }
}
