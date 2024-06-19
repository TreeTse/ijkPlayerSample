package com.example.ijk.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.ijk.R;
import com.example.ijk.bean.MultiTrackInfo;
import com.example.ijk.constant.PlayerEnum;
import com.example.ijk.util.LogUtil;
import com.example.ijk.widget.IPlayerListener;
import com.example.ijk.widget.IVolumeCallback;
import com.example.ijk.widget.IjkVideoView;
import com.example.ijk.widget.VideoSeekBar;
import com.example.ijk.widget.VolumeSeekBar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends AppCompatActivity {
    private final String TAG = "VideoActivity";
    private final HashMap<PlayerEnum, Integer> indexLoaderByPlayerType = new HashMap<PlayerEnum, Integer>() {
        {
            put(PlayerEnum.IJK, 0);
            put(PlayerEnum.NATIVE, 1);
            put(PlayerEnum.EXO, 2);
        }
    };
    private final HashMap<Integer, PlayerEnum> playerTypeLoaderByIndex = new HashMap<Integer, PlayerEnum>() {
        {
            put(0, PlayerEnum.IJK);
            put(1, PlayerEnum.NATIVE);
            put(2, PlayerEnum.EXO);
        }
    };
    private String mVideoPath;
    //--UI
    private IjkVideoView mVideoView;
    private ViewGroup mButtomController;
    private ViewGroup mCenterController;
    private View btnPlayPause;
    private AnimatorSet mShowAllBarsAnimator;
    private AnimatorSet mHideAllBarsAnimator;
    private VideoSeekBar mVideoSeekBar;//progress seekBar
    private View mVolumeController;
    private VolumeSeekBar sbVolume;//volume seekBar
    private TextView tvVolume;//volume value
    private TextView tvAudioSelector;
    private TextView tvSpeedSelector;
    private TextView tvPlayerSelector;
    private PlaybackSpeedAdapter mPlaybackSpeedAdapter;
    private PlayerAdapter mPlayerAdapter;
    private ATrackSelectionAdapter mATrackAdapter;
    private TextView tvTimePass;//current time
    private TextView tvTimeLong;//duration
    private TextView tvPlayerType;//current player type
    private TextView tvAudioTrack;//current audio track
    private TextView tvPlayLog;
    private TextView tvPlayerInfo;
    private TextView tvFirstShowTime;

    private boolean mSeekBarScroll;
    private boolean isUsingMediaCodec = true;

    private ArrayList<MultiTrackInfo> mCurrAudioTrackInfoList = null;//all audio track info
    String sAudioTracks = "";//list of all audio track languages
    private int mAudioTrackCount = -1;
    private long mDuration = 0;

    private PlayerEnum mPlayerType = PlayerEnum.IJK;
    private long playStartTime;
    private long playEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   //hide status bar
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        //getSupportActionBar().hide();

        mVideoView = findViewById(R.id.mVideoView);
        mButtomController = findViewById(R.id.mButtomBar);
        mCenterController = findViewById(R.id.mCenterControl);
        btnPlayPause = findViewById(R.id.mPlayPause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getVisibility() == View.VISIBLE) {
                    if (mVideoView != null) {
                        if (mVideoView.isPlaying()) {
                            LogUtil.logInfo("暂停播放");
                            mVideoView.pause();
                            ((ImageView) btnPlayPause).setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                        } else {
                            LogUtil.logInfo("恢复播放");
                            mVideoView.start();
                            ((ImageView) btnPlayPause).setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                        }
                    }
                }
            }
        });
        hideController();
        initAnimator();

        mVideoSeekBar = findViewById(R.id.mSeekBar);
        setSeekBarParams();

        mVolumeController = LayoutInflater.from(this).inflate(R.layout.widget_volume_controller, null);
        sbVolume = mVolumeController.findViewById(R.id.mVolumeSeekBar);
        tvVolume = mVolumeController.findViewById(R.id.mVolumeValue);
        sbVolume.setCallback(new IVolumeCallback() {
            @Override
            public void onProgress(int progress) {
                tvVolume.setText(String.valueOf(progress));
                mHandler.removeMessages(PLAYER_VOLUME);
            }

            @Override
            public void onVolumeChange(int value) {
                mHandler.removeMessages(PLAYER_VOLUME);
                Message msg = mHandler.obtainMessage(PLAYER_VOLUME);
                msg.obj = value;
                mHandler.sendMessageDelayed(msg, 500);
            }
        });

        tvTimePass = findViewById(R.id.mProgressTimepass);
        tvTimeLong = findViewById(R.id.mProgressTimelong);
        tvPlayerInfo = findViewById(R.id.mPlayerInfo);
        tvPlayLog = findViewById(R.id.mPlayLog);
        LogUtil.setTvPlayLog(tvPlayLog);
        tvAudioTrack = findViewById(R.id.mCurAudioTrack);
        tvPlayerType = findViewById(R.id.mCurPlayer);
        tvFirstShowTime = findViewById(R.id.mPlayTime);

        mATrackAdapter = new ATrackSelectionAdapter();
        tvAudioSelector = findViewById(R.id.mCtAudio);
        tvAudioSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPpw.isShowing()) {
                    mPpw.dismiss();
                } else {
                    displayPopupWindow(mATrackAdapter, v);
                }
            }
        });

        mPlaybackSpeedAdapter = new PlaybackSpeedAdapter(
                getResources().getStringArray(R.array.playback_speeds),
                getResources().getIntArray(R.array.speed_multiplied_by_100));
        tvSpeedSelector = findViewById(R.id.mCtSpeed);
        tvSpeedSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPpw.isShowing()) {
                    mPpw.dismiss();
                } else {
                    displayPopupWindow(mPlaybackSpeedAdapter, v);
                }
            }
        });

        mPlayerAdapter = new PlayerAdapter(
                getResources().getStringArray(R.array.playback_players), indexLoaderByPlayerType.get(mPlayerType));
        tvPlayerSelector = findViewById(R.id.mCtPlayer);
        tvPlayerSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPpw.isShowing()) {
                    mPpw.dismiss();
                } else {
                    displayPopupWindow(mPlayerAdapter, v);
                }
            }
        });

        initPopupWindow();

        Bundle bundle = this.getIntent().getExtras();
        mVideoPath = bundle.getString("videoUrl");
        mPlayerType = (PlayerEnum)bundle.getSerializable("playerType");
        isUsingMediaCodec = bundle.getBoolean("mediacodec");

        setSeekListener();

        startPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null)
            mVideoView.pause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            showController();
            resetHideTimer();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isResetTimer = keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_INFO &&
                keyCode != KeyEvent.KEYCODE_BACK;
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_INFO:
                if (mVideoView != null) {
                    mVideoView.showMediaInfo();
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                release();
                finish();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (btnPlayPause != null)
                    btnPlayPause.requestFocus();
                showController();
                break;
        }
        if (isResetTimer)
            resetHideTimer();
        return super.onKeyDown(keyCode, event);
    }

    private void hideController() {
        if (mButtomController != null)
            mButtomController.setVisibility(View.INVISIBLE);
        if (mCenterController != null)
            mCenterController.setVisibility(View.INVISIBLE);
    }

    private void showController() {
        mShowAllBarsAnimator.start();
    }

    private void initAnimator() {
        ValueAnimator fadeInAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        fadeInAnimator.setInterpolator(new LinearInterpolator());
        fadeInAnimator.addUpdateListener(
                animation -> {
                    float animatedValue = (float) animation.getAnimatedValue();
                    if (mCenterController != null)
                        mCenterController.setAlpha(animatedValue);
                }
        );
        fadeInAnimator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (mCenterController != null)
                            mCenterController.setVisibility(View.VISIBLE);
                    }
                }
        );

        float translationYForNoBars = getResources().getDimension(R.dimen.bottom_bar_height);

        mShowAllBarsAnimator = new AnimatorSet();
        mShowAllBarsAnimator.setDuration(250);
        mShowAllBarsAnimator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetHideTimer();
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (mButtomController != null)
                            mButtomController.setVisibility(View.VISIBLE);
                    }
                });
        mShowAllBarsAnimator
                .play(fadeInAnimator)
                .with(ofTranslationY(translationYForNoBars, 0, mButtomController));

        ValueAnimator fadeOutAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        fadeOutAnimator.setInterpolator(new LinearInterpolator());
        fadeOutAnimator.addUpdateListener(
                animation -> {
                    float animatedValue = (float) animation.getAnimatedValue();
                    if (mCenterController != null)
                        mCenterController.setAlpha(animatedValue);
                });
        fadeOutAnimator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mButtomController != null)
                            mButtomController.setVisibility(View.INVISIBLE);
                    }
                }
        );
        mHideAllBarsAnimator = new AnimatorSet();
        mHideAllBarsAnimator.setDuration(250);
        mHideAllBarsAnimator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCenterController.setVisibility(View.INVISIBLE);
                    }
                }
        );
        mHideAllBarsAnimator
                .play(fadeOutAnimator)
                .with(ofTranslationY(0, translationYForNoBars, mButtomController));
    }

    private static ObjectAnimator ofTranslationY(float startValue, float endValue, View target) {
        return ObjectAnimator.ofFloat(target, "translationY", startValue, endValue);
    }

    private final class PlaybackSpeedAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private final String[] playbackSpeedTexts;
        private final int[] playbackSpeedMultBy100;
        private int selectedIndex;
        private int curSpeed = 100;

        public PlaybackSpeedAdapter(String[] playbackSpeedTexts, int[] playbackSpeedMultBy100) {
            this.playbackSpeedTexts = playbackSpeedTexts;
            this.playbackSpeedMultBy100 = playbackSpeedMultBy100;
            for (int i = 0; i < playbackSpeedMultBy100.length; i++) {
                if (playbackSpeedMultBy100[i] == curSpeed)
                    selectedIndex = i;
            }
        }

        public String getSelctedText() {
            return playbackSpeedTexts[selectedIndex];
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(VideoActivity.this).inflate(R.layout.ppw_item, null);
            MyViewHolder h = new MyViewHolder(view);
            h.itemView.setBackgroundResource(R.drawable.selector_pw_item);
            return h;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (position < playbackSpeedTexts.length) {
                holder.tvName.setText(playbackSpeedTexts[position]);
            }
            holder.ivCheck.setVisibility(position == selectedIndex ? View.VISIBLE : View.INVISIBLE);
            if (position == selectedIndex) {
                holder.itemView.setFocusable(true);
                holder.itemView.requestFocus();
            }
            holder.itemView.setOnClickListener(
                    v -> {
                        resetHideTimer();
                        if (position != selectedIndex) {
                            float speed = playbackSpeedMultBy100[position] / 100.0f;
                            //setPlaybackSpeed(speed);
                        }
                        mPpw.dismiss();
                    }
            );
        }

        @Override
        public int getItemCount() {
            return playbackSpeedTexts.length;
        }
    }

    private final class PlayerAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private final String[] playerTexts;
        private int selectedIndex;

        public PlayerAdapter(String[] playerTexts, int selectedIndex) {
            this.playerTexts = playerTexts;
            this.selectedIndex = selectedIndex;
        }

        public void updateSelectedIndex(int playbackPlayer) {
            selectedIndex = playbackPlayer;
        }

        public String getSelctedText() {
            return playerTexts[selectedIndex];
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(VideoActivity.this).inflate(R.layout.ppw_item, null);
            MyViewHolder h = new MyViewHolder(view);
            h.itemView.setBackgroundResource(R.drawable.selector_pw_item);
            return h;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (position < playerTexts.length) {
                holder.tvName.setText(playerTexts[position]);
            }
            holder.ivCheck.setVisibility(position == selectedIndex ? View.VISIBLE : View.INVISIBLE);
            holder.itemView.setOnClickListener(
                    v -> {
                        resetHideTimer();
                        if (position != selectedIndex) {
                            mPlayerType = playerTypeLoaderByIndex.get(position);
                            switchPlayer(mPlayerType);
                            tvPlayerType.setText(mPlayerType.name());
                        }
                        mPpw.dismiss();
                    }
            );
        }

        @Override
        public int getItemCount() {
            return playerTexts.length;
        }
    }

    private final class ATrackSelectionAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<MultiTrackInfo> tracks;
        private int trackIndex;

        public ATrackSelectionAdapter() {
        }

        public void updateSelectedIndex(int audioIndex) {
            trackIndex = audioIndex;
        }

        public void init(List<MultiTrackInfo> trackInfos, int selectIndex) {
            tracks = trackInfos;
            trackIndex = selectIndex;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(VideoActivity.this).inflate(R.layout.ppw_item, null);
            MyViewHolder h = new MyViewHolder(view);
            h.itemView.setBackgroundResource(R.drawable.selector_pw_item);
            return h;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            MultiTrackInfo track = tracks.get(position);
            int expectIndex = -1;
            for (int i = 0; i < tracks.size(); i++) {
                if (trackIndex == tracks.get(i).getIndex()) {
                    expectIndex = i;
                }
            }
            holder.tvName.setText(track.getLang());
            holder.ivCheck.setVisibility(position == expectIndex ? View.VISIBLE : View.INVISIBLE);
            if (position == expectIndex) {
                holder.itemView.setFocusable(true);
                holder.itemView.requestFocus();
            }
            int finalExpectIndex = expectIndex;
            holder.itemView.setOnClickListener(
                    v -> {
                        resetHideTimer();
                        if (position != finalExpectIndex) {
                            int targetIndex = tracks.get(position).getIndex();
                            setAudioTrack(targetIndex);
                        }
                        mPpw.dismiss();
                    }
            );
        }

        @Override
        public int getItemCount() {
            return tracks.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final View ivCheck;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.mItemName);
            ivCheck = itemView.findViewById(R.id.mItemCheck);
        }
    }

    private float SEEKBAR_MAX = 10000.0f;

    private void setSeekBarParams() {
        mVideoSeekBar.setTextSize(20);
        mVideoSeekBar.setTextColor(Color.WHITE);
        mVideoSeekBar.setMyPadding(120, 0, 120, 0);
        mVideoSeekBar.setImagePadding(0, 0);
        mVideoSeekBar.setTextPadding(0, 0);
        mVideoSeekBar.setMax(10000);
        mVideoSeekBar.setKeyProgressIncrement(10000 / 200);
        mVideoSeekBar.setTextVisiable(true);
    }

    private PopupWindow mPpw;
    private View mPpwContent;
    private RecyclerView mPlaySwitcherView;

    private void initPopupWindow() {
        mPpwContent = LayoutInflater.from(this).inflate(R.layout.popup_window, null);
        mPlaySwitcherView = mPpwContent.findViewById(R.id.rv_pw);
        mPlaySwitcherView.setLayoutManager(new GridLayoutManager(this, 1));

        mPpw = new PopupWindow(mPpwContent, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPpw.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        mPpw.setFocusable(true);
        mPpw.setOutsideTouchable(true);
    }

    private void displayPopupWindow(RecyclerView.Adapter<?> adapter, View view) {
        mPlaySwitcherView.setAdapter(adapter);
        mPpwContent.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int measuredHeight = mPpwContent.getMeasuredHeight();
        int measuredWidth = mPpwContent.getMeasuredWidth();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mPpw.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth() / 2 - measuredWidth / 2, location[1] - measuredHeight);
    }

    private void setSeekListener() {
        mVideoSeekBar.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mDuration > 0) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            mSeekBarScroll = true;
                            resetHideTimer();
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                mHandler.removeMessages(PLAYER_SEEK);
                            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                                int curProg = mVideoSeekBar.getProgress();
                                mHandler.removeMessages(PLAYER_SEEK);
                                Message msg = mHandler.obtainMessage(PLAYER_SEEK);
                                msg.obj = curProg;
                                mHandler.sendMessageDelayed(msg, 300);
                            }
                            break;
                    }
                }
                return false;
            }
        });
        mVideoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            long newPosition;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                resetHideTimer();
                if (mDuration > 0) {
                    newPosition = (long) (mDuration * (progress / SEEKBAR_MAX));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                resetHideTimer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mDuration > 0) {
                    mVideoView.seekTo(newPosition);
                }
            }
        });
    }

    private void resetHideTimer() {
        mHandler.removeMessages(MSG_HIDE_CONTROLLER);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, 11000);
    }

    private void startPlayer() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        mVideoView = findViewById(R.id.mVideoView);
        if (mVideoView != null) {
            mVideoView.setOnPreparedListener(mPreparedListener);
            mVideoView.setOnCompletionListener(mCompletionListener);
            mVideoView.setOnSeekCompleteListener(mSeekCompleteListener);
            mVideoView.setOnErrorListener(mErrorListener);
            mVideoView.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mVideoView.setOnInfoListener(mInfoListener);
            mVideoView.setPlayerCallback(mPlayerListener);

            mVideoView.selectPlayer(mPlayerType);
            mPlayerAdapter.updateSelectedIndex(indexLoaderByPlayerType.get(mPlayerType));
            tvPlayerType.setText(mPlayerType.name());
            mVideoView.setIsUsingMediaCodec(isUsingMediaCodec);
            playStartTime = SystemClock.elapsedRealtime();
            mVideoView.setVideoURI(Uri.parse(mVideoPath));
        }
    }

    private void release() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
        }
        LogUtil.clear();
        if (mShowAllBarsAnimator != null) {
            mShowAllBarsAnimator.cancel();
            mShowAllBarsAnimator.removeAllListeners();
            mShowAllBarsAnimator = null;
        }
        if (mHideAllBarsAnimator != null) {
            mHideAllBarsAnimator.cancel();
            mHideAllBarsAnimator.removeAllListeners();
            mHideAllBarsAnimator = null;
        }
        mCurrAudioTrackInfoList = null;
        mAudioTrackCount = 0;
        sAudioTracks = "";
        mDuration = 0;
        tvTimePass.setText("");
        tvTimeLong.setText("");
        tvPlayerType.setText("");
        tvAudioTrack.setText("");
        tvPlayerInfo.setText("");
        tvFirstShowTime.setText("");
    }

    private final int PLAYER_SEEK = 0x146;
    private final int PLAYER_VOLUME = 0x307;
    private final int MSG_HIDE_CONTROLLER = 0x141;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAYER_SEEK:
                    int target = (int) ((Integer) msg.obj * (mDuration / SEEKBAR_MAX));
                    LogUtil.logInfo("seek to " + LogUtil.showTime(target / 1000));
                    mVideoView.seekTo(target);
                    mSeekBarScroll = false;
                    break;
                case PLAYER_VOLUME:
                    int volume = (int) msg.obj;
                    LogUtil.logInfo("change volume to " + volume);
                    mVideoView.setVolume(volume);
                case MSG_HIDE_CONTROLLER:
                    mHideAllBarsAnimator.start();
                    break;
                default:
                    break;
            }
        }
    };

    private PopupWindow ppwVolume;
    public void setVolume(View view) {
        if (ppwVolume != null && ppwVolume.isShowing()) {
            ppwVolume.dismiss();
        } else {
            if (ppwVolume == null) {
                ppwVolume = new PopupWindow(mVolumeController, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                ppwVolume.setFocusable(true);
            }
            mVolumeController.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int measuredHeight = mVolumeController.getMeasuredHeight();
            int measuredWidth = mVolumeController.getMeasuredWidth();
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            ppwVolume.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth() / 2 - measuredWidth / 2, location[1] - measuredHeight);
        }
    }

    private int curSecondaryProgress = 0;
    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            if (mVideoView != null) mDuration = mVideoView.getDuration();
            mVideoSeekBar.setEnabled(true);
            playEndTime = SystemClock.elapsedRealtime();
            tvFirstShowTime.setText((int)(playEndTime - playStartTime) + "ms");
            LogUtil.logInfo("加载完成!");
            if (mDuration > 0) {
                mVideoSeekBar.setDuration(mDuration);
                tvTimeLong.setText(LogUtil.showTime(mDuration / 1000));
                sAudioTracks = "";
                getAudioTrackInfo();
            }
        }
    };

    IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            LogUtil.logInfo("onCompletion");
            if (mVideoView != null) {
                if (!mVideoView.isPlaying()) {
                    ((ImageView)btnPlayPause).setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }
            }
        }
    };

    IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent, long bufferPosition) {
            if (!mSeekBarScroll && mButtomController.getVisibility() == View.VISIBLE) {
                long bufPos = 0;
                if (bufferPosition <= 0 && percent > 0 && mDuration > 0) {
                    bufPos = (mDuration / 100) * percent;
                } else {
                    bufPos = bufferPosition;
                }
                curSecondaryProgress = (int)(bufPos * (SEEKBAR_MAX / mDuration));
            }
        }
    };

    IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            LogUtil.logInfo("seek complete");
        }
    };

    IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    IPlayerListener mPlayerListener = new IPlayerListener() {
        @Override
        public void onProgress(Long curPosition) {
            if (mVideoView != null && mVideoView.isPlaying() && !mSeekBarScroll && mButtomController.getVisibility() == View.VISIBLE) {
                String s = LogUtil.showTime((curPosition) / 1000);
                tvTimePass.setText(s);
                if (mDuration != 0) {
                    int pos = (int)(curPosition * (SEEKBAR_MAX / mDuration));
                    mVideoSeekBar.setProgress(pos);
                    mVideoSeekBar.setSecondaryProgress(curSecondaryProgress);
                }
            }
        }

        @Override
        public void onDebugInfo(String playerInfo) {
            tvPlayerInfo.setText(playerInfo);
        }
    };

    private void switchPlayer(PlayerEnum player) {
        LogUtil.logInfo("设置为" + player + "播放器");
        mVideoView.selectPlayer(player);
        mPlayerAdapter.updateSelectedIndex(indexLoaderByPlayerType.get(player));
        tvFirstShowTime.setText("");
        tvAudioTrack.setText("");
        sAudioTracks = "";
    }

    private void setAudioTrack(int audioIndex) {
        if (mCurrAudioTrackInfoList != null) {
            String selectAudio = "";
            for (MultiTrackInfo multiTrackInfo : mCurrAudioTrackInfoList) {
                if (multiTrackInfo.getIndex() == audioIndex) {
                    selectAudio = multiTrackInfo.getLang();
                    mVideoView.selectTrack(audioIndex);
                    mATrackAdapter.updateSelectedIndex(audioIndex);
                    LogUtil.logInfo("切换到" + audioIndex + "音轨");
                    if (mAudioTrackCount > 1)
                        tvAudioTrack.setText(selectAudio + ", 共" + mAudioTrackCount + "个音轨:" + sAudioTracks);
                    else
                        tvAudioTrack.setText(selectAudio + ", 共" + mAudioTrackCount + "个音轨.");
                    break;
                }
            }
        }
    }

    private void getAudioTrackInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mCurrAudioTrackInfoList = mVideoView.getTrackInfoList();
            for (MultiTrackInfo trackInfo : mCurrAudioTrackInfoList) {
                sAudioTracks += trackInfo.getLang() + " ";
            }

            String selectAudio = "";
            int selectAudioIndex = -1;
            if (mCurrAudioTrackInfoList.size() > 0) {
                mAudioTrackCount = mCurrAudioTrackInfoList.size();
                int trackIndex = mVideoView.getSelectedTrack();
                if (trackIndex >= 0) {
                    for (MultiTrackInfo trackInfo : mCurrAudioTrackInfoList) {
                        if (trackIndex == trackInfo.getIndex()) {
                            selectAudio = trackInfo.getLang();
                            selectAudioIndex = trackIndex;
                            break;
                        }
                    }
                } else {
                    selectAudio = mCurrAudioTrackInfoList.get(0).getLang();
                    selectAudioIndex = mCurrAudioTrackInfoList.get(0).getIndex();
                }
            }
            mATrackAdapter.init(mCurrAudioTrackInfoList, selectAudioIndex);

            if (mAudioTrackCount > 1)
                tvAudioTrack.setText(selectAudio + ", 共" + mAudioTrackCount + "个音轨:" + sAudioTracks);
            else
                tvAudioTrack.setText(selectAudio + ", 共" + mAudioTrackCount + "个音轨.");
        }
    }
}
