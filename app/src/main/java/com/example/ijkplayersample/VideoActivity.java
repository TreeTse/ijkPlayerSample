package com.example.ijkplayersample;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import java.util.Map;

import tv.danmaku.ijk.media.example.application.Settings;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends AppCompatActivity {
    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private TableLayout mHudView;
    private String mVideoPath;
    private boolean mIsEnableHw;
    private int mPlayerType = 0;
    private Settings mSettings;
    private Handler mHandler;
    private Runnable mVolumeTask;
    private ProgressBar mVolumeBar;
    private TextView mTvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   //hide status bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        getSupportActionBar().hide();

        mSettings = new Settings(this);
        mVolumeBar = findViewById(R.id.volume_bar);
        updateUITask();
        mTvLog = findViewById(R.id.tv_log);
        mTvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvLog.setMaxLines(28);

        mMediaController = new AndroidMediaController(this);

        Bundle bundle = this.getIntent().getExtras();
        mVideoPath = bundle.getString("videoUrl");
        mIsEnableHw = bundle.getBoolean("isEnableHw");
        mPlayerType = bundle.getInt("playerType");
        initPlayer();

        mHandler.postDelayed(mVolumeTask, 1000);
        //Thread.currentThread().setPriority(2);  //error example
    }

    private void updateUITask() {
        mHandler = new Handler();
        mVolumeTask = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this,250);
                showVolume();
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

    private void showVolume() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        double currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        double maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeBar.setProgress((int)(currentVolume/maxVolume * mVolumeBar.getMax()));
    }

    private void initPlayer() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        //IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = findViewById(R.id.v_view);
        mHudView = findViewById(R.id.hud_view);

        mVideoView.setTextView(mTvLog);
        mVideoView.setMediaController(mMediaController);
        //MediaMetadataRetriever mmr = new MediaMetadataRetriever();  //test
        //mmr.setDataSource(mVideoPath, new HashMap<String, String>());
        MediaMetadataRetriever mmr = null;
        mVideoView.setHudView(mHudView, mmr);
        mVideoView.setVideoURI(Uri.parse(mVideoPath));
        mVideoView.setEnableHw(mIsEnableHw);
        mVideoView.setPlayerType(mPlayerType);
        mVideoView.start();
        //for test
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
