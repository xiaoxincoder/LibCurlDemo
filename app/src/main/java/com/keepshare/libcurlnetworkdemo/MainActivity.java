package com.keepshare.libcurlnetworkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

        findViewById(R.id.btn_request).setOnClickListener(v -> {

            CronetEngine engine = new CronetEngine.Builder(MainActivity.this).build();
            Executor executor = Executors.newSingleThreadExecutor();
            UrlRequest.Builder builder = engine.newUrlRequestBuilder("https://www.baidu.com", new RequestCallback(), executor);
            UrlRequest request = builder.build();

            request.start();
        });
    }


    private static class RequestCallback extends UrlRequest.Callback {

        private static final String TAG = "RequestCallback";

        @Override
        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {
            Log.d(TAG, "onRedirectReceived: ");
        }

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {
            Log.d(TAG, "onResponseStarted: ");
        }

        @Override
        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
            Log.d(TAG, "onReadCompleted: ");
        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            Log.d(TAG, "onSucceeded: ");
        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
            Log.d(TAG, "onFailed: " + error.getMessage());
        }
    }
}