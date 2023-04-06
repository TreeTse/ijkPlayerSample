package com.wisecloud.jni;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class CallMethod {
    private static String TAG = CallMethod.class.getSimpleName();
    public static final int METHOD_OK = 0;  //调用成功
    public static final int ERROR_METHOD_NOT_FIND = 2;  //逻辑方法不存在
    public static final int ERROR_PARAMS_INVALID = 22;  //params不合法，非法json或者缺少必需字段

    public static String getConfigParams(String config) {
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("config", config);
            json = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getProgramParams(int instance, String program_code, String program_info) {
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("instance", instance);
            object.put("name", program_code);
            object.put("program", program_info);
            json = object.toString();
            Log.i(TAG, "program params json string: " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getProgramParams(int instance, String program_code) {
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("instance", instance);
            object.put("name", program_code);
            json = object.toString();
            Log.i(TAG, "program params json string: " + json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getNotifyPlayerEventParams(int instance, String event, int err, int extra, long data) {
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("instance", instance);
            object.put("event", event);
            object.put("err", err);
            object.put("extra", extra);
            object.put("data", data);
            json = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getSwitchProgramLangParams(int instance, String program_code, String lang) {
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("instance", instance);
            object.put("name", program_code);
            object.put("lang", lang);
            json = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getSwitchProgramQualityParams(int instance, String program_code, String quality) {
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("instance", instance);
            object.put("name", program_code);
            object.put("quality", quality);
            json = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getNotifySysEventParams(String obj, String type, String event) {
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("object", obj);
            object.put("type", type);
            object.put("event", event);
            json = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getNotifyPlayerOperationParams(int instance, String operation, String data, int err) {
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("instance", instance);
            object.put("operation", operation);
            object.put("data", data);
            object.put("err", err);
            json = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getSeekProgramParams(int instance, String program_code,
                                              long dest_moment,long curr_moment,int internal_seek) {
        String json = "";
        try {
            JSONObject object = new JSONObject();
            object.put("instance", instance);
            object.put("name", program_code);
            object.put("curr_moment", curr_moment);
            object.put("internal_seek", internal_seek);
            object.put("dest_moment", dest_moment);
            json = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
