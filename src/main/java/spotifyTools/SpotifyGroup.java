package spotifyTools;

import logic.ActiveUsers;
import org.telegram.telegrambots.meta.api.objects.User;
import spotify.api.interfaces.TrackApi;
import spotify.models.players.requests.TransferPlaybackRequestBody;
import spotify.models.tracks.TrackSimplified;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpotifyGroup {
    private User leader;
    private final Queue<TrackSimplified> queue = new LinkedList<>();

    public SpotifyGroup(User leader) {
        this.leader = leader;
    }


    public User getLeader() {
        return leader;
    }

    public void addSong(TrackSimplified track) {
        queue.add(track);
    }

    public TrackSimplified getNextSong() {
        return queue.poll();
    }

    public void transferPlayback(String device) {

        if (ActiveUsers.getSession(leader).getTokenExpiresIn() <= 30) {
            ActiveUsers.getSession(leader).authorizeByRefreshToken();
        }

        var devices = ActiveUsers.getSession(leader).getSpotifyApi().getAvailableDevices().getDevices();

        for (var device_iter : devices) {
            if (device_iter.getName().equals(device)) {
                TransferPlaybackRequestBody requestBody = new TransferPlaybackRequestBody();
                requestBody.setDeviceIds(List.of(device_iter.getId()));

                ActiveUsers.getSession(leader).getSpotifyApi().transferPlayback(requestBody);
            }
        }
    }
}
