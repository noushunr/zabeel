package com.greenflames.myzebeel.network;

/*
 *Created by Adithya T Raj on 26-04-2021
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.greenflames.myzebeel.MainApplication;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.greenflames.myzebeel.network.Apis.ACCESS_TOKEN;

@SuppressWarnings("ALL")
public class NetworkManager {
    public static final String TAG = NetworkManager.class.getSimpleName();

    public static final int SUCCESS = 0;
    public static final int EXCEPTION = -1;
    public static final int ERROR = -2;
    private static final int MY_SOCKET_TIMEOUT_MS = 20 * 1000;

    private Context mContext;

    private final Gson gson = new Gson();

    public NetworkManager(Context context){
        mContext = context;
    }

    public void doGet(final String getParam , final String url, final String accessToken, final String requestTag, final int requestId, final NetworkCallbacks responseCallback){

        String mUrl = url;

        if (getParam != null){
            mUrl = url+"?"+getParam;

        }

        Log.d(TAG, "doGetUrl: "+mUrl);

        StringRequest postRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "doGetResponse: "+response);
                responseCallback.onResponse(SUCCESS,response,requestId);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "doGetError: "+error.getMessage());
                responseCallback.onResponse(ERROR,error.getMessage(),requestId);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers =  new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+ accessToken);
                return headers;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MainApplication.getInstance().addToRequestQueue(postRequest, requestTag);


    }

    public void doPost(final Map<String,String> postParam , final String url, final String requestTag, final int requestId, final NetworkCallbacks responseCallback){

        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                responseCallback.onResponse(SUCCESS,response, requestId);
                //     Log.d(TAG, "doPostResponse: "+response);
                Log.v("??_n_callback", "doPostResponse: "+url+" , "+requestTag+" , "+requestId+" , "+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseCallback.onResponse(ERROR,error.getMessage(),requestId);
                Log.d(TAG, "doPostError: "+error.getMessage());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> stringMap = postParam;
                return stringMap;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        postRequest.setShouldCache(false);
        MainApplication.getInstance().addToRequestQueue(postRequest, requestTag);
    }

    public void doJSONPost(
            final JSONObject jsonObject,
            final String url,
            final String requestTag,
            final int requestId,
            final NetworkCallbacks responseCallback
    ){

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//
                        responseCallback.onJsonResponse(SUCCESS,response.toString(),requestId);
                        Log.d("doJSONPostResponse",response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            if((statusCode.equals("200") || statusCode.equals("400") || statusCode.equals("401") || statusCode.equals("404")) && error.networkResponse.data!=null) {
                                try {
                                    //String body = new String(error.networkResponse.data,"UTF-8");
                                    String body = new String(
                                            error.networkResponse.data,
                                            HttpHeaderParser.parseCharset(error.networkResponse.headers));
                                    String json = gson.toJson(gson.fromJson(body, Object.class));
                                    responseCallback.onJsonResponse(SUCCESS, json, requestId);
                                    Log.d("doJSONPostResponse",json.toString());
                                } catch (Exception e) {
                                    responseCallback.onJsonResponse(ERROR,error.toString(),requestId);
                                    e.printStackTrace();
                                }
                            } else {
                                responseCallback.onJsonResponse(ERROR,error.toString(),requestId);
                                Log.e(TAG, "doJSONPostError: "+error.getMessage());
                                error.printStackTrace();
                            }
                        } catch (Exception e) {
                            responseCallback.onJsonResponse(ERROR,error.toString(),requestId);
                            Log.e(TAG, "doJSONPostError: "+e.getMessage());
                            e.printStackTrace();
                        }
                    }

                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers =  new HashMap<String, String>();
                String accesstoken = "Bearer " + ACCESS_TOKEN;
                //String encoded = "Basic "+ Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", accesstoken);
                return headers;
            }
        };

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MainApplication.getInstance().addToRequestQueue(jsObjRequest, requestTag);


    }

    public void doPostMultiData(
            final Map<String, String> stringParam,
            final Map<String, VolleyMultipartRequest.DataPart> dataParam ,
            final String url,
            final String requestTag,
            final int requestId,
            final NetworkCallbacks responseCallback
    ){

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                responseCallback.onJsonResponse(SUCCESS,resultResponse,requestId);
                // parse success output
            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //responseCallback.onJsonResponse(ERROR,error.toString(),requestId);
                Log.d(TAG, "doPostMultiDataError: "+error.toString());
                try {
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    if((statusCode.equals("400") || statusCode.equals("401") || statusCode.equals("404")) && error.networkResponse.data!=null) {
                        try {
                            //String body = new String(error.networkResponse.data,"UTF-8");
                            String body = new String(
                                    error.networkResponse.data,
                                    HttpHeaderParser.parseCharset(error.networkResponse.headers));
                            String json = gson.toJson(gson.fromJson(body, Object.class));
                            responseCallback.onJsonResponse(SUCCESS, json, requestId);
                            Log.d("doPostCustomResponse Json",json.toString());
                        } catch (Exception e) {
                            responseCallback.onJsonResponse(ERROR,error.toString(),requestId);
                            error.printStackTrace();
                        }
                    } else {
                        responseCallback.onJsonResponse(ERROR,error.toString(),requestId);
                        error.printStackTrace();
                    }
                } catch (Exception e) {
                    responseCallback.onJsonResponse(ERROR,error.toString(),requestId);
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
//                params.put("name", "Angga");
//                params.put("location", "Indonesia");
//                params.put("about", "UI/UX Designer");
//                params.put("contact", "angga@email.com");
                return stringParam;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                // file name could found file base or direct access from real path
//                // for now just get bitmap data from ImageView
//                params.put("avatar", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mAvatarImage.getDrawable()), "image/jpeg"));
//                params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));

                return dataParam;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers =  new HashMap<String, String>();
                headers.put("Authorization", "Bearer 2r5qxnxg456wlid36v868n0pq6bocwfl");
                return headers;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MainApplication.getInstance().addToRequestQueue(multipartRequest, requestTag);


    }

    public byte[] getBytearrayFromBitmap(Bitmap bitmap){
//        byte[] buffer = new byte[0];
        if (bitmap !=null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            byte[] buffer = out.toByteArray();
            return buffer;
        }
        return null;
    }

    public void doGetCustom(
            final String getParam,
            final String url,
            Class clazz,
            final JSONObject jsonObject,
            final String accessToken,
            final String requestTag,
            final int requestId,
            final NetworkCallbacks responseCallback
    ){

        String mUrl = url;

        if (getParam != null){
            mUrl = url+"?"+getParam;

        }

        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + accessToken);
        headers.put("Accept", "application/json");

        Log.d(TAG, "doGetCustomUrl: "+mUrl);
        Log.d(TAG, headers.toString());

        GsonRequest gsonRequest = new GsonRequest(
                Request.Method.GET, url, clazz, (jsonObject == null) ? null : jsonObject.toString(),
                headers, new Response.Listener() {

            @Override
            public void onResponse(Object response) {
                String json = gson.toJson(response);
                responseCallback.onResponse(SUCCESS, json, requestId);
                Log.d("doGetCustomResponse Json",json.toString());
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "doGetCustomError: "+error.toString());
                //responseCallback.onResponse(ERROR,error.toString(),requestId);
                error.printStackTrace();
                try {
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    if((statusCode.equals("400") || statusCode.equals("401") || statusCode.equals("404")) && error.networkResponse.data!=null) {
                        try {
                            //String body = new String(error.networkResponse.data,"UTF-8");
                            String body = new String(
                                    error.networkResponse.data,
                                    HttpHeaderParser.parseCharset(error.networkResponse.headers));
                            String json = gson.toJson(gson.fromJson(body, Object.class));
                            responseCallback.onResponse(SUCCESS, json, requestId);
                            Log.d("doGetCustomError Json",json.toString());
                        } catch (Exception e) {
                            responseCallback.onResponse(ERROR,error.toString(),requestId);
                            error.printStackTrace();
                        }
                    } else {
                        responseCallback.onResponse(ERROR,error.toString(),requestId);
                        error.printStackTrace();
                    }
                } catch (Exception e) {
                    responseCallback.onResponse(ERROR,error.toString(),requestId);
                    error.printStackTrace();
                }
            }
        });

        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MainApplication.getInstance().addToRequestQueue(gsonRequest, requestTag);


    }

    public void doPostCustom(
            final String url,
            Class clazz,
            final JSONObject jsonObject,
            final String accessToken,
            final String requestTag,
            final int requestId,
            final NetworkCallbacks responseCallback
    ){

        String mUrl = url;

        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + accessToken);
        headers.put("Accept", "application/json");

        Log.d(TAG, "doPostCustomUrl: " + mUrl);
        Log.d(TAG, headers.toString());

        GsonRequest gsonRequest = new GsonRequest(
                Request.Method.POST, url, clazz, (jsonObject == null) ? null : jsonObject.toString(),
                headers, new Response.Listener() {

            @Override
            public void onResponse(Object response) {
                String json = gson.toJson(response);
                responseCallback.onResponse(SUCCESS, json, requestId);
                Log.d("doPostCustomResponse",json.toString());
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "doPostCustomError: "+error.toString());
                try {
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    if((statusCode.equals("400") ||
                            statusCode.equals("500") ||
                            statusCode.equals("401") ||
                            statusCode.equals("404"))
                            && error.networkResponse.data!=null) {
                        try {
                            //String body = new String(error.networkResponse.data,"UTF-8");
                            String body = new String(
                                    error.networkResponse.data,
                                    HttpHeaderParser.parseCharset(error.networkResponse.headers));
                            String json = gson.toJson(gson.fromJson(body, Object.class));
                            responseCallback.onResponse(SUCCESS, json, requestId);
                            Log.d("doPostCustomResponse Json",json.toString());
                        } catch (Exception e) {
                            responseCallback.onResponse(ERROR,error.toString(),requestId);
                            e.printStackTrace();
                        }
                    } else {
                        responseCallback.onResponse(ERROR,error.toString(),requestId);
                        error.printStackTrace();
                    }
                } catch (Exception e) {
                    responseCallback.onResponse(ERROR,error.toString(),requestId);
                    e.printStackTrace();
                }
            }
        });

        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MainApplication.getInstance().addToRequestQueue(gsonRequest, requestTag);


    }

    public void doPutCustom(
            final String url,
            Class clazz,
            final JSONObject jsonObject,
            final String accessToken,
            final String requestTag,
            final int requestId,
            final NetworkCallbacks responseCallback
    ){

        String mUrl = url;

        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + accessToken);

        Log.d(TAG, "doPutCustomUrl: " + mUrl);
        Log.d(TAG, headers.toString());

        GsonRequest gsonRequest = new GsonRequest(
                Request.Method.PUT, url, clazz, (jsonObject == null) ? null : jsonObject.toString(),
                headers, new Response.Listener() {

            @Override
            public void onResponse(Object response) {
                String json = gson.toJson(response);
                responseCallback.onResponse(SUCCESS, json, requestId);
                Log.d("doPutCustomResponse Json",json.toString());
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "doPutCustomError: "+error.toString());
                try {
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    if((statusCode.equals("400") ||
                            statusCode.equals("500") ||
                            statusCode.equals("401") ||
                            statusCode.equals("404")) &&
                            error.networkResponse.data!=null) {
                        try {
                            //String body = new String(error.networkResponse.data,"UTF-8");
                            String body = new String(
                                    error.networkResponse.data,
                                    HttpHeaderParser.parseCharset(error.networkResponse.headers));
                            String json = gson.toJson(gson.fromJson(body, Object.class));
                            responseCallback.onResponse(SUCCESS, json, requestId);
                            Log.d("doPutCustomResponse Json",json.toString());
                        } catch (Exception e) {
                            responseCallback.onResponse(ERROR,error.toString(),requestId);
                            error.printStackTrace();
                        }
                    } else {
                        responseCallback.onResponse(ERROR,error.toString(),requestId);
                        error.printStackTrace();
                    }
                } catch (Exception e) {
                    responseCallback.onResponse(ERROR,error.toString(),requestId);
                    error.printStackTrace();
                }
            }
        });

        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MainApplication.getInstance().addToRequestQueue(gsonRequest, requestTag);


    }

    public void doDeleteCustom(
            final String url,
            Class clazz,
            final JSONObject jsonObject,
            final String accessToken,
            final String requestTag,
            final int requestId,
            final NetworkCallbacks responseCallback
    ){

        String mUrl = url;

        Map<String, String> headers =  new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + accessToken);

        Log.d(TAG, "doPostCustomUrl: " + mUrl);
        Log.d(TAG, headers.toString());

        GsonRequest gsonRequest = new GsonRequest(
                Request.Method.DELETE, url, clazz, (jsonObject == null) ? null : jsonObject.toString(),
                headers, new Response.Listener() {

            @Override
            public void onResponse(Object response) {
                String json = gson.toJson(response);
                responseCallback.onResponse(SUCCESS, json, requestId);
                Log.d("doPostCustomResponse Json",json.toString());
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "doPostCustomError: "+error.toString());
                try {
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    if((statusCode.equals("400") || statusCode.equals("401") || statusCode.equals("404")) && error.networkResponse.data!=null) {
                        try {
                            //String body = new String(error.networkResponse.data,"UTF-8");
                            String body = new String(
                                    error.networkResponse.data,
                                    HttpHeaderParser.parseCharset(error.networkResponse.headers));
                            String json = gson.toJson(gson.fromJson(body, Object.class));
                            responseCallback.onResponse(SUCCESS, json, requestId);
                            Log.d("doPostCustomResponse Json",json.toString());
                        } catch (Exception e) {
                            responseCallback.onResponse(ERROR,error.toString(),requestId);
                            error.printStackTrace();
                        }
                    } else {
                        responseCallback.onResponse(ERROR,error.toString(),requestId);
                        error.printStackTrace();
                    }
                } catch (Exception e) {
                    responseCallback.onResponse(ERROR,error.toString(),requestId);
                    error.printStackTrace();
                }
            }
        });

        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MainApplication.getInstance().addToRequestQueue(gsonRequest, requestTag);


    }

    public void doPostMultiDataCustom(
            final Map<String, String> stringParam,
            final Map<String, VolleyMultipartRequest.DataPart> dataParam ,
            final String url,
            final String requestTag,
            final int requestId,
            final NetworkCallbacks responseCallback
    ){

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                responseCallback.onJsonResponse(SUCCESS,resultResponse,requestId);
                // parse success output
            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                responseCallback.onJsonResponse(ERROR,error.toString(),requestId);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
//                params.put("name", "Angga");
//                params.put("location", "Indonesia");
//                params.put("about", "UI/UX Designer");
//                params.put("contact", "angga@email.com");
                return stringParam;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                // file name could found file base or direct access from real path
//                // for now just get bitmap data from ImageView
//                params.put("avatar", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mAvatarImage.getDrawable()), "image/jpeg"));
//                params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));

                return dataParam;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers =  new HashMap<String, String>();
                headers.put("Authorization", "Bearer 2r5qxnxg456wlid36v868n0pq6bocwfl");
                return headers;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MainApplication.getInstance().addToRequestQueue(multipartRequest, requestTag);


    }
}
