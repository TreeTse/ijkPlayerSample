package tv.danmaku.ijk.media.example.widget.media;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.TableLayout;

import java.util.Locale;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;
import com.example.ijkplayersample.R;

import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

public class InfoHudViewHolder {
    private TableLayoutBinder mTableLayoutBinder;
    private SparseArray<View> mRowMap = new SparseArray<View>();
    private IMediaPlayer mMediaPlayer;
    private long mLoadCost = 0;
    private long mSeekCost = 0;
    private boolean mIsPrepared= false;

    public InfoHudViewHolder(Context context, TableLayout tableLayout) {
        mTableLayoutBinder = new TableLayoutBinder(context, tableLayout);
    }

    public void setIsPrepared(boolean isPrepared) {
        mIsPrepared = isPrepared;
    }

    private void appendSection(int nameId) {
        mTableLayoutBinder.appendSection(nameId);
    }

    private void appendRow(int nameId) {
        View rowView = mTableLayoutBinder.appendRow2(nameId, null);
        mRowMap.put(nameId, rowView);
    }

    private void setRowValue(int id, String value) {
        View rowView = mRowMap.get(id);
        if (rowView == null) {
            rowView = mTableLayoutBinder.appendRow2(id, value);
            mRowMap.put(id, rowView);
        } else {
            mTableLayoutBinder.setValueText(rowView, value);
        }
    }

