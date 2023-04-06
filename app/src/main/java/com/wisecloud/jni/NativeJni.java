package com.wisecloud.jni;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import com.google.gson.Gson;
import tv.danmaku.ijk.media.example.application.BaseApp;

public class NativeJni {

    static {
        System.loadLibrary("ranger-jni");
    }

    private final String TAG = NativeJni.class.getSimpleName();

    public static NativeJni mNativeJni;

    public static NativeJni getJni() {
        if(mNativeJni == null) {
            synchronized (NativeJni.class) {
                if(mNativeJni == null) {
                    mNativeJni = new NativeJni();
                }
            }
        }
        return mNativeJni;
    }

    private Handler threadHandler;
    private HandlerThread handlerThread;
    private NativeJni() {
        handlerThread = new HandlerThread("handlerRanger");
        handlerThread.start();
        threadHandler = new android.os.Handler(handlerThread.getLooper());
        InitRanger();
    }

    //Todo
    public void release() {
        threadHandler.removeCallbacksAndMessages(null);
        handlerThread.quit();
        threadHandler = null;
        handlerThread = null;
    }

    private native String Call(String action, String params);

    public String CallWrapper(String action, String params) {
        String result = Call(action, params);
        return result;
    }

    private void InitRanger() {
        if(threadHandler == null) {
            Log.e(TAG, "threadHandler not init");
            return;
        }
        threadHandler.post(() -> {
            String lunaPath = BaseApp.getContext().getDir("luna", Context.MODE_PRIVATE).getAbsolutePath();
            Log.d(TAG, "Ranger work path: " + lunaPath);
            String paramStr = "{\"work_path\":\"" + lunaPath + "\"}";
            String retStr = CallWrapper("InitRanger", paramStr);
            Log.d(TAG, "InitRanger retStr: " + retStr);
        });
    }

    public void getRangerVersion() {
        threadHandler.post(() -> {
            String result = CallWrapper("GetVersion", "");
            Gson gson = new Gson();
            String version = gson.fromJson(result, RangerResult.class).getRes();
            Log.d(TAG, "GetVersion: " + version);
        });
    }

    public void setRangerConfig(final String configJson) {
        threadHandler.post(() -> {
            String params = CallMethod.getConfigParams(configJson);
            String status = CallWrapper("SetConfig", params);
            Log.d(TAG, "SetConfig status: " + status);
        });
    }

    public void prepareProgram(final int instance, final String program_code, final String program_info) {
        threadHandler.post(() -> {
            String params = CallMethod.getProgramParams(instance, program_code, program_info);
            String status = CallWrapper("PrepareProgram", params);
            Log.d(TAG, "PrepareProgram status: " + status);
        });
    }

    public void seekStream(final int instance, final String program_code, final long dest_moment, final int curr_moment, final int internal_seek, final RangerStrCallback rangerStrCallback) {
        threadHandler.post(() -> {
            String params = CallMethod.getSeekProgramParams(instance, program_code, dest_moment * 1000000L, curr_moment * 1000000L, internal_seek);
            String status = CallWrapper("SeekProgram", params);
            Log.d(TAG, "SeekProgram status: " + status);
            if(rangerStrCallback != null)
                rangerStrCallback.callback("");
        });
    }

    public void updatePullStream(final int instance, final String program_code, final String program_info) {
        threadHandler.post(() -> {
            String params = CallMethod.getProgramParams(instance, program_code, program_info);
            String status = CallWrapper("UpdateProgram", params);
            Log.d(TAG, "UpdateProgram status: " + status + "; instance: " + instance + "; program_code: " + program_code);
        });
    }

    public void pausePullStream(final int instance, final String program_code) {
        threadHandler.post(() -> {
            String params = CallMethod.getProgramParams(instance, program_code);
            String status = CallWrapper("PauseProgram", params);
            Log.d(TAG, "PauseProgram status: " + status);
        });
    }

    public void stopPullStream(final int instance, final String program_code) {
        threadHandler.post(() -> {
            String params = CallMethod.getProgramParams(instance, program_code);
            String status = CallWrapper("StopProgram", params);
            Log.d(TAG, "StopProgram status: " + status + "; program_code: " + program_code);
        });
    }

    public void getPullStreamState(final int instance, final String program_code,final RangerBeanCallback rangerBeanCallback) {
        threadHandler.post(() -> {
            String params = CallMethod.getProgramParams(instance, program_code);
            String ret = CallWrapper("GetProgram", params);
            Log.d(TAG, "GetProgram status: " + ret);
            Gson gson = new Gson();
            RangerResult result = gson.fromJson(ret, RangerResult.class);
            PlayInfo playInfo = gson.fromJson(result.getRes(), PlayInfo.class);
            if (rangerBeanCallback != null){
                rangerBeanCallback.callback(playInfo);
            }
        });
    }

    public void mediaPlayerOperation(final int instance, final String operation, final String data, final int err) {
        threadHandler.post(() -> {
            String params = CallMethod.getNotifyPlayerOperationParams(instance, operation, data, err);
            String status = CallWrapper("NotifyPlayerOperation", params);
            Log.i(TAG, "NotifyPlayerOperation status: " + status);
        });
    }

    public void mediaPlayerEvent(final int instance, final String event, final int err, final int extra, final long data) {
        threadHandler.post(() -> {
            String params = CallMethod.getNotifyPlayerEventParams(instance, event, err, extra, data);
            String status = CallWrapper("NotifyPlayerEvent", params);
            Log.i(TAG, "NotifyPlayerEvent status: " + status);
        });
    }

    public void switchProgramLangStream(final int instance, final String program_code, final String lang) {
        threadHandler.post(() -> {
            String params = CallMethod.getSwitchProgramLangParams(instance, program_code, lang);
            String status = CallWrapper("SwitchProgramLang", params);
            Log.d(TAG, "SwitchProgramLang status: " + status);
        });
    }

    public void switchQuality(final int instance, final String program_code, final String quality) {
        threadHandler.post(() -> {
            String params = CallMethod.getSwitchProgramQualityParams(instance, program_code, quality);
            String status = CallWrapper("SwitchProgramQuality", params);
            Log.d(TAG, "SwitchProgramQuality status: " + status);
        });
    }

    public void resumePullStream(final int instance, final String program_code) {
        threadHandler.post(() -> {
            String params = CallMethod.getProgramParams(instance, program_code);
            String status = CallWrapper("ResumeProgram", params);
            Log.d(TAG, "ResumeProgram status:" + status);
        });
    }

    public void notifySysEvent(final String object, final String type, final String event) {
        threadHandler.post(() -> {
            String params = CallMethod.getNotifySysEventParams(object, type, event);
            String status = CallWrapper("NotifySysEvent", params);
            Log.i(TAG, "NotifySysEvent status: " + status);
        });
    }
}
