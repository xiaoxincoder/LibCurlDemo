package com.keepshare.libcurlnetworkdemo.jni;

import java.util.Map;

public class JNI {

    // originalMethod: curl_global_init(CURL_GLOBAL_ALL);
    public static native void initGlobal();

    // originalMethod: curl_easy_init();
    public static native long init();

    // originalMethod: curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "POST")
    public static native void setMethod(long nativePtr, String method);

    // originalMethod: curl_easy_setopt(curl, CURLOPT_URL, "")
    public static native void setRequestUrl(long nativePtr, String url);

    // originalMethod: 	curl_easy_setopt(curl, CURLOPT_TIMEOUT_MS, 10);
    public static native void setTimeout(long nativePtr, long timeout);


    // originalMethod: 	curl_easy_setopt(curl, CURLOPT_CONNECTTIMEOUT_MS, 10);
    public static native void setConnectTimeout(long nativePtr, long timeout);

    // originalMethod: 	curl_easy_setopt(curl, CURLOPT_HEADER, 1);
    public static native void setBodyWithHeaders(long nativePtr, boolean showHeaders);

    // originalName: curl_slist_append(headers, "Content-Type: application/json;charset=utf-8")
    //               curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
    public static native void addHeader(long nativePtr, String key, String value);
    public static native void addHeaders(long nativePtr, Map<String, String> headers);

    // originalName: curl_easy_setopt(curl, CURLOPT_POSTFIELDS, body.c_str());
    //		         curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, body.size());
    public static native void setRequestBody(long nativePtr, String json);

    // originalName: curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);
    public static native void followLocation(long nativePtr, boolean followLocation);

    // originalName: curl_easy_setopt(curl, CURLOPT_ACCEPT_ENCODING, "gzip");
    public static native void acceptEncoding(long nativePtr, String encoding);

    // originalName: curl_easy_perform(curl);
    public static native int request(long nativePtr, JNICallback callback);

    public static native ResultInfo request(long nativePtr);

    // originalName: curl_easy_setopt(curl, CURLOPT_VERBOSE, 1);
    public static native int debug(long nativePtr, boolean debug);

    // originalName: curl_easy_cleanup(curl);
    public static native void clean(long nativePtr);
}
