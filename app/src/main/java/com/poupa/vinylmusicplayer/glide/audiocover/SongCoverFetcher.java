package com.poupa.vinylmusicplayer.glide.audiocover;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.poupa.vinylmusicplayer.App;
import com.poupa.vinylmusicplayer.util.AutoCloseAudioFile;
import com.poupa.vinylmusicplayer.util.OopsHandler;
import com.poupa.vinylmusicplayer.util.SAFUtil;

import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;

/**
 * @author Karim Abou Zeid (kabouzeid)
 * @author SC (soncaokim)
 */
public class SongCoverFetcher extends AbsCoverFetcher {
    private final SongCover model;

    public SongCoverFetcher(SongCover model) {
        super();
        this.model = model;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        try (AutoCloseAudioFile audio = SAFUtil.loadReadOnlyAudioFile(App.getStaticContext(), model.song)) {
            if (audio == null) {
                callback.onLoadFailed(new IOException("Cannot load audio file"));
                return;
            }

            Tag tags = audio.get().getTag();
            if (tags != null) {
                Artwork art = tags.getFirstArtwork();
                if (art != null) {
                    byte[] imageData = art.getBinaryData();
                    callback.onDataReady(new ByteArrayInputStream(imageData));
                    return;
                }
            }
            InputStream data = fallback(model.song.data);
            if (data != null) {callback.onDataReady(data);}
            else {callback.onLoadFailed(new MissingResourceException("No artwork", "", ""));}
        } catch (Exception e) {
            OopsHandler.collectStackTrace(e);
            callback.onLoadFailed(e);
        }
    }
}
