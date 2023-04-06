package com.wisecloud.jni;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

public class JniHandler {
    private static String TAG = JniHandler.class.getSimpleName();
    private static OnCallBackPrepared mOnCallBackPrepared;

    public static void setOnCallBackPrepared(OnCallBackPrepared onCallBackPrepared) {
        mOnCallBackPrepared = onCallBackPrepared;
    }

    private String Callback(String action, String params) {
        RangerResult result = new RangerResult(0, "");
        Gson gson = new Gson();
        Log.i(TAG, "Callback res, action: " + action + ", params: " + params);
        switch (action) {
            case "OnPlayEvent":
                if(!TextUtils.isEmpty(params)) {
                    try {
                        JSONObject jsonObject = new JSONObject(params);
                        int instance = jsonObject.getInt("instance");
                        String event = jsonObject.getString("event");
                        String play_info = jsonObject.getString("play_info");
                        long err = jsonObject.getLong("err");
                        PlayInfo playInfo = gson.fromJson(play_info, PlayInfo.class);
                        result = onPlayEvent(instance, event, playInfo, err);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                    }
                } else {
                    result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                }
                break;
            case "AskPlayer":
                if(!TextUtils.isEmpty(params)) {
                    try {
                        JSONObject jsonObject = new JSONObject(params);
                        int instance = jsonObject.getInt("instance");
                        String operation = jsonObject.getString("operation");
                        String play_info = jsonObject.getString("play_info");
                        long data = jsonObject.getLong("data");
                        PlayInfo playInfo = gson.fromJson(play_info, PlayInfo.class);
                        result = askPlayer(instance, operation, playInfo, data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                    }
                } else {
                    result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                }
                break;
            case "AskReport":
                if(!TextUtils.isEmpty(params)) {
                    try {
                        JSONObject jsonObject = new JSONObject(params);
                        int instance = jsonObject.getInt("instance");
                        String event = jsonObject.getString("event");
                        String data = jsonObject.getString("data");
                        String app_ctx = jsonObject.getString("app_ctx");
                        result = askReport(instance, event, data,app_ctx);
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                    }
                } else {
                    result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                }
                break;
            case "OnSignalEvent":
                if(!TextUtils.isEmpty(params)) {
                    try {
                        JSONObject jsonObject = new JSONObject(params);
                        int signal = jsonObject.getInt("signal");
                        result = onSignal(signal);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                    }
                } else {
                    result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                }
                break;
            case "OnAssert":
                if(!TextUtils.isEmpty(params)) {
                    try {
                        JSONObject jsonObject = new JSONObject(params);
                        int instance = jsonObject.getInt("instance");
                        String file = jsonObject.getString("file");
                        String function = jsonObject.getString("function");
                        String condition = jsonObject.getString("condition");
                        String caseTitle = jsonObject.getString("case");
                        result = onAsset(instance, file, function, condition, caseTitle);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                    }
                } else {
                    result.setErr(CallMethod.ERROR_PARAMS_INVALID);
                }
                break;
            default:
                result.setErr(CallMethod.ERROR_METHOD_NOT_FIND);
                break;
        }
        return gson.toJson(result);
    }

    private RangerResult askPlayer(int instance, String operation, Object play_info, long data) {
        RangerResult result = new RangerResult(CallMethod.METHOD_OK, "");
        if(play_info instanceof PlayInfo){
            Log.i(TAG, "askPlayer callback, operation: " + operation + ";play_info: " + play_info.toString());
            int error = mOnCallBackPrepared.askPlayer(instance, operation, (PlayInfo) play_info, data);
            result.setErr(error);
        } else {
            result.setErr(CallMethod.ERROR_PARAMS_INVALID);
        }
        return result;
    }

    private RangerResult onPlayEvent(int instance, String event, Object play_info, long err) {
        RangerResult result = new RangerResult(CallMethod.METHOD_OK, "");
        if(play_info instanceof PlayInfo) {
            Log.i(TAG, "onPlayEvent callback, event: " + event + ";play_info: " + play_info.toString());
            int error = mOnCallBackPrepared.onPlayEvent(instance, event, (PlayInfo)play_info, err);
            result.setErr(error);
        } else {
            result.setErr(CallMethod.ERROR_PARAMS_INVALID);
        }
        return result;
    }

    private RangerResult askReport(int instance, String event, String data, String app_ctx) {
        RangerResult result = new RangerResult(CallMethod.METHOD_OK, "");
        if(!TextUtils.isEmpty(data) && !TextUtils.isEmpty(event)) {
            Log.i(TAG, "askReport callback, event: " + event + ";data: " + data + ";app_ctx: " + app_ctx);
            if(event.equals("play_program")) {

            } else if(event.equals("play_source")) {

            } else if(event.equals("probe_net")) {

            }
        } else {
            result.setErr(CallMethod.ERROR_PARAMS_INVALID);
        }
        return result;
    }

    private RangerResult onSignal(int signal) {
        RangerResult result = new RangerResult(CallMethod.METHOD_OK, "");
        Log.i(TAG, "onSignal, signal: " + signal);
        return result;
    }

    private RangerResult onAsset(int instance, String file, String function, String condition, String caseTitle) {
        RangerResult result = new RangerResult(CallMethod.METHOD_OK, "");
        Log.i(TAG, "onAsset, instance: " + instance + "; file: " + file + "; function: " + function + "; condition: " + condition + "; caseTitle: " + caseTitle);
        return result;
    }

    public interface OnCallBackPrepared {
        int onPlayEvent(int instance, String event, Object play_info, long err);

        int askPlayer(int instance, String operation, Object play_info, long data);

        void askReport(int instance, String event, String data, String app_ctx);
    }
}
