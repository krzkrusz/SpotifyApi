package pl.edu.pg.spotify.util;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.mappers.jackson.JacksonMapper;
import com.spotify.protocol.types.Track;

import java.util.concurrent.atomic.AtomicReference;


public class SpotifyUtils {
    private static final String CLIENT_ID = "437d0cfe01824ddaaadc89bf865052b9";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static SpotifyAppRemote mSpotifyAppRemote;
    private static AtomicReference<Track> track = new AtomicReference<>();

    public static void connect(Context context) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .setJsonMapper(JacksonMapper.create())
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    public static AtomicReference<Track> getTrack() {
        return track;
    }

    private static void connected() {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        getTrack().set(track);
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
    }

    public static void disconnect() {
        if (mSpotifyAppRemote != null)
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    public static String getCurrentTrackInfo() {
        Track track = getTrack().get();
        if (track == null) {
            return "no track";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("uri: ").append(track.uri).append("\n")
                    .append("duration: ").append(track.duration).append("\n")
                    .append("name: ").append(track.name).append("\n")
                    .append("artist: ").append(track.artist).append("\n")
                    .append("album: ").append(track.album).append("\n");
            return builder.toString();
        }
    }

    public static void nextSong() {
        if (mSpotifyAppRemote != null) {
           mSpotifyAppRemote.getPlayerApi().skipNext();
        }
    }

}
