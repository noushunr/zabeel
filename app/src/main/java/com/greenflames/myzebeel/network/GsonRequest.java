package com.greenflames.myzebeel.network;

/*
 *Created by Adithya T Raj on 26-04-2021
 */

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private Response.Listener<T> listener;
    private final String mRequestBody;

    protected static final String PROTOCOL_CHARSET = "utf-8";

    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    public GsonRequest(int method, String url, Class<T> clazz, String requestBody, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.mRequestBody = requestBody;
    }

    /*public GsonRequest(int method, String url, Class<T> clazz, String requestBody, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.mRequestBody = requestBody;
    }*/

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            Log.d("GsonRequest", "parseNetworkResponse: ");
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                String json = new String(
                        response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                Class<T>  objClazz = (Class<T>) Object.class;
                return Response.success(
                        gson.fromJson(json, objClazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e1) {
                return Response.error(new ParseError(e));
            }
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        listener = null;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}
