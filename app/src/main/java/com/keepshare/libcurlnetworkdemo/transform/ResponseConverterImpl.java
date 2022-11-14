package com.keepshare.libcurlnetworkdemo.transform;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Ascii;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Uninterruptibles;
import com.keepshare.libcurlnetworkdemo.curl.CurlResponseInfo;

import org.chromium.net.UrlResponseInfo;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Okio;
import okio.Source;

public class ResponseConverterImpl implements IResponseConverter {

    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_ENCODING_HEADER_NAME = "Content-Encoding";

    private static final ImmutableSet<String> ENCODINGS_HANDLED_BY_CRONET =
            ImmutableSet.of("br", "deflate", "gzip", "x-gzip");

    private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();


    @Override
    public Response toResponse(Request request, OkHttpBridgeRequestCallback callback) throws IOException {

        Response.Builder responseBuilder = new Response.Builder();

        CurlResponseInfo responseInfo = null;

        @Nullable String contentType = getLastHeaderValue(CONTENT_TYPE_HEADER_NAME, null);

        @Nullable String contentLengthString = null;

        // Theoretically, the content encodings can be scattered across multiple comma separated
        // Content-Encoding headers. This list contains individual encodings.
        List<String> contentEncodingItems = new ArrayList<>();

        for (String contentEncodingHeaderValue :
                getOrDefault(
                        responseInfo.getAllHeaders(),
                        CONTENT_ENCODING_HEADER_NAME,
                        Collections.emptyList())) {
            Iterables.addAll(contentEncodingItems, COMMA_SPLITTER.split(contentEncodingHeaderValue));
        }

        boolean keepEncodingAffectedHeaders =
                contentEncodingItems.isEmpty()
                        || !ENCODINGS_HANDLED_BY_CRONET.containsAll(contentEncodingItems);

        if (keepEncodingAffectedHeaders) {
            contentLengthString = getLastHeaderValue(CONTENT_LENGTH_HEADER_NAME, responseInfo);
        }

        ResponseBody responseBody =
                createResponseBody(
                        request,
                        responseInfo.getHttpStatusCode(),
                        contentType,
                        contentLengthString,
                        null);

        responseBuilder
                .request(request)
                .code(responseInfo.getHttpStatusCode())
                .message(responseInfo.getHttpStatusText())
                .protocol(convertProtocol(responseInfo.getNegotiatedProtocol()))
                .body(responseBody);

        for (Map.Entry<String, String> header : responseInfo.getAllHeadersAsList()) {
            boolean copyHeader = true;
            if (!keepEncodingAffectedHeaders) {
                if (Ascii.equalsIgnoreCase(header.getKey(), CONTENT_LENGTH_HEADER_NAME)
                        || Ascii.equalsIgnoreCase(header.getKey(), CONTENT_ENCODING_HEADER_NAME)) {
                    copyHeader = false;
                }
            }
            if (copyHeader) {
                responseBuilder.addHeader(header.getKey(), header.getValue());
            }
        }

        return responseBuilder.build();
    }




    private static ResponseBody createResponseBody(
            Request request,
            int httpStatusCode,
            @Nullable String contentType,
            @Nullable String contentLengthString,
            Source bodySource)
            throws IOException {

        long contentLength;

        if (request.method().equals("HEAD")) {
            contentLength = 0;
        } else {
            try {
                contentLength = contentLengthString != null ? Long.parseLong(contentLengthString) : -1;
            } catch (NumberFormatException e) {
                contentLength = -1;
            }
        }

        if ((httpStatusCode == 204 || httpStatusCode == 205) && contentLength > 0) {
            throw new ProtocolException(
                    "HTTP " + httpStatusCode + " had non-zero Content-Length: " + contentLengthString);
        }

        return ResponseBody.create(
                contentType != null ? MediaType.parse(contentType) : null,
                contentLength,
                Okio.buffer(bodySource));
    }

    private static Protocol convertProtocol(String negotiatedProtocol) {
        if (negotiatedProtocol.contains("quic")) {
            return Protocol.QUIC;
        } else if (negotiatedProtocol.contains("h3")) {
            return Protocol.QUIC;
        } else if (negotiatedProtocol.contains("spdy")) {
            return Protocol.HTTP_2;
        } else if (negotiatedProtocol.contains("h2")) {
            return Protocol.HTTP_2;
        } else if (negotiatedProtocol.contains("http1.1")) {
            return Protocol.HTTP_1_1;
        }

        return Protocol.HTTP_1_0;
    }

    @Nullable
    private static String getLastHeaderValue(String name, CurlResponseInfo responseInfo) {
        List<String> headers = responseInfo.getAllHeaders().get(name);
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        return Iterables.getLast(headers);
    }

    private static <K, V> V getOrDefault(Map<K, V> map, K key, @NonNull V defaultValue) {
        V value = map.get(key);
        if (value == null) {
            return checkNotNull(defaultValue);
        } else {
            return value;
        }
    }
}
