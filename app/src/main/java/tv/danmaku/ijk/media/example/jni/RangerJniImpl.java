package tv.danmaku.ijk.media.example.jni;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.wisecloud.jni.CallMethod;
import com.wisecloud.jni.JniHandler;
import com.wisecloud.jni.NativeJni;
import com.wisecloud.jni.PlayInfo;
import com.wisecloud.jni.RangerBeanCallback;

import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;


public class RangerJniImpl {
    private final String TAG = RangerJniImpl.class.getSimpleName();
    protected boolean hasRangerStream = false;
    protected boolean hasPauseStream = false;
    protected boolean hasResumeStream = false;
    private int instanceId = 0;
    protected boolean isNotAgent = false;
    protected long lastErrCode = 0L;
    private IjkVideoView mVideoView;
    private String mMediaUrl = "";
    private String mDashBoard = "";
    private AppCompatActivity mActivity;

    public RangerJniImpl(IjkVideoView videoView) {
        mVideoView = videoView;
    }

    public void setInstanceId(int instance) {
        instanceId = instance;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void prepareProgram(int instance, String program_code, String program_info) {
        instanceId = instance;
        hasRangerStream = true;
        isNotAgent = false;
        lastErrCode = 0;
        NativeJni.getJni().prepareProgram(instance, program_code, program_info);
    }

    public void updateProgram(int instance, String program_code, String program_info) {
        instanceId = instance;
        lastErrCode = 0;
        NativeJni.getJni().updatePullStream(instance, program_code, program_info);
    }

    public void pauseProgram(int instance, String program_code) {
        instanceId = instance;
        if(hasRangerStream && hasPauseStream && !program_code.isEmpty()) {
            hasPauseStream = true;
            hasResumeStream = false;
            NativeJni.getJni().pausePullStream(instance, program_code);
        }
    }

    public void resumeProgram(int instance, String program_code) {
        instanceId = instance;
        if(hasRangerStream && !hasResumeStream && !program_code.isEmpty()) {
            hasResumeStream = true;
            hasPauseStream = false;
            NativeJni.getJni().resumePullStream(instance, program_code);
        }
    }

    public void stopProgram(int instance, String program_code) {
        instanceId = instance;
        if(hasPauseStream && !program_code.isEmpty()) {
            hasRangerStream = false;
            hasPauseStream = false;
            hasResumeStream = false;
            lastErrCode = 0;
            NativeJni.getJni().stopPullStream(instance, program_code);
        }
    }

    public void getStreamState(int instance, String program_code, RangerBeanCallback rangerBeanCallback) {
        if(hasRangerStream && !program_code.isEmpty()) {
            NativeJni.getJni().getPullStreamState(instance, program_code, rangerBeanCallback);
        }
    }

    public void notifyPlayerOperation(String operation, String data, int err) {
        NativeJni.getJni().mediaPlayerOperation(instanceId, operation, data, err);
    }

    public void notifyPlayerEvent(String event, int err, int extra, long data) {
        NativeJni.getJni().mediaPlayerEvent(instanceId, event, err, extra, data);
    }

    public void setJniCallBack(AppCompatActivity activity) {
        JniHandler.setOnCallBackPrepared(jniPlayCallBack);
        mActivity = activity;
    }

    Runnable startPlayVideo = new Runnable() {
        @Override
        public void run() {
            if(!mMediaUrl.equals("")) {
                Log.i(TAG, "get media url: " + mMediaUrl);
                mVideoView.setVideoURI(Uri.parse(mMediaUrl));
                mVideoView.start();
            }
        }
    };

    private JniHandler.OnCallBackPrepared jniPlayCallBack = new JniHandler.OnCallBackPrepared() {
        @Override
        public int onPlayEvent(int instance, String event, Object play_info, long err) {
            if(event.isEmpty() || play_info == null) {
                return CallMethod.ERROR_PARAMS_INVALID;
            }
            switch (event) {
                case "source_start": //Ranger在执行每个源播放开始前触发，用于知会APP当前源的平台
                    Log.i(TAG, "onPlayEvent, source_start!");
                    break;
                case "source_end":   //Ranger在执行每个源播放结束后触发
                    break;
                case "schedule_err": //请求SLB错误，err记录错误码
                    break;
                case "source_complete":
                    Log.i(TAG, "onPlayEvent, source_complete!");
                    break;
                case "stream_err":    //采集错误码到bigbee不需要判断是否在播放,同一个源同一个错误码不需要重复采集
                case "manifest_err":
                    break;
                case "source_err":  //处理源错误，err目前一定为93，且不会再调用source_end事件，TODO 后续版本再做，目前还没有中间件不能处理的源，APK自己处理该源播放，若该源播放失败，后续要切源请调用resumeProgram()方法
                    break;
                default:  //其它回调事件，直播点播子类区分处理
                    break;
            }
            return CallMethod.METHOD_OK;
        }

        @Override
        public int askPlayer(int instance, String operation, Object play_info, long data) {
            if(!operation.isEmpty() && play_info != null) {
                PlayInfo playInfo = (PlayInfo)play_info;
                mMediaUrl = playInfo.getPlay_url();
                //mMediaUrl = playInfo.getMedia_url();
                if(operation.equals("play")) {
                    mActivity.runOnUiThread(startPlayVideo);
                } else if(operation.equals("replay")) {
                    //需要在点播场景下
                } else {
                    //点播处理
                }
                return CallMethod.METHOD_OK;
            } else {
                return CallMethod.ERROR_PARAMS_INVALID;
            }
        }

        @Override
        public void askReport(int instance, String event, String data, String app_ctx) {

        }
    };
}