package com.greenflames.myzebeel.interfaces;

/*
 *Created by Adithya T Raj on 26-04-2021
 */

public interface NetworkCallbacks {
    void onResponse(int status, String response, int requestId);
    void onJsonResponse(int status, String response, int requestId);
}
