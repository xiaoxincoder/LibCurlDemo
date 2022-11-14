package com.keepshare.libcurlnetworkdemo.transform;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.util.Log;

import com.google.net.cronet.okhttptransport.CronetInterceptor;
import com.keepshare.libcurlnetworkdemo.curl.CurlNetEngine;
import com.keepshare.libcurlnetworkdemo.curl.CurlRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.CheckForNull;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealInterceptorChain;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;


public class CurlInterceptor implements Interceptor {

    private static final String TAG = "CurlInterceptor";
    private static final int CANCELLATION_CHECK_INTERVAL_MILLIS = 500;
    private final RequestResponseConverter converter;
    private final Map<Call, CurlRequest> activeCalls = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1);

    private CurlInterceptor(RequestResponseConverter converter) {
        this.converter = checkNotNull(converter);
        ScheduledFuture<?> unusedFuture =
                scheduledExecutor.scheduleAtFixedRate(
                        () -> {
                            Iterator<Map.Entry<Call, CurlRequest>> activeCallsIterator =
                                    activeCalls.entrySet().iterator();

                            while (activeCallsIterator.hasNext()) {
                                try {
                                    Map.Entry<Call, CurlRequest> activeCall = activeCallsIterator.next();
                                    if (activeCall.getKey().isCanceled()) {
                                        activeCallsIterator.remove();
//                                        activeCall.getValue().cancel();
                                    }
                                } catch (RuntimeException e) {
                                    Log.w(TAG, "Unable to propagate cancellation status", e);
                                }
                            }
                        },
                        CANCELLATION_CHECK_INTERVAL_MILLIS,
                        CANCELLATION_CHECK_INTERVAL_MILLIS,
                        MILLISECONDS);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        if (chain.call().isCanceled()) {
            throw new IOException("Canceled");
        }
        Request request = chain.request();

        RequestResponseConverter.CurlRequestAndOkHttpResponse requestAndOkHttpResponse =
                converter.convert(request, chain.readTimeoutMillis(), chain.writeTimeoutMillis());

        activeCalls.put(chain.call(), requestAndOkHttpResponse.getRequest());

        try {
            requestAndOkHttpResponse.getRequest().start();
            return toInterceptorResponse(requestAndOkHttpResponse.getResponse(), chain.call());
        } catch (RuntimeException | IOException e) {
            activeCalls.remove(chain.call());
            throw e;
        }
    }

    public static CurlInterceptor.Builder newBuilder(CurlNetEngine curlNetEngine) {
        return new CurlInterceptor.Builder(curlNetEngine);
    }

    public static final class Builder
            extends RequestResponseConverterBasedBuilder<CurlInterceptor.Builder, CurlInterceptor> {

        Builder(CurlNetEngine curlNetEngine) {
            super(curlNetEngine, CurlInterceptor.Builder.class);
        }

        @Override
        public CurlInterceptor build(RequestResponseConverter converter) {
            return new CurlInterceptor(converter);
        }
    }

    private Response toInterceptorResponse(Response response, Call call) {
        checkNotNull(response.body());

        if (response.body() instanceof CurlInterceptor.CurlInterceptorResponseBody) {
            return response;
        }

        return response
                .newBuilder()
                .body(new CurlInterceptorResponseBody(response.body(), call))
                .build();
    }

    private class CurlInterceptorResponseBody extends CurlConvertResponseBody {
        private final Call call;

        private CurlInterceptorResponseBody(ResponseBody delegate, Call call) {
            super(delegate);
            this.call = call;
        }

        @Override
        void customCloseHook() {
            activeCalls.remove(call);
        }
    }
}
