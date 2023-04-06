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
    private String mPlayTag = "";
    private boolean mIsEnableP2P = true;
    private String mTracketList = "";
    private int mSourcePriority = 0;
    private int mSourceIndex = 0;
    private Handler mHandler;
    private Runnable mTextViewTask;
    private TextView mTvLog;
    private TextView mTvRanger;
    private boolean mIsRangerMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   //hide status bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        //getSupportActionBar().hide();

        mTvLog = findViewById(R.id.tv_log);
        mTvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvLog.setMaxLines(56);

        mTvRanger = findViewById(R.id.tv_ranger);
        mTvRanger.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvRanger.setMaxLines(60);

        mVideoView = findViewById(R.id.v_view);
        mHudView = findViewById(R.id.hud_view);

        updateUITask();

        mMediaController = new AndroidMediaController(this);

        Bundle bundle = this.getIntent().getExtras();
        mVideoPath = bundle.getString("videoUrl");
        mIsEnableHw = bundle.getBoolean("isEnableHw");
        mPlayerType = (PlayerType)bundle.getSerializable("playerType");
        mSourceIndex = bundle.getInt("sourceIndex");
        mPlayTag = bundle.getString("playTag");
        mIsEnableP2P = bundle.getBoolean("isEnableP2P");
        mTracketList = bundle.getString("trackerList");
        mSourcePriority = bundle.getInt("priority");

        startPlayer();

        mHandler.postDelayed(mTextViewTask, 1000);
        //Thread.currentThread().setPriority(2);  //error example
    }

    private void updateUITask() {
        mHandler = new Handler();
        mTextViewTask = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this,800);
                scrollTextView();
                String dashBoard = mVideoView.getDashBoard();
                mTvRanger.setText("");
                mTvRanger.append(dashBoard + "\n");
            }
        };
    }

    private int getTextViewHeight(TextView view) {
        Layout layout = view.getLayout();
        if(layout != null) {
            int desired = layout.getLineTop(view.getLineCount());
            int padding = view.getCompoundPaddingTop() + view.getCompoundPaddingBottom();
            return desired + padding;
        }
        return 0;
    }

    private void scrollTextView() {
        int offset = getTextViewHeight(mTvLog);
        //int offset = mTvLog.getLineCount() * mTvLog.getLineHeight();
        int logHeight = mTvLog.getHeight();
        /*if(offset > logHeight) {
            mTvLog.scrollTo(0, offset - logHeight);
        }*/
        if(logHeight >= 1036) {
            mTvLog.setText("");
        }
    }

    private void startPlayer() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        //IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView.setTextView(mTvLog);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setHudView(mHudView);

        mVideoView.setEnableHw(mIsEnableHw);
        mVideoView.setPlayerType(mPlayerType.ordinal());
        if(mVideoPath.equals("")) {
            mIsRangerMode = true;
            mVideoView.setRangerMode(true, mVideoView, this, mPlayTag, mSourceIndex, mIsEnableP2P, mSourcePriority, mTracketList);
        } else {
            mVideoView.setVideoURI(Uri.parse(mVideoPath));
            mVideoView.start();
        }

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
            mVideoView.releaseRangerTask();
            mVideoView.releaseRangerLogTask();
        }
        //IjkMediaPlayer.native_profileEnd();
    }
}