    public void setMediaPlayer(IMediaPlayer mp) {
        mMediaPlayer = mp;
        if (mMediaPlayer != null) {
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);
        } else {
            mHandler.removeMessages(MSG_UPDATE_HUD);
        }
    }

    private static String formatedDurationMilli(long duration) {
        if (duration >=  1000) {
            return String.format(Locale.US, "%.2f sec", ((float)duration) / 1000);
        } else {
            return String.format(Locale.US, "%d msec", duration);
        }
    }

    private static String formatedSpeed(long bytes,long elapsed_milli) {
        if (elapsed_milli <= 0) {
            return "0 B/s";
        }

        if (bytes <= 0) {
            return "0 B/s";
        }

        float bytes_per_sec = ((float)bytes) * 1000.f /  elapsed_milli;
        if (bytes_per_sec >= 1000 * 1000) {
            return String.format(Locale.US, "%.2f MB/s", ((float)bytes_per_sec) / 1000 / 1000);
        } else if (bytes_per_sec >= 1000) {
            return String.format(Locale.US, "%.1f KB/s", ((float)bytes_per_sec) / 1000);
        } else {
            return String.format(Locale.US, "%d B/s", (long)bytes_per_sec);
        }
    }

    public void updateLoadCost(long time)  {
        mLoadCost = time;
    }

    public void updateSeekCost(long time)  {
        mSeekCost = time;
    }

    private static String formatedSize(long bytes) {
        if (bytes >= 100 * 1000) {
            return String.format(Locale.US, "%.2f MB", ((float)bytes) / 1000 / 1000);
        } else if (bytes >= 100) {
            return String.format(Locale.US, "%.1f KB", ((float)bytes) / 1000);
        } else {
            return String.format(Locale.US, "%d B", bytes);
        }
    }

    private static final int MSG_UPDATE_HUD = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_HUD: {
                    InfoHudViewHolder holder = InfoHudViewHolder.this;
                    //IjkMediaPlayer mp = null;
                    IMediaPlayer mp = null;
                    IjkMediaPlayer ijkMp = null;
                    AndroidMediaPlayer androidMp = null;
                    IjkExoMediaPlayer exoMp = null;
                    String decoder = "";
                    String fpsOutput = "";
                    String fpsDecode = "";
                    int curAudioTrack = -1;
                    IjkTrackInfo trackInfo[] = null;
                    int audioTrackSize = 0;
                    if (mMediaPlayer == null)
                        break;
                    if (mMediaPlayer instanceof IjkMediaPlayer) {
                        ijkMp = (IjkMediaPlayer) mMediaPlayer;
                        mp = ijkMp;
                        decoder = ijkMp.getMediaInfo().mVideoDecoder + ", " + ijkMp.getMediaInfo().mVideoDecoderImpl;
                        fpsDecode = Float.toString(ijkMp.getVideoDecodeFramesPerSecond());
                        fpsOutput = Float.toString(ijkMp.getVideoOutputFramesPerSecond());
                        curAudioTrack = ijkMp.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
                        trackInfo = ijkMp.getTrackInfo();
                        for(int i = 0; i < trackInfo.length; i++) {
                            if(trackInfo[i].getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO)
                                audioTrackSize++;
                        }
                        //mp = (IjkMediaPlayer) mMediaPlayer;
                    }
                    if (mMediaPlayer instanceof MediaPlayerProxy) {
                        MediaPlayerProxy proxy = (MediaPlayerProxy) mMediaPlayer;
                        IMediaPlayer internal = proxy.getInternalMediaPlayer();
                        if (internal != null && internal instanceof IjkMediaPlayer){
                            //mp = (IjkMediaPlayer) internal;
                        }
                    }
                    if(mMediaPlayer instanceof AndroidMediaPlayer) {
                        androidMp = (AndroidMediaPlayer) mMediaPlayer;
                        mp = androidMp;
                        decoder = androidMp.getMediaInfo().mVideoDecoder + androidMp.getMediaInfo().mVideoDecoderImpl;
                    } else if(mMediaPlayer instanceof IjkExoMediaPlayer) {
                        exoMp = (IjkExoMediaPlayer) mMediaPlayer;
                        mp = exoMp;
                        decoder = exoMp.getMediaInfo().mVideoDecoderImpl;
                    }
                    if (mp == null)
                        break;

                    /*int vdec = mp.getVideoDecoder();
                    switch (vdec) {
                        case IjkMediaPlayer.FFP_PROPV_DECODER_AVCODEC:
                            setRowValue(R.string.vdec, "avcodec");
                            break;
                        case IjkMediaPlayer.FFP_PROPV_DECODER_MEDIACODEC:
                            setRowValue(R.string.vdec, "MediaCodec");
                            break;
                        default:
                            setRowValue(R.string.vdec, "");
                            break;
                    }*/

                    /*float fpsOutput = mp.getVideoOutputFramesPerSecond();
                    float fpsDecode = mp.getVideoDecodeFramesPerSecond();
                    setRowValue(R.string.fps, String.format(Locale.US, "%.2f / %.2f", fpsDecode, fpsOutput));

                    long videoCachedDuration = mp.getVideoCachedDuration();
                    long audioCachedDuration = mp.getAudioCachedDuration();
                    long videoCachedBytes    = mp.getVideoCachedBytes();
                    long audioCachedBytes    = mp.getAudioCachedBytes();
                    long tcpSpeed            = mp.getTcpSpeed();
                    /*long bitRate             = mp.getBitRate();
                    long seekLoadDuration    = mp.getSeekLoadDuration();*/
                    String playerName        = mp.getMediaInfo().mMediaPlayerName;
                    long duration            = 0;
                    if(mIsPrepared == true) {
                        duration  = mp.getDuration();
                    }
                    long curDuration         = mp.getCurrentPosition();

                    setRowValue(R.string.vdec, decoder);
                    /*setRowValue(R.string.v_cache, String.format(Locale.US, "%s, %s", formatedDurationMilli(videoCachedDuration), formatedSize(videoCachedBytes)));
                    setRowValue(R.string.a_cache, String.format(Locale.US, "%s, %s", formatedDurationMilli(audioCachedDuration), formatedSize(audioCachedBytes)));
                    setRowValue(R.string.load_cost, String.format(Locale.US, "%d ms", mLoadCost));
                    setRowValue(R.string.seek_cost, String.format(Locale.US, "%d ms", mSeekCost));
                    setRowValue(R.string.seek_load_cost, String.format(Locale.US, "%d ms", seekLoadDuration));
                    setRowValue(R.string.tcp_speed, String.format(Locale.US, "%s", formatedSpeed(tcpSpeed, 1000)));
                    setRowValue(R.string.bit_rate, String.format(Locale.US, "%.2f kbs", bitRate/1000f));*/
                    setRowValue(R.string.player_name,String.format(Locale.US, "%s", playerName));
                    setRowValue(R.string.duration,String.format(Locale.US, "%d/%d s", curDuration/1000, duration/1000));
                    setRowValue(R.string.load_cost, String.format(Locale.US, "%d ms", mLoadCost));
                    setRowValue(R.string.fps, String.format(Locale.US, "%s / %s", fpsDecode, fpsOutput));
                    setRowValue(R.string.all_audio_track, String.format(Locale.US, "%d", audioTrackSize));
                    setRowValue(R.string.audio_track, String.format(Locale.US, "%d", curAudioTrack));

                    mHandler.removeMessages(MSG_UPDATE_HUD);
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);
                }
            }
        }
    };
}