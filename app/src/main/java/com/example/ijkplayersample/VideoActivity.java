package com.example.ijkplayersample;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;

import com.wisecloud.jni.CallMethod;
import com.wisecloud.jni.JniHandler;
import com.wisecloud.jni.NativeJni;
import com.wisecloud.jni.PlayInfo;

import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends AppCompatActivity {
    private final String TAG = VideoActivity.class.getSimpleName();
    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private TableLayout mHudView;
    private String mVideoPath;
    private String mMediaUrl = "";//ranger used
    private boolean mIsEnableHw;
    private PlayerType mPlayerType = PlayerType.IJK;
    private Handler mHandler;
    private Runnable mVolumeTask;
    private TextView mTvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   //hide status bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        getSupportActionBar().hide();

        mTvLog = findViewById(R.id.tv_log);
        mTvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvLog.setMaxLines(28);

        mVideoView = findViewById(R.id.v_view);
        mHudView = findViewById(R.id.hud_view);

        updateUITask();

        mMediaController = new AndroidMediaController(this);

        Bundle bundle = this.getIntent().getExtras();
        mVideoPath = bundle.getString("videoUrl");
        mIsEnableHw = bundle.getBoolean("isEnableHw");
        mPlayerType = (PlayerType)bundle.getSerializable("playerType");

        startPlayer();

        mHandler.postDelayed(mVolumeTask, 1000);
        //Thread.currentThread().setPriority(2);  //error example
    }

    private void updateUITask() {
        mHandler = new Handler();
        mVolumeTask = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this,250);
                scrollTextView();
            }
        };
    }

    private int getTextViewHeight(TextView view) {
        Layout layout = view.getLayout();
        int desired = layout.getLineTop(view.getLineCount());
        int padding = view.getCompoundPaddingTop() + view.getCompoundPaddingBottom();
        return desired + padding;
    }

    private void scrollTextView() {
        int offset = getTextViewHeight(mTvLog);
        //int offset = mTvLog.getLineCount() * mTvLog.getLineHeight();
        if(offset > mTvLog.getHeight()) {
            mTvLog.scrollTo(0, offset - mTvLog.getHeight());
        }
    }

    private void startRangerStream() {
        JniHandler.setOnCallBackPrepared(mJniPlayCallBack);
        String config_json = "{\"advertising_id\":\"\",\"android_id\":\"e3b3934db7f8e39a\",\"app\":\"com.interactive.brasiliptv\",\"app_version\":\"53800\",\"ca_info\":\"/data/user/0/com.interactive.brasiliptv/files/cacert.pem\",\"communication_key\":\"fbd14546-68fe-4ab9-9010-c6f42a73ac48\",\"dev_id\":\"\",\"params\":\"exp=3&max_cartons_1min=2&max_carton_duration_1min=15&last_retries=5&svs_address=xsvs.vfltbr.com:18084&svs_address_spare=xsvs.evlslb.com:18084&live_pcdn_mode=p2sp&tracker_list=199.189.86.249:5333,5.180.41.123:5333&vod_proxy=0&delay_ref=30&min_cache=15&max_cache=30&autodelay_icdn_min_delay=20&autodelay_icdn_max_delay=35&autodelay_icdn_enabled=1&http_stream_recv_timeout=13&blacklist_clear=1&source_weights_clear=1&source_weights_enabled=0&live_pcdn_xtimeout=2&blacklist_enabled=0&transmit_protocol=cdp_2.0&min_peers=0&max_peers=0&limit_min_rate=400000&star_proxy=1&mem_cache_enable=on\",\"player\":\"ijk\",\"sn\":\"8e.08-22.03-11512293\",\"user_id\":\"70157234\"}";
        Log.i(TAG, "config json info: " + config_json);
        NativeJni.getJni().setRangerConfig(config_json);
        String program_code = "DestaquesdaCopadoNordeste_720p";
        String programInfo_json = "{\"app_ctx\":\"Live\",\"buss\":\"live\",\"delay\":15000000000,\"desc\":\"Destaques da Copa do Nordeste\",\"lang\":\"\",\"program_code\":\"DestaquesdaCopadoNordeste_720p\",\"quality\":\"480p\",\"sources\":[{\"auth\":\"session_id=CwBCKDfU6Dnn&auth_id=70157234_com.interactive.brasiliptv&client_ip=218.18.4.86&ctrl_type=stb&app_ver=53800&app_id=com.interactive.brasiliptv&group=d831e466ea45a5daf6890805be1c263e&dev_id=8e.08-22.03-11512293&cdn_type=1&main_addr=lmslb.hkdfalk.com&user_id=70157234&spared_addr=&media_encrypted=0&expired=1667993281&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&play_limit=2&check_play_ip=true&token=A8A05C9D98AE13F90201A5391B72A1DB\",\"format\":\"\",\"id\":\"gaoq_slb_test\",\"id_code\":\"34ebf544d6a0008afa72f17b70333221\",\"lang\":\"\",\"license\":\"app_id=com.interactive.brasiliptv&tag=30a27705-8848-3fbd-afd6-9cfac1243da5&scheme=md5-01&media_code=pt_smjB4uUu991glkE5sH_720p&expired=1668581001&token=494314B30E33142CEA3454A72C200EDC\",\"main_addr\":\"lmslb.hkdfalk.com\",\"main_addr_code\":\"619e08315152e15c9af7575ab6ddbd6e9dd65661848a69ee7d542cf1f2377b24\",\"media_code\":\"pt_smjB4uUu991glkE5sH_720p\",\"priority\":1,\"quality\":\"480p\",\"rule_id_code\":\"f436108bbb4381b302eeebd380a8e0f5\",\"spared_addr\":\"\",\"spared_addr_code\":\"\",\"tag\":\"1\",\"weight\":0}],\"start\":0,\"timeout\":12000000000}";
        NativeJni.getJni().prepareProgram(1, program_code, programInfo_json);
    }

    JniHandler.OnCallBackPrepared mJniPlayCallBack = new JniHandler.OnCallBackPrepared() {
        @Override
        public int onPlayEvent(int instance, String event, Object play_info, long err) {
            if(event.isEmpty() || play_info == null) {
                return CallMethod.ERROR_PARAMS_INVALID;
            }
            switch (event) {
                case "source_start": //Ranger在执行每个源播放开始前触发，用于知会APP当前源的平台
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
                mMediaUrl = playInfo.getMedia_url();
                Log.i(TAG, "media_url: " + mMediaUrl);
                if(operation.equals("play")) {
                    VideoActivity.this.runOnUiThread(startPlayVideo);
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

        public void askReport(int instance, String event, String data, String app_ctx) {

        }
    };

    Runnable startPlayVideo = new Runnable() {
        @Override
        public void run() {
            if(!mMediaUrl.equals("")) {
                mVideoView.setVideoURI(Uri.parse(mMediaUrl));
                mVideoView.start();
            }
        }
    };

    private void startPlayer() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        //IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView.setTextView(mTvLog);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setHudView(mHudView);

        mVideoView.setEnableHw(mIsEnableHw);
        mVideoView.setPlayerType(mPlayerType.ordinal());
        mVideoView.setVideoURI(Uri.parse(mVideoPath));
        mVideoView.start();
        //startRangerStream();

        //for thread test
        //Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        //Log.d("test", "线程总数：" + allStackTraces.size());
        //error example
        /*Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                mVideoView.setVideoURI(Uri.parse(mVideoPath));
                mVideoView.setEnableHw(mIsEnableHw);
                mVideoView.setPlayerType(mPlayerType);
                mVideoView.start();
            }
        });
        t1.start();
        t1.setPriority(1);*/
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        }
        //IjkMediaPlayer.native_profileEnd();
    }
}
