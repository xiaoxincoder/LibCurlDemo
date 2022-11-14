package com.keepshare.libcurlnetworkdemo.curl;

import java.util.List;
import java.util.Map;

public class CurlRequest {

    private Builder builder;

    private CurlRequest(Builder builder) {
        this.builder = builder;
    }


    public void start() {

    }

    public static class Builder {

        private String httpUrl;
        private String httpMethod;
        private CurlRequestBody requestBody;
        private CurlHeaders headers = new CurlHeaders();


        public Builder httpUrl(String httpUrl) {
            this.httpUrl = httpUrl;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder addHeader(String name, String value) {
            this.headers.addHeader(name, value);
            return this;
        }

        public Builder postBody(CurlRequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public CurlRequest build() {
            return new CurlRequest(this);
        }
    }
}
