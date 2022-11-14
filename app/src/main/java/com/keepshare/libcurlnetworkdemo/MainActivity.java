package com.keepshare.libcurlnetworkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CronetEngine engine = new CronetEngine.Builder(this).build();

        Executor executor = Executors.newSingleThreadExecutor();

        UrlRequest.Builder builder = engine.newUrlRequestBuilder("http://www.baidu.com", new RequestCallback(), executor);
        UrlRequest request = builder.build();

        request.start();
    }


    private static class RequestCallback extends UrlRequest.Callback {

        @Override
        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {

        }

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {

        }

        @Override
        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {

        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {

        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {

        }
    }
}