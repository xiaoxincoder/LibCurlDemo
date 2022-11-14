package com.keepshare.libcurlnetworkdemo.transform;

import com.keepshare.libcurlnetworkdemo.curl.CurlNetEngine;
import com.keepshare.libcurlnetworkdemo.curl.CurlRequest;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestResponseConverter {

    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";

    private final CurlNetEngine netEngine;
    private final IResponseConverter responseConverter;
    private final IRequestBodyConverter requestBodyConverter;

    public RequestResponseConverter(CurlNetEngine engine, IResponseConverter responseConverter, IRequestBodyConverter requestBodyConverter) {
        this.netEngine = engine;
        this.responseConverter = responseConverter;
        this.requestBodyConverter = requestBodyConverter;
    }


    RequestResponseConverter.CurlRequestAndOkHttpResponse convert(
            Request okHttpRequest, int readTimeoutMillis, int writeTimeoutMillis) throws IOException {

        OkHttpBridgeRequestCallback callback =
                new OkHttpBridgeRequestCallback();

        // The OkHttp request callback methods are lightweight, the heavy lifting is done by OkHttp /
        // app owned threads. Use a direct executor to avoid extra thread hops.
//        UrlRequest.Builder builder =
//                cronetEngine
//                        .newUrlRequestBuilder(
//                                okHttpRequest.url().toString(), callback, MoreExecutors.directExecutor())
//                        .allowDirectExecutor();

        CurlRequest.Builder builder = new CurlRequest.Builder()
                .httpMethod(okHttpRequest.method())
                .httpUrl(okHttpRequest.url().toString());

        for (int i = 0; i < okHttpRequest.headers().size(); i++) {
            builder.addHeader(okHttpRequest.headers().name(i), okHttpRequest.headers().value(i));
        }

        RequestBody body = okHttpRequest.body();

        if (body != null) {
            if (okHttpRequest.header(CONTENT_LENGTH_HEADER_NAME) == null && body.contentLength() != -1) {
                builder.addHeader(CONTENT_LENGTH_HEADER_NAME, String.valueOf(body.contentLength()));
            }

            if (body.contentLength() != 0) {
                if (okHttpRequest.header(CONTENT_TYPE_HEADER_NAME) == null && body.contentType() != null) {
                    builder.addHeader(CONTENT_TYPE_HEADER_NAME, body.contentType().toString());
                }
                // 设置post请求body
                builder.postBody(requestBodyConverter.convertRequestBody(body));
//                builder.setUploadDataProvider(
//                        requestBodyConverter.convertRequestBody(body, writeTimeoutMillis),
//                        uploadDataProviderExecutor);
            }
        }

        return new RequestResponseConverter.CurlRequestAndOkHttpResponse(
                builder.build(), createResponseSupplier(okHttpRequest, callback));
    }


    static final class CurlRequestAndOkHttpResponse {

        private CurlRequest request;
        private ResponseSupplier responseSupplier;

        public CurlRequestAndOkHttpResponse(CurlRequest request, ResponseSupplier responseSupplier) {
            this.request = request;
            this.responseSupplier = responseSupplier;
        }

        public CurlRequest getRequest() {
            return request;
        }

        public Response getResponse() throws IOException{
            return this.responseSupplier.getResponse();
        }

        public ResponseSupplier getResponseSupplier() throws IOException{
            return responseSupplier;
        }
    }

    private RequestResponseConverter.ResponseSupplier createResponseSupplier(
            Request request, OkHttpBridgeRequestCallback callback) {
        return new RequestResponseConverter.ResponseSupplier() {
            @Override
            public Response getResponse() throws IOException {
                return responseConverter.toResponse(request, callback);
            }

        };
    }

    private interface ResponseSupplier {
        Response getResponse() throws IOException;

    }

}
