package com.keepshare.libcurlnetworkdemo.transform;

import com.keepshare.libcurlnetworkdemo.curl.CurlRequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import okhttp3.RequestBody;
import okio.Buffer;

public class RequestBodyConverterImpl implements IRequestBodyConverter {

    private final StreamingRequestBodyConverter converter;

    public RequestBodyConverterImpl(StreamingRequestBodyConverter converter) {
        this.converter = converter;
    }

    @Override
    public CurlRequestBody convertRequestBody(RequestBody requestBody) throws IOException {
        return converter.convertRequestBody(requestBody);
    }


    static RequestBodyConverterImpl create(ExecutorService bodyReaderExecutor) {
        return new RequestBodyConverterImpl(new RequestBodyConverterImpl.StreamingRequestBodyConverter());
    }


    static final class StreamingRequestBodyConverter implements IRequestBodyConverter {

        @Override
        public CurlRequestBody convertRequestBody(RequestBody requestBody) throws IOException {
            String params = parseParams(requestBody);
            requestBody.contentType();
            JSONObject jsonObject;
            try {
                if (requestBody.contentLength() == -1) {
                    jsonObject = new JSONObject();
                } else {
                    jsonObject = new JSONObject(params);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 解析post请求的参数
         * @param body
         * @return
         */
        private String parseParams(RequestBody body) {
            Buffer buffer = new Buffer();
            try {
                body.writeTo(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer.readUtf8();
        }
    }
}
