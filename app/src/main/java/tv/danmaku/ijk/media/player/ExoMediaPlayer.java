package tv.danmaku.ijk.media.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;

import com.example.ijk.BuildConfig;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import tv.danmaku.ijk.media.player.misc.ExoTrackInfo;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * Created by lzy on 2022/5/9.
 */
public class ExoMediaPlayer extends AbstractMediaPlayer {

    private final String TAG = "ExoMediaPlayer";

    private Context mContext;
    private SimpleExoPlayer mPlayer;
    private Handler mHandler;
    private boolean mIsPreparing = true;
    private boolean mIsBuffering = false;
    private DefaultTrackSelector mTrackSelector;

    private SurfaceHolder mSurfaceHolder;
    private boolean mScreenOnWhilePlaying;
    private boolean mStayAwake;

    public ExoMediaPlayer(Context context) {
        mContext = context;

        mTrackSelector = new DefaultTrackSelector(mContext);
        mTrackSelector.setParameters(mTrackSelector.buildUponParameters().setPreferredAudioLanguage("por"));

        mPlayer = new SimpleExoPlayer.Builder(mContext).setTrackSelector(mTrackSelector).build();

        mHandler = new Handler(Looper.getMainLooper());

        mPlayer.addListener(new Player.EventListener() {

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                if (!isLoading) {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "onIsLoadingChanged current: " + mPlayer.getCurrentPosition() + ", buffer: " + mPlayer.getBufferedPosition());
                    }
                    notifyOnBufferingUpdate(mPlayer.getBufferedPercentage()/*, mPlayer.getBufferedPosition()*/);
                }
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onPlaybackStateChanged: " + state);
                }
                switch (state) {
                    case Player.STATE_IDLE: {
                        break;
                    }
                    case Player.STATE_BUFFERING: {
                        notifyOnInfo(IMediaPlayer.MEDIA_INFO_BUFFERING_START, IMediaPlayer.MEDIA_INFO_BUFFERING_START);
                        mIsBuffering = true;
                        break;
                    }
                    case Player.STATE_READY: {
                        if (mIsBuffering) {
                            mIsBuffering = false;
                            notifyOnInfo(IMediaPlayer.MEDIA_INFO_BUFFERING_END, IMediaPlayer.MEDIA_INFO_BUFFERING_END);
                        }
                        if (mIsPreparing) {
                            mIsPreparing = false;
                            notifyOnPrepared();
                            if (BuildConfig.DEBUG) {
                                Log.i(TAG, "prepared: " + mPlayer.getDuration() + ", currentAudio: " + mPlayer.getAudioFormat().toString());
                            }
                        }
                        break;
                    }
                    case Player.STATE_ENDED: {
                        if (mIsBuffering) {
                            mIsBuffering = false;
                            notifyOnInfo(IMediaPlayer.MEDIA_INFO_BUFFERING_END, IMediaPlayer.MEDIA_INFO_BUFFERING_END);
                        } else {
                            stayAwake(false);
                            notifyOnCompletion();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "onPlayerError: ", error);
                }
                notifyOnError(error.type, error.rendererFormatSupport);
                stayAwake(false);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onPositionDiscontinuity:  reason: " + reason);
                }
            }

        });

        mPlayer.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {
                Log.w(TAG, "onDroppedVideoFrames:  droppedFrames: " + droppedFrames);
            }

            @Override
            public void onLoadStarted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onLoadStarted:  ");
                }
            }

            @Override
            public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onLoadCompleted:  ");
                }
            }

            @Override
            public void onRenderedFirstFrame(EventTime eventTime, @Nullable Surface surface) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onRenderedFirstFrame:  ");
                }
                notifyOnInfo(IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START, IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START);
            }

            @Override
            public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }
        });
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        mSurfaceHolder = sh;
        if (sh == null) {
            setSurface(null);
        } else {
            setSurface(sh.getSurface());
        }
    }

    @Override
    public void setDataSource(Context context, final Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MediaItem mediaItem = MediaItem.fromUri(uri);
                mPlayer.setMediaItem(mediaItem);
            }
        });
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(context, uri);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(final String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MediaItem mediaItem = MediaItem.fromUri(path);
                mPlayer.setMediaItem(mediaItem);
            }
        });
    }

    @Override
    public String getDataSource() {
        return null;
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        //prepare和notifyOnPrepared是顺序执行的，post使prepare在baseVideo的设置MediaState后再调用，避免修改过早
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlayer.setPlayWhenReady(true);
                mPlayer.prepare();
            }
        });
    }

    @Override
    public void start() throws IllegalStateException {
        stayAwake(true);
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
        mPlayer.play();
//            }
//        });
    }

    @Override
    public void stop() throws IllegalStateException {
        stayAwake(false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlayer.stop();
                mPlayer.release();
                mPlayer.setVideoSurface(null);
            }
        });
    }

    @Override
    public void pause() throws IllegalStateException {
        stayAwake(false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlayer.pause();
            }
        });
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mScreenOnWhilePlaying != screenOn) {
            if (screenOn && mSurfaceHolder == null) {
                Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder");
            }
            mScreenOnWhilePlaying = screenOn;
            updateSurfaceScreenOn();
        }
    }

    @Override
    public int getVideoWidth() {
        if (mPlayer.getVideoFormat() == null) {
            return 0;
        } else {
            return mPlayer.getVideoFormat().width;
        }
    }

    @Override
    public int getVideoHeight() {
        if (mPlayer.getVideoFormat() == null) {
            return 0;
        } else {
            return mPlayer.getVideoFormat().height;
        }
    }

    @Override
    public boolean isPlaying() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return mPlayer.isPlaying();
        } else {
            final AtomicBoolean wait = new AtomicBoolean(false);
            final AtomicBoolean isPlaying = new AtomicBoolean(false);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    isPlaying.set(mPlayer.isPlaying());
                    wait.set(true);
                }
            });
            while (!wait.get()) {

            }
            return isPlaying.get();
        }
    }

    @Override
    public void seekTo(final long msec) throws IllegalStateException {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "seekTo: " + msec);
                }
                mPlayer.seekTo(msec);
            }
        });
    }

    @Override
    public long getCurrentPosition() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return mPlayer.getCurrentPosition();
        } else {
            final AtomicLong position = new AtomicLong(-1);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    position.set(mPlayer.getCurrentPosition());
                }
            });
            while (position.get() == -1) {

            }
            return position.get();
        }
    }

    @Override
    public long getDuration() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return mPlayer.getDuration();
        } else {
            final AtomicLong duration = new AtomicLong(-1);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    duration.set(mPlayer.getDuration());
                }
            });
            while (duration.get() == -1) {

            }
            return duration.get();
        }
    }

    @Override
    public void release() {
        stayAwake(false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlayer.release();
            }
        });
    }

    @Override
    public void reset() {
        stayAwake(false);

    }

    @Override
    public void setVolume(final float leftVolume, float rightVolume) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlayer.setVolume(leftVolume);
            }
        });
    }

    @Override
    public int getAudioSessionId() {
        // TODO: 2022/5/13
        return mPlayer.getAudioSessionId();
    }

    @Override
    public MediaInfo getMediaInfo() {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.mMediaPlayerName = "ExoPlayer";
        return mediaInfo;
    }

    @Override
    public void setLogEnabled(boolean enable) {

    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public void setAudioStreamType(int streamtype) {

    }

    @Override
    public void setKeepInBackground(boolean keepInBackground) {

    }

    @Override
    public int getVideoSarNum() {
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        return 0;
    }

    @Override
    public void setWakeMode(Context context, int mode) {

    }

    @Override
    public void setLooping(final boolean looping) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlayer.setRepeatMode(looping ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
            }
        });
    }

    @Override
    public boolean isLooping() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return mPlayer.getRepeatMode() == Player.REPEAT_MODE_ALL;
        } else {
            final AtomicInteger repeatMode = new AtomicInteger(-1);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    repeatMode.set(mPlayer.getRepeatMode());
                }
            });
            while (repeatMode.get() == -1) {

            }
            return repeatMode.get() == Player.REPEAT_MODE_ALL;
        }
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        ArrayList<ExoTrackInfo> trackInfos = new ArrayList<>();

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = mTrackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
                TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(i);
                for (int groupIndex = 0; groupIndex < rendererTrackGroups.length; groupIndex++) {
                    TrackGroup trackGroup = rendererTrackGroups.get(groupIndex);
                    trackInfos.add(new ExoTrackInfo(trackGroup));
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, trackGroup.getFormat(0).toString());
                    }
                }
            }
        }

        if (trackInfos.size() > 0) {
            return trackInfos.toArray(new ExoTrackInfo[0]);
        }
        return new ITrackInfo[0];
    }

    @Override
    public void setSurface(final Surface surface) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlayer.setVideoSurface(surface);
            }
        });
    }

    public void selectAudioTrack(String language) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "selectAudioTrack language: " + language);
        }
        mTrackSelector.setParameters(mTrackSelector.getParameters().buildUpon().setPreferredAudioLanguage(language));
    }

    public void selectAudioTrack(int index) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "selectAudioTrack index: " + index);
        }
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = mTrackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            int audioIndex = -1;
            int mediaIndex = -1;
            for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
                TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
                int rendererType =  mappedTrackInfo.getRendererType(i);
                for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
                    mediaIndex++;
                    if (C.TRACK_TYPE_AUDIO == rendererType) {
                        audioIndex++;
                        if (index == mediaIndex)
                            break;
                    }
                }
            }
            TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(C.TRACK_TYPE_AUDIO);
            DefaultTrackSelector.SelectionOverride selectionOverride = new DefaultTrackSelector.SelectionOverride(audioIndex, 0);
            mTrackSelector.setParameters(mTrackSelector.getParameters().buildUpon().setSelectionOverride(C.TRACK_TYPE_AUDIO, rendererTrackGroups, selectionOverride));
        }
    }

    public int getSelectedAudioTrack(int trackType) {
        if (mPlayer.getAudioFormat() != null && mPlayer.getAudioFormat().id != null) {
            String id = mPlayer.getAudioFormat().id;
            //Exo trackId从1开始
            return Integer.getInteger(id, -1) - 1;
        } else {
            return -1;
        }
    }

    private void stayAwake(boolean awake) {
        mStayAwake = awake;
        updateSurfaceScreenOn();
    }

    private void updateSurfaceScreenOn() {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setKeepScreenOn(mScreenOnWhilePlaying && mStayAwake);
        }
    }
}
