package tv.danmaku.ijk.media.player.misc;

import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.util.MimeTypes;

import java.util.Locale;

public class ExoTrackInfo implements ITrackInfo {

    private TrackGroup mTrackGroup;

    public ExoTrackInfo(TrackGroup trackGroup) {
        mTrackGroup = trackGroup;
    }

    @Override
    public IMediaFormat getFormat() {
        return null;
    }

    @Override
    public String getLanguage() {
        if (mTrackGroup.getFormat(0).language != null) {
            //Exo返回的音轨语言是 IETF BCP 47，需要转化为ISO-639-2.en->eng,pt->por
            return new Locale(mTrackGroup.getFormat(0).language).getISO3Language();
        }
        return mTrackGroup.getFormat(0).language;
    }

    @Override
    public int getTrackType() {
        String sampleMimeType = mTrackGroup.getFormat(0).sampleMimeType;
        if (MimeTypes.isVideo(sampleMimeType)) {
            return MEDIA_TRACK_TYPE_VIDEO;
        } else if (MimeTypes.isAudio(sampleMimeType)) {
            return MEDIA_TRACK_TYPE_AUDIO;
        }
        return MEDIA_TRACK_TYPE_UNKNOWN;
    }

    @Override
    public String getInfoInline() {
        return null;
    }

    @Override
    public String toString() {
        return "ExoTrackInfo{" +
                "mTrackGroup.Format=" + mTrackGroup.getFormat(0).toString() +
                '}';
    }
}
